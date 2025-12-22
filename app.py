from __future__ import annotations
from flask import Flask, render_template, request, session, redirect, url_for
from flask_session import Session
from Master import *
import os
import redis
from pathlib import Path
from datetime import datetime
from zoneinfo import ZoneInfo
import requests
import json
import ipaddress
from werkzeug.middleware.proxy_fix import ProxyFix

app = Flask(__name__)
app.config["SECRET_KEY"] = os.environ.get("SECRET_KEY", "dev-secret-change-me")

# Proxy awareness (Render / reverse proxies). Logging uses XFF parsing below.
app.wsgi_app = ProxyFix(app.wsgi_app, x_for=1, x_proto=1, x_host=1, x_prefix=1)

app.config.update(
    SESSION_COOKIE_HTTPONLY=True,
    SESSION_COOKIE_SAMESITE="Lax",
    SESSION_COOKIE_SECURE=True,  # Render is HTTPS
)

redis_url = os.environ.get("REDIS_URL")

app.config["SESSION_PERMANENT"] = False
app.config["SESSION_USE_SIGNER"] = True

# IMPORTANT: isolate session keys between apps sharing Redis
app.config["SESSION_KEY_PREFIX"] = os.environ.get("SESSION_KEY_PREFIX", "session:pre:")

if redis_url:
    app.config["SESSION_TYPE"] = "redis"
    app.config["SESSION_REDIS"] = redis.from_url(redis_url)
else:
    app.config["SESSION_TYPE"] = "filesystem"
    session_dir = Path(app.instance_path) / "flask_session"
    session_dir.mkdir(parents=True, exist_ok=True)
    app.config["SESSION_FILE_DIR"] = str(session_dir)

Session(app)

TRAINER_PASSWORD_VIEW = os.environ.get("TRAINER_PASSWORD_VIEW", "change-me")


HIDDEN_IPS_RAW = os.environ.get("HIDDEN_IPS", "").strip()
HIDDEN_IPS = {x.strip() for x in HIDDEN_IPS_RAW.split(",") if x.strip()}


def is_hidden_ip(ip: str) -> bool:
    return ip in HIDDEN_IPS


DATA_KEY_PREFIX = (os.environ.get("DATA_KEY_PREFIX", "pre:trainer_log_v1").strip() or "pre:trainer_log_v1")
LOG_KEY = DATA_KEY_PREFIX
ID_KEY = f"{DATA_KEY_PREFIX}:id_counter"

DATA_LOG = []
LOG_COUNTER = 0


def _get_redis():
    r = app.config.get("SESSION_REDIS")
    if r is None:
        return None
    try:
        r.ping()
        return r
    except Exception:
        return None


def _next_local_id():
    global LOG_COUNTER
    LOG_COUNTER += 1
    return LOG_COUNTER


def log_append(entry: dict):
    entry = dict(entry)
    if is_hidden_ip(entry.get("ip", "")):
        return

    r = _get_redis()
    if r is not None:
        if "id" not in entry:
            entry["id"] = int(r.incr(ID_KEY))
        r.rpush(LOG_KEY, json.dumps(entry))
    else:
        if "id" not in entry:
            entry["id"] = _next_local_id()
        DATA_LOG.append(entry)


def log_get_all_raw():
    r = _get_redis()
    if r is not None:
        raw = r.lrange(LOG_KEY, 0, -1)
        return [json.loads(x) for x in raw]
    return list(DATA_LOG)


def filter_out_hidden_entries(entries):
    if not HIDDEN_IPS:
        return list(entries)
    return [e for e in entries if e.get("ip") not in HIDDEN_IPS]


def log_get_all():
    return filter_out_hidden_entries(log_get_all_raw())


def log_replace_all(entries):
    r = _get_redis()
    if r is not None:
        pipe = r.pipeline()
        pipe.delete(LOG_KEY)
        for e in entries:
            pipe.rpush(LOG_KEY, json.dumps(e))
        pipe.execute()
    else:
        global DATA_LOG
        DATA_LOG = list(entries)


def purge_hidden_ips_from_storage():
    if not HIDDEN_IPS:
        return
    entries = log_get_all_raw()
    filtered = filter_out_hidden_entries(entries)
    if len(filtered) != len(entries):
        log_replace_all(filtered)
        print(f"PURGE-HIDDEN removed={len(entries) - len(filtered)}", flush=True)


