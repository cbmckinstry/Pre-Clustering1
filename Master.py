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

def combine(space, shortfall, indices, backup_size=5):
    paired = list(zip(space, indices))
    paired.sort(key=lambda t: t[0], reverse=True)
    space, indices = map(list, zip(*paired)) if paired else ([], [])

    if sum(space) < backup_size * shortfall[0] + 6 * shortfall[1]:
        return None, None

    six = shortfall[1]
    backup = shortfall[0]
    used=set()
    combos = []
    init = []

    for m in range(len(space) - 2, -1, -1):
        if six == 0:
            break
        if m in used:
            continue

        for n in range(len(space) - 1, m, -1):
            if six == 0:
                break
            if n in used:
                continue

            if space[m] + space[n] >= 6 and m not in used and n not in used:
                used.add(m)
                used.add(n)
                combos.append([indices[m], indices[n]])
                six -= 1
                init.append([0, 1])

                if backup == 0 and six == 0:
                    return combos, init
    for m in range(len(space) - 2, -1, -1):
        if backup == 0:
            break
        if m in used:
            continue
        for n in range(len(space) - 1, m, -1):
            if backup == 0:
                break
            if n in used:
                continue

            if space[m] + space[n] >= 5 and m not in used and n not in used:
                used.add(m)
                used.add(n)
                combos.append([indices[m], indices[n]])
                backup -= 1
                init.append([1, 0])

                if backup == 0 and six == 0:
                    return combos, init

    return None, None

def sort_by_sum(combos, sorted_spaces, listing, actualCombos, one_based=False):
    if not combos:
        return [], [], []

    offset = 1 if one_based else 0

    paired = list(zip(combos, listing, actualCombos))
    paired.sort(key=lambda p: sum(sorted_spaces[idx - offset] for idx in p[0]))

    sorted_combos, sorted_listing, sorted_actual = zip(*paired)
    return list(sorted_combos), list(sorted_listing), list(sorted_actual)


