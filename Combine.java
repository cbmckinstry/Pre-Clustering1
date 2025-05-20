import java.util.*;
import java.util.ArrayList;
import java.util.HashSet;
import py4j.GatewayServer;

public class Combine {

    public static List<Object> combine(List<int[]> allocations, List<Integer> space, int[] shortfall, int backupSize, Set<Integer> used) {
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

        if (backupSize == 7) {
            for (int m = space0.size() - 2; m >= 0; m--) {
                if (backup4 == 0) break;
                for (int n = space0.size() - 1; n > m; n--) {
                    if (backup4 == 0) break;
                    if ((space0.get(m) + space0.get(n) >= 7) && !used4.contains(m) && !used4.contains(n)) {
                        used4.add(m);
                        used4.add(n);
                        combos4.add(new int[]{m + 1, n + 1});
                        backup4--;
                        init.add(new int[]{1, 0});
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
                if (six4 == 0) break;
                for (int n = space0.size() - 1; n > m; n--) {
                    if (six4 == 0) break;
                    if ((space0.get(m) + space0.get(n) >= 6) && !used5.contains(m) && !used5.contains(n)) {
                        used5.add(m);
                        used5.add(n);
                        combos5.add(new int[]{m + 1, n + 1});
                        six4--;
                        init1.add(new int[]{0, 1});
                        if (backup4 == 0 && six4 == 0) {
                            return Arrays.asList(combos5, init1, new int[]{backup4, six4}, used5, true);
                        }
                    }
                }
            }
            return Arrays.asList(new ArrayList<>(), new ArrayList<>(), new int[]{backup4, six4}, used5, false);
        } else {
            for (int m = space0.size() - 2; m >= 0; m--) {
                if (six4 == 0) break;
                for (int n = space0.size() - 1; n > m; n--) {
                    if (six4 == 0) break;
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
                for (int n = space0.size() - 1; n > m; n--) {
                    if (backup4 == 0) break;
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
            return Arrays.asList(new ArrayList<>(), new ArrayList<>(), new int[]{backup4, six4}, used5, false);

        }
    }

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

        if (backupSize == 7) {
            for (int m = space0.size() - 2; m >= 0; m--) {
                if (backup4 == 0) break;
                for (int n = space0.size() - 1; n > m; n--) {
                    if (backup4 == 0) break;
                    if ((space0.get(m) + space0.get(n) >= 7) && !used4.contains(m) && !used4.contains(n)) {
                        used4.add(m);
                        used4.add(n);
                        combos4.add(new int[]{m + 1, n + 1});
                        backup4--;
                        init.add(new int[]{1, 0});
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
                if (six4 == 0) break;
                for (int n = space0.size() - 1; n > m; n--) {
                    if (six4 == 0) break;
                    if ((space0.get(m) + space0.get(n) >= 6) && !used5.contains(m) && !used5.contains(n)) {
                        used5.add(m);
                        used5.add(n);
                        combos5.add(new int[]{m + 1, n + 1});
                        six4--;
                        init1.add(new int[]{0, 1});
                        if (backup4 == 0 && six4 == 0) {
                            return Arrays.asList(combos5, init1, new int[]{backup4, six4}, used5, true);
                        }
                    }
                }
            }
            return Arrays.asList(combos5, init1, new int[]{backup4, six4}, used5, false);
        } else {
            for (int m = space0.size() - 2; m >= 0; m--) {
                if (six4 == 0) break;
                for (int n = space0.size() - 1; n > m; n--) {
                    if (six4 == 0) break;
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
                for (int n = space0.size() - 1; n > m; n--) {
                    if (backup4 == 0) break;
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

        int[] needed = (int[]) trial.get(2);
        int six9 = needed[1];
        List<int[]> threes9 = new ArrayList<>((List<int[]>) trial.get(0));
        Set<Integer> used9 = new HashSet<>((Set<Integer>) trial.get(3));
        List<int[]> init3 = new ArrayList<>((List<int[]>) trial.get(1));

        for (int i = filteredSpaces.size() - 1; i >= 2; i--) {
            if (six9 == 0) break;
            for (int j = i - 1; j >= 1; j--) {
                if (six9 == 0) break;
                for (int k = j - 1; k >= 0; k--) {
                    if (six9 == 0) break;
                    int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j);
                    int minElem = Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j));

                    if (!used9.contains(i) && !used9.contains(j) && !used9.contains(k) &&
                            allSum >= (Math.max(backupSize, 6)) &&
                            allSum - minElem < (Math.max(backupSize, 6)) &&
                            (six9 > 0)) {

                        six9 -= 1;
                        used9.add(i);
                        used9.add(j);
                        used9.add(k);
                        threes9.add(new int[]{i + 1, k + 1, j + 1});
                        init3.add(new int[]{0, 1});

                        if (six9 == 0) {
                            return Arrays.asList(threes9, init3, new int[]{0, 0}, used9, true);
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
            for (int j = i - 1; j >= 1; j--) {
                if (six6 == 0) break;
                for (int k = j - 1; k >= 0; k--) {
                    if (six6 == 0) break;
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
                    }
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
                    int[] needed1 = (int[]) trial1.get(2);
                    six10 = needed1[1];
                    threes10 = new ArrayList<>(mergeLists((List<int[]>) trial1.get(0), threes6));
                    used10 = new HashSet<>((Set<Integer>) trial1.get(3));
                    init4 = new ArrayList<>(mergeLists(((List<int[]>) trial1.get(1)), init));

                    for (int l = filteredSpaces.size() - 1; l >= 2; l--) {
                        if (six10 == 0) break;
                        for (int m = l - 1; m >= 1; m--) {
                            if (six10 == 0) break;
                            for (int n = m - 1; n >= 0; n--) {
                                if (six10 == 0) break;
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

        int[] needed = (int[]) trial.get(2);
        int six9 = needed[1];
        List<int[]> fours9 = new ArrayList<>((List<int[]>) trial.get(0));
        Set<Integer> used9 = new HashSet<>((Set<Integer>) trial.get(3));
        List<int[]> init3 = new ArrayList<>((List<int[]>) trial.get(1));

        for (int i = filteredSpaces.size() - 1; i >= 3; i--) {
            if (six9 == 0) break;
            for (int j = i - 1; j >= 2; j--) {
                if (six9 == 0) break;
                for (int k = j - 1; k >= 1; k--) {
                    if (six9 == 0) break;
                    for (int o = k - 1; o >= 0; o--) {
                        if (six9 == 0) break;

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

                        }
                    }
                }
            }
        }

        int six10 = shortfall[1];
        List<int[]> fours10 = new ArrayList<>();
        Set<Integer> used10 = new HashSet<>(used5);
        List<int[]> init4 = new ArrayList<>();

        for (int i = filteredSpaces.size() - 1; i >= 3; i--) {
            if (six6 == 0) break;
            for (int j = i - 1; j >= 2; j--) {
                if (six6 == 0) break;
                for (int k = j - 1; k >= 1; k--) {
                    if (six6 == 0) break;
                    for (int o = k - 1; o >= 0; o--) {
                        if (six6 == 0) break;

                        int allSum = filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j)+filteredSpaces.get(o);
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
                            fours6.add(new int[]{i + 1, k + 1, j + 1, o+1});
                            init.add(new int[]{0, 3});
                        }
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
                        int[] needed1 = (int[]) trial1.get(2);
                        six10 = needed1[1];
                        fours10 = new ArrayList<>(mergeLists((List<int[]>) trial1.get(0), fours6));
                        used10 = new HashSet<>((Set<Integer>) trial1.get(3));
                        init4 = new ArrayList<>(mergeLists(((List<int[]>) trial1.get(1)), init));

                        for (int l = filteredSpaces.size() - 1; l >= 3; l--) {
                            if (six10 == 0) break;
                            for (int m = l - 1; m >= 2; m--) {
                                if (six10 == 0) break;
                                for (int n = m - 1; n >= 1; n--) {
                                    if (six10 == 0) break;
                                    for (int p = n - 1; p >= 0; p--) {
                                        if (six10 == 0) break;

                                        int allSum1 = filteredSpaces.get(l) + filteredSpaces.get(m) + filteredSpaces.get(n)+filteredSpaces.get(p);
                                        int minElem1 = Math.min(Math.min(Math.min(filteredSpaces.get(l), filteredSpaces.get(m)), filteredSpaces.get(n)), filteredSpaces.get(p));

                                        if (!used10.contains(l) && !used10.contains(m) && !used10.contains(n) && !used10.contains(p) &&
                                                allSum1 >= 6 &&
                                                allSum1 - minElem1 < 6 &&
                                                (six10 > 0)) {

                                            six10 -= 1;
                                            used10.add(l);
                                            used10.add(m);
                                            used10.add(n);
                                            used10.add(p);
                                            fours10.add(new int[]{l + 1, m + 1, n + 1, p+1});
                                            init4.add(new int[]{0, 1});

                                            if (six10 == 0) {
                                                return Arrays.asList(fours10, init4, new int[]{0, 0}, used10, true);
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

        return Arrays.asList(fours10, init4, new int[]{0, six10}, used10, false);
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

        int[] needed = (int[]) trial.get(2);
        int six9 = needed[1];
        List<int[]> fives9 = new ArrayList<>((List<int[]>) trial.get(0));
        Set<Integer> used9 = new HashSet<>((Set<Integer>) trial.get(3));
        List<int[]> init3 = new ArrayList<>((List<int[]>) trial.get(1));

        for (int i = filteredSpaces.size() - 1; i >= 4; i--) {
            if (six9 == 0) break;
            for (int j = i - 1; j >= 3; j--) {
                if (six9 == 0) break;
                for (int k = j - 1; k >= 2; k--) {
                    if (six9 == 0) break;
                    for (int o = k - 1; o >= 1; o--) {
                        if (six9 == 0) break;
                        for (int q = o - 1; q >= 0; q--) {
                            if (six9 == 0) break;

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

                            }
                        }
                    }
                }
            }
        }

        int six10 = shortfall[1];
        List<int[]> fives10 = new ArrayList<>();
        Set<Integer> used10 = new HashSet<>(used5);
        List<int[]> init4 = new ArrayList<>();

        for (int i = filteredSpaces.size() - 1; i >= 4; i--) {
            if (six6 == 0) break;
            for (int j = i - 1; j >= 3; j--) {
                if (six6 == 0) break;
                for (int k = j - 1; k >= 2; k--) {
                    if (six6 == 0) break;
                    for (int o = k - 1; o >= 1; o--) {
                        if (six6 == 0) break;
                        for (int q = o - 1; q >= 0; q--) {
                            if (six6 == 0) break;

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
                            }
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
                            int[] needed1 = (int[]) trial1.get(2);
                            six10 = needed1[1];
                            fives10 = new ArrayList<>(mergeLists((List<int[]>) trial1.get(0), fives6));
                            used10 = new HashSet<>((Set<Integer>) trial1.get(3));
                            init4 = new ArrayList<>(mergeLists(((List<int[]>) trial1.get(1)), init));

                            for (int l = filteredSpaces.size() - 1; l >= 4; l--) {
                                if (six10 == 0) break;
                                for (int m = l - 1; m >= 3; m--) {
                                    if (six10 == 0) break;
                                    for (int n = m - 1; n >= 2; n--) {
                                        if (six10 == 0) break;
                                        for (int p = n - 1; p >= 1; p--) {
                                            if (six10 == 0) break;
                                            for (int r = p - 1; r >= 0; r--) {
                                                if (six10 == 0) break;


                                                int allSum1 = filteredSpaces.get(l) + filteredSpaces.get(m) + filteredSpaces.get(n) + filteredSpaces.get(p) + filteredSpaces.get(r);
                                                int minElem1 = Math.min(Math.min(Math.min(Math.min(filteredSpaces.get(l), filteredSpaces.get(m)), filteredSpaces.get(n)), filteredSpaces.get(p)), filteredSpaces.get(r));

                                                if (!used10.contains(l) && !used10.contains(m) && !used10.contains(n) && !used10.contains(p) && !used10.contains(r) &&
                                                        allSum1 >= 6 &&
                                                        allSum1 - minElem1 < 6 &&
                                                        (six10 > 0)) {

                                                    six10 -= 1;
                                                    used10.add(l);
                                                    used10.add(m);
                                                    used10.add(n);
                                                    used10.add(p);
                                                    used10.add(r);
                                                    fives10.add(new int[]{l + 1, m + 1, n + 1, p + 1, r+1});
                                                    init4.add(new int[]{0, 1});

                                                    if (six10 == 0) {
                                                        return Arrays.asList(fives10, init4, new int[]{0, 0}, used10, true);
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

        return Arrays.asList(fives10, init4, new int[]{0, six10}, used10, false);
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

        int[] needed = (int[]) trial.get(2);
        int six9 = needed[1];
        List<int[]> sixes9 = new ArrayList<>((List<int[]>) trial.get(0));
        Set<Integer> used9 = new HashSet<>((Set<Integer>) trial.get(3));
        List<int[]> init3 = new ArrayList<>((List<int[]>) trial.get(1));

        for (int i = filteredSpaces.size() - 1; i >= 5; i--) {
            if (six9 == 0) break;
            for (int j = i - 1; j >= 4; j--) {
                if (six9 == 0) break;
                for (int k = j - 1; k >= 3; k--) {
                    if (six9 == 0) break;
                    for (int o = k - 1; o >= 2; o--) {
                        if (six9 == 0) break;
                        for (int q = o - 1; q >= 1; q--) {
                            if (six9 == 0) break;
                            for (int s = q - 1; s >= 0; s--) {
                                if (six9 == 0) break;

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

                                }
                            }
                        }
                    }
                }
            }
        }

        int six10 = shortfall[1];
        List<int[]> sixes10 = new ArrayList<>();
        Set<Integer> used10 = new HashSet<>(used5);
        List<int[]> init4 = new ArrayList<>();

        for (int i = filteredSpaces.size() - 1; i >= 5; i--) {
            if (six6 == 0) break;
            for (int j = i - 1; j >= 4; j--) {
                if (six6 == 0) break;
                for (int k = j - 1; k >= 3; k--) {
                    if (six6 == 0) break;
                    for (int o = k - 1; o >= 2; o--) {
                        if (six6 == 0) break;
                        for (int q = o - 1; q >= 1; q--) {
                            if (six6 == 0) break;
                            for (int s = q - 1; s >= 0; s--) {
                                if (six6 == 0) break;

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
                                    sixes6.add(new int[]{i + 1, k + 1, j + 1, o + 1, q + 1, s +1});
                                    init.add(new int[]{0, 5});
                                }
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
                                int[] needed1 = (int[]) trial1.get(2);
                                six10 = needed1[1];
                                sixes10 = new ArrayList<>(mergeLists((List<int[]>) trial1.get(0), sixes6));
                                used10 = new HashSet<>((Set<Integer>) trial1.get(3));
                                init4 = new ArrayList<>(mergeLists(((List<int[]>) trial1.get(1)), init));

                                for (int l = filteredSpaces.size() - 1; l >= 5; l--) {
                                    if (six10 == 0) break;
                                    for (int m = l - 1; m >= 4; m--) {
                                        if (six10 == 0) break;
                                        for (int n = m - 1; n >= 3; n--) {
                                            if (six10 == 0) break;
                                            for (int p = n - 1; p >= 2; p--) {
                                                if (six10 == 0) break;
                                                for (int r = p - 1; r >= 1; r--) {
                                                    if (six10 == 0) break;
                                                    for (int t = r - 1; t >= 0; t--) {
                                                        if (six10 == 0) break;

                                                        int allSum1 = filteredSpaces.get(l) + filteredSpaces.get(m) + filteredSpaces.get(n) + filteredSpaces.get(p) + filteredSpaces.get(r) + filteredSpaces.get(t);
                                                        int minElem1 = Math.min(Math.min(Math.min(Math.min(Math.min(filteredSpaces.get(l), filteredSpaces.get(m)), filteredSpaces.get(n)), filteredSpaces.get(p)), filteredSpaces.get(r)), filteredSpaces.get(t));

                                                        if (!used10.contains(l) && !used10.contains(m) && !used10.contains(n) && !used10.contains(p) && !used10.contains(r) && !used10.contains(t) &&
                                                                allSum1 >= 6 &&
                                                                allSum1 - minElem1 < 6 &&
                                                                (six10 > 0)) {

                                                            six10 -= 1;
                                                            used10.add(l);
                                                            used10.add(m);
                                                            used10.add(n);
                                                            used10.add(p);
                                                            used10.add(r);
                                                            used10.add(t);
                                                            sixes10.add(new int[]{l + 1, m + 1, n + 1, p + 1, r + 1, t + 1});
                                                            init4.add(new int[]{0, 1});

                                                            if (six10 == 0) {
                                                                return Arrays.asList(sixes10, init4, new int[]{0, 0}, used10, true);
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
        }

        return Arrays.asList(new ArrayList<>(), new ArrayList<>(), new int[]{0, six10}, used10, false);
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