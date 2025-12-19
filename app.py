from flask import Flask, render_template, request, session, redirect, url_for
from flask_session import Session
from Master import *  # your existing logic
import os
import redis
from pathlib import Path
from datetime import datetime
from zoneinfo import ZoneInfo
import requests
import json
import time
import ipaddress
from werkzeug.middleware.proxy_fix import ProxyFix

app = Flask(__name__)

# If you are behind a proxy (Render), this helps Flask understand forwarded proto/host.
# NOTE: Your logging does NOT rely on ProxyFix; it relies on X-Forwarded-For parsing below.
app.wsgi_app = ProxyFix(app.wsgi_app, x_for=1, x_proto=1, x_host=1, x_prefix=1)

# ------------------------------
# Core config / sessions
# ------------------------------
app.config["SECRET_KEY"] = os.environ.get("SECRET_KEY", "dev-secret-change-me")

app.config.update(
    SESSION_COOKIE_HTTPONLY=True,
    SESSION_COOKIE_SAMESITE="Lax",
    SESSION_COOKIE_SECURE=True,  # Render is HTTPS
)

redis_url = os.environ.get("REDIS_URL")

# IMPORTANT:
# - SESSION_PERMANENT=False => session cookie dies when the BROWSER closes
# - Tab-close logout is handled by JS calling /logout/<role>
app.config["SESSION_PERMANENT"] = False
app.config["SESSION_USE_SIGNER"] = True
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

# ------------------------------
# Role passwords (env vars)
# ------------------------------
DATA_PASSWORD_VIEW = os.environ.get("DATA_PASSWORD_VIEW", os.environ.get("DATA_PASSWORD", "change-me"))
DATA_PASSWORD_DELETE = os.environ.get("DATA_PASSWORD_DELETE", os.environ.get("DATA_PASSWORD", "change-me"))
DATA_PASSWORD_WIPE = os.environ.get("DATA_PASSWORD_WIPE", os.environ.get("DATA_PASSWORD", "change-me"))
DATA_PASSWORD_DELETE_IP = os.environ.get("DATA_PASSWORD_DELETE_IP", os.environ.get("DATA_PASSWORD", "change-me"))

TRAINER_PASSWORD_VIEW = os.environ.get("TRAINER_PASSWORD_VIEW", "change-me")
ARCHIVE_PASSWORD_VIEW = os.environ.get("ARCHIVE_PASSWORD_VIEW", "change-me")

# /carson passwords (INDEPENDENT ADMIN)
CARSON_PASSWORD_VIEW = os.environ.get("CARSON_PASSWORD_VIEW", "change-me")
CARSON_PASSWORD_DELETE = os.environ.get("CARSON_PASSWORD_DELETE", CARSON_PASSWORD_VIEW)
CARSON_PASSWORD_WIPE = os.environ.get("CARSON_PASSWORD_WIPE", CARSON_PASSWORD_VIEW)
CARSON_PASSWORD_DELETE_IP = os.environ.get("CARSON_PASSWORD_DELETE_IP", CARSON_PASSWORD_VIEW)

# ------------------------------
# TTLs
# ------------------------------
DELETE_TTL_SECONDS = int(os.environ.get("DELETE_TTL_SECONDS", "15"))


def _now() -> float:
    return time.time()


# ------------------------------
# Auth helpers (NO TIME LIMIT for view auth)
# ------------------------------
def is_data_authed() -> bool:
    return bool(session.get("data_authed", False))


def require_data() -> bool:
    if not is_data_authed():
        session.pop("data_authed", None)
        session.pop("delete_unlocked_until", None)
        return False
    return True


def is_trainer_authed() -> bool:
    return bool(session.get("trainer_authed", False))


def is_archive_authed() -> bool:
    return bool(session.get("archive_authed", False))


def is_delete_unlocked() -> bool:
    return session.get("delete_unlocked_until", 0) > _now()


# /carson auth helpers (INDEPENDENT)
def is_carson_authed() -> bool:
    return bool(session.get("carson_authed", False))


def require_carson() -> bool:
    if not is_carson_authed():
        session.pop("carson_authed", None)
        session.pop("carson_delete_unlocked_until", None)
        return False
    return True


