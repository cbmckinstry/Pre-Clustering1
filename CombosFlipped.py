from ThreeFlipped import *

def combineFlipped(allocations, space, shortfall, backup_size=5, used=None, boundlst=None):
    if used is None:
        used = set()
    if boundlst is None:
        boundlst=[[0,0],[0,0]]
    backup=shortfall[0]
    six=shortfall[1]

    allocations0=[]
    space0=[]
    for i in range(len(space)):
        if space[i]!=0:
            allocations0.append(allocations[i])
            space0.append(space[i])

    x=threeFlipped(shortfall.copy(),allocations0,space0,backup_size,used,[[0,1],[0,1]])
    if x[1]:
        return x
    lower=boundlst[0][0]
    upper=boundlst[1][1]
    six4=six
    used4=used.copy()
    combos4=[]
    backup4=backup
    init=[]
    if backup_size==7:
        for bound in range(lower,upper):
            if backup4==0:
                break
            used4=used.copy()
            backup4=backup
            combos4=[]
            init=[]
            for m in range(len(space0)):
                if backup4==0:
                    break
                for n in range(len(space0)-1,m,-1):
                    if backup4==0:
                        break
                    if (space0[m]+space0[n]>=7) and (m not in used4) and (n not in used4) and sum(allocations[m])+sum(allocations[n])<=bound:
                        trial=threeFlipped([backup4,six4],allocations0,space0,backup_size,used4,[[boundlst[0][0],bound+1],[boundlst[0][0],bound+1]])
                        if trial[1]:
                            return trial[0]+combos4,trial[1]+init
                        used4.add(m)
                        used4.add(n)
                        combos4.append([m+1,n+1])
                        backup4-=1
                        init.append([1,0])
                        if backup4==0 and six4==0:
                            return combos4,init
                        trial=threeFlipped([backup4,six4],allocations0,space0,backup_size,used4,[[boundlst[0][0],bound+1],[boundlst[0][0],bound+1]])
                        if trial[1]:
                            return trial[0]+combos4,trial[1]+init
        combos5=combos4.copy()
        used5=used4.copy()
        init1=init.copy()
        for bound in range(lower,upper):
            if six4==0:
                break
            used5=used4.copy()
            six4=six
            combos5=combos4.copy()
            init1=init.copy()
            for m in range(len(space0)):
                if six4==0:
                    break
                for n in range(len(space0)-1,m,-1):
                    if six4==0:
                        break
                    if (space0[m]+space0[n]>=6) and (m not in used5) and (n not in used5) and sum(allocations[m])+sum(allocations[n])<=bound:
                        trial=threeFlipped([backup4,six4],allocations0,space0,backup_size,used5,[[boundlst[0][0],bound+1],[boundlst[0][0],bound+1]])
                        if trial[1]:
                            return trial[0]+combos5,trial[1]+init1
                        used5.add(m)
                        used5.add(n)
                        combos5.append([m+1,n+1])
                        six4-=1
                        init1.append([0,1])
                        if backup4==0 and six4==0:
                            return combos5,init1
                        trial=threeFlipped([backup4,six4],allocations0,space0,backup_size,used5,[[boundlst[0][0],bound+1],[boundlst[0][0],bound+1]])
                        if trial[1]:
                            return trial[0]+combos5,trial[1]+init1
    else:
        for bound in range(lower,upper):
            if six4==0:
                break
            used4=used.copy()
            six4=six
            combos4=[]
            init=[]
            for m in range(len(space0)):
                if six4==0:
                    break
                for n in range(len(space0)-1,m,-1):
                    if six4==0:
                        break
                    if (space0[m]+space0[n]>=6) and (m not in used4) and (n not in used4) and sum(allocations[m])+sum(allocations[n])<=bound:
                        trial=threeFlipped([backup4,six4],allocations0,space0,backup_size,used4,[[boundlst[0][0],bound+1],[boundlst[0][0],bound+1]])
                        if trial[1]:
                            return trial[0]+combos4,trial[1]+init
                        used4.add(m)
                        used4.add(n)
                        combos4.append([m+1,n+1])
                        six4-=1
                        init.append([0,1])
                        if backup4==0 and six4==0:
                            return combos4,init
                        trial=threeFlipped([backup4,six4],allocations0,space0,backup_size,used4,[[boundlst[0][0],bound+1],[boundlst[0][0],bound+1]])
                        if trial[1]:
                            return trial[0]+combos4,trial[1]+init
        combos5=combos4.copy()
        used5=used4.copy()
        init1=init.copy()
        for bound in range(lower,upper):
            if backup4==0:
                break
            used5=used4.copy()
            backup4=backup
            combos5=combos4.copy()
            init1=init.copy()
            for m in range(len(space0)):
                if backup4==0:
                    break
                for n in range(len(space0)-1,m,-1):
                    if backup4==0:
                        break
                    if (space0[m]+space0[n]>=5) and (m not in used5) and (n not in used5) and sum(allocations[m])+sum(allocations[n])<=bound:
                        trial=threeFlipped([backup4,six4],allocations0,space0,backup_size,used5,[[boundlst[0][0],bound+1],[boundlst[0][0],bound+1]])
                        if trial[1]:
                            return trial[0]+combos5,trial[1]+init1
                        used5.add(m)
                        used5.add(n)
                        combos5.append([m+1,n+1])
                        backup4-=1
                        init1.append([1,0])
                        if backup4==0 and six4==0:
                            return combos5,init1
                        trial=threeFlipped([backup4,six4],allocations0,space0,backup_size,used5,[[boundlst[0][0],bound+1],[boundlst[0][0],bound+1]])
                        if trial[1]:
                            return trial[0]+combos5,trial[1]+init1
    if backup4==0 and six4==0:
        return combos5,init1

    return [],[]