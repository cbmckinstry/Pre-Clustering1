from py4j.java_gateway import JavaGateway, java_import

# ✅ Create a global gateway to avoid reopening connections
gateway = JavaGateway()

# ✅ Import Java classes **once** (not inside functions)
java_import(gateway.jvm, 'java.util.ArrayList')
java_import(gateway.jvm, 'java.util.HashSet')

def to_java_list(py_list):
    """ Converts a Python list to a Java ArrayList """
    java_list = gateway.jvm.ArrayList()
    for item in py_list:
        java_list.add(item)
    return java_list

def to_java_int_array(py_list):
    """ Converts a Python list to a Java integer array """
    java_array = gateway.new_array(gateway.jvm.int, len(py_list))
    for i, val in enumerate(py_list):
        java_array[i] = val
    return java_array

def call_combine(allocations, spaces, shortfall, backupsize, used, bound_list):
    java_allocations = to_java_list([to_java_int_array(a) for a in allocations])
    java_spaces = to_java_list(spaces)
    java_shortfall = to_java_int_array(shortfall)
    java_used = to_java_list(used) if used else gateway.jvm.HashSet()
    java_bound_list = to_java_int_array(bound_list)

    combine = gateway.entry_point
    java_result = combine.combine(java_allocations, java_spaces, java_shortfall, int(backupsize), java_used, java_bound_list)

    return [list(sublist) for sublist in java_result]

def call_threes(allocations, spaces, shortfall, backup_size, used, bound_list):
    java_allocations = to_java_list([to_java_int_array(a) for a in allocations])
    java_spaces = to_java_list(spaces)
    java_shortfall = to_java_int_array(shortfall)
    java_used = to_java_list(used) if used else gateway.jvm.HashSet()
    java_bound_list = to_java_int_array(bound_list)

    combine = gateway.entry_point
    java_result = combine.threes(java_shortfall, java_allocations, java_spaces, int(backup_size), java_used, java_bound_list)

    return [list(sublist) for sublist in java_result]

def call_combineFlipped(allocations, spaces, shortfall, backupsize, used, bound_list):
    java_allocations = to_java_list([to_java_int_array(a) for a in allocations])
    java_spaces = to_java_list(spaces)
    java_shortfall = to_java_int_array(shortfall)
    java_used = to_java_list(used) if used else gateway.jvm.HashSet()
    java_bound_list = to_java_int_array(bound_list)

    combine = gateway.entry_point
    java_result = combine.combineFlipped(java_allocations, java_spaces, java_shortfall, int(backupsize), java_used, java_bound_list)

    return [list(sublist) for sublist in java_result]

def call_threesFlipped(allocations, spaces, shortfall, backup_size, used, bound_list):
    java_allocations = to_java_list([to_java_int_array(a) for a in allocations])
    java_spaces = to_java_list(spaces)
    java_shortfall = to_java_int_array(shortfall)
    java_used = to_java_list(used) if used else gateway.jvm.HashSet()
    java_bound_list = to_java_int_array(bound_list)

    combine = gateway.entry_point
    java_result = combine.threesFlipped(java_shortfall, java_allocations, java_spaces, int(backup_size), java_used, java_bound_list)

    return [list(sublist) for sublist in java_result]

def call_optimize(sorted_allocations, allocations, backup_size, out_combos, spaces):
    java_sorted_allocations = to_java_list([to_java_list(a) for a in sorted_allocations])
    java_allocations = to_java_list([to_java_int_array(a) for a in allocations])
    java_out_combos = to_java_list([to_java_list(c) for c in out_combos])
    java_spaces = to_java_list(spaces)

    combine = gateway.entry_point
    java_result = combine.optimize(java_sorted_allocations, java_allocations, int(backup_size), java_out_combos, java_spaces)

    return [list(sublist) for sublist in java_result]

sorted_allocations = [[0, 0], [0,1], [0,1], [0,1],[0,0],[0,0]]
allocations=[[0, 1], [0,2],[0,0]]
spaces = [5,2,5,5,5,2]
combos=[[1,3],[1,4,5],[6]]


shortfall = [0, 3]
used = {}
bound_list = [0, 3]

#result = call_optimize(sorted_allocations,allocations,5, combos, spaces)

#print(result)