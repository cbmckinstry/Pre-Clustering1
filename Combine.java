import java.util.*;
import java.util.ArrayList;
import java.util.HashSet;
import py4j.GatewayServer;

public class Combine {

    public static List<Object> combineFlipped(List<int[]> allocations, List<Integer> space, int[] shortfall, int backupSize, Set<Integer> used) {
        if (used == null) {
            used = new HashSet<>();
        }
        if (sum1(space) < backupSize * shortfall[0] + 6 * shortfall[1]) {
            return Arrays.asList(new ArrayList<>(), new ArrayList<>(), shortfall, used, false);
        }
        List<int[]> allocations0 = new ArrayList<>();
        List<Integer> space0 = new ArrayList<>();

        if (allocations.size() < 2) {
            return Arrays.asList(new ArrayList<>(), new ArrayList<>(), shortfall, used, false);
        }

        for (int i = 0; i < space.size(); i++) {
            if (space.get(i) != 0) {
                allocations0.add(allocations.get(i));
                space0.add(space.get(i));
            }
        }

        int six = shortfall[1];
        int backup = shortfall[0];

        int six4 = six;
        Set<Integer> used4 = new HashSet<>(used);
        List<int[]> combos4 = new ArrayList<>();
        int backup4 = backup;
        List<int[]> init = new ArrayList<>();


            for (int m = space0.size() - 2; m >= 0; m--) {
                if (six4 == 0) break;
                if (used4.contains(m)) continue;
                for (int n = space0.size() - 1; n > m; n--) {
                    if (six4 == 0) break;
                    if (used4.contains(n)) continue;
                    if ((space0.get(m) + space0.get(n) >= 6) && !used4.contains(m) && !used4.contains(n)) {
                        used4.add(m);
                        used4.add(n);
                        combos4.add(new int[]{m + 1, n + 1});
                        six4--;
                        init.add(new int[]{0, 1});
                        if (backup4 == 0 && six4 == 0) {
                            return Arrays.asList(combos4, init, new int[]{backup4, six4}, used4, true);
                        }
                    }
                }
            }

            List<int[]> combos5 = new ArrayList<>(combos4);
            Set<Integer> used5 = new HashSet<>(used4);
            List<int[]> init1 = new ArrayList<>(init);

            for (int m = space0.size() - 2; m >= 0; m--) {
                if (backup4 == 0) break;
                if (used5.contains(m)) continue;
                for (int n = space0.size() - 1; n > m; n--) {
                    if (backup4 == 0) break;
                    if (used5.contains(n)) continue;
                    if ((space0.get(m) + space0.get(n) >= 5) && !used5.contains(m) && !used5.contains(n)) {
                        used5.add(m);
                        used5.add(n);
                        combos5.add(new int[]{m + 1, n + 1});
                        backup4 -= 1;
                        init1.add(new int[]{1, 0});
                        if (backup4 == 0 && six4 == 0) {
                            return Arrays.asList(combos5, init1, new int[]{backup4, six4}, used5, true);
                        }
                    }
                }
            }
            return Arrays.asList(combos5, init1, new int[]{backup4, six4}, used5, false);



    }

