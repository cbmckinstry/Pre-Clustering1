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
    return [twolow,twoup,threelow,threeup]

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
def replacing_twos(indeces1_combos,all_listings,sizes,allocations,backup_size):
    indeces_combos=[]

    for x in indeces1_combos:
        runner=[]
        for y in x:
            runner.append(y-1)
        indeces_combos.append(runner)
    remainders=[]
    for elem in range(len(indeces_combos)):
        tobe=[]
        for item in range(len(indeces_combos[elem])):
            tobe.append(sizes[indeces_combos[elem][item]]-backup_size*allocations[indeces_combos[elem][item]][0]-6*allocations[indeces_combos[elem][item]][1])
        remainders.append(tobe)
    sorted_remainders, sorted_combos,sorted_listings=sort_by_sum_w2(remainders,indeces_combos,all_listings)
    combos=[]
    listings=[]
    other_combos=[]
    other_listings=[]

    for elem in range(len(sorted_combos)):
        if len(sorted_combos[elem])==2:
            combos.append(sorted_combos[elem])
            listings.append(sorted_listings[elem])
        if len(sorted_combos[elem])==3:
            other_combos.append(sorted_combos[elem])
            other_listings.append(sorted_listings[elem])
    print(combos,allocations,listings)
    to_skip = set()

    for elem in range(len(combos)):
        if elem in to_skip:
            continue
        for other in range(len(combos)):
            if elem == other or len(combos[elem]) != 2 or len(combos[other]) != 2:
                continue
            for item in [0, 1]:
                if sum(allocations[combos[elem][item]]) == 0 and remainders[elem][item] + sum(remainders[other]) >= backup_size * (listings[elem][0] + listings[other][0]) + 6 * (listings[elem][1] + listings[other][1]):
                    value = combos[elem].pop(item)
                    combos[other].append(value)
                    print(combos)


                    listings[other][0] += listings[elem][0]
                    listings[other][1] += listings[elem][1]
                    listings[elem][0] = 0
                    listings[elem][1] = 0


                    to_skip.add(elem)
                    to_skip.add(other)

                    break
            break

    out_combos=[]
    out_listings=[]
    for piece in range(len(combos)):
        if len(sorted_combos[piece])!=1:
            out_combos.append(combos[piece])
            out_listings.append(listings[piece])
    print(5)
    out1=out_combos+other_combos
    out2=out_listings+other_listings
    out3=[]
    for i in out1:
        run=[]
        for j in i:
            run.append(j+1)
        out3.append(run)
    print(out3,out2)

    return out3,out2