def cleanup(combos, sorted_spaces, listing):
    size4, size3, other = [], [], []
    init4, init3, init_other = [], [], []
    actualCombos, actual4, actual3 = [],[],[]

    for i in range(len(combos)):
        inner=[]
        for j in range(len(combos[i])):
            combos[i][j]-=1
            inner.append(sorted_spaces[combos[i][j]])
        actualCombos.append(inner)

    for c, l, ac in zip(combos, listing, actualCombos):
        if len(c) == 4:
            size4.append(c); init4.append(l); actual4.append(ac)
        elif len(c) == 3:
            size3.append(c); init3.append(l); actual3.append(ac)
        else:
            other.append(c); init_other.append(l)

    size4, init4, actual4 = sort_by_sum(size4, sorted_spaces, init4, actual4)
    size3, init3, actual3 = sort_by_sum(size3, sorted_spaces, init3, actual3)
    new3, new3init = [],[]
    used4,used3 = set(),set()
    progressFlag = False
    if size4 and size3:
        for m in range(len(size4)):
            if m in used4:
                continue
            for n in range(len(size3)):
                if n in used3 or m in used4:
                    continue
                total5s, total6s = init4[m][0]+init3[n][0],init4[m][1]+init3[n][1]
                placedFlag = False

                for a in range(len(size3[n])):
                    if placedFlag:
                        break
                    for b in range(0,len(size4[m])-2):
                        if placedFlag:
                            break
                        for c in range(b+1,len(size4[m])-1):
                            if placedFlag:
                                break
                            placed6s = min((actual4[m][c]+actual4[m][b]+actual3[n][a])//6,total6s)
                            placed5s = min((actual4[m][c]+actual4[m][b]+actual3[n][a]-6*placed6s)//5,total5s)
                            remaining = [total5s-placed5s,total6s-placed6s]
                            spacesL, indL = actual4[m].copy(), size4[m].copy()
                            spacesL.pop(c); indL.pop(c); spacesL.pop(b); indL.pop(b)
                            spacesR, indR = actual3[n].copy(), size3[n].copy()
                            spacesR.pop(a); indR.pop(a)
                            spaces, ind = spacesL+spacesR, indL+indR
                            comb, init = combine(spaces,remaining,ind)
                            if comb and m not in used4 and n not in used3:
                                new3.append([size4[m][b],size4[m][c],size3[n][a]])
                                actual3.append([actual4[m][b],actual4[m][c],actual3[n][a]])
                                used3.add(n);used4.add(m)
                                new3init.append([placed5s,placed6s])
                                size3[n]=[]; init3[n]=[];size4[m]=[];init4[m]=[];actual3[n]=[]
                                other.extend(comb); init_other.extend(init)
                                placedFlag, progressFlag = True, True
        size3.extend(new3); init3.extend(new3init)
        size3 = [x for x in size3 if x!=[]]
        init3 = [x for x in init3 if x!=[]]
        size4 = [x for x in size4 if x!=[]]
        init4 = [x for x in init4 if x!=[]]
        actual3 = [x for x in actual3 if x!=[]]
    used3.clear()
    if len(size3)>=2:
        for m in range(0,len(size3)-1):
            if m in used3:
                continue
            for n in range(m+1,len(size3)):
                if n in used3 or m in used3:
                    continue
                total5s, total6s = init3[m][0]+init3[n][0],init3[m][1]+init3[n][1]
                spaces = actual3[m].copy()+actual3[n].copy()
                ind = size3[m].copy()+size3[n].copy()
                comb, init = combine(spaces,[total5s,total6s],ind)
                if comb and m not in used4 and n not in used3:
                    used3.add(n);used3.add(m)
                    size3[n]=[]; init3[n]=[];size3[m]=[];init3[m]=[]
                    other.extend(comb); init_other.extend(init)

        size3 = [x for x in size3 if x!=[]]
        init3 = [x for x in init3 if x!=[]]
    used3.clear()
    if len(size3)>=3:
        for m in range(0,len(size3)-2):
            if m in used3:
                continue
            for n in range(m+1,len(size3)-1):
                if n in used3 or m in used3:
                    continue
                for o in range(n+1,len(size3)):
                    if n in used3 or m in used3 or o in used3:
                        continue
                    total5s, total6s = init3[m][0]+init3[n][0]+init3[o][0],init3[m][1]+init3[n][1]+init3[o][1]
                    placedFlag = False

                    for a in range(len(size3[m])):
                        if placedFlag:
                            break
                        for b in range(len(size3[n])):
                            if placedFlag:
                                break
                            for c in range(len(size3[o])):
                                if placedFlag:
                                    break
                                placed6s = min((actual3[m][a]+actual3[n][b]+actual3[o][c])//6,total6s)
                                placed5s = min((actual3[m][a]+actual3[n][b]+actual3[o][c]-6*placed6s)//5,total5s)
                                remaining = [total5s-placed5s,total6s-placed6s]
                                spacesL, indL = actual3[m].copy(), size3[m].copy()
                                spacesL.pop(a); indL.pop(a)
                                spacesM, indM = actual3[n].copy(), size3[n].copy()
                                spacesM.pop(b); indM.pop(b)
                                spacesR, indR = actual3[o].copy(), size3[o].copy()
                                spacesR.pop(c); indR.pop(c)
                                spaces, ind = spacesL+spacesM+spacesR, indL+indM+indR
                                comb, init = combine(spaces,remaining,ind)
                                if comb and m not in used3 and n not in used3 and o not in used3:
                                    new3.append([size3[m][a],size3[n][b],size3[o][c]])
                                    actual3.append([actual3[m][a],actual3[n][b],actual3[o][c]])
                                    used3.add(n);used4.add(m);used3.add(o)
                                    new3init.append([placed5s,placed6s])
                                    size3[n]=[]; init3[n]=[];size3[m]=[];init3[m]=[]; size3[o]=[];init3[o]=[]
                                    other.extend(comb); init_other.extend(init)
                                    placedFlag, progressFlag = True,True
        size3.extend(new3); init3.extend(new3init)
        size3 = [x for x in size3 if x!=[]]
        init3 = [x for x in init3 if x!=[]]
        size4 = [x for x in size4 if x!=[]]
        init4 = [x for x in init4 if x!=[]]

    size4 = [[x+1 for x in combo]for combo in size4]
    size3 = [[x+1 for x in combo]for combo in size3]
    other = [[x+1 for x in combo]for combo in other]

    return size4+size3+other, init4+init3+init_other, progressFlag