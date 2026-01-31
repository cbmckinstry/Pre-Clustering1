from Master import *
import csv
from pathlib import Path
from itertools import combinations_with_replacement
from datetime import datetime
from zoneinfo import ZoneInfo

def main(vehlist,pers5,pers6):
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

    combos, listing, progress = cleanup(combos3, sorted_spaces, listing3)
    while progress:
        combos,listing, progress = cleanup(combos, sorted_spaces, listing)
    combos = person_calc(combos.copy(), sorted_sizes.copy())
    return combos, listing

def determineflags(combos, init):
    flags = ['N', 'N', 'N', 'N', 'N']

    if not combos:
        flags[4] = 'Y'
        return flags

    count4, count5, count6 = 0,0,0
    total4, total5, total6  = 0,0,0
    for x in range(len(combos)):
        total = 5*init[x][0]+6*init[x][1]
        length = len(combos[x])
        if length==4:
            count4+=1
            if count4==1:
                total4 = total
            if count4==2 and total4!=total:
                flags[0] = 'Y'
                continue
        if length==5:
            count5+=1
            if count5==1:
                total5 = total
            if count5==2 and total5!=total:
                flags[1] = 'Y'
                continue
        if length==6:
            count6+=1
            if count6==1:
                total6 = total
            if count6==2 and total6!=total:
                flags[2] = 'Y'
                continue
        if length==5 and sum(init[x]) not in {1,4}:
            flags[3] = 'Y'
    return flags

def writetocsv(filepath, vehicles, pers5, pers6, combos, flags):
    path = Path(filepath)
    path.parent.mkdir(parents=True, exist_ok=True)

    file_exists = path.exists()

    combos_str = "" if not combos else "|".join(",".join(map(str, c)) for c in combos)

    with path.open("a", newline="") as f:
        w = csv.writer(f)
        if not file_exists:
            w.writerow([
                "vehicles",
                "sum_vehicles",
                "pers5",
                "pers6",
                "flags_unequal_4s",
                "flags_unequal_5s",
                "flags_unequal_6s",
                "flags_weird_5",
                "failure",
                "combos"
            ])

        w.writerow([
            list(vehicles),
            sum(vehicles),
            pers5,
            pers6,
            flags[0],
            flags[1],
            flags[2],
            flags[3],
            flags[4],
            combos_str
        ])

def multisets(n,upper):
    return [list(c) for c in combinations_with_replacement(range(1, upper), n)]

if __name__ == '__main__':
    lower=9
    upper=31
    priority = 6
    vers = 1
    for size in range(lower,upper):
        x = [0, 5, 4, 3, 2, 1]
        if priority==1:
            sets = multisets(size, 6)
        elif priority==2:
            sets = multisets(size, 5)
        else:
            sets = multisets(size, priority)

        timestamp = datetime.now().strftime("%M%S")
        filepath = Path.home() / "Desktop" / f"trial{priority}s-{vers}" / f"StressTest_{size}_{timestamp}.csv"

        skip_sums = {1, 2, 3, 4, 7, 8, 9, 13, 14, 19}

        for vehicles in sets:
            s = sum(vehicles)
            if s in skip_sums and priority!=1 and priority!=2:
                continue
            if priority==6:
                pers5 = x[s%6]
                remainder = s - 5*pers5
                pers6 = remainder // 6
            elif priority==5:
                pers6 = s%5
                remainder = s - 6*pers6
                pers5 = remainder // 5
            elif priority==1:
                pers6 = s//6
                pers5 = 0
            elif priority==2:
                pers5 = s//5
                pers6 = 0
            else:
                break
            combos, init = main(vehicles, pers5, pers6)

            flags = determineflags(combos, init)
            writetocsv(filepath, vehicles, pers5, pers6, combos, flags)

        timestamp = datetime.now(ZoneInfo("America/Chicago")).strftime("%Y-%m-%d %H:%M:%S %Z")

        print(f"[{timestamp}] Wrote results for {size} to: {filepath}")
    print("All done!")
