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
import time

app = Flask(__name__)

# ------------------------------
# Core config / sessions
# ------------------------------
app.config["SECRET_KEY"] = os.environ.get("SECRET_KEY", "dev-secret-change-me")

# ------------------------------
# Security: cookie hardening
# ------------------------------
app.config.update(
    SESSION_COOKIE_HTTPONLY=True,
    SESSION_COOKIE_SAMESITE="Lax",
    SESSION_COOKIE_SECURE=True,  # Render is HTTPS
)

redis_url = os.environ.get("REDIS_URL")

app.config["SESSION_PERMANENT"] = False
app.config["SESSION_USE_SIGNER"] = True

# IMPORTANT: isolate session keys between apps sharing Redis
# Render env example:
#   SESSION_KEY_PREFIX=session:pre:
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
# Admin passwords
# ------------------------------
DATA_PASSWORD = os.environ.get("DATA_PASSWORD", "change-me")
DATA_PASSWORD_VIEW = os.environ.get("DATA_PASSWORD_VIEW", DATA_PASSWORD)
DATA_PASSWORD_DELETE = os.environ.get("DATA_PASSWORD_DELETE", DATA_PASSWORD)
DATA_PASSWORD_WIPE = os.environ.get("DATA_PASSWORD_WIPE", DATA_PASSWORD)

# ------------------------------
# Admin TTL (seconds)
# ------------------------------
ADMIN_TTL_SECONDS = int(os.environ.get("ADMIN_TTL_SECONDS", "300"))

# If 0 => requires delete password every delete
# If >0 => after entering delete password once, it stays unlocked for that many seconds
DELETE_TTL_SECONDS = int(os.environ.get("DELETE_TTL_SECONDS", "30"))

def _now() -> float:
    return time.time()

def is_admin_authed() -> bool:
    return session.get("data_admin_until", 0) > _now()

def require_admin() -> bool:
    if not is_admin_authed():
        session.pop("data_admin_until", None)
        session.pop("delete_unlocked_until", None)
        return False
    return True

def is_delete_unlocked() -> bool:
    return session.get("delete_unlocked_until", 0) > _now()

# ------------------------------
# Logging storage (Redis + fallback)
# ------------------------------
DATA_LOG = []
LOG_COUNTER = 0

# IMPORTANT: isolate log keys between apps sharing Redis
# Render env example:
#   DATA_KEY_PREFIX=pre:data_log_v2
DATA_KEY_PREFIX = os.environ.get("DATA_KEY_PREFIX", "pre:data_log_v2").strip() or "pre:data_log_v2"

def _k(suffix: str) -> str:
    return f"{DATA_KEY_PREFIX}{suffix}"

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
    r = _get_redis()
    entry = dict(entry)

    if r is not None:
        if "id" not in entry:
            entry["id"] = int(r.incr(_k(":id_counter")))
        r.rpush(_k(""), json.dumps(entry))
    else:
        if "id" not in entry:
            entry["id"] = _next_local_id()
        DATA_LOG.append(entry)

def log_get_all():
    r = _get_redis()
    if r is not None:
        raw = r.lrange(_k(""), 0, -1)
        return [json.loads(x) for x in raw]
    return list(DATA_LOG)

def log_replace_all(entries):
    r = _get_redis()
    if r is not None:
        pipe = r.pipeline()
        pipe.delete(_k(""))
        for e in entries:
            pipe.rpush(_k(""), json.dumps(e))
        pipe.execute()
    else:
        global DATA_LOG
        DATA_LOG = list(entries)

def log_clear_all():
    r = _get_redis()
    if r is not None:
        r.delete(_k(""))
        r.delete(_k(":id_counter"))
    else:
        global DATA_LOG, LOG_COUNTER
        DATA_LOG.clear()
        LOG_COUNTER = 0

def build_grouped_entries():
    entries = list(reversed(log_get_all()))
    grouped = {}
    for e in entries:
        ip = e.get("ip", "Unknown IP")
        grouped.setdefault(ip, []).append(e)
    return grouped

def lookup_city(ip: str):
    try:
        if ip.startswith("127.") or ip == "::1":
            return {"city": "Localhost", "region": None, "country": None}

        url = f"http://ip-api.com/json/{ip}"
        resp = requests.get(url, timeout=2)
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

@app.route("/", methods=["GET", "POST"], strict_slashes=False)
def index():
    user_ip = request.headers.get("X-Forwarded-For", request.remote_addr).split(",")[0].strip()
    user_agent = request.headers.get("User-Agent", "").lower()
    is_bot = (
            "go-http-client/" in user_agent
            or "cron-job.org" in user_agent
            or user_agent.strip() == ""
    )

    geo = lookup_city(user_ip)

    if request.method == "GET" and not is_bot:
        log_append({
            "ip": user_ip,
            "geo": geo,
            "timestamp": datetime.now(ZoneInfo("America/Chicago")).strftime("%Y-%m-%d,  %H:%M:%S"),
            "event": "view",
            "input": None,
        })

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

            log_append({
                "ip": user_ip,
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
            })

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

@app.route("/data_login", methods=["GET", "POST"], strict_slashes=False)
def data_login():
    error = None
    if request.method == "POST":
        pwd = request.form.get("password", "")
        if pwd == DATA_PASSWORD_VIEW:
            session["data_admin_until"] = _now() + ADMIN_TTL_SECONDS
            session.pop("delete_unlocked_until", None)
            return redirect(url_for("data_view"))
        error = "Incorrect password."
    return render_template("data_login.html", error=error)

@app.route("/data", strict_slashes=False)
def data_view():
    if not require_admin():
        return redirect(url_for("data_login"))

    grouped_entries = build_grouped_entries()
    delete_unlocked = is_delete_unlocked()

    return render_template(
        "data.html",
        grouped_entries=grouped_entries,
        delete_unlocked=delete_unlocked,
        delete_error=None,
        wipe_error=None,
    )

@app.route("/delete_entry", methods=["POST"], strict_slashes=False)
def delete_entry():
    if not require_admin():
        return redirect(url_for("data_login"))

    entry_id = request.form.get("entry_id", type=int)
    if entry_id is None:
        return redirect(url_for("data_view"))

    delete_unlocked = is_delete_unlocked()

    if not delete_unlocked:
        pwd = request.form.get("delete_password", "")
        if pwd != DATA_PASSWORD_DELETE:
            grouped_entries = build_grouped_entries()
            return render_template(
                "data.html",
                grouped_entries=grouped_entries,
                delete_unlocked=is_delete_unlocked(),
                delete_error="Incorrect delete password.",
                wipe_error=None,
            )
        session["delete_unlocked_until"] = _now() + DELETE_TTL_SECONDS

    entries = log_get_all()
    filtered = [e for e in entries if e.get("id") != entry_id]
    log_replace_all(filtered)
    return redirect(url_for("data_view"))

@app.route("/wipe_data", methods=["POST"], strict_slashes=False)
def wipe_data():
    if not require_admin():
        return redirect(url_for("data_login"))

    pwd = request.form.get("wipe_password", "")
    if pwd != DATA_PASSWORD_WIPE:
        grouped_entries = build_grouped_entries()
        return render_template(
            "data.html",
            grouped_entries=grouped_entries,
            delete_unlocked=is_delete_unlocked(),
            delete_error=None,
            wipe_error="Incorrect wipe password.",
        )

    log_clear_all()
    return redirect(url_for("data_view"))

if __name__ == "__main__":
    app.run()
