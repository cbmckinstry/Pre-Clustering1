from Extension import *
from Allocations import *
from collections import defaultdict, deque

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
    twolow=threelow=twoup=threeup=0
    if len(lst)>=2:
        twoup=sum(x[1])+sum(x[0])
        twolow=sum(x[-1])+sum(x[-2])
        threeup=twoup
        threelow=twolow
    if len(lst)>=3:
        threeup=sum(x[1])+sum(x[2])+sum(x[0])
        threelow=sum(x[-1])+sum(x[-2])+sum(x[-3])
    return threeup

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

def unused1(sizes, combos):
    usable = []
    for elem in combos:
        for i in elem:
            usable.append(i)
    for item in usable:
        sizes.remove(item)
    return sizes

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

def sort_by_sum_w2(list1, list2, list3):
    sorted_pairs = sorted(zip(list1, list2,list3), key=lambda pair: sum(pair[0]))
    sorted_list1, sorted_list2, sorted_list3= zip(*sorted_pairs)

    return list(sorted_list1), list(sorted_list2), list(sorted_list3)
def replacing_twos(indeces1_combos, all_listings, sizes, allocations, backup_size):
    # Convert 1-based indices to 0-based
    indeces_combos = [[y - 1 for y in x] for x in indeces1_combos]

    # Compute remainders based on sizes and allocations
    remainders = [
        [sizes[ind] - backup_size * allocations[ind][0] - 6 * allocations[ind][1] for ind in sublist]
        for sublist in indeces_combos
    ]

    # Sort by sum of remainders
    sorted_remainders, sorted_combos, sorted_listings = sort_by_sum_w2(remainders, indeces_combos, all_listings)

    # Separate 2-element and 3-element combos
    combos = []
    listings = []
    other_combos = []
    other_listings = []

    for i in range(len(sorted_combos)):
        if len(sorted_combos[i]) == 2:
            combos.append(sorted_combos[i])
            listings.append(sorted_listings[i])
        elif len(sorted_combos[i]) == 3:
            other_combos.append(sorted_combos[i])
            other_listings.append(sorted_listings[i])

    # Track modified elements to avoid re-processing
    to_skip = set()
    new_combos = []  # Store newly formed groups
    new_listings = []

    for elem in range(len(combos)):
        if elem in to_skip:
            continue
        for other in range(len(combos)):
            if elem == other or len(combos[elem]) != 2 or len(combos[other]) != 2:
                continue

            # Get all elements involved
            all_elements = combos[elem] + combos[other]
            zero_allocs = [idx for idx in all_elements if sum(allocations[idx]) == 0]
            non_zero_allocs = [idx for idx in all_elements if sum(allocations[idx]) > 0]

            if len(zero_allocs) == 3 and len(non_zero_allocs) == 1:
                # Calculate the total remainder only for the zero-allocation elements
                total_remainder = sum(remainders[elem][combos[elem].index(idx)] for idx in zero_allocs if idx in combos[elem]) + \
                                  sum(remainders[other][combos[other].index(idx)] for idx in zero_allocs if idx in combos[other])

                # Compute the required space for merging
                required_space = backup_size * (listings[elem][0] + listings[other][0]) + 6 * (listings[elem][1] + listings[other][1])

                # Only merge if there's enough space
                if total_remainder >= required_space:
                    # Create a new group with the 3 zero-allocation elements
                    new_combos.append(sorted(zero_allocs))

                    print(new_combos)
                    new_listings.append([
                        listings[elem][0] + listings[other][0],
                        listings[elem][1] + listings[other][1]
                    ])
                    listings[elem]=[0,0]
                    listings[other]=[0,0]
                    print(new_listings)
                    # Keep the remaining non-zero element in its original group
                    non_zero_element = non_zero_allocs[0]
                    combos[elem] = [non_zero_element] if non_zero_element in combos[elem] else []
                    combos[other] = [non_zero_element] if non_zero_element in combos[other] else []

                    # Mark as processed
                    to_skip.add(elem)
                    to_skip.add(other)

                    break  # Stop after a valid move

    # Remove empty groups
    out_combos = [c for c in combos if c] + new_combos + other_combos
    out_listings = [l for c, l in zip(combos, listings) if c and l!=[0,0]] + new_listings + other_listings
    print(out_listings)

    # Convert indices back to 1-based
    out1 = [[j + 1 for j in i] for i in out_combos if len(i)>1]
    out2 = out_listings
    return out1, out2