def purge_null_entries_from_storage():
    entries = log_get_all_raw()

    cleaned = []
    removed = 0

    for e in entries:
        if not isinstance(e, dict):
            removed += 1
            continue

        inp = e.get("input")
        if not isinstance(inp, dict) or len(inp) == 0:
            removed += 1
            continue

        ev = e.get("event")

        if ev == "matrices":
            if inp.get("people") is None or inp.get("crews") is None:
                removed += 1
                continue
        else:
            # submit/test-submit
            if "vehlist" not in inp or "pers5" not in inp or "pers6" not in inp:
                removed += 1
                continue

        cleaned.append(e)

    if removed:
        log_replace_all(cleaned)
        print(f"PURGE-NULL removed={removed}", flush=True)


purge_hidden_ips_from_storage()
purge_null_entries_from_storage()


def is_public_ip(ip: str) -> bool:
    try:
        a = ipaddress.ip_address(ip)
        return not (
                a.is_private or a.is_loopback or a.is_reserved or a.is_multicast or a.is_link_local
        )
    except ValueError:
        return False


def get_client_ip():
    xff = request.headers.get("X-Forwarded-For", "")
    if xff:
        parts = [p.strip() for p in xff.split(",") if p.strip()]
        for ip in parts:
            if is_public_ip(ip):
                return ip, xff, True
        return (parts[0] if parts else (request.remote_addr or "")), xff, False

    ra = request.remote_addr or ""
    return ra, "", is_public_ip(ra)


def is_request_bot(user_agent: str) -> bool:
    ua = (user_agent or "").lower()
    return (
            "go-http-client/" in ua
            or "cron-job.org" in ua
            or "uptimerobot.com" in ua
            or ua.strip() == ""
    )


def lookup_city(ip: str):
    try:
        if ip.startswith("127.") or ip == "::1":
            return {"city": "Localhost", "region": None, "country": None}
        resp = requests.get(f"http://ip-api.com/json/{ip}", timeout=2)
        data = resp.json()
        if data.get("status") != "success":
            return None
        return {
            "city": data.get("city"),
            "region": data.get("regionName"),
            "country": data.get("country"),
        }
    except Exception:
        return None


def _format_loc(geo):
    if not geo:
        return "Location unknown"
    city = geo.get("city") or "Unknown city"
    region = geo.get("region") or "Unknown region"
    country = geo.get("country") or "Unknown country"
    return f"{city}, {region}, {country}"


def format_inputs_pretty(inp: dict) -> str:
    return (
        f"\n  Vehicle List: {inp.get('vehlist', 'NULL')}"
        f"\n  5-Person: {inp.get('pers5', 'NULL')}"
        f"\n  6-Person: {inp.get('pers6', 'NULL')}"
        f"\n  Pull Combos: {inp.get('pull_combinations', 'NULL')}"
        f"\n  Use Combos: {inp.get('use_combinations', 'NULL')}"
    )

def print_event(
        event: str,
        user_ip: str,
        geo,
        xff_chain: str,
        remote_addr: str,
        payload_str: str | None = None,
):
    ts = datetime.now(ZoneInfo("America/Chicago")).strftime("%Y-%m-%d %H:%M:%S")
    loc = _format_loc(geo)

    print(f"\n{event.upper()} @ {ts}", flush=True)
    print(f"  IP: {user_ip}", flush=True)
    print(f"  Location: {loc}", flush=True)

    if payload_str:
        print(payload_str, flush=True)

    print("-" * 40, flush=True)

def build_grouped_entries(entries):
    entries = list(reversed(entries))  # most recent first
    grouped = {}
    for e in entries:
        ip = e.get("ip", "Unknown IP")
        grouped.setdefault(ip, []).append(e)
    return grouped


def is_trainer_authed() -> bool:
    return bool(session.get("trainer_authed", False))


def _safe_return_path(path: str | None) -> str:
    allowed = {"/", "/test"}
    return path if path in allowed else "/"


