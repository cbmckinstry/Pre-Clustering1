import java.util.*;
import py4j.GatewayServer;

public class Combine {

    public static List<Object> threesFlipped( List<int[]> allocations, List<Integer> spaces, int[] shortfall, int backupSize, Set<Integer> used5, int boundlst) {
        if (used5 == null) {
            used5 = new HashSet<>();
        }

        List<int[]> filteredAllocations = new ArrayList<>();
        List<Integer> filteredSpaces = new ArrayList<>();

        int lower = boundlst;
        int upperBound = boundlst;

        for (int i = 0; i < spaces.size(); i++) {
            if (spaces.get(i) != 0) {
                filteredAllocations.add(allocations.get(i));
                filteredSpaces.add(spaces.get(i));
            }
        }
        int six6 = shortfall[1];
        int backup6 = shortfall[0];
        List<int[]> threes6 = new ArrayList<>();
        Set<Integer> used6 = new HashSet<>(used5);
        List<int[]> init = new ArrayList<>();

        for (int m = lower; m <= upperBound; m++) {
            six6 = shortfall[1];
            backup6 = shortfall[0];
            used6 = new HashSet<>(used5);
            threes6.clear();
            init.clear();

            for (int i = filteredSpaces.size() - 3; i >=0; i--) {
                if (six6 == 0 && backup6 == 0) break;
                for (int j = filteredSpaces.size() - 2; j > i; j--) {
                    if (six6 == 0 && backup6 == 0) break;
                    for (int k = filteredSpaces.size() - 1; k > j; k--) {
                        if (six6 == 0 && backup6 == 0) break;

                        if (!used6.contains(i) && !used6.contains(j) && !used6.contains(k) &&
                                filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j) >= (2 * Math.max(backupSize, 6)) &&
                                filteredSpaces.get(i) + filteredSpaces.get(k) < (2 * Math.max(backupSize, 6)) &&
                                filteredSpaces.get(k) + filteredSpaces.get(j) < (2 * Math.max(backupSize, 6)) &&
                                filteredSpaces.get(i) + filteredSpaces.get(j) < (2 * Math.max(backupSize, 6)) &&
                                (six6 > 1 || backup6 > 1) &&
                                (sum(filteredAllocations.get(i)) + sum(filteredAllocations.get(j)) + sum(filteredAllocations.get(k))) <= m) {

                            if (backupSize == 7 && backup6 >= 2) {
                                backup6 -= 2;
                                used6.add(i);
                                used6.add(j);
                                used6.add(k);
                                threes6.add(new int[]{i + 1, k + 1, j + 1});
                                init.add(new int[]{2, 0});
                            } else if (backupSize == 5 && six6 >= 2) {
                                six6 -= 2;
                                used6.add(i);
                                used6.add(j);
                                used6.add(k);
                                threes6.add(new int[]{i + 1, k + 1, j + 1});
                                init.add(new int[]{0, 2});
                            }

                            if (six6 == 0 && backup6 == 0) {
                                return Arrays.asList(threes6, init);
                            }

                        }
                    }
                }
            }
        }
        if (six6==0 && backup6==0){
            return Arrays.asList(threes6, init);
        }

        int six7 = six6;
        int backup7 = backup6;
        List<int[]> threes7 = new ArrayList<>(threes6);
        Set<Integer> used7 = new HashSet<>(used6);
        List<int[]> init1 = new ArrayList<>(init);

