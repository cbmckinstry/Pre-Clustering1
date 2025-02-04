from Fives import*

def validate_inputs(vehicle_capacities, five_person_groups, six_person_groups, seven_person_groups):
    if not all(isinstance(cap, int) and cap >= 0 for cap in vehicle_capacities):
        raise ValueError("Vehicle capacities must be a list of non-negative integers.")
    if not isinstance(five_person_groups, int) or five_person_groups < 0:
        raise ValueError("Five-person groups must be a non-negative integer.")
    if not isinstance(six_person_groups, int) or six_person_groups < 0:
        raise ValueError("Six-person groups must be a non-negative integer.")
    if not isinstance(seven_person_groups, int) or seven_person_groups < 0:
        raise ValueError("Seven-person groups must be a non-negative integer.")
    if seven_person_groups!=0 and five_person_groups!=0:
        raise ValueError("There cannot be both 5 and 7 person crews")

def alltogether(combos,allist,damage):
    i=zip(combos,allist,damage)
    out=[]
    for elem in enumerate(i):
        out.append(elem[1])
    twos=[]
    threes=[]
    for item in out:
        if len(item[0])==2:
            twos.append(item)
        if len(item[0])==3:
            threes.append(item)
    return twos,threes

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

def sort_by_sum(lst):
    x=sorted(lst, key=lambda sublist: sum(sublist), reverse=True)
    twoup=twolow=threeup=threelow=0
    if len(lst)>=2:
        twoup=sum(x[1])+sum(x[0])+1
        twolow=sum(x[-1])+sum(x[-2])
    if len(lst)>=3:
        threeup=sum(x[1])+sum(x[2])+sum(x[0])+1
        threelow=sum(x[-1])+sum(x[-2])+sum(x[-3])
    return [[twolow,twoup],[threelow,threeup]]
def harm(combos,allocations):
    out=[]
    for combo in combos:
        running=0
        for vehicle in combo:
            running+=sum(allocations[vehicle-1])
        out.append(running)
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

def unused1(sizes,combos):
    usable=[]
    for elem in combos:
        for i in elem:
            usable.append(i)
    for item in usable:
        sizes.remove(item)
    return sizes

def optimize_combinations(sorted_allocations, allocations, backupsize, out_combos, spaces):
    combos=[]
    for item in out_combos:
        combos1=[]
        for elem in item:
            combos1.append(elem-1)
        combos.append(combos1)
    index_combos=combos.copy()

    weights = [sum(item) for item in sorted_allocations]

    def total_weight(combo):
        return sum(weights[i] for i in combo)

    def total_space(combo):
        return sum(spaces[i] for i in combo)

    def total_allocation_threshold(idx):
        return allocations[idx][0] * backupsize + allocations[idx][1] * 6

    progress = True

    while progress:
        progress = False

        for i in range(len(index_combos) - 1):
            for j in range(i + 1, len(index_combos)):
                combo1, combo2 = index_combos[i], index_combos[j]

                if len(combo1) == 1 and len(combo2) == 1:
                    continue

                for idx1 in combo1:
                    for idx2 in combo2:
                        weight1_before = total_weight(combo1)
                        weight2_before = total_weight(combo2)

                        new_combo1 = combo1[:]
                        new_combo2 = combo2[:]

                        new_combo1[new_combo1.index(idx1)] = idx2
                        new_combo2[new_combo2.index(idx2)] = idx1

                        weight1_after = total_weight(new_combo1)
                        weight2_after = total_weight(new_combo2)

                        space1_after = total_space(new_combo1)
                        space2_after = total_space(new_combo2)

                        min_space1 = total_allocation_threshold(i)
                        min_space2 = total_allocation_threshold(j)

                        # Handle the case where exactly one of the combos is of length 1
                        if len(combo1) == 1 or len(combo2) == 1:
                            if (len(combo1) == 1 and weight2_after < weight2_before) or \
                                    (len(combo2) == 1 and weight1_after < weight1_before):
                                if space1_after >= min_space1 and space2_after >= min_space2:
                                    index_combos[i] = new_combo1
                                    index_combos[j] = new_combo2
                                    progress = True
                                    break
                        else:
                            if max(weight1_after, weight2_after) < max(weight1_before, weight2_before) and \
                                    space1_after >= min_space1 and space2_after >= min_space2:
                                index_combos[i] = new_combo1
                                index_combos[j] = new_combo2
                                progress = True
                                break

                    if progress:
                        break
            if progress:
                break

    index_combos = [[idx + 1 for idx in combo] for combo in index_combos if len(combo) > 1]

    return index_combos
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