@app.route("/", methods=["GET", "POST"], strict_slashes=False)
def index():
    user_ip, xff_chain, ip_ok = get_client_ip()
    is_bot = is_request_bot(request.headers.get("User-Agent", ""))
    geo = lookup_city(user_ip)

    if request.method == "GET":
        session["return_after_matrices"] = "/"
        return render_template(
            "index.html",
            vehlist=",".join(
                map(
                    str,
                    session.get("vehlist", [])
                    if isinstance(session.get("vehlist", []), list)
                    else [session.get("vehlist")],
                )
            ),
            pers5=session.get("pers5", ""),
            pers6=session.get("pers6", ""),
            results=session.get("results"),
            totalhelp=session.get("totalhelp"),
            sorted_allocations=session.get("sorted_allocations"),
            rem_vehs=session.get("rem_vehs"),
            allocations_only=session.get("allocations_only", 0),
            pull_combinations=session.get("pull_combinations", 0),
            error_message=None,
            backupsize=session.get("backupsize"),
            alllist=session.get("alllist"),
            matrices_result=session.get("matrices_result"),
            ranges_result=session.get("ranges_result"),
            total_people=session.get("total_people", ""),
            people=session.get("people", ""),
            crews=session.get("crews", ""),
            zip=zip,
            enumerate=enumerate,
            len=len,
        )

    pers5 = pers6 = 0
    vehlist_input = ""
    try:
        vehlist_input = request.form.get("vehlist", "").strip()
        pull_combinations = int(request.form.get("pull_combinations", 0))
        use_combinations = int(request.form.get("use_combinations", 0))

        pers5 = int(request.form.get("pers5") or 0)
        pers6 = int(request.form.get("pers6") or 0)
        vehlist = [int(x.strip()) for x in vehlist_input.split(",") if x.strip()]

        # log submits only
        if (not is_bot) and (not is_hidden_ip(user_ip)):
            log_entry = {
                "ip": user_ip,
                "xff": xff_chain,
                "remote_addr": request.remote_addr,
                "geo": geo,
                "timestamp": datetime.now(ZoneInfo("America/Chicago")).strftime("%Y-%m-%d  %H:%M:%S"),
                "event": "submit",
                "input": {
                    "vehlist": vehlist,
                    "pers5": pers5,
                    "pers6": pers6,
                    "pull_combinations": pull_combinations,
                    "use_combinations": use_combinations,
                },
            }
            log_append(log_entry)

        veh2 = vehlist.copy()
        veh2.sort(reverse=True)
        validate_inputs(vehlist, pers5, pers6)

        backup_group = pers5
        backupsize = 5
        primary_group = pers6

        results_1 = optimal_allocation(veh2[:].copy(), primary_group, backup_group, 6, backupsize)
        results = trickle_down(results_1, backupsize)
        off = [backup_group - results[0][0], primary_group - results[0][1]]

        if not results or not isinstance(results, list) or len(results) < 2:
            raise ValueError("Invalid results returned from calculations.")

        sorted_allocations, sorted_spaces, sorted_sizes, number = sort_closestalg_output(results, backupsize)

        if sum(off) <= pers6 and backupsize == 5:
            combos, listing = call_sixesFlipped(sorted_allocations, sorted_spaces, off.copy(), backupsize, None)
        else:
            combos, listing = call_combine(sorted_allocations, sorted_spaces, off.copy(), backupsize, None)

        listing1 = listing.copy()
        combos1 = combos.copy()
        combos3 = combos1.copy()
        listing3 = listing1.copy()
        rem_vehs1 = unused(sorted_allocations.copy(), combos.copy())

        if combos:
            for elem in rem_vehs1:
                combos1.append([elem])
                listing1.append([0, 0])

            combos2, newalloc = call_optimize(
                sorted_allocations.copy(),
                listing1,
                backupsize,
                combos1,
                sorted_spaces,
            )
            combos3 = combos2
            listing3 = newalloc

        combos, listing = cleanup(combos3, sorted_spaces, listing3)
        damage = harm(combos.copy(), sorted_allocations.copy())
        totalhelp = combosSum(combos.copy(), sorted_allocations.copy(), off.copy())

        combos = person_calc(combos.copy(), sorted_sizes.copy())
        alllist = alltogether(combos, listing, damage)

        less = nonzero(sorted_spaces, sorted_sizes)
        rem_vehs2 = unused1(less[1], combos.copy())
        rem_vehs = quant(rem_vehs2)

        restored_vehs, restored_all, restored_spaces = restore_order(
            vehlist[:].copy(), sorted_sizes, sorted_allocations, sorted_spaces
        )

        combined_sorted_data = [
            [restored_vehs[i], restored_all[i], restored_spaces[i], number[i]]
            for i in range(len(sorted_sizes))
        ]

        session["sorted_allocations"] = combined_sorted_data
        session["totalhelp"] = totalhelp
        session["alllist"] = alllist
        session["backupsize"] = backupsize

        if pull_combinations == 0 and use_combinations == 0:
            session["vehlist"] = vehlist
            session["pers5"] = pers5
            session["pers6"] = pers6
        elif pull_combinations != 0:
            session["vehlist"] = allone(combos.copy())
            session["pers6"] = totalhelp[1]
            session["pers5"] = totalhelp[0]
        elif use_combinations != 0:
            session["vehlist"] = sumAll(combos.copy(), vehlist)
            session["pers6"] = pers6
            session["pers5"] = pers5

        session["rem_vehs"] = rem_vehs
        session["results"] = [results[0], off]

        return redirect(url_for("index"))

    except Exception as e:
        return render_template(
            "index.html",
            error_message=f"An error occurred: {str(e)}",
            vehlist=vehlist_input,
            pers5=pers5,
            pers6=pers6,
            results=None,
            totalhelp=None,
            sorted_allocations=None,
            rem_vehs=None,
            alllist=None,
            backupsize=None,
            matrices_result=session.get("matrices_result"),
            allocations_only=int(request.form.get("allocations_only", 0)),
            ranges_result=session.get("ranges_result"),
            total_people=session.get("total_people", ""),
            people=session.get("people", ""),
            crews=session.get("crews", ""),
            zip=zip,
            enumerate=enumerate,
            len=len,
        )