def is_carson_delete_unlocked() -> bool:
    return session.get("carson_delete_unlocked_until", 0) > _now()


# ------------------------------
# Logging keys (Redis + fallback)
# ------------------------------
DATA_KEY_PREFIX = os.environ.get("DATA_KEY_PREFIX", "pre:data_log_v2").strip() or "pre:data_log_v2"
ARCHIVE_KEY_PREFIX = os.environ.get("ARCHIVE_KEY_PREFIX", "pre:archive_v1").strip() or "pre:archive_v1"
CARSON_KEY_PREFIX = os.environ.get("CARSON_KEY_PREFIX", "pre:carson_log_v1").strip() or "pre:carson_log_v1"

if len({DATA_KEY_PREFIX, ARCHIVE_KEY_PREFIX, CARSON_KEY_PREFIX}) != 3:
    raise RuntimeError("FATAL CONFIG ERROR: DATA_KEY_PREFIX, ARCHIVE_KEY_PREFIX, CARSON_KEY_PREFIX must all be different.")

DATA_LOG = []
ARCHIVE_LOG = []
CARSON_LOG = []
LOG_COUNTER = 0
ARCHIVE_COUNTER = 0
CARSON_COUNTER = 0


def _get_redis():
    r = app.config.get("SESSION_REDIS")
    if r is None:
        return None
    try:
        r.ping()
        return r
    except Exception:
        return None


def _k(prefix: str, suffix: str) -> str:
    return f"{prefix}{suffix}"


def _next_local_id():
    global LOG_COUNTER
    LOG_COUNTER += 1
    return LOG_COUNTER


def _next_archive_id():
    global ARCHIVE_COUNTER
    ARCHIVE_COUNTER += 1
    return ARCHIVE_COUNTER


def _next_carson_id():
    global CARSON_COUNTER
    CARSON_COUNTER += 1
    return CARSON_COUNTER


# ------------------------------
# IP helpers
# ------------------------------
def is_public_ip(ip: str) -> bool:
    try:
        a = ipaddress.ip_address(ip)
        return not (a.is_private or a.is_loopback or a.is_reserved or a.is_multicast or a.is_link_local)
    except ValueError:
        return False


def get_client_ip():
    """
    Returns (client_ip, xff_chain, ip_ok)

    - Prefer first PUBLIC IP in X-Forwarded-For chain.
    - If none, fall back to remote_addr.
    - ip_ok tells you whether the returned IP looks like a public client IP.
    """
    xff = request.headers.get("X-Forwarded-For", "")
    if xff:
        parts = [p.strip() for p in xff.split(",") if p.strip()]
        for ip in parts:  # leftmost-first
            if is_public_ip(ip):
                return ip, xff, True
        # XFF existed but no public IPs found
        return (parts[0] if parts else request.remote_addr or ""), xff, False

    ra = request.remote_addr or ""
    return ra, "", is_public_ip(ra)


# ------------------------------
# Generic list storage helpers
# ------------------------------
def _list_append(prefix: str, entry: dict, local_list: list, local_next_id_fn):
    r = _get_redis()
    entry = dict(entry)

    if r is not None:
        if "id" not in entry:
            entry["id"] = int(r.incr(_k(prefix, ":id_counter")))
        r.rpush(_k(prefix, ""), json.dumps(entry))
    else:
        if "id" not in entry:
            entry["id"] = local_next_id_fn()
        local_list.append(entry)

    return entry


def _list_get_all(prefix: str, local_list: list):
    r = _get_redis()
    if r is not None:
        raw = r.lrange(_k(prefix, ""), 0, -1)
        return [json.loads(x) for x in raw]
    return list(local_list)


def _list_replace_all(prefix: str, entries, local_setter):
    r = _get_redis()
    if r is not None:
        pipe = r.pipeline()
        pipe.delete(_k(prefix, ""))
        for e in entries:
            pipe.rpush(_k(prefix, ""), json.dumps(e))
        pipe.execute()
    else:
        local_setter(list(entries))


def _list_clear(prefix: str, local_clear_fn, local_counter_reset_fn):
    r = _get_redis()
    if r is not None:
        r.delete(_k(prefix, ""))
        r.delete(_k(prefix, ":id_counter"))
    else:
        local_clear_fn()
        local_counter_reset_fn()


