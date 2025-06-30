from Extension import *
from Allocations import *
from collections import defaultdict, deque

def validate_inputs(vehicle_capacities, five_person_groups, six_person_groups):
    if not all(isinstance(cap, int) and cap >= 0 for cap in vehicle_capacities):
        raise ValueError("Vehicle capacities must be a list of non-negative integers.")
    if not isinstance(five_person_groups, int) or five_person_groups < 0:
        raise ValueError("Five-person groups must be a non-negative integer.")
    if not isinstance(six_person_groups, int) or six_person_groups < 0:
        raise ValueError("Six-person groups must be a non-negative integer.")
def alltogether(combos,allist,damage):
    i=zip(combos,allist,damage)
    out=[]
    for elem in enumerate(i):
        out.append(elem[1])
    twos=[]
    threes=[]
    fours=[]
    fives=[]
    sixes=[]
    sevens=[]
    for item in out:
        if len(item[0])==2:
            twos.append(item)
        if len(item[0])==3:
            threes.append(item)
        if len(item[0])==4:
            fours.append(item)
        if len(item[0])==5:
            fives.append(item)
        if len(item[0])==6:
            sixes.append(item)
    return twos,threes,fours,fives,sixes,sevens

def compute_ranges(people):
    final=[]
    counter1=0
    people1=people
    final1=[]
    while people1>=0:
        if people1%6==0:
            final1.append(counter1+(people1//6))
        counter1+=1
        people1-=5
    counter2=0
    people2=people
    final2=[]
    while people2>=0:
        if people2%6==0:
            final2.append(counter2+(people2//6))
        counter2+=1
        people2-=7
    if final1:
        final.append([min(final1),max(final1)])
    else:
        final.append([])
    if final2:
        final.append([min(final2),max(final2)])
    else:
        final.append([])
    return final

def compute_matrices(people,crews):
    pers5=-1*people+6*crews
    pers6=people-5*crews
    if pers5>=0 and pers6>=0 and isinstance(pers5,int) and isinstance(pers6,int):
        return pers5,pers6,0
    pers7=people-6*crews
    pers6n=-1*people+7*crews
    if pers7>=0 and pers6n>=0 and isinstance(pers7,int) and isinstance(pers6n,int):
        return 0,pers6n,pers7
    return []

def harm(combos,allocations):
    out=[]
    for combo in combos:
        running=0
        for vehicle in combo:
            running+=sum(allocations[vehicle-1])
        out.append(running)
    return out

def combosSum(combos,allocations,shortfall):
    out=shortfall.copy()
    for combo in combos:
        for vehicle in combo:
            out[0]+=allocations[vehicle-1][0]
            out[1]+=allocations[vehicle-1][1]
    return out

def unused(allocations,combos):
    indeces=list(range(len(allocations)))
    unused=[]
    used=[]
    for combo in combos:
        for index in combo:
            used.append(index)
    for elem in indeces:
        if elem+1 not in used:
            unused.append(elem+1)
    return unused

def unused1(sizes, combos):
    usable = []
    for elem in combos:
        for i in elem:
            usable.append(i)
    for item in usable:
        sizes.remove(item)
    return sizes

def nonzero(remainders,vehicles):
    rem_af=[]
    veh_af=[]
    for elem in range(len(remainders)):
        if remainders[elem]!=0:
            rem_af.append(remainders[elem])
            veh_af.append(vehicles[elem])
    return rem_af, veh_af

def allone(combos):
    out=[]
    for elem in combos:
        for item in elem:
            out.append(item)
    return out
def oppallone(allone,vehlist):
    for x in allone:
        vehlist.remove(x)
    return vehlist
def sumAll(combos,vehlist):
    summ=0
    for x in combos:
        summ=0
        for m in x:
            summ+=m
            vehlist.remove(m)
        if summ!=0:
            vehlist.append(summ)
    return vehlist
def person_calc(combos,sizes):
    pers_out=[]
    for elem in combos:
        each=[]
        for i in elem:
            each.append(sizes[i-1])
        pers_out.append(each)
    return pers_out

def quant(unused_veh):
    look=list(set(unused_veh))
    look.sort()
    paired=[]
    for elem in look:
        paired.append([unused_veh.count(elem),elem])
    return paired

def restore_order(original, shuffled, list_of_lists, list_of_ints):

    index_map = defaultdict(deque)
    for i, value in enumerate(shuffled):
        index_map[value].append(i)

    sorted_indices = [index_map[value].popleft() for value in original]

    restored_shuffled = [shuffled[i] for i in sorted_indices]
    restored_list_of_lists = [list_of_lists[i] for i in sorted_indices]
    restored_list_of_ints = [list_of_ints[i] for i in sorted_indices]

    return restored_shuffled, restored_list_of_lists, restored_list_of_ints

def can_do(combo1, combo2, remaining, init1, init2):
    trials = []
    indices = []

    for a in range(len(combo1)):
        for b in range(len(combo1)):
            if b == a:
                continue
            for c in range(len(combo1)):
                if c == a or c == b:
                    continue
                if sum(init1)+sum(init2)!=3:
                    continue

                triple = [
                    [remaining[combo1[a]], remaining[combo2[0]]],
                    [remaining[combo1[b]], remaining[combo2[1]]],
                    [remaining[combo1[c]], remaining[combo2[2]]]
                ]
                index_set = [
                    [combo1[a], combo2[0]],
                    [combo1[b], combo2[1]],
                    [combo1[c], combo2[2]]
                ]

                trials.append(triple)
                indices.append(index_set)

    summed_trials = [[sum(pair) for pair in trial] for trial in trials]

    sorted_summed = []
    sorted_indices = []
    for sums, ids in zip(summed_trials, indices):
        paired = sorted(zip(sums, ids))
        nums_sorted, ids_sorted = zip(*paired)
        sorted_summed.append(list(nums_sorted))
        sorted_indices.append(list(ids_sorted))

    num_fives = init1[0] + init2[0]
    num_sixes = init1[1] + init2[1]
    required = [5] * num_fives + [6] * num_sixes

    for i in range(len(sorted_summed)):
        if all(sorted_summed[i][j] >= required[j] for j in range(3)):
            result_init = [[1, 0] if x == 5 else [0, 1] for x in required]
            return [sorted_indices[i], result_init]

    return None


def cleanup(combos, remaining, init):
    final = []
    final_init = []
    just3 = []
    init3 = []
    used = set()
    combos1 = [lst.copy() for lst in combos]
    for e in range(len(combos1)):
        for f in range(len(combos1[e])):
            combos1[e][f]-=1
    for i in range(len(combos1)):
        if len(combos1[i]) == 3:
            just3.append(combos1[i])
            init3.append(init[i])
        else:
            final.append(combos1[i])
            final_init.append(init[i])
    if not just3:
        return combos, init
    sort_keys = [sum(remaining[i] for i in idxs) for idxs in just3]
    combined = list(zip(sort_keys, just3, init3))
    combined.sort(key=lambda x: x[0])
    _, just3, init3 = zip(*combined)
    just3 = list(just3)
    init3 = list(init3)
    for i in range(len(just3) - 1):
        if i in used:
            continue
        for j in range(i + 1, len(just3)):
            if j in used:
                continue
            trial = can_do(just3[i], just3[j], remaining, init3[i], init3[j])
            if trial is not None and i not in used and j not in used:
                used.add(i)
                used.add(j)
                final.extend(trial[0])
                final_init.extend(trial[1])
    for p in range(len(just3)):
        if p not in used:
            final.append(just3[p])
            final_init.append(init3[p])
    for e in range(len(final)):
        for f in range(len(final[e])):
            final[e][f]+=1
    return final, final_init