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
    fours=[]
    fives=[]
    for item in out:
        if len(item[0])==2:
            twos.append(item)
        if len(item[0])==3:
            threes.append(item)
        if len(item[0])==4:
            fours.append(item)
        if len(item[0])==5:
            fives.append(item)
    return twos,threes,fours,fives


def assigntogether(allocations,spaces,shortfall,backupsize,boundlst):
    round1=[],[]
    if len(allocations)>=5:
        round1=fives(shortfall,allocations,spaces,backupsize,None,boundlst)
    elif len(allocations)>=4:
        round1=fours(shortfall,allocations,spaces,backupsize,None,boundlst)
    elif len(allocations)>=3:
        round1=threes(shortfall,allocations,spaces,backupsize,None,boundlst)
    elif len(allocations)>=2:
        round1=combine(allocations,spaces,shortfall,backupsize,None,boundlst)
    return round1

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
    twoup=twolow=threeup=threelow=fourup=fourlow=fiveup=fivelow=0
    if len(lst)>=2:
        twoup=sum(x[1])+sum(x[0])+1
        twolow=sum(x[-1])+sum(x[-2])
    if len(lst)>=3:
        threeup=sum(x[1])+sum(x[2])+sum(x[0])+1
        threelow=sum(x[-1])+sum(x[-2])+sum(x[-3])
    if len(lst)>=4:
        fourup=sum(x[1])+sum(x[2])+sum(x[3])+sum(x[0])+1
        fourlow=sum(x[-1])+sum(x[-2])+sum(x[-3])+sum(x[-4])
    if len(lst)>=5:
        fiveup=sum(x[1])+sum(x[2])+sum(x[3])+sum(x[0])+sum(x[4])+1
        fivelow=sum(x[-1])+sum(x[-2])+sum(x[-3])+sum(x[-4])+sum(x[-5])
    return [[twolow,twoup],[threelow,threeup],[fourlow,fourup],[fivelow,fiveup]]
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