        for (int m = lower; m <= upperBound; m++) {
            six7 = six6;
            backup7 = backup6;
            used7 = new HashSet<>(used6);
            threes7= new ArrayList<>(threes6);
            init1= new ArrayList<>(init);

            for (int i = filteredSpaces.size() - 3; i >=0; i--) {
                if (six7 == 0 && backup7 == 0) break;
                for (int j = filteredSpaces.size() - 2; j > i; j--) {
                    if (six7 == 0 && backup7 == 0) break;
                    for (int k = filteredSpaces.size() - 1; k > j; k--) {
                        if (six7 == 0 && backup7 == 0) break;

                        if (!used7.contains(i) && !used7.contains(j) && !used7.contains(k) &&
                                filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j) >= ( Math.max(backupSize, 6)+Math.min(backupSize,6)) &&
                                filteredSpaces.get(i) + filteredSpaces.get(k) < ( Math.max(backupSize, 6)+Math.min(backupSize,6)) &&
                                filteredSpaces.get(k) + filteredSpaces.get(j) < ( Math.max(backupSize, 6)+Math.min(backupSize,6)) &&
                                filteredSpaces.get(i) + filteredSpaces.get(j) < ( Math.max(backupSize, 6)+Math.min(backupSize,6)) &&
                                (six7 > 0 && backup7 > 0) &&
                                (sum(filteredAllocations.get(i)) + sum(filteredAllocations.get(j)) + sum(filteredAllocations.get(k))) <= m) {


                            if (backup7 >= 1 && six7 >= 1) {
                                backup7 -= 1;
                                six7-=1;
                                used7.add(i);
                                used7.add(j);
                                used7.add(k);
                                threes7.add(new int[]{i + 1, k + 1, j + 1});
                                init1.add(new int[]{1, 1});
                            }
                            if (six7 == 0 && backup7 == 0) {
                                return Arrays.asList(threes7, init1);
                            }

                        }
                    }
                }
            }
        }
        if (six7==0 && backup7==0){
            return Arrays.asList(threes7, init1);
        }

        int six8 = six7;
        int backup8 = backup7;
        List<int[]> threes8 = new ArrayList<>(threes7);
        Set<Integer> used8 = new HashSet<>(used7);
        List<int[]> init2 = new ArrayList<>(init1);

        for (int m = lower; m <= upperBound; m++) {
            six8 = six7;
            backup8 = backup7;
            used8 = new HashSet<>(used7);
            threes8 = new ArrayList<>(threes7);
            init2 = new ArrayList<>(init1);

            for (int i = filteredSpaces.size() - 3; i >=0; i--) {
                if (six8 == 0 && backup8 == 0) break;
                for (int j = filteredSpaces.size() - 2; j > i; j--) {
                    if (six8 == 0 && backup8 == 0) break;
                    for (int k = filteredSpaces.size() - 1; k > j; k--) {
                        if (six8 == 0 && backup8 == 0) break;

                        if (!used8.contains(i) && !used8.contains(j) && !used8.contains(k) &&
                                filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j) >= (2* Math.min(backupSize, 6)) &&
                                filteredSpaces.get(i) + filteredSpaces.get(k) < (2*Math.min(backupSize, 6)) &&
                                filteredSpaces.get(k) + filteredSpaces.get(j) < (2*Math.min(backupSize, 6)) &&
                                filteredSpaces.get(i) + filteredSpaces.get(j) < (2*Math.min(backupSize, 6)) &&
                                (six8 > 1 || backup8 > 1) &&
                                (sum(filteredAllocations.get(i)) + sum(filteredAllocations.get(j)) + sum(filteredAllocations.get(k))) <= m) {


                            if (backupSize==7 &&  six8 >= 2){
                                six8 -= 2;
                                used8.add(i);
                                used8.add(j);
                                used8.add(k);
                                threes8.add(new int[]{i + 1, k + 1, j + 1});
                                init2.add(new int[]{0, 2});
                            }
                            else if (backupSize==5 &&  backup8 >= 2){
                                backup8 -= 2;
                                used8.add(i);
                                used8.add(j);
                                used8.add(k);
                                threes8.add(new int[]{i + 1, k + 1, j + 1});
                                init2.add(new int[]{2, 0});
                            }
                            if (six8 == 0 && backup8 == 0) {
                                return Arrays.asList(threes8, init2);
                            }

                        }
                    }
                }
            }
        }
        if (six8==0 && backup8==0){
            return Arrays.asList(threes8, init2);
        }

        int six9 = six8;
        int backup9 = backup8;
        List<int[]> threes9 = new ArrayList<>(threes8);
        Set<Integer> used9 = new HashSet<>(used8);
        List<int[]> init3 = new ArrayList<>(init2);

        for (int m = lower; m <= upperBound; m++) {
            six9 = six8;
            backup9 = backup8;
            used9 = new HashSet<>(used8);
            threes9 = new ArrayList<>(threes8);
            init3 = new ArrayList<>(init2);

            for (int i = filteredSpaces.size() - 3; i >=0; i--) {
                if (six9 == 0 && backup9 == 0) break;
                for (int j = filteredSpaces.size() - 2; j > i; j--) {
                    if (six9 == 0 && backup9 == 0) break;
                    for (int k = filteredSpaces.size() - 1; k > j; k--) {
                        if (six9 == 0 && backup9 == 0) break;

                        if (!used9.contains(i) && !used9.contains(j) && !used9.contains(k) &&
                                filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j) >= (Math.max(backupSize, 6)) &&
                                filteredSpaces.get(i) + filteredSpaces.get(k) < (Math.max(backupSize, 6)) &&
                                filteredSpaces.get(k) + filteredSpaces.get(j) < (Math.max(backupSize, 6)) &&
                                filteredSpaces.get(i) + filteredSpaces.get(j) < (Math.max(backupSize, 6)) &&
                                (six9 > 0 || backup9 > 0) &&
                                (sum(filteredAllocations.get(i)) + sum(filteredAllocations.get(j)) + sum(filteredAllocations.get(k))) <= m) {


                            if (backupSize==7 && backup9 >= 1){
                                backup9 -= 1;
                                used9.add(i);
                                used9.add(j);
                                used9.add(k);
                                threes9.add(new int[]{i + 1, k + 1, j + 1});
                                init3.add(new int[]{1, 0});
                            }
                            else if (backupSize==5 && six9 >= 1){
                                six9 -= 1;
                                used9.add(i);
                                used9.add(j);
                                used9.add(k);
                                threes9.add(new int[]{i + 1, k + 1, j + 1});
                                init3.add(new int[]{0, 1});
                            }
                            if (six9 == 0 && backup9 == 0) {
                                return Arrays.asList(threes9, init3);
                            }

                        }
                    }
                }
            }
        }
        if (six9==0 && backup9==0){
            return Arrays.asList(threes9, init3);
        }

        int six10 = six9;
        int backup10 = backup9;
        List<int[]> threes10 = new ArrayList<>(threes9);
        Set<Integer> used10 = new HashSet<>(used9);
        List<int[]> init4 = new ArrayList<>(init3);

        for (int m = lower; m <= upperBound; m++) {
            six10 = six9;
            backup10 = backup9;
            used10 = new HashSet<>(used9);
            threes10 = new ArrayList<>(threes9);
            init4 = new ArrayList<>(init3);

            for (int i = filteredSpaces.size() - 3; i >=0; i--) {
                if (six10 == 0 && backup10 == 0) break;
                for (int j = filteredSpaces.size() - 2; j > i; j--) {
                    if (six10 == 0 && backup10 == 0) break;
                    for (int k = filteredSpaces.size() - 1; k > j; k--) {
                        if (six10 == 0 && backup10 == 0) break;

                        if (!used10.contains(i) && !used10.contains(j) && !used10.contains(k) &&
                                filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j) >= (Math.min(backupSize, 6)) &&
                                filteredSpaces.get(i) + filteredSpaces.get(k) < (Math.min(backupSize, 6)) &&
                                filteredSpaces.get(k) + filteredSpaces.get(j) < (Math.min(backupSize, 6)) &&
                                filteredSpaces.get(i) + filteredSpaces.get(j) < (Math.min(backupSize, 6)) &&
                                (six10 > 0 || backup10 > 0) &&
                                (sum(filteredAllocations.get(i)) + sum(filteredAllocations.get(j)) + sum(filteredAllocations.get(k))) <= m) {


                            if (backupSize==7 && six10 >= 1){
                                six10 -= 1;
                                used10.add(i);
                                used10.add(j);
                                used10.add(k);
                                threes10.add(new int[]{i + 1, k + 1, j + 1});
                                init4.add(new int[]{0, 1});
                            }
                            else if (backupSize==5 && backup10 >= 1){
                                backup10 -= 1;
                                used10.add(i);
                                used10.add(j);
                                used10.add(k);
                                threes10.add(new int[]{i + 1, k + 1, j + 1});
                                init4.add(new int[]{1, 0});
                            }
                            if (six10 == 0 && backup10 == 0) {
                                return Arrays.asList(threes10, init4);
                            }

                        }
                    }
                }
            }
        }
        if (six10==0 && backup10==0){
            return Arrays.asList(threes10, init4);
        }

        return Arrays.asList(new ArrayList<>(), new ArrayList<>());
    }

    public static List<Object> combineFlipped(List<int[]> allocations, List<Integer> space, int[] shortfall, int backupSize, Set<Integer> used, int boundlst) {
        if (used == null) {
            used = new HashSet<>();
        }

        int backup = shortfall[0];
        int six = shortfall[1];


        List<int[]> allocations0 = new ArrayList<>();
        List<Integer> space0 = new ArrayList<>();

        List<int[]> finalCombos = new ArrayList<>();
        List<int[]> finalInit = new ArrayList<>();

        List<int[]> finalCombos1=new ArrayList<>(finalCombos);
        List<int[]> finalInit1=new ArrayList<>(finalInit);

        if (allocations.size()<2){
            return Arrays.asList(finalCombos1, finalInit1);
        }

        for (int i = 0; i < space.size(); i++) {
            if (space.get(i) != 0) {
                allocations0.add(allocations.get(i));
                space0.add(space.get(i));
            }
        }

        int lower = boundlst;
        int upper = boundlst;
        int six4 = six;
        Set<Integer> used4 = new HashSet<>(used);
        List<int[]> combos4 = new ArrayList<>();
        int backup4 = backup;
        List<int[]> init = new ArrayList<>();



        if (backupSize == 7) {
            for (int bound = lower; bound <= upper; bound++) {
                if (backup4 == 0) break;
                used4 = new HashSet<>(used);
                backup4 = backup;
                combos4.clear();
                init.clear();

                if (space0.size()>=3) {
                    List<Object> trial = threesFlipped(allocations0, space0, new int[]{backup4, six4}, backupSize, used4, bound);
                    if (!((List<?>) trial.get(1)).isEmpty()) {
                        finalCombos = mergeLists((List<int[]>) trial.get(0), combos4);
                        finalInit = mergeLists((List<int[]>) trial.get(1), init);
                    }
                }

                for (int m = space0.size()-2; m >= 0; m--) {
                    if (backup4 == 0) break;
                    for (int n = space0.size() - 1; n > m; n--) {
                        if (backup4 == 0) break;
                        if ((space0.get(m) + space0.get(n) >= 7) && !used4.contains(m) && !used4.contains(n) && sum(allocations0.get(m)) + sum(allocations0.get(n)) <= bound) {
                            used4.add(m);
                            used4.add(n);
                            combos4.add(new int[]{m + 1, n + 1});
                            backup4--;
                            init.add(new int[]{1, 0});
                            if (backup4 == 0 && six4 == 0) {
                                return Arrays.asList(combos4, init);
                            }
                            if (space0.size()>=3) {
                                List<Object> trial = threesFlipped(allocations0, space0, new int[]{backup4, six4}, backupSize, used4, bound );
                                if (!((List<?>) trial.get(1)).isEmpty()) {
                                    finalCombos = mergeLists((List<int[]>) trial.get(0), combos4);
                                    finalInit = mergeLists((List<int[]>) trial.get(1), init);

                                }
                            }
                        }
                    }
                }

            }

            List<int[]> combos5 = new ArrayList<>(combos4);
            Set<Integer> used5 = new HashSet<>(used4);
            List<int[]> init1 = new ArrayList<>(init);

            finalCombos1=new ArrayList<>(finalCombos);
            finalInit1=new ArrayList<>(finalInit);

            for (int bound = lower; bound <= upper; bound++) {
                if (six4 == 0) break;
                used5 = new HashSet<>(used4);
                six4 = six;
                combos5= new ArrayList<>(combos4);
                init1= new ArrayList<>(init);
                finalCombos1=new ArrayList<>(finalCombos);
                finalInit1=new ArrayList<>(finalInit);

                if (space0.size()>=3) {
                    List<Object> trial = threesFlipped(allocations0, space0, new int[]{backup4, six4}, backupSize, used5, bound );
                    if (!((List<?>) trial.get(1)).isEmpty()) {
                        finalCombos1 = mergeLists((List<int[]>) trial.get(0), combos5);
                        finalInit1 = mergeLists((List<int[]>) trial.get(1), init1);

                    }
                }
                for (int m = space0.size()-2; m >= 0; m--) {
                    if (six4 == 0) break;
                    for (int n = space0.size() - 1; n > m; n--) {
                        if (six4 == 0) break;
                        if ((space0.get(m) + space0.get(n) >= 6) && !used5.contains(m) && !used5.contains(n) && sum(allocations0.get(m)) + sum(allocations0.get(n)) <= bound) {
                            used5.add(m);
                            used5.add(n);
                            combos5.add(new int[]{m + 1, n + 1});
                            six4--;
                            init1.add(new int[]{0, 1});
                            if (backup4 == 0 && six4 == 0) {
                                return Arrays.asList(combos5, init1);
                            }

                            if (space0.size()>=3) {
                                List<Object> trial = threesFlipped(allocations0, space0, new int[]{backup4, six4}, backupSize, used5, bound);
                                if (!((List<?>) trial.get(1)).isEmpty()) {
                                    finalCombos1 = mergeLists((List<int[]>) trial.get(0), combos5);
                                    finalInit1 = mergeLists((List<int[]>) trial.get(1), init1);

                                }
                            }
                        }
                    }
                }

            }
            if (backup4 == 0 && six4 == 0){
                return Arrays.asList(finalCombos1, finalInit1);
            }
        } else {
            for (int bound = lower; bound <= upper; bound++) {
                if (six4 == 0) break;
                used4 = new HashSet<>(used);
                six4 = six;
                combos4.clear();
                init.clear();

                if (space0.size()>=3) {
                    List<Object> trial = threesFlipped(allocations0, space0, new int[]{backup4, six4}, backupSize, used4, bound);
                    if (!((List<?>) trial.get(1)).isEmpty()) {
                        finalCombos = mergeLists((List<int[]>) trial.get(0), combos4);
                        finalInit = mergeLists((List<int[]>) trial.get(1), init);
                    }
                }

                for (int m = space0.size()-2; m >= 0; m--) {
                    if (six4 == 0) break;
                    for (int n = space0.size() - 1; n > m; n--) {
                        if (six4 == 0) break;
                        if ((space0.get(m) + space0.get(n) >= 6) && !used4.contains(m) && !used4.contains(n) && sum(allocations0.get(m)) + sum(allocations0.get(n)) <= bound) {
                            used4.add(m);
                            used4.add(n);
                            combos4.add(new int[]{m + 1, n + 1});
                            six4--;
                            init.add(new int[]{0, 1});

                            if (backup4 == 0 && six4 == 0) {
                                return Arrays.asList(combos4, init);
                            }

                            if (space0.size()>=3) {
                                List<Object> trial = threesFlipped(allocations0, space0, new int[]{backup4, six4}, backupSize, used4, bound);
                                if (!((List<?>) trial.get(1)).isEmpty()) {
                                    finalCombos = mergeLists((List<int[]>) trial.get(0), combos4);
                                    finalInit = mergeLists((List<int[]>) trial.get(1), init);

                                }
                            }

                        }
                    }
                }

            }

            List<int[]> combos5 = new ArrayList<>(combos4);
            Set<Integer> used5 = new HashSet<>(used4);
            List<int[]> init1 = new ArrayList<>(init);

            finalCombos1=new ArrayList<>(finalCombos);
            finalInit1=new ArrayList<>(finalInit);
            for (int bound = lower; bound <= upper; bound++) {

                if (backup4 == 0) break;
                used5 = new HashSet<>(used4);
                backup4 = backup;
                combos5= new ArrayList<>(combos4);
                init1= new ArrayList<>(init);
                finalCombos1=new ArrayList<>(finalCombos);
                finalInit1=new ArrayList<>(finalInit);

                if (space0.size()>=3) {
                    List<Object> trial = threesFlipped(allocations0, space0, new int[]{backup4, six4}, backupSize, used5, bound );
                    if (!((List<?>) trial.get(1)).isEmpty()) {
                        finalCombos1 = mergeLists((List<int[]>) trial.get(0), combos5);
                        finalInit1 = mergeLists((List<int[]>) trial.get(1), init1);

                    }
                }

                for (int m = space0.size()-2; m >= 0; m--) {
                    if (backup4 == 0) break;
                    for (int n = space0.size() - 1; n > m; n--) {
                        if (backup4 == 0) break;
                        if ((space0.get(m) + space0.get(n) >= 5) && !used5.contains(m) && !used5.contains(n) && sum(allocations0.get(m)) + sum(allocations0.get(n)) <= bound) {
                            used5.add(m);
                            used5.add(n);
                            combos5.add(new int[]{m + 1, n + 1});
                            backup4-=1;
                            init1.add(new int[]{1, 0});
                            if (backup4 == 0 && six4 == 0) {
                                return Arrays.asList(combos4, init);
                            }

                            if (space0.size()>=3) {
                                List<Object> trial = threesFlipped(allocations0, space0, new int[]{backup4, six4}, backupSize, used5, bound);
                                if (!((List<?>) trial.get(1)).isEmpty()) {
                                    finalCombos1 = mergeLists((List<int[]>) trial.get(0), combos5);
                                    finalInit1 = mergeLists((List<int[]>) trial.get(1), init1);

                                }
                            }
                        }
                    }
                }
            }
            if (backup4 == 0 && six4 == 0) {
                return Arrays.asList(finalCombos1, finalInit1);
            }
        }

        return Arrays.asList(finalCombos1, finalInit1);
    }

        public static List<List<Integer>> optimize(List<List<Integer>> sortedAllocations, List<int[]> allocations, int backupSize, List<List<Integer>> outCombos, List<Integer> spaces) {
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

                        for (Integer idx1 : combo1) {
                            for (Integer idx2 : combo2) {
                                int weight1Before = totalWeight(combo1, weights);
                                int weight2Before = totalWeight(combo2, weights);

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
                                } else {
                                    if (Math.max(weight1After, weight2After) < Math.max(weight1Before, weight2Before) &&
                                            space1After >= minSpace1 && space2After >= minSpace2) {
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

    // Helper method to format list output
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


}