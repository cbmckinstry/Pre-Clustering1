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

def optimize_combinations(harm_list, combo_indices, allocations, backup_size, remaining_spaces):
    # Convert harm_list into correct weight values
    harm_weights = [sum(x) for x in harm_list]

    # Compute weights of each combination
    combo_weights = [sum(harm_weights[i] for i in combo) for combo in combo_indices]

    max_weight = max(combo_weights)

    while max_weight >= 0:
        for i in range(len(combo_indices)):
            if len(combo_indices[i]) == 1:
                target_weight = max_weight - 1
            else:
                target_weight = max_weight - 2

        if target_weight < 0:
            break

        max_weight_indices = [i for i, w in enumerate(combo_weights) if w == max_weight]
        target_weight_indices = [i for i, w in enumerate(combo_weights) if w <= target_weight]

        if not max_weight_indices or not target_weight_indices:
            max_weight -= 1
            continue

        for i in max_weight_indices:
            for j in target_weight_indices:
                for idx1 in combo_indices[i]:
                    for idx2 in combo_indices[j]:
                        new_combo_i = set(combo_indices[i]) - {idx1} | {idx2}
                        new_combo_j = set(combo_indices[j]) - {idx2} | {idx1}

                        # Compute new weights
                        new_weight_i = sum(harm_weights[x] for x in new_combo_i)
                        new_weight_j = sum(harm_weights[x] for x in new_combo_j)

                        # Compute allocation constraints
                        alloc_i = allocations[i][1] * 6 + allocations[i][0] * backup_size
                        alloc_j = allocations[j][1] * 6 + allocations[j][0] * backup_size

                        # Check remaining spaces for feasibility
                        remaining_i = sum(remaining_spaces[x] for x in new_combo_i)
                        remaining_j = sum(remaining_spaces[x] for x in new_combo_j)

                        # Ensure new combinations meet allocation requirements
                        if remaining_i >= alloc_i and remaining_j >= alloc_j and max(new_weight_i, new_weight_j) < max_weight:
                            # Apply swap
                            combo_indices[i] = list(new_combo_i)
                            combo_indices[j] = list(new_combo_j)
                            combo_weights[i] = new_weight_i
                            combo_weights[j] = new_weight_j

                            return optimize_combinations(harm_list, combo_indices, allocations, backup_size, remaining_spaces)

        # Additional check for single-element swaps
        for i in range(len(combo_indices)):
            if len(combo_indices[i]) == 1:
                single_element = combo_indices[i][0]
                for j in range(len(combo_indices)):
                    if i != j and len(combo_indices[j]) > 1:
                        for idx in combo_indices[j]:
                            new_combo_i = {idx}
                            new_combo_j = set(combo_indices[j]) - {idx} | {single_element}

                            new_weight_i = sum(harm_weights[x] for x in new_combo_i)
                            new_weight_j = sum(harm_weights[x] for x in new_combo_j)

                            if new_weight_j < combo_weights[j]:
                                combo_indices[i] = list(new_combo_i)
                                combo_indices[j] = list(new_combo_j)
                                combo_weights[i] = new_weight_i
                                combo_weights[j] = new_weight_j

                                return optimize_combinations(harm_list, combo_indices, allocations, backup_size, remaining_spaces)

        max_weight -= 1

    adjusted_combos = [[index + 1 for index in combo] for combo in combo_indices]
    return adjusted_combos




