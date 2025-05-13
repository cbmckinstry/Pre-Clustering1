def allocate_groups(vehicle_capacities, backup_groups, six_person_groups, vers, sort_order="none", minimize_remainder=False, fill_before_next=False, switch_to_seven=False):
    original_indices = list(range(len(vehicle_capacities)))

    if sort_order == "asc":
        sorted_data = sorted(zip(vehicle_capacities, original_indices))
        vehicle_capacities, original_indices = zip(*sorted_data)
    elif sort_order == "desc":
        sorted_data = sorted(zip(vehicle_capacities, original_indices), reverse=True)
        vehicle_capacities, original_indices = zip(*sorted_data)

    vehicle_capacities = list(vehicle_capacities)
    vehicle_assignments = [[0, 0] for _ in vehicle_capacities]
    totals = [0, 0]

    backup_size = 7 if switch_to_seven else 5
    if vers == 1:
        primary_size, primary_groups, secondary_groups = 6, six_person_groups, backup_groups
    else:
        primary_size, primary_groups, secondary_groups, backup_size = backup_size, backup_groups, six_person_groups,6

    def find_best_vehicle(primary_size,backup_size):
        best_vehicle = None
        smallest_remainder = -1*float('inf')
        largest_remainder=float('inf')
        other=[1,2,3,4,5,0]

        for i, capacity in enumerate(vehicle_capacities):
            if capacity >= backup_size:
                remainder = capacity % primary_size
                if (primary_size==6 and backup_size==5) or (primary_size==7 and backup_size==6):
                    if remainder > smallest_remainder:
                        smallest_remainder = remainder
                        best_vehicle = i
                else:
                    score=other.index(remainder)
                    if score < largest_remainder:
                        largest_remainder = score
                        best_vehicle = i

        return best_vehicle

    if minimize_remainder:
        if fill_before_next:
            indexed_caps = list(enumerate(vehicle_capacities))
            sorted_caps = sorted(indexed_caps, key=lambda x: x[1] % primary_size)
            sorted_indices = [i for i, _ in sorted_caps]
            vehicle_capacities = [vehicle_capacities[i] for i in sorted_indices]
            vehicle_assignments = [vehicle_assignments[i] for i in sorted_indices]

            for current_vehicle in range(len(vehicle_capacities)):
                while vehicle_capacities[current_vehicle] >= primary_size and primary_groups > 0:
                    vehicle_assignments[current_vehicle][primary_size == 6] += 1
                    totals[primary_size == 6] += 1
                    vehicle_capacities[current_vehicle] -= primary_size
                    primary_groups -= 1

            for current_vehicle in range(len(vehicle_capacities)):
                while vehicle_capacities[current_vehicle] >= backup_size and secondary_groups > 0:
                    vehicle_assignments[current_vehicle][backup_size == 6] += 1
                    totals[backup_size == 6] += 1
                    vehicle_capacities[current_vehicle] -= backup_size
                    secondary_groups -= 1

            restored = sorted(zip(sorted_indices, vehicle_assignments, vehicle_capacities), key=lambda x: x[0])
            vehicle_assignments = [x[1] for x in restored]
            vehicle_capacities = [x[2] for x in restored]
        else:
            progress=True
            while secondary_groups>0 and progress:
                progress=False
                best_vehicle=find_best_vehicle(primary_size,backup_size)
                if best_vehicle is not None and secondary_groups>0:
                    vehicle_assignments[best_vehicle][backup_size==6]+=1
                    totals[backup_size==6]+=1
                    vehicle_capacities[best_vehicle] -= backup_size
                    secondary_groups -= 1
                    progress=True
            placement=True
            while primary_groups>0 and placement:
                placement=False
                for vehicle in range(len(vehicle_capacities)):
                    if vehicle_capacities[vehicle]>=primary_size and primary_groups>0:
                        vehicle_assignments[vehicle][primary_size == 6] += 1
                        totals[primary_size == 6] += 1
                        vehicle_capacities[vehicle] -= primary_size
                        primary_groups -= 1
                        placement=True

    else:
        if fill_before_next:
            for current_vehicle in range(len(vehicle_capacities)):
                while vehicle_capacities[current_vehicle] >= primary_size and primary_groups > 0:
                    vehicle_assignments[current_vehicle][primary_size == 6] += 1
                    totals[primary_size == 6] += 1
                    vehicle_capacities[current_vehicle] -= primary_size
                    primary_groups -= 1

            for current_vehicle in range(len(vehicle_capacities)):
                while vehicle_capacities[current_vehicle] >= backup_size and secondary_groups > 0:
                    vehicle_assignments[current_vehicle][backup_size == 6] += 1
                    totals[backup_size == 6] += 1
                    vehicle_capacities[current_vehicle] -= backup_size
                    secondary_groups -= 1
        else:
            progress=True
            while (primary_groups > 0 or secondary_groups > 0) and progress:
                progress=False
                for current_vehicle in range(len(vehicle_capacities)):
                    if vehicle_capacities[current_vehicle] >= primary_size and primary_groups > 0:
                        group_size = primary_size
                    elif vehicle_capacities[current_vehicle] >= backup_size and secondary_groups > 0:
                        group_size = backup_size
                    else:
                        continue

                    if vehicle_capacities[current_vehicle] >= group_size:
                        vehicle_assignments[current_vehicle][group_size == 6] += 1
                        totals[group_size == 6] += 1
                        vehicle_capacities[current_vehicle] -= group_size
                        if group_size == primary_size:
                            primary_groups -= 1
                        else:
                            secondary_groups -= 1
                        progress = True

    space_remaining = list(vehicle_capacities)
    restored_order = sorted(zip(original_indices, vehicle_assignments, space_remaining), key=lambda x: x[0])
    vehicle_assignments = [x[1] for x in restored_order]
    space_remaining = [x[2] for x in restored_order]

    return [totals, vehicle_assignments, space_remaining]

