from flask import Flask, render_template, request, session
from flask_session import Session
from Master import *
import os
import redis


app = Flask(__name__)


app.config["SECRET_KEY"] = os.environ.get("SECRET_KEY")

app.config["SESSION_TYPE"] = "redis"
app.config["SESSION_PERMANENT"] = False
app.config["SESSION_USE_SIGNER"] = True
app.config["SESSION_KEY_PREFIX"] = "session:"
app.config["SESSION_REDIS"] = redis.from_url(os.environ.get("REDIS_URL"))

Session(app)


@app.route("/", methods=["GET", "POST"])
def index():
    user_ip = request.headers.get("X-Forwarded-For", request.remote_addr).split(",")[0].strip()
    user_agent = request.headers.get("User-Agent", "").lower()
    is_bot = (
        "go-http-client/" in user_agent
        or "cron-job.org" in user_agent
        or user_agent.strip() == ""
)
    if str(user_ip) != '127.0.0.1' and not is_bot:
        print("Viewer IP: "+str(user_ip))
    if request.method == "POST":
        try:
            # Input parsing and validation
            vehlist_input = request.form.get("vehlist", "").strip()
            pers5_input = request.form.get("pers5", "").strip()
            pers6_input = request.form.get("pers6", "").strip()
            pull_combinations = int(request.form.get("pull_combinations", 0))
            use_combinations=int(request.form.get("use_combinations", 0))

            vehlist = [int(x.strip()) for x in vehlist_input.split(",") if x.strip()]
            pers5 = int(pers5_input) if pers5_input else 0
            pers6 = int(pers6_input) if pers6_input else 0
            print("User IP: " +str(user_ip)+", Vehicles: "+ str(vehlist) + ", 5-person: " + str(pers5)+ ", 6-person: "+str(pers6))

            veh2=vehlist.copy()
            veh2.sort(reverse=True)

            # Validate inputs
            validate_inputs(vehlist, pers5, pers6)

            backup_group =  pers5
            backupsize = 5
            primary_group = pers6

            results_1=optimal_allocation(veh2[:].copy(),primary_group,backup_group,6,backupsize)
            results=trickle_down(results_1,backupsize)
            off=[backup_group-results[0][0],primary_group-results[0][1]]
            if not results or not isinstance(results, list) or len(results) < 2:
                raise ValueError("Invalid results returned from calculations.")
            sorted_allocations, sorted_spaces, sorted_sizes, number = sort_closestalg_output(results, backupsize)

            if sum(off)<=pers6 and backupsize==5:
                combos,listing=call_sixesFlipped(sorted_allocations,sorted_spaces,off.copy(),backupsize,None)
            else:
                combos,listing=call_combine(sorted_allocations,sorted_spaces,off.copy(),backupsize,None)
            listing1=listing.copy()
            combos1=combos.copy()
            combos3=combos1.copy()
            listing3=listing1.copy()
            rem_vehs1=unused(sorted_allocations.copy(),combos.copy())
            if combos:
                for elem in rem_vehs1:
                    combos1.append([elem])
                    listing1.append([0,0])
                combos2,newalloc=call_optimize(sorted_allocations.copy(),listing1,backupsize,combos1,sorted_spaces)
                combos3=combos2
                listing3=newalloc

            #combos=combos3
            #listing=listing3
            combos,listing=cleanup(combos3,sorted_spaces,listing3)

            damage=harm(combos.copy(),sorted_allocations.copy())
            totalhelp=combosSum(combos.copy(),sorted_allocations.copy(),off.copy())

            combos=person_calc(combos.copy(),sorted_sizes.copy())
            alllist=alltogether(combos,listing,damage)

            less=nonzero(sorted_spaces,sorted_sizes)

            rem_vehs2=unused1(less[1],combos.copy())

            rem_vehs=quant(rem_vehs2)

            restored_vehs, restored_all, restored_spaces =restore_order(vehlist[:].copy(),sorted_sizes,sorted_allocations,sorted_spaces)

            combined_sorted_data = [
                [restored_vehs[i], restored_all[i], restored_spaces[i], number[i]]
                for i in range(len(sorted_sizes))
            ]


            # Store sorted allocations and results in session
            session["sorted_allocations"] = combined_sorted_data
            session["totalhelp"] = totalhelp

            session["alllist"]=alllist
            session["backupsize"]=backupsize

            if pull_combinations==0 and use_combinations==0:
                session["vehlist"] = vehlist
                session["pers5"] = pers5
                session["pers6"] = pers6
            elif pull_combinations!=0:
                session["vehlist"] = allone(combos.copy())
                session["pers6"] = totalhelp[1]
                session["pers5"] = totalhelp[0]
            elif use_combinations!=0:
                session["vehlist"]=sumAll(combos.copy(),vehlist)
                session["pers6"] = pers6
                session["pers5"] = pers5
            session["rem_vehs"]=rem_vehs
            session["results"] = [results[0],off]

        except Exception as e:
            print("Error: "+str(e))
            return render_template(
                "index.html",
                error_message=f"An error occurred: {str(e)}",
                vehlist=vehlist_input,
                pers5=pers5_input,
                pers6=pers6_input,
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
        vehlist = ",".join(map(str, session.get("vehlist", []) if isinstance(session.get("vehlist", []), list) else [session.get("vehlist")])),
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
        # Input parsing
        people_input = request.form.get("people", "").strip()
        crews_input = request.form.get("crews", "").strip()

        people = int(people_input) if people_input else 0
        crews = int(crews_input) if crews_input else 0

        # Run matrices algorithm
        matrices_result = compute_matrices(people, crews)

        # Store the result in session
        session["matrices_result"] = matrices_result
        session["people"] = people
        session["crews"] = crews

    except Exception as e:
        print("Error: "+str(e))
        return render_template(
            "index.html",
            error_message=f"An error occurred: {str(e)}",
            vehlist = ",".join(map(str, session.get("vehlist", []) if isinstance(session.get("vehlist", []), list) else [session.get("vehlist")])),
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
            matrices_result=None,
            ranges_result=session.get("ranges_result"),
            total_people=session.get("total_people", ""),
            people=people_input,
            crews=crews_input,
            zip=zip,
            enumerate=enumerate,
            len=len,
        )

    return render_template(
        "index.html",
        vehlist = ",".join(map(str, session.get("vehlist", []) if isinstance(session.get("vehlist", []), list) else [session.get("vehlist")])),
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
        people=session.get("people", ""),
        crews=session.get("crews", ""),
        zip=zip,
        enumerate=enumerate,
        len=len,
    )

@app.route("/ranges", methods=["POST"])
def ranges():
    try:
        # Input parsing
        total_people_input = request.form.get("total_people", "").strip()
        total_people = int(total_people_input) if total_people_input else 0

        # Run ranges algorithm
        ranges_result = compute_ranges(total_people)

        # Store the result in session
        session["ranges_result"] = ranges_result
        session["total_people"] = total_people

    except Exception as e:
        print("Error: "+str(e))
        return render_template(
            "index.html",
            error_message=f"An error occurred: {str(e)}",
            vehlist = ",".join(map(str, session.get("vehlist", []) if isinstance(session.get("vehlist", []), list) else [session.get("vehlist")])),
            pers5=session.get("pers5", ""),
            pers6=session.get("pers6", ""),
            results=session.get("results"),
            totalhelp=session.get("totalhelp"),
            sorted_allocations=session.get("sorted_allocations"),
            backupsize=session.get("backupsize"),
            alllist=session.get("alllist"),
            rem_vehs=session.get("rem_vehs"),
            allocations_only=int(request.form.get("allocations_only", 0)),
            pull_combinations=session.get("pull_combinations", 0),
            matrices_result=session.get("matrices_result"),
            ranges_result=None,
            total_people=total_people_input,
            people=session.get("people", ""),
            crews=session.get("crews", ""),
            zip=zip,
            enumerate=enumerate,
            len=len,
        )

    return render_template(
        "index.html",
        vehlist = ",".join(map(str, session.get("vehlist", []) if isinstance(session.get("vehlist", []), list) else [session.get("vehlist")])),
        pers5=session.get("pers5", ""),
        pers6=session.get("pers6", ""),
        results=session.get("results"),
        totalhelp=session.get("totalhelp"),
        sorted_allocations=session.get("sorted_allocations"),
        backupsize=session.get("backupsize"),
        alllist=session.get("alllist"),
        allocations_only=int(request.form.get("allocations_only", 0)),
        pull_combinations=session.get("pull_combinations", 0),
        rem_vehs=session.get("rem_vehs"),
        matrices_result=session.get("matrices_result"),
        ranges_result=session.get("ranges_result"),
        total_people=session.get("total_people", ""),
        people=session.get("people", ""),
        crews=session.get("crews", ""),
        zip=zip,
        enumerate=enumerate,
        len=len,
    )

if __name__ == "__main__":
    app.run(debug=True)