# ------------------------------
# Log storage (DATA main + ARCHIVE immutable + CARSON main)
# ------------------------------
def log_append(entry: dict):
    """
    Your single logging call.

    - Writes to DATA main (mutable)
    - Writes to ARCHIVE (immutable / append-only)
    - Writes to CARSON main (mutable, independent)

    This matches:
      /data edits affect /trainer (because both read DATA main)
      /carson edits affect only CARSON main
      /archive can never be changed (no endpoints modify it)
    """
    e_main = _list_append(DATA_KEY_PREFIX, entry, DATA_LOG, _next_local_id)

    # Append-only archive copy (never replaced/cleared by any route)
    archive_entry = dict(e_main)
    if _get_redis() is not None:
        archive_entry["archive_id"] = int(_get_redis().incr(_k(ARCHIVE_KEY_PREFIX, ":id_counter")))
        _get_redis().rpush(_k(ARCHIVE_KEY_PREFIX, ""), json.dumps(archive_entry))
    else:
        archive_entry["archive_id"] = _next_archive_id()
        ARCHIVE_LOG.append(archive_entry)

    # Carson receives same events (independent mutable log)
    _list_append(CARSON_KEY_PREFIX, entry, CARSON_LOG, _next_carson_id)


def log_get_all_main():
    return _list_get_all(DATA_KEY_PREFIX, DATA_LOG)


def log_replace_all_main(entries):
    global DATA_LOG
    _list_replace_all(DATA_KEY_PREFIX, entries, lambda x: _set_data_log(x))


def _set_data_log(entries):
    global DATA_LOG
    DATA_LOG = list(entries)


def log_clear_main():
    global DATA_LOG, LOG_COUNTER
    _list_clear(DATA_KEY_PREFIX, lambda: DATA_LOG.clear(), lambda: _reset_data_counter())


def _reset_data_counter():
    global LOG_COUNTER
    LOG_COUNTER = 0


def log_get_all_archive():
    return _list_get_all(ARCHIVE_KEY_PREFIX, ARCHIVE_LOG)


# CARSON helpers
def carson_get_all_main():
    return _list_get_all(CARSON_KEY_PREFIX, CARSON_LOG)


def carson_replace_all_main(entries):
    global CARSON_LOG
    _list_replace_all(CARSON_KEY_PREFIX, entries, lambda x: _set_carson_log(x))


def _set_carson_log(entries):
    global CARSON_LOG
    CARSON_LOG = list(entries)


def carson_clear_main():
    global CARSON_LOG, CARSON_COUNTER
    _list_clear(CARSON_KEY_PREFIX, lambda: CARSON_LOG.clear(), lambda: _reset_carson_counter())


def _reset_carson_counter():
    global CARSON_COUNTER
    CARSON_COUNTER = 0


def build_grouped_entries(entries):
    entries = list(reversed(entries))
    grouped = {}
    for e in entries:
        ip = e.get("ip", "Unknown IP")
        grouped.setdefault(ip, []).append(e)
    return grouped


def lookup_city(ip: str):
    try:
        if ip.startswith("127.") or ip == "::1":
            return {"city": "Localhost", "region": None, "country": None}
        resp = requests.get(f"http://ip-api.com/json/{ip}", timeout=2)
        data = resp.json()
        if data.get("status") != "success":
            return None
        return {"city": data.get("city"), "region": data.get("regionName"), "country": data.get("country")}
    except Exception:
        return None


