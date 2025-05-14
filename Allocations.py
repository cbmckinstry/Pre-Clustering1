def sort_closestalg_output(closestalg_output,backup):
    # Safely extract the allocation details
    try:
        allocation = closestalg_output  # First element contains totals, allocations, and remaining spaces
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

from functools import lru_cache

def optimal_allocation(capacities, num_primary, num_backup, primary_size=6, backup_size=5):
    n = len(capacities)

    @lru_cache(maxsize=None)
    def dp(i, rem_primary, rem_backup):
        if i == n:
            return 0, [], 0, 0

        best_groups = -1
        best_alloc = None
        best_primary_used = 0
        best_backup_used = 0

        cap = capacities[i]

        max_backup = min(rem_backup, cap // backup_size)
        for use_backup in range(max_backup + 1):
            remaining_cap = cap - use_backup * backup_size
            max_primary = min(rem_primary, remaining_cap // primary_size)
            for use_primary in range(max_primary + 1):
                used_space = use_backup * backup_size + use_primary * primary_size
                if used_space <= cap:
                    next_groups, next_alloc, used_p_next, used_b_next = dp(
                        i + 1, rem_primary - use_primary, rem_backup - use_backup
                    )
                    total_groups = use_backup + use_primary + next_groups
                    if total_groups > best_groups:
                        best_groups = total_groups
                        best_alloc = [[use_backup, use_primary]] + next_alloc
                        best_primary_used = use_primary + used_p_next
                        best_backup_used = use_backup + used_b_next

        return best_groups, best_alloc, best_primary_used, best_backup_used

    _, allocations, used_primary, used_backup = dp(0, num_primary, num_backup)

    remaining_spaces = [
        capacities[i] - (alloc[0] * backup_size + alloc[1] * primary_size)
        for i, alloc in enumerate(allocations)
    ]

    return [[used_backup, used_primary], allocations, remaining_spaces]