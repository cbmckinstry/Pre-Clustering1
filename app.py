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

app = Flask(__name__)

# ------------------------------
# Core config / sessions
# ------------------------------
app.config["SECRET_KEY"] = os.environ.get("SECRET_KEY", "dev-secret-change-me")

redis_url = os.environ.get("REDIS_URL")

app.config["SESSION_PERMANENT"] = False
app.config["SESSION_USE_SIGNER"] = True
app.config["SESSION_KEY_PREFIX"] = "session:"

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
# Logging storage (Redis + fallback)
# ------------------------------
DATA_LOG = []   # local fallback (dev only)

DATA_PASSWORD = os.environ.get("DATA_PASSWORD", "change-me")


def _get_redis():
    """Return Redis connection if available, else None."""
    return app.config.get("SESSION_REDIS")


def log_append(entry: dict):
    """
    Append entry to Redis (production) or in-memory list (local dev).
    """
    r = _get_redis()
    if r is not None:
        # key name for this app's data
        r.rpush("data_log_v2", json.dumps(entry))
    else:
        DATA_LOG.append(entry)


def log_get_all():
    """
    Return list of entries (oldest first).
    """
    r = _get_redis()
    if r is not None:
        raw = r.lrange("data_log_v2", 0, -1)
        return [json.loads(x) for x in raw]
    else:
        return list(DATA_LOG)


# ------------------------------
# IP → City/Region/Country lookup
# ------------------------------
def lookup_city(ip: str):
    """
    Lookup using ip-api.com (no API key required).
    Returns dict {city, region, country} or None on failure.
    """
    try:
        # Localhost / dev
        if ip.startswith("127.") or ip == "::1":
            return {"city": "Localhost", "region": None, "country": None}

        url = f"http://ip-api.com/json/{ip}"
        resp = requests.get(url, timeout=2)
        data = resp.json()

        if data.get("status") != "success":
            print(f"Geo lookup failed for {ip}: {data.get('message')}")
            return None

        return {
            "city": data.get("city"),
            "region": data.get("regionName"),
            "country": data.get("country"),
        }

    except Exception as e:
        print("Geo lookup exception:", e)
        return None


@app.route("/", methods=["GET", "POST"])
def index():
    user_ip = request.headers.get("X-Forwarded-For", request.remote_addr).split(",")[0].strip()
    user_agent = request.headers.get("User-Agent", "").lower()
    is_bot = (
            "go-http-client/" in user_agent
            or "cron-job.org" in user_agent
            or user_agent.strip() == ""
    )

    # --- IP logging ---
    if str(user_ip) != "127.0.0.1" and not is_bot:
        print("Viewer IP:", user_ip)

    # --- City lookup (for logging & /data) ---
    geo = lookup_city(user_ip)
    if geo:
        city_str = geo.get("city") or "Unknown city"
        region_str = geo.get("region") or ""
        country_str = geo.get("country") or ""
        location_print = ", ".join([s for s in [city_str, region_str, country_str] if s])
        print("Approx. location:", location_print)

    # --- Log pure viewers (GET) with null input ---
    if request.method == "GET" and not is_bot:
        log_append(
            {
                "ip": user_ip,
                "geo": geo,  # may be None if lookup failed
                "timestamp": datetime.now(ZoneInfo("America/Chicago")).strftime("%Y-%m-%d,  %H:%M:%S"),
                "event": "view",
                "input": None,  # viewer: no inputs
            }
        )

    if request.method == "POST":
        pers5 = pers6 = 0
        try:
            vehlist_input = request.form.get("vehlist", "").strip()
            pull_combinations = int(request.form.get("pull_combinations", 0))
            use_combinations = int(request.form.get("use_combinations", 0))

            pers5 = int(request.form.get("pers5") or 0)
            pers6 = int(request.form.get("pers6") or 0)
            vehlist = [int(x.strip()) for x in vehlist_input.split(",") if x.strip()]

            print(
                "User IP:",
                user_ip,
                "Vehicles:",
                vehlist,
                "5-person:",
                pers5,
                "6-person:",
                pers6,
            )

            # --- add to /data log (Redis or in-memory) ---
            log_append(
                {
                    "ip": user_ip,
                    "geo": geo,  # may be None if lookup failed
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

            # ----- your existing allocation logic -----
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
                    sorted_allocations.copy(), listing1, backupsize, combos1, sorted_spaces
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
            print("Error:", e)
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

    # GET (or POST success) render
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

# ------------------------------
# /data page
# ------------------------------
@app.route("/data_login", methods=["GET", "POST"])
def data_login():
    error = None
    if request.method == "POST":
        if request.form.get("password") == DATA_PASSWORD:
            session["data_admin"] = True
            return redirect(url_for("data_view"))
        error = "Incorrect password."
    return render_template("data_login.html", error=error)


@app.route("/data")
def data_view():
    if not session.get("data_admin"):
        return redirect(url_for("data_login"))

    entries = list(reversed(log_get_all()))  # newest first
    return render_template("data.html", entries=entries)

@app.route("/wipe_data", methods=["POST"])
def wipe_data():
    if not session.get("data_admin"):
        return redirect(url_for("data_login"))

    r = _get_redis()
    if r:
        r.delete("data_log_v2")   # removes all logged entries in Redis
    else:
        DATA_LOG.clear()         # in-memory fallback

    return redirect(url_for("data_view"))



if __name__ == "__main__":
    app.run()