# ------------------------------
# Main app route
# ------------------------------
@app.route("/", methods=["GET", "POST"], strict_slashes=False)
def index():
    user_ip, xff_chain, ip_ok = get_client_ip()

    user_agent = request.headers.get("User-Agent", "").lower()
    is_bot = (
            "go-http-client/" in user_agent
            or "cron-job.org" in user_agent
            or user_agent.strip() == ""
    )

    geo = lookup_city(user_ip)

    # Log GET "view" only if public client IP.
    if request.method == "GET" and (not is_bot) and ip_ok:
        log_append(
            {
                "ip": user_ip,
                "xff": xff_chain,
                "remote_addr": request.remote_addr,
                "geo": geo,
                "timestamp": datetime.now(ZoneInfo("America/Chicago")).strftime("%Y-%m-%d,  %H:%M:%S"),
                "event": "view",
                "input": None,
            }
        )

    if request.method == "POST":
        pers5 = pers6 = 0
        vehlist_input = ""
        try:
            vehlist_input = request.form.get("vehlist", "").strip()
            pull_combinations = int(request.form.get("pull_combinations", 0))
            use_combinations = int(request.form.get("use_combinations", 0))

            pers5 = int(request.form.get("pers5") or 0)
            pers6 = int(request.form.get("pers6") or 0)
            vehlist = [int(x.strip()) for x in vehlist_input.split(",") if x.strip()]

            log_append(
                {
                    "ip": user_ip,
                    "xff": xff_chain,
                    "remote_addr": request.remote_addr,
                    "geo": geo,
                    "timestamp": datetime.now(ZoneInfo("America/Chicago")).strftime("%Y-%m-%d,  %H:%M:%S"),
                    "event": "submit",
                    "input": {
                        "vehlist": vehlist,
                        "pers5": pers5,
                        "pers6": pers6,
                        "pull_combinations": pull_combinations,
                        "use_combinations": use_combinations,
                    },
                }
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


@app.route("/matrices", methods=["POST"])
def matrices():
    try:
        people_input = request.form.get("people", "").strip()
        crews_input = request.form.get("crews", "").strip()
        people = int(people_input) if people_input else 0
        crews = int(crews_input) if crews_input else 0

        matrices_result = compute_matrices(people, crews)
        ranges_result = compute_ranges(people)

        session["matrices_result"] = matrices_result
        session["ranges_result"] = ranges_result
        session["people"] = people
        session["crews"] = crews

    except Exception as e:
        print("Error: " + str(e))
        return render_template(
            "index.html",
            error_message=f"An error occurred: {str(e)}",
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
            alllist=session.get("alllist"),
            rem_vehs=session.get("rem_vehs"),
            backupsize=session.get("backupsize"),
            allocations_only=int(request.form.get("allocations_only", 0)),
            pull_combinations=session.get("pull_combinations", 0),
            matrices_result=session.get("matrices_result"),
            ranges_result=session.get("ranges_result"),
            total_people=session.get("total_people", ""),
            people=people_input,
            crews=crews,
            zip=zip,
            enumerate=enumerate,
            len=len,
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
        alllist=session.get("alllist"),
        rem_vehs=session.get("rem_vehs"),
        backupsize=session.get("backupsize"),
        allocations_only=session.get("allocations_only", 0),
        pull_combinations=session.get("pull_combinations", 0),
        matrices_result=session.get("matrices_result"),
        ranges_result=session.get("ranges_result"),
        total_people=session.get("total_people", ""),
        people=session.get("people", ""),
        crews=session.get("crews", ""),
        zip=zip,
        enumerate=enumerate,
        len=len,
    )


# ==========================================================
# Logout beacon endpoints (TAB CLOSE)
# ==========================================================
@app.route("/logout/<role>", methods=["POST"], strict_slashes=False)
def logout_role(role: str):
    if role == "data":
        session.pop("data_authed", None)
        session.pop("delete_unlocked_until", None)
    elif role == "trainer":
        session.pop("trainer_authed", None)
    elif role == "archive":
        session.pop("archive_authed", None)
    elif role == "carson":
        session.pop("carson_authed", None)
        session.pop("carson_delete_unlocked_until", None)
    return ("", 204)


# ==========================================================
# /data (EDITOR) — login + view + delete + wipe + delete-ip
# ==========================================================
@app.route("/data_login", methods=["GET", "POST"], strict_slashes=False)
def data_login():
    error = None
    if request.method == "POST":
        pwd = request.form.get("password", "")
        if pwd == DATA_PASSWORD_VIEW:
            session["data_authed"] = True
            session.pop("delete_unlocked_until", None)
            return render_template("set_tab_ok.html", tab_key="tab_ok_data", next_url=url_for("data_view"))
        error = "Incorrect password."
    return render_template("data_login.html", error=error)


@app.route("/data", strict_slashes=False)
def data_view():
    if not require_data():
        return redirect(url_for("data_login"))

    grouped_entries = build_grouped_entries(log_get_all_main())
    return render_template(
        "data.html",
        grouped_entries=grouped_entries,
        delete_unlocked=is_delete_unlocked(),
        can_delete=True,
        can_wipe=True,
        delete_error=None,
        wipe_error=None,
    )


@app.route("/delete_entry", methods=["POST"], strict_slashes=False)
def delete_entry():
    if not require_data():
        return redirect(url_for("data_login"))

    entry_id = request.form.get("entry_id", type=int)
    if entry_id is None:
        return redirect(url_for("data_view"))

    if not is_delete_unlocked():
        pwd = request.form.get("delete_password", "")
        if pwd != DATA_PASSWORD_DELETE:
            grouped_entries = build_grouped_entries(log_get_all_main())
            return render_template(
                "data.html",
                grouped_entries=grouped_entries,
                delete_unlocked=is_delete_unlocked(),
                can_delete=True,
                can_wipe=True,
                delete_error="Incorrect delete password.",
                wipe_error=None,
            )
        session["delete_unlocked_until"] = _now() + DELETE_TTL_SECONDS

    entries = log_get_all_main()
    filtered = [e for e in entries if e.get("id") != entry_id]
    log_replace_all_main(filtered)
    return redirect(url_for("data_view"))


@app.route("/wipe_data", methods=["POST"], strict_slashes=False)
def wipe_data():
    if not require_data():
        return redirect(url_for("data_login"))

    pwd = request.form.get("wipe_password", "")
    if pwd != DATA_PASSWORD_WIPE:
        grouped_entries = build_grouped_entries(log_get_all_main())
        return render_template(
            "data.html",
            grouped_entries=grouped_entries,
            delete_unlocked=is_delete_unlocked(),
            can_delete=True,
            can_wipe=True,
            delete_error=None,
            wipe_error="Incorrect wipe password.",
        )

    log_clear_main()
    return redirect(url_for("data_view"))


@app.route("/delete_ip", methods=["POST"], strict_slashes=False)
def delete_ip():
    if not require_data():
        return redirect(url_for("data_login"))

    ip_to_delete = (request.form.get("ip") or "").strip()
    if not ip_to_delete:
        return redirect(url_for("data_view"))

    pwd = request.form.get("delete_ip_password", "")
    if pwd != DATA_PASSWORD_DELETE_IP:
        grouped_entries = build_grouped_entries(log_get_all_main())
        return render_template(
            "data.html",
            grouped_entries=grouped_entries,
            delete_unlocked=is_delete_unlocked(),
            can_delete=True,
            can_wipe=True,
            delete_error=None,
            wipe_error="Incorrect IP delete password.",
        )

    entries = log_get_all_main()
    filtered = [e for e in entries if e.get("ip", "Unknown IP") != ip_to_delete]
    log_replace_all_main(filtered)
    return redirect(url_for("data_view"))


# ==========================================================
# /trainer (VIEWER) — view-only DATA main log
# ==========================================================
@app.route("/trainer_login", methods=["GET", "POST"], strict_slashes=False)
def trainer_login():
    error = None
    if request.method == "POST":
        pwd = request.form.get("password", "")
        if pwd == TRAINER_PASSWORD_VIEW:
            session["trainer_authed"] = True
            return render_template("set_tab_ok.html", tab_key="tab_ok_trainer", next_url=url_for("trainer_view"))
        error = "Incorrect password."
    return render_template("trainer_login.html", error=error)


@app.route("/trainer", strict_slashes=False)
def trainer_view():
    if not is_trainer_authed():
        session.pop("trainer_authed", None)
        return redirect(url_for("trainer_login"))

    grouped_entries = build_grouped_entries(log_get_all_main())
    return render_template("trainer.html", grouped_entries=grouped_entries)


# ==========================================================
# /archive (IMMUTABLE VIEWER) — view-only ARCHIVE log
# ==========================================================
@app.route("/archive_login", methods=["GET", "POST"], strict_slashes=False)
def archive_login():
    error = None
    if request.method == "POST":
        pwd = request.form.get("password", "")
        if pwd == ARCHIVE_PASSWORD_VIEW:
            session["archive_authed"] = True
            return render_template("set_tab_ok.html", tab_key="tab_ok_archive", next_url=url_for("archive_view"))
        error = "Incorrect password."
    return render_template("archive_login.html", error=error)


@app.route("/archive", strict_slashes=False)
def archive_view():
    if not is_archive_authed():
        session.pop("archive_authed", None)
        return redirect(url_for("archive_login"))

    grouped_entries = build_grouped_entries(log_get_all_archive())
    return render_template("archive.html", grouped_entries=grouped_entries)


# ==========================================================
# /carson (INDEPENDENT ADMIN) — login + view + delete + wipe + delete-ip
# ==========================================================
@app.route("/carson_login", methods=["GET", "POST"], strict_slashes=False)
def carson_login():
    error = None
    if request.method == "POST":
        pwd = request.form.get("password", "")
        if pwd == CARSON_PASSWORD_VIEW:
            session["carson_authed"] = True
            session.pop("carson_delete_unlocked_until", None)
            return render_template("set_tab_ok.html", tab_key="tab_ok_carson", next_url=url_for("carson_view"))
        error = "Incorrect password."
    return render_template("carson_login.html", error=error)


@app.route("/carson", strict_slashes=False)
def carson_view():
    if not require_carson():
        return redirect(url_for("carson_login"))

    grouped_entries = build_grouped_entries(carson_get_all_main())
    return render_template(
        "carson.html",
        grouped_entries=grouped_entries,
        delete_unlocked=is_carson_delete_unlocked(),
        can_delete=True,
        can_wipe=True,
        delete_error=None,
        wipe_error=None,
    )


@app.route("/carson_delete_entry", methods=["POST"], strict_slashes=False)
def carson_delete_entry():
    if not require_carson():
        return redirect(url_for("carson_login"))

    entry_id = request.form.get("entry_id", type=int)
    if entry_id is None:
        return redirect(url_for("carson_view"))

    if not is_carson_delete_unlocked():
        pwd = request.form.get("delete_password", "")
        if pwd != CARSON_PASSWORD_DELETE:
            grouped_entries = build_grouped_entries(carson_get_all_main())
            return render_template(
                "carson.html",
                grouped_entries=grouped_entries,
                delete_unlocked=is_carson_delete_unlocked(),
                can_delete=True,
                can_wipe=True,
                delete_error="Incorrect delete password.",
                wipe_error=None,
            )
        session["carson_delete_unlocked_until"] = _now() + DELETE_TTL_SECONDS

    entries = carson_get_all_main()
    filtered = [e for e in entries if e.get("id") != entry_id]
    carson_replace_all_main(filtered)
    return redirect(url_for("carson_view"))


@app.route("/carson_wipe", methods=["POST"], strict_slashes=False)
def carson_wipe():
    if not require_carson():
        return redirect(url_for("carson_login"))

    pwd = request.form.get("wipe_password", "")
    if pwd != CARSON_PASSWORD_WIPE:
        grouped_entries = build_grouped_entries(carson_get_all_main())
        return render_template(
            "carson.html",
            grouped_entries=grouped_entries,
            delete_unlocked=is_carson_delete_unlocked(),
            can_delete=True,
            can_wipe=True,
            delete_error=None,
            wipe_error="Incorrect wipe password.",
        )

    carson_clear_main()
    return redirect(url_for("carson_view"))


@app.route("/carson_delete_ip", methods=["POST"], strict_slashes=False)
def carson_delete_ip():
    if not require_carson():
        return redirect(url_for("carson_login"))

    ip_to_delete = (request.form.get("ip") or "").strip()
    if not ip_to_delete:
        return redirect(url_for("carson_view"))

    pwd = request.form.get("delete_ip_password", "")
    if pwd != CARSON_PASSWORD_DELETE_IP:
        grouped_entries = build_grouped_entries(carson_get_all_main())
        return render_template(
            "carson.html",
            grouped_entries=grouped_entries,
            delete_unlocked=is_carson_delete_unlocked(),
            can_delete=True,
            can_wipe=True,
            delete_error=None,
            wipe_error="Incorrect IP delete password.",
        )

    entries = carson_get_all_main()
    filtered = [e for e in entries if e.get("ip", "Unknown IP") != ip_to_delete]
    carson_replace_all_main(filtered)
    return redirect(url_for("carson_view"))


if __name__ == "__main__":
    app.run()
