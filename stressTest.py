from Master import *
import time
import csv
from pathlib import Path
from itertools import combinations_with_replacement

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
    return combos

def determineflags(combos):
    flags = ['N', 'N', 'N']

    if not combos:
        flags[2] = 'Y'
        return flags

    count_len3 = 0
    for elem in combos:
        if len(elem) >= 4:
            flags[1] = 'Y'
        if len(elem) == 3 and count_len3<3:
            count_len3 += 1
            if count_len3 >= 2:
                flags[0] = 'Y'
        if flags[0] == 'Y' and flags[1] == 'Y':
            break

    return flags


def writetocsv(filepath, vehicles, pers5, pers6, combos, flags):
    path = Path(filepath)
    path.parent.mkdir(parents=True, exist_ok=True)

    file_exists = path.exists()

    combos_str = "" if not combos else "||".join(",".join(map(str, c)) for c in combos)

    with path.open("a", newline="") as f:
        w = csv.writer(f)
        if not file_exists:
            w.writerow([
                "vehicles",
                "sum_vehicles",
                "pers5",
                "pers6",
                "flags_3len3",
                "flags_len4plus",
                "flags_empty",
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
            combos_str
        ])

def multisets(n):
    return [list(c) for c in combinations_with_replacement(range(1, 6), n)]

if __name__ == '__main__':
    lower=6
    upper=13
    for size in range(lower,upper):
        x = [0, 5, 4, 3, 2, 1]
        sets = multisets(size)

        filepath = Path.home() / "Desktop" / "trials" / f"StressTest_{size}_{int(time.time())}.csv"

        skip_sums = {1, 2, 3, 4, 7, 8, 9, 14, 13, 19, 9}

        for vehicles in sets:
            s = sum(vehicles)
            if s in skip_sums:
                continue

            pers5 = x[s % 6]
            remainder = s - 5 * pers5
            if remainder < 0 or remainder % 6 != 0:
                continue
            pers6 = remainder // 6
            combos = main(vehicles, pers5, pers6)

            flags = determineflags(combos)
            writetocsv(filepath, vehicles, pers5, pers6, combos, flags)

        print(f"Wrote results for {str(size)} to: {filepath}")
    print("All done!")