def closestalg(required_groups, allocations, backupsize=5):
    offby = []
    total_shortfalls = []

    for allocation in allocations:
        shortfall = [
            max(0, required_groups[0] - allocation[0][0]),  # Shortfall of backup groups
            max(0, required_groups[1] - allocation[0][1])   # Shortfall of 6-person groups
        ]
        offby.append(shortfall)
        total_shortfalls.append(sum(shortfall))

    min_shortfall = min(total_shortfalls)

    best_indices = [i for i, total in enumerate(total_shortfalls) if total == min_shortfall]
    if min_shortfall==0:
        best_index_avg=[]
        for l in best_indices:
            best_index_avg.append(allocations[l])
        return [allocations[best_indices[lowest_average(best_index_avg)]],[0,0]]

    optimized_allocations = optimize_allocations(allocations, min(backupsize,6),required_groups)

    offby = []
    total_shortfalls = []

    for allocation in optimized_allocations:
        shortfall = [
            max(0, required_groups[0] - allocation[0][0]),  # Shortfall of backup groups
            max(0, required_groups[1] - allocation[0][1])   # Shortfall of 6-person groups
        ]
        offby.append(shortfall)
        total_shortfalls.append(sum(shortfall))

    min_shortfall = min(total_shortfalls)
    best_indices = [i for i, total in enumerate(total_shortfalls) if total == min_shortfall]

    if len(best_indices) > 1:
        best_indices.sort(key=lambda i: (
            sum(1 for cap in optimized_allocations[i][2] if cap == 0) * -1 ,  # Count of zeros
            -optimized_allocations[i][0][backupsize == 5]  # Number of larger groups
        ))

    best_index = best_indices[0]

    return [optimized_allocations[best_index], offby[best_index]]

def optimize_allocations(allocations,backupsize,required):
    for m in range(len(allocations)):
        for i in range(len(allocations[m][1])-1,0,-1):
            for j in range(0,i):
                if allocations[m][1][i][backupsize==6]>=1 and allocations[m][2][j]>=6*(backupsize==6)+5*(backupsize==5):
                    allocations[m][1][i][backupsize==6]-=1
                    allocations[m][1][j][backupsize==6]+=1
                    allocations[m][2][j]-=6*(backupsize==6)+5*(backupsize==5)
                    allocations[m][2][i]+=6*(backupsize==6)+5*(backupsize==5)
        for x in range(len(allocations[m][1])):
            if required[0]-allocations[m][0][0]>0 and backupsize==6 and allocations[m][2][x]>=7:
                allocations[m][0][0]+=1
                allocations[m][2][x]-=7
                allocations[m][1][x][0]+=1
            elif required[1]-allocations[m][0][1]>0 and backupsize==5 and allocations[m][2][x]>=6:
                allocations[m][0][1]+=1
                allocations[m][2][x]-=6
                allocations[m][1][x][1]+=1
    return allocations
def lowest_average(allocations):
    averagelist = []
    for elem in allocations:
        total = 0
        n = 0
        for item in elem[1]:
            total += item[0] + item[1]
            if item[0] != 0 or item[1] != 0:
                n += 1
        averagelist.append(total / n if n > 0 else float('inf'))
    return averagelist.index(min(averagelist))

def sort_closestalg_output(closestalg_output,backup):
    # Safely extract the allocation details
    try:
        allocation = closestalg_output[0]  # First element contains totals, allocations, and remaining spaces
        remaining_spaces = allocation[2]   # Remaining spaces in vehicles
        allocations = allocation[1]        # Group allocations (5-person, 6-person)
    except (IndexError, TypeError, ValueError) as e:
        raise ValueError("Invalid closestalg_output structure") from e
    # Calculate vehicle sizes dynamically
    vehicle_sizes = []
    for remaining_space, assignment in zip(remaining_spaces, allocations):
        size = remaining_space + (backup * assignment[0]) + (6 * assignment[1])
        vehicle_sizes.append(size)
    # Combine sizes, allocations, and remaining spaces into a list of tuples
    combined_data = []
    for i in range(len(remaining_spaces)):
        combined_data.append((vehicle_sizes[i], allocations[i], remaining_spaces[i]))
    # Sort the combined data by remaining spaces in descending order
    combined_data.sort(key=lambda x: x[2], reverse=True)
    # Separate the sorted data into three lists
    sorted_sizes = [entry[0] for entry in combined_data]
    sorted_allocations = [entry[1] for entry in combined_data]
    sorted_remaining_spaces = [entry[2] for entry in combined_data]
    number = list(range(1, len(sorted_sizes) + 1))
    return sorted_allocations, sorted_remaining_spaces, sorted_sizes,number
x=allocate_groups([13,5,10,15,10,12,4,11,13,11,5,4,12,10,12,15,10,10,14,11,10,5,12,11,11,12,14,15,13,11,10,5,9,2,11],2,58,0,"desc",True,False)
print(x)