@app.route("/test", methods=["GET", "POST"], strict_slashes=False)
def test_page():
    user_ip, xff_chain, ip_ok = get_client_ip()
    is_bot = is_request_bot(request.headers.get("User-Agent", ""))
    geo = lookup_city(user_ip)

    if request.method == "GET":
        session["return_after_matrices"] = "/test"

        # PRINT on GET (do NOT require ip_ok so localhost prints)
        if (not is_bot) and (not is_hidden_ip(user_ip)):
            print_event(
                event="view-test",
                user_ip=user_ip,
                geo=geo,
                xff_chain=xff_chain,
                remote_addr=request.remote_addr,
                payload_str=None,
            )

        return render_template(
            "index.html",
            vehlist=",".join(
                map(
                    str,
                    session.get("vehlist", [])
                    if isinstance(session.get("vehlist", []), list)
                    else [session.get("vehlist")],
                )
            ),
            pers5=session.get("pers5", ""),
            pers6=session.get("pers6", ""),
            results=session.get("results"),
            totalhelp=session.get("totalhelp"),
            sorted_allocations=session.get("sorted_allocations"),
            rem_vehs=session.get("rem_vehs"),
            allocations_only=session.get("allocations_only", 0),
            pull_combinations=session.get("pull_combinations", 0),
            error_message=None,
            backupsize=session.get("backupsize"),
            alllist=session.get("alllist"),
            matrices_result=session.get("matrices_result"),
            ranges_result=session.get("ranges_result"),
            total_people=session.get("total_people", ""),
            people=session.get("people", ""),
            crews=session.get("crews", ""),
            zip=zip,
            enumerate=enumerate,
            len=len,
        )

    pers5 = pers6 = 0
    vehlist_input = ""
    try:
        vehlist_input = request.form.get("vehlist", "").strip()
        pull_combinations = int(request.form.get("pull_combinations", 0))
        use_combinations = int(request.form.get("use_combinations", 0))

        pers5 = int(request.form.get("pers5") or 0)
        pers6 = int(request.form.get("pers6") or 0)
        vehlist = [int(x.strip()) for x in vehlist_input.split(",") if x.strip()]

        if (not is_bot) and (not is_hidden_ip(user_ip)):
            pretty = format_inputs_pretty({
                "vehlist": vehlist,
                "pers5": pers5,
                "pers6": pers6,
                "pull_combinations": pull_combinations,
                "use_combinations": use_combinations,
            })
            print_event(
                event="user-test",
                user_ip=user_ip,
                geo=geo,
                xff_chain=xff_chain,
                remote_addr=request.remote_addr,
                payload_str=pretty,
            )

        veh2 = vehlist.copy()
        veh2.sort(reverse=True)
        validate_inputs(vehlist, pers5, pers6)

        backup_group = pers5
        backupsize = 5
        primary_group = pers6

        results_1 = optimal_allocation(veh2[:].copy(), primary_group, backup_group, 6, backupsize)
        results = trickle_down(results_1, backupsize)
        off = [backup_group - results[0][0], primary_group - results[0][1]]

        if not results or not isinstance(results, list) or len(results) < 2:
            raise ValueError("Invalid results returned from calculations.")

        sorted_allocations, sorted_spaces, sorted_sizes, number = sort_closestalg_output(results, backupsize)

        if sum(off) <= pers6 and backupsize == 5:
            combos, listing = call_sixesFlipped(sorted_allocations, sorted_spaces, off.copy(), backupsize, None)
        else:
            combos, listing = call_combine(sorted_allocations, sorted_spaces, off.copy(), backupsize, None)

        listing1 = listing.copy()
        combos1 = combos.copy()
        combos3 = combos1.copy()
        listing3 = listing1.copy()
        rem_vehs1 = unused(sorted_allocations.copy(), combos.copy())

        if combos:
            for elem in rem_vehs1:
                combos1.append([elem])
                listing1.append([0, 0])

            combos2, newalloc = call_optimize(
                sorted_allocations.copy(),
                listing1,
                backupsize,
                combos1,
                sorted_spaces,
            )
            combos3 = combos2
            listing3 = newalloc

        combos, listing = cleanup(combos3, sorted_spaces, listing3)
        damage = harm(combos.copy(), sorted_allocations.copy())
        totalhelp = combosSum(combos.copy(), sorted_allocations.copy(), off.copy())

        combos = person_calc(combos.copy(), sorted_sizes.copy())
        alllist = alltogether(combos, listing, damage)

        less = nonzero(sorted_spaces, sorted_sizes)
        rem_vehs2 = unused1(less[1], combos.copy())
        rem_vehs = quant(rem_vehs2)

        restored_vehs, restored_all, restored_spaces = restore_order(
            vehlist[:].copy(), sorted_sizes, sorted_allocations, sorted_spaces
        )

        combined_sorted_data = [
            [restored_vehs[i], restored_all[i], restored_spaces[i], number[i]]
            for i in range(len(sorted_sizes))
        ]

        session["sorted_allocations"] = combined_sorted_data
        session["totalhelp"] = totalhelp
        session["alllist"] = alllist
        session["backupsize"] = backupsize
        session["vehlist"] = vehlist
        session["pers5"] = pers5
        session["pers6"] = pers6
        session["rem_vehs"] = rem_vehs
        session["results"] = [results[0], off]

        return redirect(url_for("test_page"))

    except Exception as e:
        return render_template(
            "index.html",
            error_message=f"An error occurred: {str(e)}",
            vehlist=vehlist_input,
            pers5=pers5,
            pers6=pers6,
            results=None,
            totalhelp=None,
            sorted_allocations=None,
            rem_vehs=None,
            alllist=None,
            backupsize=None,
            matrices_result=session.get("matrices_result"),
            allocations_only=int(request.form.get("allocations_only", 0)),
            ranges_result=session.get("ranges_result"),
            total_people=session.get("total_people", ""),
            people=session.get("people", ""),
            crews=session.get("crews", ""),
            zip=zip,
            enumerate=enumerate,
            len=len,
        )


