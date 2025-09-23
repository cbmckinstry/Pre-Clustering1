Logic for Preassignment:

Allocation:
    1)  We first place the crews optimally using dynamic programming
        a)  When the algorithm finds a solution that allocates more than or equal to the current max, it stores it
        b)  If a new max is discovered, the list is cleared
        c)  The final choice is the option that places the most 5-person crews
    2)  We take the solution from 1) and apply it to each capacity, leaving us with a list of remainders
    3)  This list from 2) is then sorted and the number of 5- and 6-person is adjusted to reflect the shortfall

Combination:

    Notation and Vocabulary:
        size - the number of vehicles combined
        cardinality - the number of 5- and 6-person crews, denoted as A-B (A 5-person and B 6-person crews)
        level - the ordering within size with respect to cardinality, i.e. level = A+B
        target - a given solution of size n and cardinality c, (n,c), e.g. (2, 1-0)

    Algorithm Structure:
        Smallest sum iteration:
            We iterate through the sorted remainder list by starting with the smallest elements and slowly
            expanding. This ensures the tighest possible bound for our specified target. This greedy approach
            can be proven optimal via contradiction.
        Backtracking:
            Smallest sum only holds for equal targets. This adds a great number of complexity
            to the problem.
                We solve this by creating a directed graph using the sizes:
                    2 <- 3 <- 4 <- 5 <- 6 (start)
                Each element contains its possible nodes/targets:
                       2           3           4           5          6
                                            (4, 2-1)
                                (3, 0-2)    (4, 0-3)    (5, 0-4)
                    (2, 1-0)    (3, 2-0)    (4, 2-0)    (5, 4-0)    (6, 0-5)
                    (2, 0-1) <- (3, 1-1) <- (4, 3-0) <- (5, 0-1) <- (6, 0-1)     (start)
                                (3, 0-1)    (4, 0-1)    (5, 1-0)
                                (3, 1-0)    (4, 0-2)
                                            (4, 1-0)
        The algorithm implements a recursive depth first search terminating when a solution is found.
        Pruning:
            We are able to use a number of break and continue statements in our backtracking as a result
            of the sorted list, iterative method, and numerical contradictions. Our official worst-case
            time complexity is O(n^20), however, I have yet to get a runtime over 8 seconds with a reasonable
            input.