    public static List<Object> threesFlipped(List<int[]> allocations, List<Integer> spaces, int[] shortfall, int backupSize, Set<Integer> used5) {
        if (used5 == null) {
            used5 = new HashSet<>();
        }

        if (sum1(spaces) < backupSize * shortfall[0] + 6 * shortfall[1]) {
            return Arrays.asList(new ArrayList<>(), new ArrayList<>(), shortfall, used5, false);
        }

        List<int[]> filteredAllocations = new ArrayList<>();
        List<Integer> filteredSpaces = new ArrayList<>();

        List<int[]> finalCombos = new ArrayList<>();
        List<int[]> finalInit = new ArrayList<>();
        Set<Integer> finalUsed = new HashSet<>();

        for (int i = 0; i < spaces.size(); i++) {
            if (spaces.get(i) != 0) {
                filteredAllocations.add(allocations.get(i));
                filteredSpaces.add(spaces.get(i));
            }
        }
        int six6 = shortfall[1];
        List<int[]> threes6 = new ArrayList<>();
        Set<Integer> used6 = new HashSet<>(used5);
        List<int[]> init = new ArrayList<>();

        List<Object> trial = combineFlipped(filteredAllocations, filteredSpaces, new int[]{0, six6}, backupSize, used6);
        if (((boolean) trial.get(4))) {
            finalCombos = (List<int[]>) trial.get(0);
            finalInit = (List<int[]>) trial.get(1);
            finalUsed = (Set<Integer>) trial.get(3);
            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
        }

        int six9= shortfall[1];
        List<int[]> threes9 = new ArrayList<>();
        Set<Integer> used9 = new HashSet<>(used5);
        List<int[]> init3 = new ArrayList<>();

        for (int i = filteredSpaces.size() - 1; i >= 2; i--) {
            if (six9 == 0) break;
            if (used9.contains(i)) continue;
            if (filteredSpaces.get(i)>2) break;
            for (int j = i - 1; j >= 1; j--) {
                if (used9.contains(j)) continue;
                if (filteredSpaces.get(j)>2) break;
                for (int k = j - 1; k >= 0; k--) {
                    if (used9.contains(k)) continue;
                    if (filteredSpaces.get(k)>4) break;
                    if (filteredSpaces.get(k)<2) continue;
                    int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j);
                    int minElem = Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j));

                    if (!used9.contains(i) && !used9.contains(j) && !used9.contains(k) &&
                            allSum >= 6 &&
                            allSum - minElem < 6 &&
                            (six9 > 0)) {

                        six9 -= 1;
                        used9.add(i);
                        used9.add(j);
                        used9.add(k);
                        threes9.add(new int[]{i + 1, k + 1, j + 1});
                        init3.add(new int[]{0, 1});

                        if (six9==0){
                            return Arrays.asList(threes9, init3, new int[]{0, 0}, used9, true);
                        }

                        trial= combineFlipped(filteredAllocations, filteredSpaces, new int[]{0, six9}, backupSize, used9);

                        if (((boolean) trial.get(4))) {
                            finalCombos = mergeLists((List<int[]>) trial.get(0),threes9);
                            finalInit = mergeLists((List<int[]>) trial.get(1),init3);
                            finalUsed = (Set<Integer>) trial.get(3);
                            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                        }

                    }
                }
            }
        }

        int six10 = shortfall[1];
        List<int[]> threes10 = new ArrayList<>();
        Set<Integer> used10 = new HashSet<>(used5);
        List<int[]> init4 = new ArrayList<>();

        for (int i = filteredSpaces.size() - 1; i >= 2; i--) {
            if (six6 == 0) break;
            if (used6.contains(i)) continue;
            if (filteredSpaces.get(i)<2) continue;
            for (int j = i - 1; j >= 1; j--) {
                if (used6.contains(j)) continue;
                if (filteredSpaces.get(j)<4) continue;
                for (int k = j - 1; k >= 0; k--) {
                    if (used6.contains(k)) continue;
                    if (filteredSpaces.get(k)<4) continue;
                    int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j);
                    int minElem = Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j));

                    if (!used6.contains(i) && !used6.contains(j) && !used6.contains(k) &&
                            allSum >= (12) &&
                            allSum - minElem < (12) &&
                            (six6 > 1)) {

                        six6 -= 2;
                        used6.add(i);
                        used6.add(j);
                        used6.add(k);
                        threes6.add(new int[]{i + 1, k + 1, j + 1});
                        init.add(new int[]{0, 2});

                        if (six6 == 0) {
                            return Arrays.asList(threes6, init, new int[]{0, 0}, used6, true);
                        }
                        List<Object> trial1 = combineFlipped(filteredAllocations, filteredSpaces, new int[]{0, six6}, 5, used6);

                        if ((boolean) trial1.get(4)) {
                            finalCombos = mergeLists((List<int[]>) trial1.get(0), threes6);
                            finalInit = mergeLists((List<int[]>) trial1.get(1), init);
                            finalUsed = (Set<Integer>) trial1.get(3);
                            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                        }

                        six10 = six6;
                        threes10 = new ArrayList<>(threes6);
                        used10 = new HashSet<>(used6);
                        init4 = new ArrayList<>(init);

                        for (int l = filteredSpaces.size() - 1; l >= 2; l--) {
                            if (six10 == 0) break;
                            if (used10.contains(l)) continue;
                            if (filteredSpaces.get(l)>2) break;
                            for (int m = l - 1; m >= 1; m--) {
                                if (used10.contains(m)) continue;
                                if (filteredSpaces.get(m)>2) break;
                                for (int n = m - 1; n >= 0; n--) {
                                    if (used10.contains(n)) continue;
                                    if (filteredSpaces.get(n) < 2) continue;
                                    if (filteredSpaces.get(n) > 4) break;
                                    int allSum1 = filteredSpaces.get(l) + filteredSpaces.get(m) + filteredSpaces.get(n);
                                    int minElem1 = Math.min(Math.min(filteredSpaces.get(l), filteredSpaces.get(m)), filteredSpaces.get(n));

                                    if (!used10.contains(l) && !used10.contains(m) && !used10.contains(n) &&
                                            allSum1 >= 6 &&
                                            allSum1 - minElem1 < 6 &&
                                            (six10 > 0)) {

                                        six10 -= 1;
                                        used10.add(l);
                                        used10.add(m);
                                        used10.add(n);
                                        threes10.add(new int[]{l + 1, m + 1, n + 1});
                                        init4.add(new int[]{0, 1});

                                        if (six10 == 0) {
                                            return Arrays.asList(threes10, init4, new int[]{0, 0}, used10, true);
                                        }

                                        trial = combineFlipped(filteredAllocations, filteredSpaces, new int[]{0, six10}, backupSize, used10);

                                        if (((boolean) trial.get(4))) {
                                            finalCombos = mergeLists((List<int[]>) trial.get(0), threes10);
                                            finalInit = mergeLists((List<int[]>) trial.get(1), init4);
                                            finalUsed = (Set<Integer>) trial.get(3);
                                            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return Arrays.asList(threes10, init4, new int[]{0, six10}, used10, false);

    }

    public static List<Object> foursFlipped(List<int[]> allocations, List<Integer> spaces, int[] shortfall, int backupSize, Set<Integer> used5) {
        if (used5 == null) {
            used5 = new HashSet<>();
        }

        if (sum1(spaces) < backupSize * shortfall[0] + 6 * shortfall[1]) {
            return Arrays.asList(new ArrayList<>(), new ArrayList<>(), shortfall, used5, false);
        }

        List<int[]> filteredAllocations = new ArrayList<>();
        List<Integer> filteredSpaces = new ArrayList<>();

        List<int[]> finalCombos = new ArrayList<>();
        List<int[]> finalInit = new ArrayList<>();
        Set<Integer> finalUsed = new HashSet<>();

        for (int i = 0; i < spaces.size(); i++) {
            if (spaces.get(i) != 0) {
                filteredAllocations.add(allocations.get(i));
                filteredSpaces.add(spaces.get(i));
            }
        }
        int six6 = shortfall[1];
        List<int[]> fours6 = new ArrayList<>();
        Set<Integer> used6 = new HashSet<>(used5);
        List<int[]> init = new ArrayList<>();

        List<Object> trial = threesFlipped(filteredAllocations, filteredSpaces, new int[]{0, six6}, backupSize, used6);
        if (((boolean) trial.get(4))) {
            finalCombos = (List<int[]>) trial.get(0);
            finalInit = (List<int[]>) trial.get(1);
            finalUsed = (Set<Integer>) trial.get(3);
            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
        }

        for (int i = filteredSpaces.size() - 1; i >= 3; i--) {
            if (six6 == 0) break;
            if (used6.contains(i)) continue;
            if (filteredSpaces.get(i)<3) continue;
            for (int j = i - 1; j >= 2; j--) {
                if (used6.contains(j)) continue;
                if (filteredSpaces.get(j)<4) continue;
                for (int k = j - 1; k >= 1; k--) {
                    if (used6.contains(k)) continue;
                    if (filteredSpaces.get(k)<5) continue;
                    for (int o = k - 1; o >= 0; o--) {
                        if (used6.contains(o)) continue;
                        if (filteredSpaces.get(o) < 5) continue;

                        int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j) + filteredSpaces.get(o);

                        int minElem = Math.min(Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j)), filteredSpaces.get(o));

                        if (!used6.contains(i) && !used6.contains(j) && !used6.contains(k) && !used6.contains(o) &&
                                allSum >= (18) &&
                                allSum - minElem < (18) &&
                                (six6 > 2)) {

                            six6 -= 3;
                            used6.add(i);
                            used6.add(j);
                            used6.add(k);
                            used6.add(o);
                            fours6.add(new int[]{i + 1, k + 1, j + 1, o + 1});
                            init.add(new int[]{0, 3});

                            if (six6 == 0) {
                                return Arrays.asList(fours6, init, new int[]{0, 0}, used6, true);
                            }
                            List<Object> trial1 = threesFlipped(filteredAllocations, filteredSpaces, new int[]{0, six6}, 5, used6);

                            if ((boolean) trial1.get(4)) {
                                finalCombos = mergeLists((List<int[]>) trial1.get(0), fours6);
                                finalInit = mergeLists((List<int[]>) trial1.get(1), init);
                                finalUsed = (Set<Integer>) trial1.get(3);
                                return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                            }

                        }
                    }
                }
            }

        }
        if (!(fours6.isEmpty())) return Arrays.asList(fours6, init, new int[]{0, six6}, used6, false);

        six6 = shortfall[1];
        fours6 = new ArrayList<>();
        used6 = new HashSet<>(used5);
        init = new ArrayList<>();

        for (int i = filteredSpaces.size() - 1; i >= 3; i--) {
            if (six6 == 0) break;
            if (used6.contains(i)) continue;
            if (filteredSpaces.get(i)>2) break;
            for (int j = i - 1; j >= 2; j--) {
                if (used6.contains(j)) continue;
                if (filteredSpaces.get(j)<2) continue;
                if (filteredSpaces.get(j)>3) break;
                for (int k = j - 1; k >= 1; k--) {
                    if (used6.contains(k)) continue;
                    if (filteredSpaces.get(k)<3) continue;
                    if (filteredSpaces.get(k)>4) break;
                    for (int o = k - 1; o >= 0; o--) {
                        if (used6.contains(o)) continue;
                        if (filteredSpaces.get(o) < 4) continue;

                        int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j) + filteredSpaces.get(o);

                        int minElem = Math.min(Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j)), filteredSpaces.get(o));

                        if (!used6.contains(i) && !used6.contains(j) && !used6.contains(k) && !used6.contains(o) &&
                                allSum >= (12) &&
                                allSum - minElem < (12) &&
                                (six6 > 1)) {

                            six6 -= 2;
                            used6.add(i);
                            used6.add(j);
                            used6.add(k);
                            used6.add(o);
                            fours6.add(new int[]{i + 1, k + 1, j + 1, o + 1});
                            init.add(new int[]{0, 2});

                            if (six6 == 0) {
                                return Arrays.asList(fours6, init, new int[]{0, 0}, used6, true);
                            }
                            List<Object> trial1 = threesFlipped(filteredAllocations, filteredSpaces, new int[]{0, six6}, 5, used6);

                            if ((boolean) trial1.get(4)) {
                                finalCombos = mergeLists((List<int[]>) trial1.get(0), fours6);
                                finalInit = mergeLists((List<int[]>) trial1.get(1), init);
                                finalUsed = (Set<Integer>) trial1.get(3);
                                return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                            }

                        }
                    }
                }
            }

        }
        if (!(fours6.isEmpty())) return Arrays.asList(fours6, init, new int[]{0, six6}, used6, false);

        int six9 = shortfall[1];
        List<int[]> fours9 = new ArrayList<>();
        Set<Integer> used9 = new HashSet<>(used5);
        List<int[]> init3 = new ArrayList<>();

        for (int i = filteredSpaces.size() - 1; i >= 3; i--) {
            if (six9 == 0) break;
            if (used9.contains(i)) continue;
            if (filteredSpaces.get(i)>1) break;
            for (int j = i - 1; j >= 2; j--) {
                if (used9.contains(j)) continue;
                if (filteredSpaces.get(j)>1) break;
                for (int k = j - 1; k >= 1; k--) {
                    if (used9.contains(k)) continue;
                    if (filteredSpaces.get(k)>2) break;
                    for (int o = k - 1; o >= 0; o--) {
                        if (used9.contains(o)) continue;
                        if (filteredSpaces.get(o)>3) break;
                        if (filteredSpaces.get(o)<2) continue;

                        int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j) + filteredSpaces.get(o);
                        int minElem = Math.min(Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j)), filteredSpaces.get(o));

                        if (!used9.contains(i) && !used9.contains(j) && !used9.contains(k) && !used9.contains(o) &&
                                allSum >= (6) &&
                                allSum - minElem < (6) &&
                                (six9 > 0)) {

                            six9 -= 1;
                            used9.add(i);
                            used9.add(j);
                            used9.add(k);
                            used9.add(o);
                            fours9.add(new int[]{i + 1, k + 1, j + 1, o + 1});
                            init3.add(new int[]{0, 1});

                            if (six9 == 0) {
                                return Arrays.asList(fours9, init3, new int[]{0, 0}, used9, true);
                            }

                            List<Object> trial1 = threesFlipped(filteredAllocations, filteredSpaces, new int[]{0, six9}, 5, used9);

                            if ((boolean) trial1.get(4)) {
                                finalCombos = mergeLists((List<int[]>) trial1.get(0), fours9);
                                finalInit = mergeLists((List<int[]>) trial1.get(1), init3);
                                finalUsed = (Set<Integer>) trial1.get(3);
                                return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                            }

                        }
                    }
                }
            }
        }

        return Arrays.asList(fours9, init3, new int[]{0, six9}, used9, false);
    }

    public static List<Object> fivesFlipped(List<int[]> allocations, List<Integer> spaces, int[] shortfall, int backupSize, Set<Integer> used5) {
        if (used5 == null) {
            used5 = new HashSet<>();
        }

        if (sum1(spaces) < backupSize * shortfall[0] + 6 * shortfall[1]) {
            return Arrays.asList(new ArrayList<>(), new ArrayList<>(), shortfall, used5, false);
        }

        List<int[]> filteredAllocations = new ArrayList<>();
        List<Integer> filteredSpaces = new ArrayList<>();

        List<int[]> finalCombos = new ArrayList<>();
        List<int[]> finalInit = new ArrayList<>();
        Set<Integer> finalUsed = new HashSet<>();

        for (int i = 0; i < spaces.size(); i++) {
            if (spaces.get(i) != 0) {
                filteredAllocations.add(allocations.get(i));
                filteredSpaces.add(spaces.get(i));
            }
        }
        int six6 = shortfall[1];
        List<int[]> fives6 = new ArrayList<>();
        Set<Integer> used6 = new HashSet<>(used5);
        List<int[]> init = new ArrayList<>();

        List<Object> trial = foursFlipped(filteredAllocations, filteredSpaces, new int[]{0, six6}, backupSize, used6);
        if (((boolean) trial.get(4))) {
            finalCombos = (List<int[]>) trial.get(0);
            finalInit = (List<int[]>) trial.get(1);
            finalUsed = (Set<Integer>) trial.get(3);
            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
        }

        int six9 = shortfall[1];
        List<int[]> fives9 = new ArrayList<>();
        Set<Integer> used9 = new HashSet<>(used5);
        List<int[]> init3 = new ArrayList<>();

        for (int i = filteredSpaces.size() - 1; i >= 4; i--) {
            if (six9 == 0) break;
            if (used9.contains(i)) continue;
            if (filteredSpaces.get(i)>2) break;
            for (int j = i - 1; j >= 3; j--) {
                if (used9.contains(j)) continue;
                if (filteredSpaces.get(j)>2) break;
                for (int k = j - 1; k >= 2; k--) {
                    if (used9.contains(k)) continue;
                    if (filteredSpaces.get(k)>2) break;
                    for (int o = k - 1; o >= 1; o--) {
                        if (used9.contains(o)) continue;
                        if (filteredSpaces.get(o)>2) break;
                        for (int q = o - 1; q >= 0; q--) {
                            if (used9.contains(q)) continue;
                            if (filteredSpaces.get(q)>2) break;
                            if (filteredSpaces.get(q)<2) continue;

                            int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j) + filteredSpaces.get(o) + filteredSpaces.get(q);
                            int minElem = Math.min(Math.min(Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j)), filteredSpaces.get(o)), filteredSpaces.get(q));

                            if (!used9.contains(i) && !used9.contains(j) && !used9.contains(k) && !used9.contains(o) && !used9.contains(q) &&
                                    allSum >= (6) &&
                                    allSum - minElem < (6) &&
                                    (six9 > 0)) {

                                six9 -= 1;
                                used9.add(i);
                                used9.add(j);
                                used9.add(k);
                                used9.add(o);
                                used9.add(q);
                                fives9.add(new int[]{i + 1, k + 1, j + 1, o + 1, q+1});
                                init3.add(new int[]{0, 1});

                                if (six9 == 0) {
                                    return Arrays.asList(fives9, init3, new int[]{0, 0}, used9, true);
                                }

                                List<Object> trial1 = foursFlipped(filteredAllocations, filteredSpaces, new int[]{0, six9}, 5, used9);

                                if ((boolean) trial1.get(4)) {
                                    finalCombos = mergeLists((List<int[]>) trial1.get(0), fives9);
                                    finalInit = mergeLists((List<int[]>) trial1.get(1), init3);
                                    finalUsed = (Set<Integer>) trial1.get(3);
                                    return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                                }

                            }
                        }
                    }
                }
            }
        }
        if (!(fives9.isEmpty())) return Arrays.asList(fives9, init3, new int[]{0, six9}, used9, false);

        for (int i = filteredSpaces.size() - 1; i >= 4; i--) {
            if (six6 == 0) break;
            if (used6.contains(i)) continue;
            if (filteredSpaces.get(i)<4) continue;
            for (int j = i - 1; j >= 3; j--) {
                if (used6.contains(j)) continue;
                if (filteredSpaces.get(j)<5) continue;
                for (int k = j - 1; k >= 2; k--) {
                    if (used6.contains(k)) continue;
                    if (filteredSpaces.get(k)<5) continue;
                    for (int o = k - 1; o >= 1; o--) {
                        if (used6.contains(o)) continue;
                        if (filteredSpaces.get(o)<5) continue;
                        for (int q = o - 1; q >= 0; q--) {
                            if (used6.contains(q)) continue;
                            if (filteredSpaces.get(q)<5) continue;

                            int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j) + filteredSpaces.get(o) + filteredSpaces.get(q);
                            int minElem = Math.min(Math.min(Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j)), filteredSpaces.get(o)), filteredSpaces.get(q));

                            if (!used6.contains(i) && !used6.contains(j) && !used6.contains(k) && !used6.contains(o) && !used6.contains(q) &&
                                    allSum >= (24) &&
                                    allSum - minElem < (24) &&
                                    (six6 > 3)) {

                                six6 -= 4;
                                used6.add(i);
                                used6.add(j);
                                used6.add(k);
                                used6.add(o);
                                used6.add(q);
                                fives6.add(new int[]{i + 1, k + 1, j + 1, o + 1, q + 1});
                                init.add(new int[]{0, 4});

                                if (six6 == 0) {
                                    return Arrays.asList(fives6, init, new int[]{0, 0}, used6, true);
                                }
                                List<Object> trial1 = foursFlipped(filteredAllocations, filteredSpaces, new int[]{0, six6}, 5, used6);

                                if ((boolean) trial1.get(4)) {
                                    finalCombos = mergeLists((List<int[]>) trial1.get(0), fives6);
                                    finalInit = mergeLists((List<int[]>) trial1.get(1), init);
                                    finalUsed = (Set<Integer>) trial1.get(3);
                                    return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                                }
                            }
                        }
                    }

                }
            }
        }

        return Arrays.asList(fives6, init, new int[]{0, six6}, used6, false);
    }

    public static List<Object> sixesFlipped(List<int[]> allocations, List<Integer> spaces, int[] shortfall, int backupSize, Set<Integer> used5) {
        if (used5 == null) {
            used5 = new HashSet<>();
        }

        if (sum1(spaces) < backupSize * shortfall[0] + 6 * shortfall[1]) {
            return Arrays.asList(new ArrayList<>(), new ArrayList<>(), shortfall, used5, false);
        }

        List<int[]> filteredAllocations = new ArrayList<>();
        List<Integer> filteredSpaces = new ArrayList<>();

        List<int[]> finalCombos = new ArrayList<>();
        List<int[]> finalInit = new ArrayList<>();
        Set<Integer> finalUsed = new HashSet<>();

        for (int i = 0; i < spaces.size(); i++) {
            if (spaces.get(i) != 0) {
                filteredAllocations.add(allocations.get(i));
                filteredSpaces.add(spaces.get(i));
            }
        }
        int six6 = shortfall[1];
        List<int[]> sixes6 = new ArrayList<>();
        Set<Integer> used6 = new HashSet<>(used5);
        List<int[]> init = new ArrayList<>();

        List<Object> trial = fivesFlipped(filteredAllocations, filteredSpaces, new int[]{0, six6}, backupSize, used6);
        if (((boolean) trial.get(4))) {
            finalCombos = (List<int[]>) trial.get(0);
            finalInit = (List<int[]>) trial.get(1);
            finalUsed = (Set<Integer>) trial.get(3);
            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
        }


        int six9 = shortfall[1];
        List<int[]> sixes9 = new ArrayList<>();
        Set<Integer> used9 = new HashSet<>(used5);
        List<int[]> init3 = new ArrayList<>();

        for (int i = filteredSpaces.size() - 1; i >= 5; i--) {
            if (six9 == 0) break;
            if (filteredSpaces.get(i)>1) break;
            if (used9.contains(i)) continue;
            for (int j = i - 1; j >= 4; j--) {
                if (filteredSpaces.get(j)>1) break;
                if (used9.contains(j)) continue;
                for (int k = j - 1; k >= 3; k--) {
                    if (filteredSpaces.get(k)>1) break;
                    if (used9.contains(k)) continue;
                    for (int o = k - 1; o >= 2; o--) {
                        if (filteredSpaces.get(o)>1) break;
                        if (used9.contains(o)) continue;
                        for (int q = o - 1; q >= 1; q--) {
                            if (filteredSpaces.get(q)>1) break;
                            if (used9.contains(q)) continue;
                            for (int s = q - 1; s >= 0; s--) {
                                if (filteredSpaces.get(s)>1) break;
                                if (used9.contains(s)) continue;

                                int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j) + filteredSpaces.get(o) + filteredSpaces.get(q) + filteredSpaces.get(s);
                                int minElem = Math.min(Math.min(Math.min(Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j)), filteredSpaces.get(o)), filteredSpaces.get(q)), filteredSpaces.get(s));

                                if (!used9.contains(i) && !used9.contains(j) && !used9.contains(k) && !used9.contains(o) && !used9.contains(q) && !used9.contains(s) &&
                                        allSum >= (6) &&
                                        allSum - minElem < (6) &&
                                        (six9 > 0)) {

                                    six9 -= 1;
                                    used9.add(i);
                                    used9.add(j);
                                    used9.add(k);
                                    used9.add(o);
                                    used9.add(q);
                                    used9.add(s);
                                    sixes9.add(new int[]{i + 1, k + 1, j + 1, o + 1, q + 1, s +1});
                                    init3.add(new int[]{0, 1});

                                    if (six9 == 0) {
                                        return Arrays.asList(sixes9, init3, new int[]{0, 0}, used9, true);
                                    }

                                    List<Object> trial1 = fivesFlipped(filteredAllocations, filteredSpaces, new int[]{0, six9}, 5, used9);

                                    if ((boolean) trial1.get(4)) {
                                        finalCombos = mergeLists((List<int[]>) trial1.get(0), sixes9);
                                        finalInit = mergeLists((List<int[]>) trial1.get(1), init3);
                                        finalUsed = (Set<Integer>) trial1.get(3);
                                        return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }

        for (int i = filteredSpaces.size() - 1; i >= 5; i--) {
            if (six6 == 0) break;
            if (filteredSpaces.get(i)<5) continue;
            if (used6.contains(i)) continue;
            for (int j = i - 1; j >= 4; j--) {
                if (filteredSpaces.get(j)<5) continue;
                if (used6.contains(j)) continue;
                for (int k = j - 1; k >= 3; k--) {
                    if (filteredSpaces.get(k)<5) continue;
                    if (used6.contains(k)) continue;
                    for (int o = k - 1; o >= 2; o--) {
                        if (filteredSpaces.get(o)<5) continue;
                        if (used6.contains(o)) continue;
                        for (int q = o - 1; q >= 1; q--) {
                            if (filteredSpaces.get(q)<5) continue;
                            if (used6.contains(q)) continue;
                            for (int s = q - 1; s >= 0; s--) {
                                if (filteredSpaces.get(s)<5) continue;
                                if (used6.contains(s)) continue;

                                int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j) + filteredSpaces.get(o) + filteredSpaces.get(q) + filteredSpaces.get(s);
                                int minElem = Math.min(Math.min(Math.min(Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j)), filteredSpaces.get(o)), filteredSpaces.get(q)), filteredSpaces.get(s));

                                if (!used6.contains(i) && !used6.contains(j) && !used6.contains(k) && !used6.contains(o) && !used6.contains(q) && !used6.contains(s) &&
                                        allSum >= (30) &&
                                        allSum - minElem < (30) &&
                                        (six6 > 4)) {

                                    six6 -= 5;
                                    used6.add(i);
                                    used6.add(j);
                                    used6.add(k);
                                    used6.add(o);
                                    used6.add(q);
                                    used6.add(s);
                                    sixes6.add(new int[]{i + 1, k + 1, j + 1, o + 1, q + 1, s + 1});
                                    init.add(new int[]{0, 5});

                                    if (six6 == 0) {
                                        return Arrays.asList(sixes6, init, new int[]{0, 0}, used6, true);
                                    }
                                    List<Object> trial1 = fivesFlipped(filteredAllocations, filteredSpaces, new int[]{0, six6}, 5, used6);

                                    if ((boolean) trial1.get(4)) {
                                        finalCombos = mergeLists((List<int[]>) trial1.get(0), sixes6);
                                        finalInit = mergeLists((List<int[]>) trial1.get(1), init);
                                        finalUsed = (Set<Integer>) trial1.get(3);
                                        return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                                    }
                                }

                            }
                        }
                    }

                }
            }
        }

        return Arrays.asList(new ArrayList<>(), new ArrayList<>(), new int[]{0, six6}, used6, false);
    }

    public static List<Object> threes(List<int[]> allocations, List<Integer> spaces, int[] shortfall, int backupSize, Set<Integer> used5) {
        if (used5 == null) {
            used5 = new HashSet<>();
        }

        if (sum1(spaces) < backupSize * shortfall[0] + 6 * shortfall[1]) {
            return Arrays.asList(new ArrayList<>(), new ArrayList<>(), shortfall, used5, false);
        }

        List<int[]> filteredAllocations = new ArrayList<>();
        List<Integer> filteredSpaces = new ArrayList<>();

        List<int[]> finalCombos = new ArrayList<>();
        List<int[]> finalInit = new ArrayList<>();
        Set<Integer> finalUsed = new HashSet<>();

        for (int i = 0; i < spaces.size(); i++) {
            if (spaces.get(i) != 0) {
                filteredAllocations.add(allocations.get(i));
                filteredSpaces.add(spaces.get(i));
            }
        }
        int six6 = shortfall[1];
        int five6 = shortfall[0];
        List<int[]> threes6 = new ArrayList<>();
        Set<Integer> used6 = new HashSet<>(used5);
        List<int[]> init = new ArrayList<>();

        List<Object> trial = combineFlipped(filteredAllocations, filteredSpaces, new int[]{five6, six6}, backupSize, used6);
        if (((boolean) trial.get(4))) {
            finalCombos = (List<int[]>) trial.get(0);
            finalInit = (List<int[]>) trial.get(1);
            finalUsed = (Set<Integer>) trial.get(3);
            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
        }

        int six9 = shortfall[1];
        int five9 = shortfall[0];
        List<int[]> threes9 = new ArrayList<>();
        Set<Integer> used9 = new HashSet<>(used5);
        List<int[]> init3 = new ArrayList<>();

        for (int i = filteredSpaces.size() - 1; i >= 2; i--) {
            if (six9 == 0) break;
            if (used9.contains(i)) continue;
            if (filteredSpaces.get(i) > 2) break;
            for (int j = i - 1; j >= 1; j--) {
                if (used9.contains(j)) continue;
                if (filteredSpaces.get(j) > 2) break;
                for (int k = j - 1; k >= 0; k--) {
                    if (used9.contains(k)) continue;
                    if (filteredSpaces.get(k) < 2) continue;
                    int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j);
                    int minElem = Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j));

                    if (!used9.contains(i) && !used9.contains(j) && !used9.contains(k) &&
                            allSum >= 6 &&
                            allSum - minElem < 6 &&
                            (six9 > 0)) {

                        six9 -= 1;
                        used9.add(i);
                        used9.add(j);
                        used9.add(k);
                        threes9.add(new int[]{i + 1, k + 1, j + 1});
                        init3.add(new int[]{0, 1});

                        if (six9 == 0 && five9 == 0) {
                            return Arrays.asList(threes9, init3, new int[]{0, 0}, used9, true);
                        }

                        trial = combineFlipped(filteredAllocations, filteredSpaces, new int[]{five9, six9}, backupSize, used9);

                        if (((boolean) trial.get(4))) {
                            finalCombos = mergeLists((List<int[]>) trial.get(0), threes9);
                            finalInit = mergeLists((List<int[]>) trial.get(1), init3);
                            finalUsed = (Set<Integer>) trial.get(3);
                            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                        }
                    }

                }

            }
        }

        int six10 = shortfall[1];
        int five10 = shortfall[0];
        List<int[]> threes10 = new ArrayList<>();
        Set<Integer> used10 = new HashSet<>(used5);
        List<int[]> init4 = new ArrayList<>();

        for (int i = filteredSpaces.size() - 1; i >= 2; i--) {
            if (six10 == 0 || five10 == 0) break;
            if (used10.contains(i)) continue;
            if (filteredSpaces.get(i) < 3) continue;
            for (int j = i - 1; j >= 1; j--) {
                if (used10.contains(j)) continue;
                if (filteredSpaces.get(j) < 4) continue;
                for (int k = j - 1; k >= 0; k--) {
                    if (used10.contains(k)) continue;
                    if (filteredSpaces.get(k) < 4) continue;
                    int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j);
                    int minElem = Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j));

                    if (!used10.contains(i) && !used10.contains(j) && !used10.contains(k) &&
                            allSum >= (11) &&
                            allSum - minElem < (11) &&
                            (six10 > 0 && five10 > 0)) {

                        six10 -= 1;
                        five10 -= 1;
                        used10.add(i);
                        used10.add(j);
                        used10.add(k);
                        threes10.add(new int[]{i + 1, k + 1, j + 1});
                        init4.add(new int[]{1, 1});

                        if (six10 == 0 && five10 == 0) {
                            return Arrays.asList(threes10, init4, new int[]{0, 0}, used10, true);
                        }
                        List<Object> trial1 = combineFlipped(filteredAllocations, filteredSpaces, new int[]{five10, six10}, 5, used10);

                        if ((boolean) trial1.get(4)) {
                            finalCombos = mergeLists((List<int[]>) trial1.get(0), threes10);
                            finalInit = mergeLists((List<int[]>) trial1.get(1), init4);
                            finalUsed = (Set<Integer>) trial1.get(3);
                            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                        }


                        for (int l = filteredSpaces.size() - 1; l >= 2; l--) {
                            if (six10 <= 1) break;
                            if (used10.contains(l)) continue;
                            if (filteredSpaces.get(l) < 4) continue;
                            for (int m = l - 1; m >= 1; m--) {
                                if (used10.contains(m)) continue;
                                if (filteredSpaces.get(m) < 4) continue;
                                for (int n = m - 1; n >= 0; n--) {
                                    if (used10.contains(n)) continue;
                                    if (filteredSpaces.get(n) < 4) continue;
                                    int allSum1 = filteredSpaces.get(l) + filteredSpaces.get(m) + filteredSpaces.get(n);
                                    int minElem1 = Math.min(Math.min(filteredSpaces.get(l), filteredSpaces.get(m)), filteredSpaces.get(n));

                                    if (!used10.contains(l) && !used10.contains(m) && !used10.contains(n) &&
                                            allSum1 >= 12 &&
                                            allSum1 - minElem1 < 12 &&
                                            (six10 > 1)) {

                                        six10 -= 2;
                                        used10.add(l);
                                        used10.add(m);
                                        used10.add(n);
                                        threes10.add(new int[]{l + 1, m + 1, n + 1});
                                        init4.add(new int[]{0, 2});

                                        if (six10 == 0 && five10 == 0) {
                                            return Arrays.asList(threes10, init4, new int[]{0, 0}, used10, true);
                                        }

                                        trial = combineFlipped(filteredAllocations, filteredSpaces, new int[]{five10, six10}, backupSize, used10);

                                        if (((boolean) trial.get(4))) {
                                            finalCombos = mergeLists((List<int[]>) trial.get(0), threes10);
                                            finalInit = mergeLists((List<int[]>) trial.get(1), init4);
                                            finalUsed = (Set<Integer>) trial.get(3);
                                            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }

        six10 = shortfall[1];
        five10 = shortfall[0];
        threes10 = new ArrayList<>();
        used10 = new HashSet<>(used5);
        init4 = new ArrayList<>();

        for (int i = filteredSpaces.size() - 1; i >= 2; i--) {
            if (six10 <= 1) break;
            if (used10.contains(i)) continue;
            if (filteredSpaces.get(i) < 4) continue;
            for (int j = i - 1; j >= 1; j--) {
                if (used10.contains(j)) continue;
                if (filteredSpaces.get(j) < 4) continue;
                for (int k = j - 1; k >= 0; k--) {
                    if (used10.contains(k)) continue;
                    if (filteredSpaces.get(k) < 4) continue;
                    int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j);
                    int minElem = Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j));

                    if (!used10.contains(i) && !used10.contains(j) && !used10.contains(k) &&
                            allSum >= (12) &&
                            allSum - minElem < (12) &&
                            (six10 > 1)) {

                        six10 -= 2;
                        used10.add(i);
                        used10.add(j);
                        used10.add(k);
                        threes10.add(new int[]{i + 1, k + 1, j + 1});
                        init4.add(new int[]{0, 2});

                        if (six10 == 0 && five10 == 0) {
                            return Arrays.asList(threes10, init4, new int[]{0, 0}, used10, true);
                        }
                        List<Object> trial1 = combineFlipped(filteredAllocations, filteredSpaces, new int[]{five10, six10}, 5, used10);

                        if ((boolean) trial1.get(4)) {
                            finalCombos = mergeLists((List<int[]>) trial1.get(0), threes10);
                            finalInit = mergeLists((List<int[]>) trial1.get(1), init4);
                            finalUsed = (Set<Integer>) trial1.get(3);
                            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                        }


                        for (int l = filteredSpaces.size() - 1; l >= 2; l--) {
                            if (five10 <= 1) break;
                            if (used10.contains(l)) continue;
                            if (filteredSpaces.get(l) < 2) continue;
                            for (int m = l - 1; m >= 1; m--) {
                                if (used10.contains(m)) continue;
                                if (filteredSpaces.get(m) < 3) continue;
                                for (int n = m - 1; n >= 0; n--) {
                                    if (used10.contains(n)) continue;
                                    if (filteredSpaces.get(n) < 4) continue;
                                    int allSum1 = filteredSpaces.get(l) + filteredSpaces.get(m) + filteredSpaces.get(n);
                                    int minElem1 = Math.min(Math.min(filteredSpaces.get(l), filteredSpaces.get(m)), filteredSpaces.get(n));

                                    if (!used10.contains(l) && !used10.contains(m) && !used10.contains(n) &&
                                            allSum1 >= 10 &&
                                            allSum1 - minElem1 < 10 &&
                                            (five10 > 1)) {

                                        five10 -= 2;
                                        used10.add(l);
                                        used10.add(m);
                                        used10.add(n);
                                        threes10.add(new int[]{l + 1, m + 1, n + 1});
                                        init4.add(new int[]{2, 0});

                                        if (six10 == 0 && five10 == 0) {
                                            return Arrays.asList(threes10, init4, new int[]{0, 0}, used10, true);
                                        }

                                        trial = combineFlipped(filteredAllocations, filteredSpaces, new int[]{five10, six10}, backupSize, used10);

                                        if (((boolean) trial.get(4))) {
                                            finalCombos = mergeLists((List<int[]>) trial.get(0), threes10);
                                            finalInit = mergeLists((List<int[]>) trial.get(1), init4);
                                            finalUsed = (Set<Integer>) trial.get(3);
                                            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }

        six10 = shortfall[1];
        five10 = shortfall[0];
        threes10 = new ArrayList<>();
        used10 = new HashSet<>(used5);
        init4 = new ArrayList<>();

        for (int i = filteredSpaces.size() - 1; i >= 2; i--) {
            if (five10 <= 1) break;
            if (used10.contains(i)) continue;
            if (filteredSpaces.get(i) < 2) continue;
            for (int j = i - 1; j >= 1; j--) {
                if (used10.contains(j)) continue;
                if (filteredSpaces.get(j) < 3) continue;
                for (int k = j - 1; k >= 0; k--) {
                    if (used10.contains(k)) continue;
                    if (filteredSpaces.get(k) < 4) continue;
                    int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j);
                    int minElem = Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j));

                    if (!used10.contains(i) && !used10.contains(j) && !used10.contains(k) &&
                            allSum >= (10) &&
                            allSum - minElem < (10) &&
                            (five10 > 1)) {

                        five10 -= 2;
                        used10.add(i);
                        used10.add(j);
                        used10.add(k);
                        threes10.add(new int[]{i + 1, k + 1, j + 1});
                        init4.add(new int[]{2, 0});

                        if (six10 == 0 && five10 == 0) {
                            return Arrays.asList(threes10, init4, new int[]{0, 0}, used10, true);
                        }
                        List<Object> trial1 = combineFlipped(filteredAllocations, filteredSpaces, new int[]{five10, six10}, 5, used10);

                        if ((boolean) trial1.get(4)) {
                            finalCombos = mergeLists((List<int[]>) trial1.get(0), threes10);
                            finalInit = mergeLists((List<int[]>) trial1.get(1), init4);
                            finalUsed = (Set<Integer>) trial1.get(3);
                            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                        }


                        for (int l = filteredSpaces.size() - 1; l >= 2; l--) {
                            if (five10 == 0) break;
                            if (used10.contains(l)) continue;
                            if (filteredSpaces.get(l) > 2) break;
                            for (int m = l - 1; m >= 1; m--) {
                                if (used10.contains(m)) continue;
                                if (filteredSpaces.get(m) > 2) break;
                                for (int n = m - 1; n >= 0; n--) {
                                    if (used10.contains(n)) continue;
                                    if (filteredSpaces.get(n) < 2) continue;
                                    if (filteredSpaces.get(n) > 3) break;
                                    int allSum1 = filteredSpaces.get(l) + filteredSpaces.get(m) + filteredSpaces.get(n);
                                    int minElem1 = Math.min(Math.min(filteredSpaces.get(l), filteredSpaces.get(m)), filteredSpaces.get(n));

                                    if (!used10.contains(l) && !used10.contains(m) && !used10.contains(n) &&
                                            allSum1 >= 5 &&
                                            allSum1 - minElem1 < 5 &&
                                            (five10 > 0)) {

                                        five10 -= 1;
                                        used10.add(l);
                                        used10.add(m);
                                        used10.add(n);
                                        threes10.add(new int[]{l + 1, m + 1, n + 1});
                                        init4.add(new int[]{1, 0});

                                        if (six10 == 0 && five10 == 0) {
                                            return Arrays.asList(threes10, init4, new int[]{0, 0}, used10, true);
                                        }

                                        trial = combineFlipped(filteredAllocations, filteredSpaces, new int[]{five10, six10}, backupSize, used10);

                                        if (((boolean) trial.get(4))) {
                                            finalCombos = mergeLists((List<int[]>) trial.get(0), threes10);
                                            finalInit = mergeLists((List<int[]>) trial.get(1), init4);
                                            finalUsed = (Set<Integer>) trial.get(3);
                                            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }

        six10 = shortfall[1];
        five10 = shortfall[0];
        threes10 = new ArrayList<>();
        used10 = new HashSet<>(used5);
        init4 = new ArrayList<>();

        for (int i = filteredSpaces.size() - 1; i >= 2; i--) {
            if (five10 == 0) break;
            if (used10.contains(i)) continue;
            if (filteredSpaces.get(i) > 2) break;
            for (int j = i - 1; j >= 1; j--) {
                if (used10.contains(j)) continue;
                if (filteredSpaces.get(j) > 2) break;
                for (int k = j - 1; k >= 0; k--) {
                    if (used10.contains(k)) continue;
                    if (filteredSpaces.get(k) > 3) break;
                    if (filteredSpaces.get(k) < 2) continue;
                    int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j);
                    int minElem = Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j));

                    if (!used10.contains(i) && !used10.contains(j) && !used10.contains(k) &&
                            allSum >= (5) &&
                            allSum - minElem < (5) &&
                            (five10 > 0)) {

                        five10 -= 1;
                        used10.add(i);
                        used10.add(j);
                        used10.add(k);
                        threes10.add(new int[]{i + 1, k + 1, j + 1});
                        init4.add(new int[]{1, 0});

                        if (six10 == 0 && five10 == 0) {
                            return Arrays.asList(threes10, init4, new int[]{0, 0}, used10, true);
                        }
                        List<Object> trial1 = combineFlipped(filteredAllocations, filteredSpaces, new int[]{five10, six10}, 5, used10);

                        if ((boolean) trial1.get(4)) {
                            finalCombos = mergeLists((List<int[]>) trial1.get(0), threes10);
                            finalInit = mergeLists((List<int[]>) trial1.get(1), init4);
                            finalUsed = (Set<Integer>) trial1.get(3);
                            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                        }


                        for (int l = filteredSpaces.size() - 1; l >= 2; l--) {
                            if (six10 == 0) break;
                            if (used10.contains(l)) continue;
                            if (filteredSpaces.get(l) > 2) break;
                            for (int m = l - 1; m >= 1; m--) {
                                if (used10.contains(m)) continue;
                                if (filteredSpaces.get(m) > 2) break;
                                for (int n = m - 1; n >= 0; n--) {
                                    if (used10.contains(n)) continue;
                                    if (filteredSpaces.get(n) < 2) continue;
                                    int allSum1 = filteredSpaces.get(l) + filteredSpaces.get(m) + filteredSpaces.get(n);
                                    int minElem1 = Math.min(Math.min(filteredSpaces.get(l), filteredSpaces.get(m)), filteredSpaces.get(n));

                                    if (!used10.contains(l) && !used10.contains(m) && !used10.contains(n) &&
                                            allSum1 >= 6 &&
                                            allSum1 - minElem1 < 6 &&
                                            (six10 > 0)) {

                                        six10 -= 1;
                                        used10.add(l);
                                        used10.add(m);
                                        used10.add(n);
                                        threes10.add(new int[]{l + 1, m + 1, n + 1});
                                        init4.add(new int[]{0, 1});

                                        if (six10 == 0 && five10 == 0) {
                                            return Arrays.asList(threes10, init4, new int[]{0, 0}, used10, true);
                                        }

                                        trial = combineFlipped(filteredAllocations, filteredSpaces, new int[]{five10, six10}, backupSize, used10);

                                        if (((boolean) trial.get(4))) {
                                            finalCombos = mergeLists((List<int[]>) trial.get(0), threes10);
                                            finalInit = mergeLists((List<int[]>) trial.get(1), init4);
                                            finalUsed = (Set<Integer>) trial.get(3);
                                            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }

        six10 = shortfall[1];
        five10 = shortfall[0];
        threes10 = new ArrayList<>();
        used10 = new HashSet<>(used5);
        init4 = new ArrayList<>();

        for (int i = filteredSpaces.size() - 1; i >= 2; i--) {
            if (five10 == 0 || six10 == 0) break;
            if (used10.contains(i)) continue;
            if (filteredSpaces.get(i) < 3) continue;
            for (int j = i - 1; j >= 1; j--) {
                if (used10.contains(j)) continue;
                if (filteredSpaces.get(j) < 4) continue;
                for (int k = j - 1; k >= 0; k--) {
                    if (used10.contains(k)) continue;
                    if (filteredSpaces.get(k) < 4) continue;
                    int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j);
                    int minElem = Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j));

                    if (!used10.contains(i) && !used10.contains(j) && !used10.contains(k) &&
                            allSum >= (11) &&
                            allSum - minElem < (11) &&
                            (five10 > 0 && six10 > 0)) {

                        five10 -= 1;
                        six10 -= 1;
                        used10.add(i);
                        used10.add(j);
                        used10.add(k);
                        threes10.add(new int[]{i + 1, k + 1, j + 1});
                        init4.add(new int[]{1, 1});

                        if (six10 == 0 && five10 == 0) {
                            return Arrays.asList(threes10, init4, new int[]{0, 0}, used10, true);
                        }
                        List<Object> trial1 = combineFlipped(filteredAllocations, filteredSpaces, new int[]{five10, six10}, 5, used10);

                        if ((boolean) trial1.get(4)) {
                            finalCombos = mergeLists((List<int[]>) trial1.get(0), threes10);
                            finalInit = mergeLists((List<int[]>) trial1.get(1), init4);
                            finalUsed = (Set<Integer>) trial1.get(3);
                            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                        }


                        for (int l = filteredSpaces.size() - 1; l >= 2; l--) {
                            if (five10 <= 1) break;
                            if (used10.contains(l)) continue;
                            if (filteredSpaces.get(l) < 2) continue;
                            for (int m = l - 1; m >= 1; m--) {
                                if (used10.contains(m)) continue;
                                if (filteredSpaces.get(m) < 3) continue;
                                for (int n = m - 1; n >= 0; n--) {
                                    if (used10.contains(n)) continue;
                                    if (filteredSpaces.get(n) < 4) continue;
                                    int allSum1 = filteredSpaces.get(l) + filteredSpaces.get(m) + filteredSpaces.get(n);
                                    int minElem1 = Math.min(Math.min(filteredSpaces.get(l), filteredSpaces.get(m)), filteredSpaces.get(n));

                                    if (!used10.contains(l) && !used10.contains(m) && !used10.contains(n) &&
                                            allSum1 >= 10 &&
                                            allSum1 - minElem1 < 10 &&
                                            (five10 > 1)) {

                                        five10 -= 2;
                                        used10.add(l);
                                        used10.add(m);
                                        used10.add(n);
                                        threes10.add(new int[]{l + 1, m + 1, n + 1});
                                        init4.add(new int[]{2, 0});

                                        if (six10 == 0 && five10 == 0) {
                                            return Arrays.asList(threes10, init4, new int[]{0, 0}, used10, true);
                                        }

                                        trial = combineFlipped(filteredAllocations, filteredSpaces, new int[]{five10, six10}, backupSize, used10);

                                        if (((boolean) trial.get(4))) {
                                            finalCombos = mergeLists((List<int[]>) trial.get(0), threes10);
                                            finalInit = mergeLists((List<int[]>) trial.get(1), init4);
                                            finalUsed = (Set<Integer>) trial.get(3);
                                            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }

        six10 = shortfall[1];
        five10 = shortfall[0];
        threes10 = new ArrayList<>();
        used10 = new HashSet<>(used5);
        init4 = new ArrayList<>();

        for (int i = filteredSpaces.size() - 1; i >= 2; i--) {
            if (five10 <= 1) break;
            if (used10.contains(i)) continue;
            if (filteredSpaces.get(i) < 2) continue;
            for (int j = i - 1; j >= 1; j--) {
                if (used10.contains(j)) continue;
                if (filteredSpaces.get(j) < 3) continue;
                for (int k = j - 1; k >= 0; k--) {
                    if (used10.contains(k)) continue;
                    if (filteredSpaces.get(k) < 4) continue;
                    int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j);
                    int minElem = Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j));

                    if (!used10.contains(i) && !used10.contains(j) && !used10.contains(k) &&
                            allSum >= (10) &&
                            allSum - minElem < (10) &&
                            (five10 > 1)) {

                        five10 -= 2;
                        used10.add(i);
                        used10.add(j);
                        used10.add(k);
                        threes10.add(new int[]{i + 1, k + 1, j + 1});
                        init4.add(new int[]{2, 0});

                        if (six10 == 0 && five10 == 0) {
                            return Arrays.asList(threes10, init4, new int[]{0, 0}, used10, true);
                        }
                        List<Object> trial1 = combineFlipped(filteredAllocations, filteredSpaces, new int[]{five10, six10}, 5, used10);

                        if ((boolean) trial1.get(4)) {
                            finalCombos = mergeLists((List<int[]>) trial1.get(0), threes10);
                            finalInit = mergeLists((List<int[]>) trial1.get(1), init4);
                            finalUsed = (Set<Integer>) trial1.get(3);
                            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                        }


                        for (int l = filteredSpaces.size() - 1; l >= 2; l--) {
                            if (six10 == 0) break;
                            if (used10.contains(l)) continue;
                            if (filteredSpaces.get(l) > 2) break;
                            for (int m = l - 1; m >= 1; m--) {
                                if (used10.contains(m)) continue;
                                if (filteredSpaces.get(m) > 2) break;
                                for (int n = m - 1; n >= 0; n--) {
                                    if (used10.contains(n)) continue;
                                    if (filteredSpaces.get(n) < 2) continue;
                                    int allSum1 = filteredSpaces.get(l) + filteredSpaces.get(m) + filteredSpaces.get(n);
                                    int minElem1 = Math.min(Math.min(filteredSpaces.get(l), filteredSpaces.get(m)), filteredSpaces.get(n));

                                    if (!used10.contains(l) && !used10.contains(m) && !used10.contains(n) &&
                                            allSum1 >= 6 &&
                                            allSum1 - minElem1 < 6 &&
                                            (six10 > 0)) {

                                        six10 -= 1;
                                        used10.add(l);
                                        used10.add(m);
                                        used10.add(n);
                                        threes10.add(new int[]{l + 1, m + 1, n + 1});
                                        init4.add(new int[]{0, 1});

                                        if (six10 == 0 && five10 == 0) {
                                            return Arrays.asList(threes10, init4, new int[]{0, 0}, used10, true);
                                        }

                                        trial = combineFlipped(filteredAllocations, filteredSpaces, new int[]{five10, six10}, backupSize, used10);

                                        if (((boolean) trial.get(4))) {
                                            finalCombos = mergeLists((List<int[]>) trial.get(0), threes10);
                                            finalInit = mergeLists((List<int[]>) trial.get(1), init4);
                                            finalUsed = (Set<Integer>) trial.get(3);
                                            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }

        six10 = shortfall[1];
        five10 = shortfall[0];
        threes10 = new ArrayList<>();
        used10 = new HashSet<>(used5);
        init4 = new ArrayList<>();

        for (int i = filteredSpaces.size() - 1; i >= 2; i--) {
            if (six10 <= 1) break;
            if (used10.contains(i)) continue;
            if (filteredSpaces.get(i) < 4) continue;
            for (int j = i - 1; j >= 1; j--) {
                if (used10.contains(j)) continue;
                if (filteredSpaces.get(j) < 4) continue;
                for (int k = j - 1; k >= 0; k--) {
                    if (used10.contains(k)) continue;
                    if (filteredSpaces.get(k) < 4) continue;
                    int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j);
                    int minElem = Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j));

                    if (!used10.contains(i) && !used10.contains(j) && !used10.contains(k) &&
                            allSum >= (12) &&
                            allSum - minElem < (12) &&
                            (six10 > 1)) {

                        six10 -= 2;
                        used10.add(i);
                        used10.add(j);
                        used10.add(k);
                        threes10.add(new int[]{i + 1, k + 1, j + 1});
                        init4.add(new int[]{0, 2});

                        if (six10 == 0 && five10 == 0) {
                            return Arrays.asList(threes10, init4, new int[]{0, 0}, used10, true);
                        }
                        List<Object> trial1 = combineFlipped(filteredAllocations, filteredSpaces, new int[]{five10, six10}, 5, used10);

                        if ((boolean) trial1.get(4)) {
                            finalCombos = mergeLists((List<int[]>) trial1.get(0), threes10);
                            finalInit = mergeLists((List<int[]>) trial1.get(1), init4);
                            finalUsed = (Set<Integer>) trial1.get(3);
                            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                        }


                        for (int l = filteredSpaces.size() - 1; l >= 2; l--) {
                            if (six10 == 0) break;
                            if (used10.contains(l)) continue;
                            if (filteredSpaces.get(l) > 2) break;
                            for (int m = l - 1; m >= 1; m--) {
                                if (used10.contains(m)) continue;
                                if (filteredSpaces.get(m) > 2) break;
                                for (int n = m - 1; n >= 0; n--) {
                                    if (used10.contains(n)) continue;
                                    if (filteredSpaces.get(n) < 2) continue;
                                    int allSum1 = filteredSpaces.get(l) + filteredSpaces.get(m) + filteredSpaces.get(n);
                                    int minElem1 = Math.min(Math.min(filteredSpaces.get(l), filteredSpaces.get(m)), filteredSpaces.get(n));

                                    if (!used10.contains(l) && !used10.contains(m) && !used10.contains(n) &&
                                            allSum1 >= 6 &&
                                            allSum1 - minElem1 < 6 &&
                                            (six10 > 0)) {

                                        six10 -= 1;
                                        used10.add(l);
                                        used10.add(m);
                                        used10.add(n);
                                        threes10.add(new int[]{l + 1, m + 1, n + 1});
                                        init4.add(new int[]{0, 1});

                                        if (six10 == 0 && five10 == 0) {
                                            return Arrays.asList(threes10, init4, new int[]{0, 0}, used10, true);
                                        }

                                        trial = combineFlipped(filteredAllocations, filteredSpaces, new int[]{five10, six10}, backupSize, used10);

                                        if (((boolean) trial.get(4))) {
                                            finalCombos = mergeLists((List<int[]>) trial.get(0), threes10);
                                            finalInit = mergeLists((List<int[]>) trial.get(1), init4);
                                            finalUsed = (Set<Integer>) trial.get(3);
                                            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }

        six10 = shortfall[1];
        five10 = shortfall[0];
        threes10 = new ArrayList<>();
        used10 = new HashSet<>(used5);
        init4 = new ArrayList<>();

        for (int i = filteredSpaces.size() - 1; i >= 2; i--) {
            if (six10 <= 1) break;
            if (used10.contains(i)) continue;
            if (filteredSpaces.get(i) < 4) continue;
            for (int j = i - 1; j >= 1; j--) {
                if (used10.contains(j)) continue;
                if (filteredSpaces.get(j) < 4) continue;
                for (int k = j - 1; k >= 0; k--) {
                    if (used10.contains(k)) continue;
                    if (filteredSpaces.get(k) < 4) continue;
                    int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j);
                    int minElem = Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j));

                    if (!used10.contains(i) && !used10.contains(j) && !used10.contains(k) &&
                            allSum >= (12) &&
                            allSum - minElem < (12) &&
                            (six10 > 1)) {

                        six10 -= 2;
                        used10.add(i);
                        used10.add(j);
                        used10.add(k);
                        threes10.add(new int[]{i + 1, k + 1, j + 1});
                        init4.add(new int[]{0, 2});

                        if (six10 == 0 && five10 == 0) {
                            return Arrays.asList(threes10, init4, new int[]{0, 0}, used10, true);
                        }
                        List<Object> trial1 = combineFlipped(filteredAllocations, filteredSpaces, new int[]{five10, six10}, 5, used10);

                        if ((boolean) trial1.get(4)) {
                            finalCombos = mergeLists((List<int[]>) trial1.get(0), threes10);
                            finalInit = mergeLists((List<int[]>) trial1.get(1), init4);
                            finalUsed = (Set<Integer>) trial1.get(3);
                            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                        }


                        for (int l = filteredSpaces.size() - 1; l >= 2; l--) {
                            if (six10 == 0) break;
                            if (used10.contains(l)) continue;
                            if (filteredSpaces.get(l) > 2) break;
                            for (int m = l - 1; m >= 1; m--) {
                                if (used10.contains(m)) continue;
                                if (filteredSpaces.get(m) > 2) break;
                                for (int n = m - 1; n >= 0; n--) {
                                    if (used10.contains(n)) continue;
                                    if (filteredSpaces.get(n) < 2) continue;
                                    if (filteredSpaces.get(n) > 3) break;
                                    int allSum1 = filteredSpaces.get(l) + filteredSpaces.get(m) + filteredSpaces.get(n);
                                    int minElem1 = Math.min(Math.min(filteredSpaces.get(l), filteredSpaces.get(m)), filteredSpaces.get(n));

                                    if (!used10.contains(l) && !used10.contains(m) && !used10.contains(n) &&
                                            allSum1 >= 5 &&
                                            allSum1 - minElem1 < 5 &&
                                            (five10 > 0)) {

                                        five10 -= 1;
                                        used10.add(l);
                                        used10.add(m);
                                        used10.add(n);
                                        threes10.add(new int[]{l + 1, m + 1, n + 1});
                                        init4.add(new int[]{1, 0});

                                        if (six10 == 0 && five10 == 0) {
                                            return Arrays.asList(threes10, init4, new int[]{0, 0}, used10, true);
                                        }

                                        trial = combineFlipped(filteredAllocations, filteredSpaces, new int[]{five10, six10}, backupSize, used10);

                                        if (((boolean) trial.get(4))) {
                                            finalCombos = mergeLists((List<int[]>) trial.get(0), threes10);
                                            finalInit = mergeLists((List<int[]>) trial.get(1), init4);
                                            finalUsed = (Set<Integer>) trial.get(3);
                                            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }

        six10 = shortfall[1];
        five10 = shortfall[0];
        threes10 = new ArrayList<>();
        used10 = new HashSet<>(used5);
        init4 = new ArrayList<>();

        for (int i = filteredSpaces.size() - 1; i >= 2; i--) {
            if (six10 == 0 || five10 == 0) break;
            if (used10.contains(i)) continue;
            if (filteredSpaces.get(i) < 3) continue;
            for (int j = i - 1; j >= 1; j--) {
                if (used10.contains(j)) continue;
                if (filteredSpaces.get(j) < 4) continue;
                for (int k = j - 1; k >= 0; k--) {
                    if (used10.contains(k)) continue;
                    if (filteredSpaces.get(k) < 4) continue;
                    int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j);
                    int minElem = Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j));

                    if (!used10.contains(i) && !used10.contains(j) && !used10.contains(k) &&
                            allSum >= (11) &&
                            allSum - minElem < (11) &&
                            (six10 > 0 && five10 > 0)) {

                        six10 -= 1;
                        five10 -= 1;
                        used10.add(i);
                        used10.add(j);
                        used10.add(k);
                        threes10.add(new int[]{i + 1, k + 1, j + 1});
                        init4.add(new int[]{1, 1});

                        if (six10 == 0 && five10 == 0) {
                            return Arrays.asList(threes10, init4, new int[]{0, 0}, used10, true);
                        }
                        List<Object> trial1 = combineFlipped(filteredAllocations, filteredSpaces, new int[]{five10, six10}, 5, used10);

                        if ((boolean) trial1.get(4)) {
                            finalCombos = mergeLists((List<int[]>) trial1.get(0), threes10);
                            finalInit = mergeLists((List<int[]>) trial1.get(1), init4);
                            finalUsed = (Set<Integer>) trial1.get(3);
                            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                        }


                        for (int l = filteredSpaces.size() - 1; l >= 2; l--) {
                            if (six10 == 0) break;
                            if (used10.contains(l)) continue;
                            if (filteredSpaces.get(l) > 2) break;
                            for (int m = l - 1; m >= 1; m--) {
                                if (used10.contains(m)) continue;
                                if (filteredSpaces.get(m) > 2) break;
                                for (int n = m - 1; n >= 0; n--) {
                                    if (used10.contains(n)) continue;
                                    if (filteredSpaces.get(n) < 2) continue;
                                    int allSum1 = filteredSpaces.get(l) + filteredSpaces.get(m) + filteredSpaces.get(n);
                                    int minElem1 = Math.min(Math.min(filteredSpaces.get(l), filteredSpaces.get(m)), filteredSpaces.get(n));

                                    if (!used10.contains(l) && !used10.contains(m) && !used10.contains(n) &&
                                            allSum1 >= 6 &&
                                            allSum1 - minElem1 < 6 &&
                                            (six10 > 0)) {

                                        six10 -= 1;
                                        used10.add(l);
                                        used10.add(m);
                                        used10.add(n);
                                        threes10.add(new int[]{l + 1, m + 1, n + 1});
                                        init4.add(new int[]{0, 1});

                                        if (six10 == 0 && five10 == 0) {
                                            return Arrays.asList(threes10, init4, new int[]{0, 0}, used10, true);
                                        }

                                        trial = combineFlipped(filteredAllocations, filteredSpaces, new int[]{five10, six10}, backupSize, used10);

                                        if (((boolean) trial.get(4))) {
                                            finalCombos = mergeLists((List<int[]>) trial.get(0), threes10);
                                            finalInit = mergeLists((List<int[]>) trial.get(1), init4);
                                            finalUsed = (Set<Integer>) trial.get(3);
                                            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }

        return Arrays.asList(threes10, init4, new int[]{0, six10}, used10, false);

    }

    public static List<Object> fours(List<int[]> allocations, List<Integer> spaces, int[] shortfall, int backupSize, Set<Integer> used5) {
        if (used5 == null) {
            used5 = new HashSet<>();
        }

        if (sum1(spaces) < backupSize * shortfall[0] + 6 * shortfall[1]) {
            return Arrays.asList(new ArrayList<>(), new ArrayList<>(), shortfall, used5, false);
        }

        List<int[]> filteredAllocations = new ArrayList<>();
        List<Integer> filteredSpaces = new ArrayList<>();

        List<int[]> finalCombos = new ArrayList<>();
        List<int[]> finalInit = new ArrayList<>();
        Set<Integer> finalUsed = new HashSet<>();

        for (int i = 0; i < spaces.size(); i++) {
            if (spaces.get(i) != 0) {
                filteredAllocations.add(allocations.get(i));
                filteredSpaces.add(spaces.get(i));
            }
        }
        int six6 = shortfall[1];
        int five6=shortfall[0];
        List<int[]> fours6 = new ArrayList<>();
        Set<Integer> used6 = new HashSet<>(used5);
        List<int[]> init = new ArrayList<>();

        List<Object> trial = threes(filteredAllocations, filteredSpaces, new int[]{five6, six6}, backupSize, used6);
        if (((boolean) trial.get(4))) {
            finalCombos = (List<int[]>) trial.get(0);
            finalInit = (List<int[]>) trial.get(1);
            finalUsed = (Set<Integer>) trial.get(3);
            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
        }


        for (int l = filteredSpaces.size() - 1; l >= 3; l--) {
            if (five6 <= 2) break;
            if (used6.contains(l)) continue;
            if (filteredSpaces.get(l)<3) continue;
            for (int m = l - 1; m >= 2; m--) {
                if (used6.contains(m)) continue;
                if (filteredSpaces.get(m)<4) continue;
                for (int n = m - 1; n >= 1; n--) {
                    if (used6.contains(n)) continue;
                    if (filteredSpaces.get(n)<4) continue;
                    for (int p = n - 1; p >= 0; p--) {
                        if (used6.contains(p)) continue;
                        if (filteredSpaces.get(p) < 4) continue;

                        int allSum = filteredSpaces.get(m) + filteredSpaces.get(n) + filteredSpaces.get(l) + filteredSpaces.get(p);

                        int minElem = Math.min(Math.min(Math.min(filteredSpaces.get(l), filteredSpaces.get(m)), filteredSpaces.get(n)), filteredSpaces.get(p));

                        if (!used6.contains(l) && !used6.contains(m) && !used6.contains(n) && !used6.contains(p) &&
                                allSum >= (15) &&
                                allSum - minElem < (15) &&
                                (five6 > 2)) {

                            five6 -= 3;
                            used6.add(l);
                            used6.add(m);
                            used6.add(n);
                            used6.add(p);
                            fours6.add(new int[]{l + 1, m + 1, n + 1, p + 1});
                            init.add(new int[]{3, 0});

                            if (six6 == 0 && five6==0) {
                                return Arrays.asList(fours6, init, new int[]{0, 0}, used6, true);
                            }
                            List<Object> trial1 = threes(filteredAllocations, filteredSpaces, new int[]{five6, six6}, 5, used6);

                            if ((boolean) trial1.get(4)) {
                                finalCombos = mergeLists((List<int[]>) trial1.get(0), fours6);
                                finalInit = mergeLists((List<int[]>) trial1.get(1), init);
                                finalUsed = (Set<Integer>) trial1.get(3);
                                return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                            }

                        }
                    }
                }
            }

        }

        six6 = shortfall[1];
        five6=shortfall[0];
        fours6 = new ArrayList<>();
        used6 = new HashSet<>(used5);
        init = new ArrayList<>();

        for (int i = filteredSpaces.size() - 1; i >= 3; i--) {
            if (six6 == 0 ||five6<=1) break;
            if (used6.contains(i)) continue;
            if (filteredSpaces.get(i)<4) continue;
            for (int j = i - 1; j >= 2; j--) {
                if (used6.contains(j)) continue;
                if (filteredSpaces.get(j)<4) continue;
                for (int k = j - 1; k >= 1; k--) {
                    if (used6.contains(k)) continue;
                    if (filteredSpaces.get(k)<4) continue;
                    for (int o = k - 1; o >= 0; o--) {
                        if (used6.contains(o)) continue;
                        if (filteredSpaces.get(o) < 4) continue;

                        int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j) + filteredSpaces.get(o);

                        int minElem = Math.min(Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j)), filteredSpaces.get(o));

                        if (!used6.contains(i) && !used6.contains(j) && !used6.contains(k) && !used6.contains(o) &&
                                allSum >= (16) &&
                                allSum - minElem < (16) &&
                                (six6 > 0 && five6>1)) {

                            six6 -= 1;
                            five6-=2;
                            used6.add(i);
                            used6.add(j);
                            used6.add(k);
                            used6.add(o);
                            fours6.add(new int[]{i + 1, k + 1, j + 1, o + 1});
                            init.add(new int[]{2, 1});

                            if (six6 == 0 && five6==0) {
                                return Arrays.asList(fours6, init, new int[]{0, 0}, used6, true);
                            }
                            List<Object> trial1 = threes(filteredAllocations, filteredSpaces, new int[]{five6, six6}, 5, used6);

                            if ((boolean) trial1.get(4)) {
                                finalCombos = mergeLists((List<int[]>) trial1.get(0), fours6);
                                finalInit = mergeLists((List<int[]>) trial1.get(1), init);
                                finalUsed = (Set<Integer>) trial1.get(3);
                                return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                            }

                            for (int l = filteredSpaces.size() - 1; l >= 3; l--) {
                                if (five6 <= 2) break;
                                if (used6.contains(l)) continue;
                                if (filteredSpaces.get(l)<3) continue;
                                for (int m = l - 1; m >= 2; m--) {
                                    if (used6.contains(m)) continue;
                                    if (filteredSpaces.get(m)<4) continue;
                                    for (int n = m - 1; n >= 1; n--) {
                                        if (used6.contains(n)) continue;
                                        if (filteredSpaces.get(n)<4) continue;
                                        for (int p = n - 1; p >= 0; p--) {
                                            if (used6.contains(p)) continue;
                                            if (filteredSpaces.get(p) < 4) continue;

                                            allSum = filteredSpaces.get(m) + filteredSpaces.get(n) + filteredSpaces.get(l) + filteredSpaces.get(p);

                                            minElem = Math.min(Math.min(Math.min(filteredSpaces.get(l), filteredSpaces.get(m)), filteredSpaces.get(n)), filteredSpaces.get(p));

                                            if (!used6.contains(l) && !used6.contains(m) && !used6.contains(n) && !used6.contains(p) &&
                                                    allSum >= (15) &&
                                                    allSum - minElem < (15) &&
                                                    (five6 > 2)) {

                                                five6 -= 3;
                                                used6.add(l);
                                                used6.add(m);
                                                used6.add(n);
                                                used6.add(p);
                                                fours6.add(new int[]{l + 1, m + 1, n + 1, p + 1});
                                                init.add(new int[]{3, 0});

                                                if (six6 == 0 && five6==0) {
                                                    return Arrays.asList(fours6, init, new int[]{0, 0}, used6, true);
                                                }
                                                trial1 = threes(filteredAllocations, filteredSpaces, new int[]{five6, six6}, 5, used6);

                                                if ((boolean) trial1.get(4)) {
                                                    finalCombos = mergeLists((List<int[]>) trial1.get(0), fours6);
                                                    finalInit = mergeLists((List<int[]>) trial1.get(1), init);
                                                    finalUsed = (Set<Integer>) trial1.get(3);
                                                    return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                                                }

                                            }
                                        }
                                    }
                                }

                            }

                        }
                    }
                }
            }

        }

        six6 = shortfall[1];
        five6=shortfall[0];
        fours6 = new ArrayList<>();
        used6 = new HashSet<>(used5);
        init = new ArrayList<>();

        for (int l = filteredSpaces.size() - 1; l >= 3; l--) {
            if (five6 ==0) break;
            if (used6.contains(l)) continue;
            if (filteredSpaces.get(l)>1) break;
            for (int m = l - 1; m >= 2; m--) {
                if (used6.contains(m)) continue;
                if (filteredSpaces.get(m)>1) break;
                for (int n = m - 1; n >= 1; n--) {
                    if (used6.contains(n)) continue;
                    if (filteredSpaces.get(n)>1) break;
                    for (int p = n - 1; p >= 0; p--) {
                        if (used6.contains(p)) continue;
                        if (filteredSpaces.get(p) >2) break;
                        if (filteredSpaces.get(p) <2) continue;

                        int allSum = filteredSpaces.get(l) + filteredSpaces.get(m) + filteredSpaces.get(n) + filteredSpaces.get(p);

                        int minElem = Math.min(Math.min(Math.min(filteredSpaces.get(l), filteredSpaces.get(m)), filteredSpaces.get(n)), filteredSpaces.get(p));

                        if (!used6.contains(l) && !used6.contains(m) && !used6.contains(n) && !used6.contains(p) &&
                                allSum >= (5) &&
                                allSum - minElem < (5) &&
                                (five6 > 0)) {

                            five6 -= 1;
                            used6.add(l);
                            used6.add(m);
                            used6.add(n);
                            used6.add(p);
                            fours6.add(new int[]{l + 1, m + 1, n + 1, p + 1});
                            init.add(new int[]{1, 0});

                            if (six6 == 0 && five6==0) {
                                return Arrays.asList(fours6, init, new int[]{0, 0}, used6, true);
                            }
                            List<Object> trial1 = threes(filteredAllocations, filteredSpaces, new int[]{five6, six6}, 5, used6);

                            if ((boolean) trial1.get(4)) {
                                finalCombos = mergeLists((List<int[]>) trial1.get(0), fours6);
                                finalInit = mergeLists((List<int[]>) trial1.get(1), init);
                                finalUsed = (Set<Integer>) trial1.get(3);
                                return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                            }

                        }
                    }
                }
            }

        }

        six6 = shortfall[1];
        five6=shortfall[0];
        fours6 = new ArrayList<>();
        used6 = new HashSet<>(used5);
        init = new ArrayList<>();

        for (int i = filteredSpaces.size() - 1; i >= 3; i--) {
            if (six6 == 0) break;
            if (used6.contains(i)) continue;
            if (filteredSpaces.get(i)>1) break;
            for (int j = i - 1; j >= 2; j--) {
                if (used6.contains(j)) continue;
                if (filteredSpaces.get(j)>1) break;
                for (int k = j - 1; k >= 1; k--) {
                    if (used6.contains(k)) continue;
                    if (filteredSpaces.get(k)>2) break;
                    for (int o = k - 1; o >= 0; o--) {
                        if (used6.contains(o)) continue;
                        if (filteredSpaces.get(o) < 2) continue;
                        if (filteredSpaces.get(k)>3) break;

                        int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j) + filteredSpaces.get(o);

                        int minElem = Math.min(Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j)), filteredSpaces.get(o));

                        if (!used6.contains(i) && !used6.contains(j) && !used6.contains(k) && !used6.contains(o) &&
                                allSum >= (6) &&
                                allSum - minElem < (6) &&
                                (six6 > 0)) {

                            six6 -= 1;
                            used6.add(i);
                            used6.add(j);
                            used6.add(k);
                            used6.add(o);
                            fours6.add(new int[]{i + 1, k + 1, j + 1, o + 1});
                            init.add(new int[]{0, 1});

                            if (six6 == 0 && five6==0) {
                                return Arrays.asList(fours6, init, new int[]{0, 0}, used6, true);
                            }
                            List<Object> trial1 = threes(filteredAllocations, filteredSpaces, new int[]{five6, six6}, 5, used6);

                            if ((boolean) trial1.get(4)) {
                                finalCombos = mergeLists((List<int[]>) trial1.get(0), fours6);
                                finalInit = mergeLists((List<int[]>) trial1.get(1), init);
                                finalUsed = (Set<Integer>) trial1.get(3);
                                return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                            }

                            for (int l = filteredSpaces.size() - 1; l >= 3; l--) {
                                if (five6 ==0) break;
                                if (used6.contains(l)) continue;
                                if (filteredSpaces.get(l)>1) break;
                                for (int m = l - 1; m >= 2; m--) {
                                    if (used6.contains(m)) continue;
                                    if (filteredSpaces.get(m)>1) break;
                                    for (int n = m - 1; n >= 1; n--) {
                                        if (used6.contains(n)) continue;
                                        if (filteredSpaces.get(n)>1) break;
                                        for (int p = n - 1; p >= 0; p--) {
                                            if (used6.contains(p)) continue;
                                            if (filteredSpaces.get(p) >2) break;
                                            if (filteredSpaces.get(p) <2) continue;

                                            allSum = filteredSpaces.get(l) + filteredSpaces.get(m) + filteredSpaces.get(n) + filteredSpaces.get(p);

                                            minElem = Math.min(Math.min(Math.min(filteredSpaces.get(l), filteredSpaces.get(m)), filteredSpaces.get(n)), filteredSpaces.get(p));

                                            if (!used6.contains(l) && !used6.contains(m) && !used6.contains(n) && !used6.contains(p) &&
                                                    allSum >= (5) &&
                                                    allSum - minElem < (5) &&
                                                    (five6 > 0)) {

                                                five6 -= 1;
                                                used6.add(l);
                                                used6.add(m);
                                                used6.add(n);
                                                used6.add(p);
                                                fours6.add(new int[]{l + 1, m + 1, n + 1, p + 1});
                                                init.add(new int[]{1, 0});

                                                if (six6 == 0 && five6==0) {
                                                    return Arrays.asList(fours6, init, new int[]{0, 0}, used6, true);
                                                }
                                                trial1 = threes(filteredAllocations, filteredSpaces, new int[]{five6, six6}, 5, used6);

                                                if ((boolean) trial1.get(4)) {
                                                    finalCombos = mergeLists((List<int[]>) trial1.get(0), fours6);
                                                    finalInit = mergeLists((List<int[]>) trial1.get(1), init);
                                                    finalUsed = (Set<Integer>) trial1.get(3);
                                                    return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                                                }

                                            }
                                        }
                                    }
                                }

                            }

                        }
                    }
                }
            }

        }

        six6 = shortfall[1];
        five6=shortfall[0];
        fours6 = new ArrayList<>();
        used6 = new HashSet<>(used5);
        init = new ArrayList<>();

        for (int i = filteredSpaces.size() - 1; i >= 3; i--) {
            if (six6 <= 1) break;
            if (used6.contains(i)) continue;
            if (filteredSpaces.get(i)>1) break;
            for (int j = i - 1; j >= 2; j--) {
                if (used6.contains(j)) continue;
                if (filteredSpaces.get(j)<2) continue;
                if (filteredSpaces.get(j)>3) break;
                for (int k = j - 1; k >= 1; k--) {
                    if (used6.contains(k)) continue;
                    if (filteredSpaces.get(k)<3) continue;
                    for (int o = k - 1; o >= 0; o--) {
                        if (used6.contains(o)) continue;
                        if (filteredSpaces.get(o) < 3) continue;

                        int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j) + filteredSpaces.get(o);

                        int minElem = Math.min(Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j)), filteredSpaces.get(o));

                        if (!used6.contains(i) && !used6.contains(j) && !used6.contains(k) && !used6.contains(o) &&
                                allSum >= (12) &&
                                allSum - minElem < (12) &&
                                (six6 > 1)) {

                            six6 -= 2;
                            used6.add(i);
                            used6.add(j);
                            used6.add(k);
                            used6.add(o);
                            fours6.add(new int[]{i + 1, k + 1, j + 1, o + 1});
                            init.add(new int[]{0, 2});

                            if (six6 == 0 && five6==0) {
                                return Arrays.asList(fours6, init, new int[]{0, 0}, used6, true);
                            }
                            List<Object> trial1 = threes(filteredAllocations, filteredSpaces, new int[]{five6, six6}, 5, used6);

                            if ((boolean) trial1.get(4)) {
                                finalCombos = mergeLists((List<int[]>) trial1.get(0), fours6);
                                finalInit = mergeLists((List<int[]>) trial1.get(1), init);
                                finalUsed = (Set<Integer>) trial1.get(3);
                                return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                            }

                        }
                    }
                }
            }

        }

        six6 = shortfall[1];
        fours6 = new ArrayList<>();
        used6 = new HashSet<>(used5);
        init = new ArrayList<>();

        for (int i = filteredSpaces.size() - 1; i >= 3; i--) {
            if (five6 <= 1) break;
            if (used6.contains(i)) continue;
            if (filteredSpaces.get(i)>2) break;
            if (filteredSpaces.get(i)<2) continue;
            for (int j = i - 1; j >= 2; j--) {
                if (used6.contains(j)) continue;
                if (filteredSpaces.get(j)<2) continue;
                if (filteredSpaces.get(j)>2) break;
                for (int k = j - 1; k >= 1; k--) {
                    if (used6.contains(k)) continue;
                    if (filteredSpaces.get(k)<2) continue;
                    if (filteredSpaces.get(k)>2) break;
                    for (int o = k - 1; o >= 0; o--) {
                        if (used6.contains(o)) continue;
                        if (filteredSpaces.get(o) < 4) continue;

                        int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j) + filteredSpaces.get(o);

                        int minElem = Math.min(Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j)), filteredSpaces.get(o));

                        if (!used6.contains(i) && !used6.contains(j) && !used6.contains(k) && !used6.contains(o) &&
                                allSum >= (10) &&
                                allSum - minElem < (10) &&
                                (five6 > 1)) {

                            five6 -= 2;
                            used6.add(i);
                            used6.add(j);
                            used6.add(k);
                            used6.add(o);
                            fours6.add(new int[]{i + 1, k + 1, j + 1, o + 1});
                            init.add(new int[]{2, 0});

                            if (six6 == 0 && five6==0) {
                                return Arrays.asList(fours6, init, new int[]{0, 0}, used6, true);
                            }
                            List<Object> trial1 = threes(filteredAllocations, filteredSpaces, new int[]{five6, six6}, 5, used6);

                            if ((boolean) trial1.get(4)) {
                                finalCombos = mergeLists((List<int[]>) trial1.get(0), fours6);
                                finalInit = mergeLists((List<int[]>) trial1.get(1), init);
                                finalUsed = (Set<Integer>) trial1.get(3);
                                return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                            }

                        }
                    }
                }
            }

        }

        return Arrays.asList(fours6, init, new int[]{five6, six6}, used6, false);
    }

    public static List<Object> fives(List<int[]> allocations, List<Integer> spaces, int[] shortfall, int backupSize, Set<Integer> used5) {
        if (used5 == null) {
            used5 = new HashSet<>();
        }

        if (sum1(spaces) < backupSize * shortfall[0] + 6 * shortfall[1]) {
            return Arrays.asList(new ArrayList<>(), new ArrayList<>(), shortfall, used5, false);
        }

        List<int[]> filteredAllocations = new ArrayList<>();
        List<Integer> filteredSpaces = new ArrayList<>();

        List<int[]> finalCombos = new ArrayList<>();
        List<int[]> finalInit = new ArrayList<>();
        Set<Integer> finalUsed = new HashSet<>();

        for (int i = 0; i < spaces.size(); i++) {
            if (spaces.get(i) != 0) {
                filteredAllocations.add(allocations.get(i));
                filteredSpaces.add(spaces.get(i));
            }
        }
        int six6 = shortfall[1];
        int five6 = shortfall[0];
        List<int[]> fives6 = new ArrayList<>();
        Set<Integer> used6 = new HashSet<>(used5);
        List<int[]> init = new ArrayList<>();

        List<Object> trial = fours(filteredAllocations, filteredSpaces, new int[]{five6, six6}, backupSize, used6);
        if (((boolean) trial.get(4))) {
            finalCombos = (List<int[]>) trial.get(0);
            finalInit = (List<int[]>) trial.get(1);
            finalUsed = (Set<Integer>) trial.get(3);
            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
        }

        int six9 = shortfall[1];
        int five9= shortfall[0];
        List<int[]> fives9 = new ArrayList<>();
        Set<Integer> used9 = new HashSet<>(used5);
        List<int[]> init3 = new ArrayList<>();

        for (int i = filteredSpaces.size() - 1; i >= 4; i--) {
            if (six9 == 0) break;
            if (used9.contains(i)) continue;
            if (filteredSpaces.get(i)>1) break;
            for (int j = i - 1; j >= 3; j--) {
                if (used9.contains(j)) continue;
                if (filteredSpaces.get(j)>1) break;
                for (int k = j - 1; k >= 2; k--) {
                    if (used9.contains(k)) continue;
                    if (filteredSpaces.get(k)>1) break;
                    for (int o = k - 1; o >= 1; o--) {
                        if (used9.contains(o)) continue;
                        if (filteredSpaces.get(o)>1) break;
                        for (int q = o - 1; q >= 0; q--) {
                            if (used9.contains(q)) continue;
                            if (filteredSpaces.get(q)>2) break;
                            if (filteredSpaces.get(q)<2) continue;

                            int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j) + filteredSpaces.get(o) + filteredSpaces.get(q);
                            int minElem = Math.min(Math.min(Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j)), filteredSpaces.get(o)), filteredSpaces.get(q));

                            if (!used9.contains(i) && !used9.contains(j) && !used9.contains(k) && !used9.contains(o) && !used9.contains(q) &&
                                    allSum >= (6) &&
                                    allSum - minElem < (6) &&
                                    (six9 > 0)) {

                                six9 -= 1;
                                used9.add(i);
                                used9.add(j);
                                used9.add(k);
                                used9.add(o);
                                used9.add(q);
                                fives9.add(new int[]{i + 1, k + 1, j + 1, o + 1, q+1});
                                init3.add(new int[]{0, 1});

                                if (six9 == 0 && five9==0) {
                                    return Arrays.asList(fives9, init3, new int[]{0, 0}, used9, true);
                                }

                                List<Object> trial1 = fours(filteredAllocations, filteredSpaces, new int[]{five9, six9}, 5, used9);

                                if ((boolean) trial1.get(4)) {
                                    finalCombos = mergeLists((List<int[]>) trial1.get(0), fives9);
                                    finalInit = mergeLists((List<int[]>) trial1.get(1), init3);
                                    finalUsed = (Set<Integer>) trial1.get(3);
                                    return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                                }

                                for (int a = filteredSpaces.size() - 1; a >= 4; a--) {
                                    if (five9 == 0) break;
                                    if (used9.contains(a)) continue;
                                    if (filteredSpaces.get(a)>1) break;
                                    for (int b = a - 1; b >= 3; b--) {
                                        if (used9.contains(b)) continue;
                                        if (filteredSpaces.get(b)>1) break;
                                        for (int c = b - 1; c >= 2; c--) {
                                            if (used9.contains(c)) continue;
                                            if (filteredSpaces.get(c)>1) break;
                                            for (int d = c - 1; d >= 1; d--) {
                                                if (used9.contains(d)) continue;
                                                if (filteredSpaces.get(d)>1) break;
                                                for (int e = d - 1; e >= 0; e--) {
                                                    if (used9.contains(e)) continue;
                                                    if (filteredSpaces.get(e)>1) break;

                                                    allSum = filteredSpaces.get(a) + filteredSpaces.get(b) + filteredSpaces.get(c) + filteredSpaces.get(d) + filteredSpaces.get(e);
                                                    minElem = Math.min(Math.min(Math.min(Math.min(filteredSpaces.get(a), filteredSpaces.get(b)), filteredSpaces.get(c)), filteredSpaces.get(d)), filteredSpaces.get(e));

                                                    if (!used9.contains(a) && !used9.contains(b) && !used9.contains(c) && !used9.contains(d) && !used9.contains(e) &&
                                                            allSum >= (5) &&
                                                            allSum - minElem < (5) &&
                                                            (five9 > 0)) {

                                                        five9 -= 1;
                                                        used9.add(a);
                                                        used9.add(b);
                                                        used9.add(c);
                                                        used9.add(d);
                                                        used9.add(e);
                                                        fives9.add(new int[]{a + 1, b + 1, c + 1, d + 1, e+1});
                                                        init3.add(new int[]{1, 0});

                                                        if (six9 == 0 && five9==0) {
                                                            return Arrays.asList(fives9, init3, new int[]{0, 0}, used9, true);
                                                        }

                                                        trial1 = fours(filteredAllocations, filteredSpaces, new int[]{five9, six9}, 5, used9);

                                                        if ((boolean) trial1.get(4)) {
                                                            finalCombos = mergeLists((List<int[]>) trial1.get(0), fives9);
                                                            finalInit = mergeLists((List<int[]>) trial1.get(1), init3);
                                                            finalUsed = (Set<Integer>) trial1.get(3);
                                                            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                                                        }

                                                    }
                                                }
                                            }
                                        }
                                    }
                                }



                            }
                        }
                    }
                }
            }
        }

        six9 = shortfall[1];
        five9= shortfall[0];
        fives9 = new ArrayList<>();
        used9 = new HashSet<>(used5);
        init3 = new ArrayList<>();


        for (int a = filteredSpaces.size() - 1; a >= 4; a--) {
            if (five9 == 0) break;
            if (used9.contains(a)) continue;
            if (filteredSpaces.get(a)>1) break;
            for (int b = a - 1; b >= 3; b--) {
                if (used9.contains(b)) continue;
                if (filteredSpaces.get(b)>1) break;
                for (int c = b - 1; c >= 2; c--) {
                    if (used9.contains(c)) continue;
                    if (filteredSpaces.get(c)>1) break;
                    for (int d = c - 1; d >= 1; d--) {
                        if (used9.contains(d)) continue;
                        if (filteredSpaces.get(d)>1) break;
                        for (int e = d - 1; e >= 0; e--) {
                            if (used9.contains(e)) continue;
                            if (filteredSpaces.get(e)>1) break;

                            int allSum = filteredSpaces.get(a) + filteredSpaces.get(b) + filteredSpaces.get(c) + filteredSpaces.get(d) + filteredSpaces.get(e);
                            int minElem = Math.min(Math.min(Math.min(Math.min(filteredSpaces.get(a), filteredSpaces.get(b)), filteredSpaces.get(c)), filteredSpaces.get(d)), filteredSpaces.get(e));

                            if (!used9.contains(a) && !used9.contains(b) && !used9.contains(c) && !used9.contains(d) && !used9.contains(e) &&
                                    allSum >= (5) &&
                                    allSum - minElem < (5) &&
                                    (five9 > 0)) {

                                five9 -= 1;
                                used9.add(a);
                                used9.add(b);
                                used9.add(c);
                                used9.add(d);
                                used9.add(e);
                                fives9.add(new int[]{a + 1, b + 1, c + 1, d + 1, e+1});
                                init3.add(new int[]{1, 0});

                                if (six9 == 0 && five9==0) {
                                    return Arrays.asList(fives9, init3, new int[]{0, 0}, used9, true);
                                }

                                List<Object> trial1 = fours(filteredAllocations, filteredSpaces, new int[]{five9, six9}, 5, used9);

                                if ((boolean) trial1.get(4)) {
                                    finalCombos = mergeLists((List<int[]>) trial1.get(0), fives9);
                                    finalInit = mergeLists((List<int[]>) trial1.get(1), init3);
                                    finalUsed = (Set<Integer>) trial1.get(3);
                                    return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                                }

                            }
                        }
                    }
                }
            }
        }


        six6 = shortfall[1];
        five6 = shortfall[0];
        fives6 = new ArrayList<>();
        used6 = new HashSet<>(used5);
        init = new ArrayList<>();

        for (int i = filteredSpaces.size() - 1; i >= 4; i--) {
            if (five6 <= 3) break;
            if (used6.contains(i)) continue;
            if (filteredSpaces.get(i)<4) continue;
            for (int j = i - 1; j >= 3; j--) {
                if (used6.contains(j)) continue;
                if (filteredSpaces.get(j)<4) continue;
                for (int k = j - 1; k >= 2; k--) {
                    if (used6.contains(k)) continue;
                    if (filteredSpaces.get(k)<4) continue;
                    for (int o = k - 1; o >= 1; o--) {
                        if (used6.contains(o)) continue;
                        if (filteredSpaces.get(o)<4) continue;
                        for (int q = o - 1; q >= 0; q--) {
                            if (used6.contains(q)) continue;
                            if (filteredSpaces.get(q)<4) continue;

                            int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j) + filteredSpaces.get(o) + filteredSpaces.get(q);
                            int minElem = Math.min(Math.min(Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j)), filteredSpaces.get(o)), filteredSpaces.get(q));

                            if (!used6.contains(i) && !used6.contains(j) && !used6.contains(k) && !used6.contains(o) && !used6.contains(q) &&
                                    allSum >= (20) &&
                                    allSum - minElem < (20) &&
                                    (five6 > 3)) {

                                five6 -= 4;
                                used6.add(i);
                                used6.add(j);
                                used6.add(k);
                                used6.add(o);
                                used6.add(q);
                                fives6.add(new int[]{i + 1, k + 1, j + 1, o + 1, q + 1});
                                init.add(new int[]{4, 0});

                                if (six6 == 0 && five6==0) {
                                    return Arrays.asList(fives6, init, new int[]{0, 0}, used6, true);
                                }
                                List<Object> trial1 = fours(filteredAllocations, filteredSpaces, new int[]{five6, six6}, 5, used6);

                                if ((boolean) trial1.get(4)) {
                                    finalCombos = mergeLists((List<int[]>) trial1.get(0), fives6);
                                    finalInit = mergeLists((List<int[]>) trial1.get(1), init);
                                    finalUsed = (Set<Integer>) trial1.get(3);
                                    return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                                }
                            }
                        }
                    }

                }
            }
        }

        return Arrays.asList(fives6, init, new int[]{five6, six6}, used6, false);
    }

    public static List<Object> sixes(List<int[]> allocations, List<Integer> spaces, int[] shortfall, int backupSize, Set<Integer> used5) {
        if (used5 == null) {
            used5 = new HashSet<>();
        }

        if (sum1(spaces) < backupSize * shortfall[0] + 6 * shortfall[1]) {
            return Arrays.asList(new ArrayList<>(), new ArrayList<>(), shortfall, used5, false);
        }

        List<int[]> filteredAllocations = new ArrayList<>();
        List<Integer> filteredSpaces = new ArrayList<>();

        List<int[]> finalCombos = new ArrayList<>();
        List<int[]> finalInit = new ArrayList<>();
        Set<Integer> finalUsed = new HashSet<>();

        for (int i = 0; i < spaces.size(); i++) {
            if (spaces.get(i) != 0) {
                filteredAllocations.add(allocations.get(i));
                filteredSpaces.add(spaces.get(i));
            }
        }
        int six6 = shortfall[1];
        int five6= shortfall[0];
        List<int[]> sixes6 = new ArrayList<>();
        Set<Integer> used6 = new HashSet<>(used5);
        List<int[]> init = new ArrayList<>();

        List<Object> trial = fives(filteredAllocations, filteredSpaces, new int[]{five6, six6}, backupSize, used6);
        if (((boolean) trial.get(4))) {
            finalCombos = (List<int[]>) trial.get(0);
            finalInit = (List<int[]>) trial.get(1);
            finalUsed = (Set<Integer>) trial.get(3);
            return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
        }


        int six9 = shortfall[1];
        int five9= shortfall[0];
        List<int[]> sixes9 = new ArrayList<>();
        Set<Integer> used9 = new HashSet<>(used5);
        List<int[]> init3 = new ArrayList<>();

        for (int i = filteredSpaces.size() - 1; i >= 5; i--) {
            if (six9 == 0) break;
            if (filteredSpaces.get(i)>1) break;
            if (used9.contains(i)) continue;
            for (int j = i - 1; j >= 4; j--) {
                if (filteredSpaces.get(j)>1) break;
                if (used9.contains(j)) continue;
                for (int k = j - 1; k >= 3; k--) {
                    if (filteredSpaces.get(k)>1) break;
                    if (used9.contains(k)) continue;
                    for (int o = k - 1; o >= 2; o--) {
                        if (filteredSpaces.get(o)>1) break;
                        if (used9.contains(o)) continue;
                        for (int q = o - 1; q >= 1; q--) {
                            if (filteredSpaces.get(q)>1) break;
                            if (used9.contains(q)) continue;
                            for (int s = q - 1; s >= 0; s--) {
                                if (filteredSpaces.get(s)>1) break;
                                if (used9.contains(s)) continue;

                                int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j) + filteredSpaces.get(o) + filteredSpaces.get(q) + filteredSpaces.get(s);
                                int minElem = Math.min(Math.min(Math.min(Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j)), filteredSpaces.get(o)), filteredSpaces.get(q)), filteredSpaces.get(s));

                                if (!used9.contains(i) && !used9.contains(j) && !used9.contains(k) && !used9.contains(o) && !used9.contains(q) && !used9.contains(s) &&
                                        allSum >= (6) &&
                                        allSum - minElem < (6) &&
                                        (six9 > 0)) {

                                    six9 -= 1;
                                    used9.add(i);
                                    used9.add(j);
                                    used9.add(k);
                                    used9.add(o);
                                    used9.add(q);
                                    used9.add(s);
                                    sixes9.add(new int[]{i + 1, k + 1, j + 1, o + 1, q + 1, s +1});
                                    init3.add(new int[]{0, 1});

                                    if (six9 == 0 && five9==0) {
                                        return Arrays.asList(sixes9, init3, new int[]{0, 0}, used9, true);
                                    }

                                    List<Object> trial1 = fives(filteredAllocations, filteredSpaces, new int[]{five9, six9}, 5, used9);

                                    if ((boolean) trial1.get(4)) {
                                        finalCombos = mergeLists((List<int[]>) trial1.get(0), sixes9);
                                        finalInit = mergeLists((List<int[]>) trial1.get(1), init3);
                                        finalUsed = (Set<Integer>) trial1.get(3);
                                        return Arrays.asList(finalCombos, finalInit, new int[]{0, 0}, finalUsed, true);
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }

        return Arrays.asList(new ArrayList<>(), new ArrayList<>(), new int[]{five9, six9}, used9, false);
    }

    public static List<List<Object>> optimize(List<List<Integer>> sortedAllocations, List<int[]> allocations, int backupSize, List<List<Integer>> outCombos, List<Integer> spaces) {
        List<List<Integer>> combos = new ArrayList<>();
        for (List<Integer> item : outCombos) {
            List<Integer> combos1 = new ArrayList<>();
            for (Integer elem : item) {
                combos1.add(elem - 1);
            }
            combos.add(combos1);
        }
        List<List<Integer>> indexCombos = new ArrayList<>(combos);

        List<Integer> weights = new ArrayList<>();
        for (List<Integer> item : sortedAllocations) {
            int sum = item.stream().mapToInt(Integer::intValue).sum();
            weights.add(sum);
        }

        boolean progress = true;
        while (progress) {
            progress = false;
            for (int i = 0; i < indexCombos.size() - 1; i++) {
                for (int j = i + 1; j < indexCombos.size(); j++) {
                    List<Integer> combo1 = new ArrayList<>(indexCombos.get(i));
                    List<Integer> combo2 = new ArrayList<>(indexCombos.get(j));

                    if (combo1.size() == 1 && combo2.size() == 1) {
                        continue;
                    }

                    int weight1Before = totalWeight(combo1, weights);
                    int weight2Before = totalWeight(combo2, weights);

                    for (Integer idx1 : combo1) {
                        for (Integer idx2 : combo2) {
                            List<Integer> newCombo1 = new ArrayList<>(combo1);
                            List<Integer> newCombo2 = new ArrayList<>(combo2);

                            newCombo1.set(newCombo1.indexOf(idx1), idx2);
                            newCombo2.set(newCombo2.indexOf(idx2), idx1);

                            int weight1After = totalWeight(newCombo1, weights);
                            int weight2After = totalWeight(newCombo2, weights);

                            int space1After = totalSpace(newCombo1, spaces);
                            int space2After = totalSpace(newCombo2, spaces);

                            int minSpace1 = totalAllocationThreshold(i, allocations, backupSize);
                            int minSpace2 = totalAllocationThreshold(j, allocations, backupSize);


                            if (combo1.size() == 1 || combo2.size() == 1) {
                                if ((combo1.size() == 1 && weight2After < weight2Before) ||
                                        (combo2.size() == 1 && weight1After < weight1Before)) {
                                    if (space1After >= minSpace1 && space2After >= minSpace2) {
                                        indexCombos.set(i, newCombo1);
                                        indexCombos.set(j, newCombo2);
                                        progress = true;
                                        break;
                                    }
                                }

                            } else if (Math.max(weight1After, weight2After) < Math.max(weight1Before, weight2Before) && space2After+space1After>=minSpace2+minSpace1) {

                                int[] totalCrewNeed = {allocations.get(i)[0] + allocations.get(j)[0], allocations.get(i)[1] + allocations.get(j)[1]};
                                List<int[]> unique = uniquePairs(allocations.get(i)[0] + allocations.get(i)[1], totalCrewNeed);

                                for (int[] item : unique) {
                                    int[] otherSide={totalCrewNeed[0]-item[0], totalCrewNeed[1]-item[1]};

                                    if ((item[0]*backupSize + item[1]*6 <=space1After) && (backupSize*otherSide[0] + 6*otherSide[1]<=space2After)) {
                                        allocations.set(i,item);
                                        allocations.set(j,otherSide);
                                        indexCombos.set(i, newCombo1);
                                        indexCombos.set(j, newCombo2);
                                        progress = true;
                                        break;
                                    }
                                }
                            }
                            if (progress) break;
                        }
                        if (progress) break;
                    }
                    if (progress) break;
                }
                if (progress) break;
            }
        }

        boolean secondaryProgress = true;
        while (secondaryProgress) {
            secondaryProgress = false;
            for (int i = 0; i < indexCombos.size(); i++) {
                for (int j = 0; j < indexCombos.size(); j++) {
                    if (i == j) continue;
                    List<Integer> combo1 = indexCombos.get(i);
                    List<Integer> combo2 = indexCombos.get(j);

                    if (combo1.size() >=3 && combo2.size() < combo1.size() && combo2.size()>1) {
                        int weight1 = totalWeight(combo1, weights);
                        int weight2 = totalWeight(combo2, weights);
                        int weightDiff= weight1 - weight2;

                        if (weight1 > weight2) {
                            for (Integer idx1 : combo1) {
                                for (Integer idx2 : combo2) {
                                    List<Integer> newCombo1 = new ArrayList<>(combo1);
                                    List<Integer> newCombo2 = new ArrayList<>(combo2);

                                    newCombo1.set(newCombo1.indexOf(idx1), idx2);
                                    newCombo2.set(newCombo2.indexOf(idx2), idx1);

                                    int newWeight1 = totalWeight(newCombo1, weights);
                                    int newWeight2 = totalWeight(newCombo2, weights);

                                    int space1After = totalSpace(newCombo1, spaces);
                                    int space2After = totalSpace(newCombo2, spaces);

                                    int minSpace1 = totalAllocationThreshold(i, allocations, backupSize);
                                    int minSpace2 = totalAllocationThreshold(j, allocations, backupSize);

                                    if (newWeight2 == newWeight1 + weightDiff && space1After+space2After>=minSpace2+minSpace1) {
                                        int[] totalCrewNeed= {allocations.get(i)[0]+allocations.get(j)[0],allocations.get(i)[1]+allocations.get(j)[1]};
                                        List<int[]> unique=uniquePairs(allocations.get(i)[0]+allocations.get(i)[1],totalCrewNeed);

                                        for (int[] item : unique) {
                                            int[] otherSide={totalCrewNeed[0]-item[0], totalCrewNeed[1]-item[1]};
                                            if (item[0]*backupSize + item[1]*6<=space1After && backupSize*otherSide[0] + 6*otherSide[1]<=space2After) {
                                                allocations.set(i,item);
                                                allocations.set(j,otherSide);
                                                indexCombos.set(i, newCombo1);
                                                indexCombos.set(j, newCombo2);
                                                secondaryProgress = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (secondaryProgress) break;
                                }
                                if (secondaryProgress) break;
                            }
                        }
                    }
                    else if (combo1.size() == 1 && combo2.size() > 1) {
                        int singleIdx = combo1.get(0);

                        for (Integer idx2 : combo2) {
                            if (Objects.equals(weights.get(singleIdx), weights.get(idx2)) &&
                                    spaces.get(singleIdx) > spaces.get(idx2)) {

                                List<Integer> newCombo1 = new ArrayList<>();
                                newCombo1.add(idx2);

                                List<Integer> newCombo2 = new ArrayList<>(combo2);
                                newCombo2.set(newCombo2.indexOf(idx2), singleIdx);

                                indexCombos.set(i, newCombo1);
                                indexCombos.set(j, newCombo2);
                                secondaryProgress = true;

                                break;

                            }
                        }

                    }
                    if (secondaryProgress) break;
                }
                if (secondaryProgress) break;
            }
        }
        List<List<Integer>> result = new ArrayList<>();
        for (List<Integer> combo : indexCombos) {
            if (combo.size() > 1) {
                List<Integer> adjustedCombo = new ArrayList<>();
                for (Integer idx : combo) {
                    adjustedCombo.add(idx + 1);
                }
                result.add(adjustedCombo);
            }
        }
        List<List<Integer>> allocationsList = new ArrayList<>();
        for (int[] alloc : allocations) {
            List<Integer> allocList = new ArrayList<>();
            for (int val : alloc) {
                allocList.add(val);
            }
            allocationsList.add(allocList);
        }

        List<List<Object>> combinedResult = new ArrayList<>();
        combinedResult.add((List<Object>) (List<?>) result);  // Cast required for Java generics
        combinedResult.add((List<Object>) (List<?>) allocationsList);

        return combinedResult;
    }

    public static List<int[]> uniquePairs(int sumTotal, int[] maxVals) {
            List<int[]> result = new ArrayList<>();
            for (int a = 0; a <= maxVals[0] && a <= sumTotal; a++) {
                int b = sumTotal - a;
                if (b <= maxVals[1]) {
                    result.add(new int[]{a, b});
                }
            }
            return result;
        }

    private static int totalWeight(List<Integer> combo, List<Integer> weights) {
        return combo.stream().mapToInt(weights::get).sum();
    }

    private static int totalSpace(List<Integer> combo, List<Integer> spaces) {
        return combo.stream().mapToInt(spaces::get).sum();
    }

    private static int totalAllocationThreshold(int idx, List<int[]> allocations, int backupSize) {
        return allocations.get(idx)[0] * backupSize + allocations.get(idx)[1] * 6;
    }

    private static int sum(int[] arr) {
        int sum = 0;
        for (int num : arr) {
            sum += num;
        }
        return sum;
    }

    public static int sum1(List<Integer> numbers) {
        int total = 0;
        for (int num : numbers) {
            total += num;
        }
        return total;

    }
    public static Set<Integer> mergeSets(Set<Integer> set1, Set<Integer> set2) {
        Set<Integer> merged = new HashSet<>(set1);
        merged.addAll(set2);
        return merged;
    }

    private static List<int[]> mergeLists(List<int[]> list1, List<int[]> list2) {
        List<int[]> merged = new ArrayList<>(list1);
        merged.addAll(list2);
        return merged;
    }


    public static void main(String[] args) {
        Combine app = new Combine();
        GatewayServer server = new GatewayServer(app);
        server.start();
        System.out.println("Gateway Server Started");

    }

    private static String listToString(List<int[]> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int[] arr : list) {
            sb.append(Arrays.toString(arr)).append(", ");
        }
        if (!list.isEmpty()) sb.setLength(sb.length() - 2); // Remove last comma
        sb.append("]");
        return sb.toString();
    }

    private static String trialToString(List<Object> trial) {
        if (trial.isEmpty()) return "[]";
        return "[Combos: " + listToString((List<int[]>) trial.get(0)) + ", Init: " + listToString((List<int[]>) trial.get(1)) + "]";
    }

    private static String formatList(List<int[]> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int[] arr : list) {
            sb.append(Arrays.toString(arr)).append(", ");
        }
        if (!list.isEmpty()) sb.setLength(sb.length() - 2); // Remove last comma
        sb.append("]");
        return sb.toString();
    }

    public static String formatNestedList(List<List<Integer>> nestedList) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (List<Integer> innerList : nestedList) {
            sb.append("  ").append(innerList.toString()).append(",\n");
        }
        if (!nestedList.isEmpty()) {
            sb.setLength(sb.length() - 2); // Remove last comma and newline
        }
        sb.append("\n]");
        return sb.toString();
    }

}