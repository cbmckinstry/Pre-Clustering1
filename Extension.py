from py4j.java_gateway import JavaGateway, java_import
gateway = JavaGateway()

java_import(gateway.jvm, 'java.util.ArrayList')
java_import(gateway.jvm, 'java.util.HashSet')

def call_combine(allocations, spaces, shortfall, backupsize, used, bound_list):

    java_allocations = gateway.jvm.ArrayList()
    for allocation in allocations:
        java_array = gateway.new_array(gateway.jvm.int, len(allocation))
        for i, val in enumerate(allocation):
            java_array[i] = val
        java_allocations.add(java_array)

    java_spaces = gateway.jvm.ArrayList()
    for space in spaces:
        java_spaces.add(space)

    java_shortfall = gateway.new_array(gateway.jvm.int, 2)
    java_shortfall[0] = shortfall[0]
    java_shortfall[1] = shortfall[1]

    java_used = gateway.jvm.HashSet()
    if used:
        for index in used:
            java_used.add(index)

    java_bound_list = gateway.new_array(gateway.jvm.int, 2)
    java_bound_list[0] = bound_list[0]
    java_bound_list[1] = bound_list[1]

    combine = gateway.entry_point
    java_result = combine.combine(java_allocations, java_spaces, java_shortfall, int(backupsize), java_used, java_bound_list)

    def java_list_to_python(java_list):
        return [list(item) if hasattr(item, '__iter__') else item for item in java_list]

    python_result = [java_list_to_python(sublist) for sublist in java_result]

    return python_result


def call_threes(allocations, spaces, shortfall, backup_size, used, bound_list):

    java_allocations = gateway.jvm.ArrayList()
    for allocation in allocations:
        java_array = gateway.new_array(gateway.jvm.int, len(allocation))
        for i, val in enumerate(allocation):
            java_array[i] = val
        java_allocations.add(java_array)

    java_spaces = gateway.jvm.ArrayList()
    for space in spaces:
        java_spaces.add(space)

    java_shortfall = gateway.new_array(gateway.jvm.int, 2)
    java_shortfall[0] = shortfall[0]
    java_shortfall[1] = shortfall[1]

    java_used = gateway.jvm.HashSet()
    if used:
        for index in used:
            java_used.add(index)

    java_bound_list = gateway.new_array(gateway.jvm.int, 4)
    java_bound_list[0] = bound_list[0]
    java_bound_list[1] = bound_list[1]
    java_bound_list[2] = bound_list[2]
    java_bound_list[3] = bound_list[3]

    backup_size = int(backup_size)

    combine = gateway.entry_point
    java_result = combine.threes(java_shortfall, java_allocations, java_spaces, backup_size, java_used, java_bound_list)

    def java_list_to_python(java_list):
        return [list(item) if hasattr(item, '__iter__') else item for item in java_list]

    python_result = [java_list_to_python(sublist) for sublist in java_result]

    return python_result

'''def call_combineFlipped(allocations, spaces, shortfall, backupsize, used, bound_list):

    java_allocations = gateway.jvm.ArrayList()
    for allocation in allocations:
        java_array = gateway.new_array(gateway.jvm.int, len(allocation))
        for i, val in enumerate(allocation):
            java_array[i] = val
        java_allocations.add(java_array)

    java_spaces = gateway.jvm.ArrayList()
    for space in spaces:
        java_spaces.add(space)

    java_shortfall = gateway.new_array(gateway.jvm.int, 2)
    java_shortfall[0] = shortfall[0]
    java_shortfall[1] = shortfall[1]

    java_used = gateway.jvm.HashSet()
    if used:
        for index in used:
            java_used.add(index)

    java_bound_list = gateway.new_array(gateway.jvm.int, 2)
    java_bound_list[0] = bound_list[0]
    java_bound_list[1] = bound_list[1]

    combine = gateway.entry_point
    java_result = combine.combineFlipped(java_allocations, java_spaces, java_shortfall, int(backupsize), java_used, java_bound_list)

    def java_list_to_python(java_list):
        return [list(item) if hasattr(item, '__iter__') else item for item in java_list]

    python_result = [java_list_to_python(sublist) for sublist in java_result]

    return python_result

def call_threesFlipped(allocations, spaces, shortfall, backup_size, used, bound_list):

    java_allocations = gateway.jvm.ArrayList()
    for allocation in allocations:
        java_array = gateway.new_array(gateway.jvm.int, len(allocation))
        for i, val in enumerate(allocation):
            java_array[i] = val
        java_allocations.add(java_array)

    java_spaces = gateway.jvm.ArrayList()
    for space in spaces:
        java_spaces.add(space)

    java_shortfall = gateway.new_array(gateway.jvm.int, 2)
    java_shortfall[0] = shortfall[0]
    java_shortfall[1] = shortfall[1]

    java_used = gateway.jvm.HashSet()
    if used:
        for index in used:
            java_used.add(index)

    java_bound_list = gateway.new_array(gateway.jvm.int, 2)
    java_bound_list[0] = bound_list[0]
    java_bound_list[1] = bound_list[1]

    backup_size = int(backup_size)

    combine = gateway.entry_point
    java_result = combine.threesFlipped(java_shortfall, java_allocations, java_spaces, backup_size, java_used, java_bound_list)

    def java_list_to_python(java_list):
        return [list(item) if hasattr(item, '__iter__') else item for item in java_list]

    python_result = [java_list_to_python(sublist) for sublist in java_result]

    return python_result '''

def call_optimize(sorted_allocations, allocations, backup_size, out_combos, spaces):

    java_sorted_allocations = gateway.jvm.ArrayList()
    for allocation in sorted_allocations:
        java_list = gateway.jvm.ArrayList()
        for val in allocation:
            java_list.add(val)
        java_sorted_allocations.add(java_list)

    java_allocations = gateway.jvm.ArrayList()
    for allocation in allocations:
        java_array = gateway.new_array(gateway.jvm.int, len(allocation))
        for i, val in enumerate(allocation):
            java_array[i] = val
        java_allocations.add(java_array)

    java_out_combos = gateway.jvm.ArrayList()
    for combo in out_combos:
        java_list = gateway.jvm.ArrayList()
        for val in combo:
            java_list.add(val)
        java_out_combos.add(java_list)

    java_spaces = gateway.jvm.ArrayList()
    for space in spaces:
        java_spaces.add(space)

    backup_size = int(backup_size)

    combine = gateway.entry_point

    java_result = combine.optimize(java_sorted_allocations, java_allocations, backup_size, java_out_combos, java_spaces)

    # Convert Java result back to Python
    def java_list_to_python(java_list):
        return [list(item) if hasattr(item, '__iter__') else item for item in java_list]

    python_result = [java_list_to_python(sublist) for sublist in java_result]

    return python_result


sorted_allocations = [[0, 0], [0,1], [0,1], [0,1],[0,0],[0,0]]
allocations=[[0, 1], [0,2],[0,0]]
spaces = [5,2,5,5,5,2]
combos=[[1,3],[1,4,5],[6]]


shortfall = [0, 3]
used = {}
bound_list = [0, 3]

#result = call_optimize(sorted_allocations,allocations,5, combos, spaces)

#print(result)

