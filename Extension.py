from py4j.java_gateway import JavaGateway

gateway = JavaGateway()

ArrayList = gateway.jvm.java.util.ArrayList
HashSet = gateway.jvm.java.util.HashSet

java_list = ArrayList()
java_set = HashSet()


def call_sevensFlipped(allocations, spaces, shortfall, backup_size, used):

    java_allocations = ArrayList()
    for allocation in allocations:
        java_array = gateway.new_array(gateway.jvm.int, len(allocation))

        for i, val in enumerate(allocation):
            java_array[i] = val
        java_allocations.add(java_array)

    java_spaces = java_spaces = ArrayList()
    for space in spaces:
        java_spaces.add(space)

    java_shortfall = gateway.new_array(gateway.jvm.int, 2)
    java_shortfall[0] = shortfall[0]
    java_shortfall[1] = shortfall[1]

    java_used = gateway.jvm.java.util.HashSet()
    if used:
        for index in used:
            java_used.add(index)


    backup_size = int(backup_size)

    combine = gateway.entry_point
    java_result = combine.sevensFlipped( java_allocations, java_spaces,java_shortfall, backup_size, java_used)

    def java_list_to_python(java_list):
        return [list(item) if hasattr(item, '__iter__') else item for item in java_list]

    python_result = [java_list_to_python(sublist) for sublist in java_result]

    return python_result

def call_optimize(sorted_allocations, allocations, backup_size, out_combos, spaces):
    java_util = gateway.jvm.java.util  # Shorter reference to Java's utility classes

    # Convert sorted_allocations to Java ArrayList
    java_sorted_allocations = java_util.ArrayList()
    for allocation in sorted_allocations:
        java_list = java_util.ArrayList()
        for val in allocation:
            java_list.add(val)
        java_sorted_allocations.add(java_list)

    # Convert allocations to Java ArrayList of int arrays
    java_allocations = java_allocations = ArrayList()
    for allocation in allocations:
        java_array = gateway.new_array(gateway.jvm.int, len(allocation))
        for i, val in enumerate(allocation):
            java_array[i] = val
        java_allocations.add(java_array)

    # Convert out_combos to Java ArrayList
    java_out_combos = java_util.ArrayList()
    for combo in out_combos:
        java_list = java_util.ArrayList()
        for val in combo:
            java_list.add(val)
        java_out_combos.add(java_list)

    # Convert spaces to Java ArrayList
    java_spaces = java_spaces = ArrayList()
    for space in spaces:
        java_spaces.add(space)

    # Convert backup_size to integer
    backup_size = int(backup_size)

    # Call Java method via Py4J
    combine = gateway.entry_point
    java_result = combine.optimize(java_sorted_allocations, java_allocations, backup_size, java_out_combos, java_spaces)

    # Convert Java result back to Python list
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

