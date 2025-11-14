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
        target - a given solution of size n and cardinality c, (n,c), e.g. (2, 1-0)
    Algorithm Structure:
        Smallest Sum(-ish) Iteration:
            We iterate through the sorted remainder list by starting with the smallest valid element combination
            and slowly expanding. This ensures the tightest possible bound for our specified target. This bound
            ensures that selecting one grouping cannot take away from others within the target. This greedy
            approach can be rigorously proven optimal for our specific situation via proof by exhaustion. The
            proof takes a minute, but it's doable using mod6 logic and the assumption that no combination can be
            further reduced.
        Solution Existence and Size:
            We can also claim that there is always a valid solution and that each grouping will be of size 6
            or less. Check Proofs.txt for the formal proof. Within the proof, an algorithm for finding a 
            solution is detailed, however, it does not provide an optimal solution.
        Backtracking:
            The algorithm provided in the proof was not optimal, so we solve this by instead forming
            combinations as opposed to splitting them. This method, however, is far more complex.
                We solve this by creating a directed graph using the sizes:

                    2 <- 3 <- 4 <- 5 <- 6 (start)                                                   (1)

                Each element contains its possible nodes/targets (5s and 6s):
                       2           3           4           5          6
                                            (4, 0-3)
                                (3, 0-2)    (4, 2-1)    (5, 0-4)
                    (2, 0-1)    (3, 1-1)    (4, 3-0)    (5, 4-0)    (6, 0-5)
                    (2, 1-0) <- (3, 2-0) <- (4, 0-2) <- (5, 0-1) <- (6, 0-1)     (start)            (2)
                                (3, 0-1)    (4, 2-0)    (5, 1-0)
                                (3, 1-0)    (4, 0-1)
                                            (4, 1-0)
                Primary Case (No 5s):
                       2           3           4           5          6
                                (3, 0-2)    (4, 0-3)    (5, 0-4)    (6, 0-5)
                    (2, 0-1) <- (3, 0-1) <- (4, 0-2) <- (5, 0-1) <- (6, 0-1)     (start)            (3)
                                            (4, 0-1)

            For simplicity, we'll look at the first diagram. The algorithm is finding a solution with the
            fewest large combinations by backtracking and continuously calling down after a placement. For
            example, suppose we could not find a solution of size 2 and 3. We form 1 combination of size 4,
            then call down to 3, which calls 2. It cannot find a solution with the rest being size 2, so it
            forms a size 3, calling down to size 2, and so on. In short, it’s a top-down recursive search
            with backtracking that prioritizes smaller group sizes but allows larger ones when necessary,
            systematically exploring combinations along the directed graph. From this, the worst-case
            runtime is roughly O(N^(2+3+4+5+6)) = O(N^20).
        Pruning:
            We are able to use a number of break and continue statements in our iterations as a result
            of numerical contradictions and limitations. For example, if we want to place 5 6-person crews
            in a combination of size 6, each vehicle must have remainder 5.
            From the diagram above, you may also notice it appears that nodes are "missing" from this
            diagram; they are not. For example, a size 6 combination that fits 4 6-person crews can always
            be broken down into smaller combinations.
            Additionally, there are certain combinations that cannot exist at the same time or in quantities,
            such as 2 size 6 combinations with one fitting 1 crew and the other fitting 5. Another example
            is that no more than two types of size 4 combinations can exist at the same time.
            All of these statements can be proven via easy direct numerical proofs; their results
            significantly reduce the realistic runtime from O(N^20).
        All Together:
            Taken together, the algorithm is a modified version of the one detailed in the proof. The Smallest
            Sum Iteration and Backtracking ensure that we follow the same sound logic, while Pruning ensures
            that we remain efficient. Overall, the algorithm is extremely close to optimal with 2 drawbacks:
            a larger size can take from a smaller when recursing and there is no weight for vehicle size when
            only considering remainder. Thus, we need "cleanup" and "optimize" methods to finalize our result.

Optimize:
The function iteratively improves the set of combos by swapping vehicles between pairs of combos to reduce the
maximum combo weight across the pair, while preserving total crew counts and re-splitting crews between the two
combos to respect space constraints. After no more such improvements exist, a secondary balancing pass adjusts
lopsided cases: it seeks swaps that better balance large vs. smaller combos and fixes singleton edge cases by
swapping equal-weight vehicles so the roomier vehicle moves into the larger combo. Finally, single-vehicle combos
are dropped from the output; the result is the updated multi-vehicle combos and their corresponding crew allocations.

Cleanup:
As mentioned in the previous section, a larger group can take from a smaller. The only case I have found of this
is a size 3 taking from 2, so there is another method that tests if the size 3s can be altered and reduced.

Ranges and Structure:
Crew ranges is simply running a loop that finds all solutions to 5x+6y=people. The crew structure uses the two
inputs and solves 5x+6y=people and x+y=crews using the invertible matrix theorem. By the Chicken McNugget Thm,
any number greater integer greater than 20 can be written as a linear combination of 5 and 6, thus we can form
our 5- and 6-person crews. We also rely upon the commutative property of multiplication as 5 6-person crews is
the same as 6 5-person crews, thus crew structure for a given number of people is not necessarily unique.

Notes:
The proofs were done for 6-person crews that were unplaced only. I believe this logic holds for 5-person crews as
well, but I have yet to formally prove it as it is a bit more difficult. You will notice that there is slightly
tweaked code for 5-person crews that follows all the same rules. This should not be an issue as the allocation
algorithm chooses the output that places the most 5-person crews. Since the placement of a 6-person crew can be
swapped with 5, and we do not have many 5-person crews in general, there should never be a situation where we have
both 5-person crews unplaced.