@app.route("/matrices", methods=["POST"])
def matrices():
    user_ip, xff_chain, ip_ok = get_client_ip()
    is_bot = is_request_bot(request.headers.get("User-Agent", ""))
    geo = lookup_city(user_ip)

    try:
        people_input = request.form.get("people", "").strip()
        crews_input = request.form.get("crews", "").strip()

        people = int(people_input) if people_input else 0
        crews = int(crews_input) if crews_input else 0

        if (not is_bot) and (not is_hidden_ip(user_ip)):
            log_entry = {
                "ip": user_ip,
                "xff": xff_chain,
                "remote_addr": request.remote_addr,
                "geo": geo,
                "timestamp": datetime.now(ZoneInfo("America/Chicago")).strftime("%Y-%m-%d  %H:%M:%S"),
                "event": "matrices",
                "input": {
                    "people": people,
                    "crews": crews,
                },
            }
            log_append(log_entry)

        matrices_result = compute_matrices(people, crews)
        ranges_result = compute_ranges(people)

        session["matrices_result"] = matrices_result
        session["ranges_result"] = ranges_result
        session["people"] = people
        session["crews"] = crews

    except Exception as e:
        print("Error: " + str(e), flush=True)

    return redirect(_safe_return_path(session.get("return_after_matrices")))


@app.route("/logout/trainer", methods=["POST"], strict_slashes=False)
def logout_trainer():
    session.pop("trainer_authed", None)
    return ("", 204)


@app.route("/trainer_login", methods=["GET", "POST"], strict_slashes=False)
def trainer_login():
    error = None
    if request.method == "POST":
        pwd = request.form.get("password", "")
        if pwd == TRAINER_PASSWORD_VIEW:
            session["trainer_authed"] = True
            return render_template(
                "set_tab_ok.html",
                tab_key="tab_ok_trainer",
                next_url=url_for("trainer_view"),
            )
        error = "Incorrect password."
    return render_template("trainer_login.html", error=error)


@app.route("/trainer", strict_slashes=False)
def trainer_view():
    if not is_trainer_authed():
        session.pop("trainer_authed", None)
        return redirect(url_for("trainer_login"))

    grouped_entries = build_grouped_entries(log_get_all())
    return render_template("trainer.html", grouped_entries=grouped_entries)


if __name__ == "__main__":
    app.run()
