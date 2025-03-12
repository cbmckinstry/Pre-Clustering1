import java.util.*;
import java.util.ArrayList;
import java.util.HashSet;
import py4j.GatewayServer;

public class Combine {
    public static List<Object> combineFlipped(List<int[]> allocations, List<Integer> space, int[] shortfall, int backupSize, Set<Integer> used) {
        if (used == null) {
            used = new HashSet<>();
        }
        if (sum1(space)<backupSize*shortfall[0]+6*shortfall[1]) {
            return Arrays.asList(new ArrayList<>(), new ArrayList<>());
        }
        List<int[]> allocations0 = new ArrayList<>();
        List<Integer> space0 = new ArrayList<>();

        if (allocations.size() < 2) {
            return Arrays.asList(new ArrayList<>(), new ArrayList<>());
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
                            return Arrays.asList(combos4, init);
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
                            return Arrays.asList(combos5, init1);
                        }
                    }
                }
            }
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
                            return Arrays.asList(combos4, init);
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
                            return Arrays.asList(combos5, init1);
                        }
                    }
                }
            }
        }
        return Arrays.asList(new ArrayList<>(), new ArrayList<>());
    }

    public static List<Object> threesFlipped(List<int[]> allocations, List<Integer> spaces, int[] shortfall, int backupSize, Set<Integer> used5) {
        if (used5 == null) {
            used5 = new HashSet<>();
        }

        if (sum1(spaces)<backupSize*shortfall[0]+6*shortfall[1]) {
            return Arrays.asList(new ArrayList<>(), new ArrayList<>());
        }

        List<int[]> filteredAllocations = new ArrayList<>();
        List<Integer> filteredSpaces = new ArrayList<>();

        List<int[]> finalCombos = new ArrayList<>();
        List<int[]> finalInit = new ArrayList<>();

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

        List<Object> trial = combineFlipped(filteredAllocations, filteredSpaces, new int[]{backup6, six6}, backupSize, used6);
        if (!((List<?>) trial.get(1)).isEmpty()) {
            finalCombos = (List<int[]>) trial.get(0);
            finalInit = (List<int[]>) trial.get(1);
            return Arrays.asList(finalCombos, finalInit);
        }

        for (int i = filteredSpaces.size() - 3; i >= 0; i--) {
            if (six6 == 0 && backup6 == 0) break;
            for (int j = filteredSpaces.size() - 2; j > i; j--) {
                if (six6 == 0 && backup6 == 0) break;
                for (int k = filteredSpaces.size() - 1; k > j; k--) {
                    if (six6 == 0 && backup6 == 0) break;
                    int allSum=filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j);
                    int minElem=Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j));

                    if (!used6.contains(i) && !used6.contains(j) && !used6.contains(k) &&
                            allSum >= (2 * Math.max(backupSize, 6)) &&
                            allSum-minElem<(2*Math.max(backupSize,6)) &&
                            (six6 > 1 || backup6 > 1) ) {

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
                        if (six6==0 && backup6==0){
                            return Arrays.asList(threes6, init);
                        }
                        List<Object> trial1 = combineFlipped(filteredAllocations, filteredSpaces, new int[]{backup6, six6}, backupSize, used6);
                        if (!((List<?>) trial1.get(1)).isEmpty()) {
                            finalCombos = mergeLists((List<int[]>) trial1.get(0),threes6);
                            finalInit = mergeLists((List<int[]>) trial1.get(1),init);
                            return Arrays.asList(finalCombos, finalInit);
                        }
                    }
                }
            }
        }

        int six7 = six6;
        int backup7 = backup6;
        List<int[]> threes7 = new ArrayList<>(threes6);
        Set<Integer> used7 = new HashSet<>(used6);
        List<int[]> init1 = new ArrayList<>(init);

        for (int i = filteredSpaces.size() - 3; i >= 0; i--) {
            if (six7 == 0 && backup7 == 0) break;
            for (int j = filteredSpaces.size() - 2; j > i; j--) {
                if (six7 == 0 && backup7 == 0) break;
                for (int k = filteredSpaces.size() - 1; k > j; k--) {
                    if (six7 == 0 && backup7 == 0) break;
                    int allSum=filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j);
                    int minElem=Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j));

                    if (!used7.contains(i) && !used7.contains(j) && !used7.contains(k) &&
                            allSum >= (Math.max(backupSize, 6) + Math.min(backupSize, 6)) &&
                            allSum-minElem < (Math.max(backupSize, 6) + Math.min(backupSize, 6)) &&
                            (six7 > 0 && backup7 > 0) ) {

                        if (backup7 >= 1 && six7 >= 1) {
                            backup7 -= 1;
                            six7 -= 1;
                            used7.add(i);
                            used7.add(j);
                            used7.add(k);
                            threes7.add(new int[]{i + 1, k + 1, j + 1});
                            init1.add(new int[]{1, 1});
                        }
                        if (six7==0 && backup7==0){
                            return Arrays.asList(threes7, init1);
                        }
                        List<Object> trial2 = combineFlipped(filteredAllocations, filteredSpaces, new int[]{backup7, six7}, backupSize, used7);
                        if (!((List<?>) trial2.get(1)).isEmpty()) {
                            finalCombos = mergeLists((List<int[]>) trial2.get(0),threes7);
                            finalInit = mergeLists((List<int[]>) trial2.get(1),init1);
                            return Arrays.asList(finalCombos, finalInit);
                        }
                    }
                }
            }
        }

        int six8 = six7;
        int backup8 = backup7;
        List<int[]> threes8 = new ArrayList<>(threes7);
        Set<Integer> used8 = new HashSet<>(used7);
        List<int[]> init2 = new ArrayList<>(init1);

        for (int i = filteredSpaces.size() - 3; i >= 0; i--) {
            if (six8 == 0 && backup8 == 0) break;
            for (int j = filteredSpaces.size() - 2; j > i; j--) {
                if (six8 == 0 && backup8 == 0) break;
                for (int k = filteredSpaces.size() - 1; k > j; k--) {
                    if (six8 == 0 && backup8 == 0) break;
                    int allSum=filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j);
                    int minElem=Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j));

                    if (!used8.contains(i) && !used8.contains(j) && !used8.contains(k) &&
                            allSum >= (2 * Math.min(backupSize, 6)) &&
                            allSum-minElem < (2 * Math.min(backupSize, 6)) &&
                            (six8 > 1 || backup8 > 1) ) {

                        if (backupSize == 7 && six8 >= 2) {
                            six8 -= 2;
                            used8.add(i);
                            used8.add(j);
                            used8.add(k);
                            threes8.add(new int[]{i + 1, k + 1, j + 1});
                            init2.add(new int[]{0, 2});
                        } else if (backupSize == 5 && backup8 >= 2) {
                            backup8 -= 2;
                            used8.add(i);
                            used8.add(j);
                            used8.add(k);
                            threes8.add(new int[]{i + 1, k + 1, j + 1});
                            init2.add(new int[]{2, 0});
                        }
                        if (six8==0 && backup8==0){
                            return Arrays.asList(threes8, init2);
                        }
                        List<Object> trial3 = combineFlipped(filteredAllocations, filteredSpaces, new int[]{backup8, six8}, backupSize, used8);
                        if (!((List<?>) trial3.get(1)).isEmpty()) {
                            finalCombos = mergeLists((List<int[]>) trial3.get(0),threes8);
                            finalInit = mergeLists((List<int[]>) trial3.get(1),init2);
                            return Arrays.asList(finalCombos, finalInit);
                        }
                    }
                }
            }
        }

        int six9 = six8;
        int backup9 = backup8;
        List<int[]> threes9 = new ArrayList<>(threes8);
        Set<Integer> used9 = new HashSet<>(used8);
        List<int[]> init3 = new ArrayList<>(init2);

        for (int i = filteredSpaces.size() - 3; i >= 0; i--) {
            if (six9 == 0 && backup9 == 0) break;
            for (int j = filteredSpaces.size() - 2; j > i; j--) {
                if (six9 == 0 && backup9 == 0) break;
                for (int k = filteredSpaces.size() - 1; k > j; k--) {
                    if (six9 == 0 && backup9 == 0) break;
                    int allSum=filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j);
                    int minElem=Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j));

                    if (!used9.contains(i) && !used9.contains(j) && !used9.contains(k) &&
                            allSum >= (Math.max(backupSize, 6)) &&
                            allSum-minElem < (Math.max(backupSize, 6)) &&
                            (six9 > 0 || backup9 > 0) ) {

                        if (backupSize == 7 && backup9 >= 1) {
                            backup9 -= 1;
                            used9.add(i);
                            used9.add(j);
                            used9.add(k);
                            threes9.add(new int[]{i + 1, k + 1, j + 1});
                            init3.add(new int[]{1, 0});
                        } else if (backupSize == 5 && six9 >= 1) {
                            six9 -= 1;
                            used9.add(i);
                            used9.add(j);
                            used9.add(k);
                            threes9.add(new int[]{i + 1, k + 1, j + 1});
                            init3.add(new int[]{0, 1});
                        }
                        if (six9==0 && backup9==0){
                            return Arrays.asList(threes9, init3);
                        }
                        List<Object> trial4 = combineFlipped(filteredAllocations, filteredSpaces, new int[]{backup9, six9}, backupSize, used9);
                        if (!((List<?>) trial4.get(1)).isEmpty()) {
                            finalCombos = mergeLists((List<int[]>) trial4.get(0),threes9);
                            finalInit = mergeLists((List<int[]>) trial4.get(1),init3);
                            return Arrays.asList(finalCombos, finalInit);
                        }
                    }
                }
            }
        }

        int six10 = six9;
        int backup10 = backup9;
        List<int[]> threes10 = new ArrayList<>(threes9);
        Set<Integer> used10 = new HashSet<>(used9);
        List<int[]> init4 = new ArrayList<>(init3);

        for (int i = filteredSpaces.size() - 3; i >= 0; i--) {
            if (six10 == 0 && backup10 == 0) break;
            for (int j = filteredSpaces.size() - 2; j > i; j--) {
                if (six10 == 0 && backup10 == 0) break;
                for (int k = filteredSpaces.size() - 1; k > j; k--) {
                    if (six10 == 0 && backup10 == 0) break;
                    int allSum=filteredSpaces.get(i) + filteredSpaces.get(k) + filteredSpaces.get(j);
                    int minElem=Math.min(Math.min(filteredSpaces.get(i), filteredSpaces.get(k)), filteredSpaces.get(j));

                    if (!used10.contains(i) && !used10.contains(j) && !used10.contains(k) &&
                            allSum >= (Math.min(backupSize, 6)) &&
                            allSum-minElem<(Math.min(backupSize, 6)) &&
                            (six10 > 0 || backup10 > 0) ) {

                        if (backupSize == 7 && six10 >= 1) {
                            six10 -= 1;
                            used10.add(i);
                            used10.add(j);
                            used10.add(k);
                            threes10.add(new int[]{i + 1, k + 1, j + 1});
                            init4.add(new int[]{0, 1});
                        } else if (backupSize == 5 && backup10 >= 1) {
                            backup10 -= 1;
                            used10.add(i);
                            used10.add(j);
                            used10.add(k);
                            threes10.add(new int[]{i + 1, k + 1, j + 1});
                            init4.add(new int[]{1, 0});
                        }
                        if (six10==0 && backup10==0){
                            return Arrays.asList(threes10, init4);
                        }
                        List<Object> trial5 = combineFlipped(filteredAllocations, filteredSpaces, new int[]{backup10, six10}, backupSize, used10);
                        if (!((List<?>) trial5.get(1)).isEmpty()) {
                            finalCombos = mergeLists((List<int[]>) trial5.get(0),threes10);
                            finalInit = mergeLists((List<int[]>) trial5.get(1),init4);
                            return Arrays.asList(finalCombos, finalInit);
                        }
                    }
                }
            }
        }

        return Arrays.asList(new ArrayList<>(), new ArrayList<>());
    }

    public static List<Object> foursFlipped(List<int[]> allocations, List<Integer> spaces, int[] shortfall, int backupSize, Set<Integer> used5) {
        if (used5 == null) {
            used5 = new HashSet<>();
        }

        if (sum1(spaces)<backupSize*shortfall[0]+6*shortfall[1]) {
            return Arrays.asList(new ArrayList<>(), new ArrayList<>());
        }

        List<int[]> filteredAllocations = new ArrayList<>();
        List<Integer> filteredSpaces = new ArrayList<>();

        List<int[]> finalCombos = new ArrayList<>();
        List<int[]> finalInit = new ArrayList<>();

        for (int i = 0; i < spaces.size(); i++) {
            if (spaces.get(i) != 0) {
                filteredAllocations.add(allocations.get(i));
                filteredSpaces.add(spaces.get(i));
            }
        }
        int six6 = shortfall[1];
        int backup6 = shortfall[0];
        List<int[]> fours6 = new ArrayList<>();
        Set<Integer> used6 = new HashSet<>(used5);
        List<int[]> init = new ArrayList<>();

        List<Object> trial = threesFlipped(filteredAllocations, filteredSpaces, new int[]{backup6, six6}, backupSize, used6);
        if (!((List<?>) trial.get(1)).isEmpty()) {
            finalCombos = (List<int[]>) trial.get(0);
            finalInit = (List<int[]>) trial.get(1);
            return Arrays.asList(finalCombos, finalInit);
        }

        for (int h = filteredSpaces.size() - 4; h >= 0; h--) {
            if (six6 == 0 && backup6 == 0) break;
            for (int i = filteredSpaces.size() - 3; i > h; i--) {
                if (six6 == 0 && backup6 == 0) break;
                for (int j = filteredSpaces.size() - 2; j > i; j--) {
                    if (six6 == 0 && backup6 == 0) break;
                    for (int k = filteredSpaces.size() - 1; k > j; k--) {
                        if (six6 == 0 && backup6 == 0) break;
                        int value1= filteredSpaces.get(i);
                        int value2= filteredSpaces.get(j);
                        int value3= filteredSpaces.get(k);
                        int value4= filteredSpaces.get(h);
                        int totalSum=sum(new int[]{value1, value2, value3, value4});

                        if (!used6.contains(i) && !used6.contains(j) && !used6.contains(k) &&!used6.contains(h) &&
                                totalSum >= (3 * Math.max(backupSize, 6)) &&
                                totalSum-Math.min(Math.min(Math.min(value1,value2),value3),value4)  < (3 * Math.max(backupSize, 6)) &&
                                (six6 > 2 || backup6 > 2)){

                            if (backupSize == 7 && backup6 >= 3) {
                                backup6 -= 3;
                                used6.add(h);
                                used6.add(i);
                                used6.add(j);
                                used6.add(k);
                                fours6.add(new int[]{h+1, i + 1, k + 1, j + 1});
                                init.add(new int[]{3, 0});
                            } else if (backupSize == 5 && six6 >= 3) {
                                six6 -= 3;
                                used6.add(h);
                                used6.add(i);
                                used6.add(j);
                                used6.add(k);
                                fours6.add(new int[]{h+1, i + 1, k + 1, j + 1});
                                init.add(new int[]{0, 3});
                            }
                            if (six6==0 && backup6==0){
                                return Arrays.asList(fours6, init);
                            }
                            List<Object> trial1 = threesFlipped(filteredAllocations, filteredSpaces, new int[]{backup6, six6}, backupSize, used6);
                            if (!((List<?>) trial1.get(1)).isEmpty()) {
                                finalCombos = mergeLists((List<int[]>) trial1.get(0),fours6);
                                finalInit = mergeLists((List<int[]>) trial1.get(1),init);
                                return Arrays.asList(finalCombos, finalInit);
                            }
                        }
                    }
                }
            }
        }

        int six7 = six6;
        int backup7 = backup6;
        List<int[]> fours7 = new ArrayList<>(fours6);
        Set<Integer> used7 = new HashSet<>(used6);
        List<int[]> init1 = new ArrayList<>(init);

        if (backupSize==7) {
            for (int h = filteredSpaces.size() - 4; h >= 0; h--) {
                if (six7 == 0 && backup7 == 0) break;
                for (int i = filteredSpaces.size() - 3; i > h; i--) {
                    if (six7 == 0 && backup7 == 0) break;
                    for (int j = filteredSpaces.size() - 2; j > i; j--) {
                        if (six7 == 0 && backup7 == 0) break;
                        for (int k = filteredSpaces.size() - 1; k > j; k--) {
                            if (six7 == 0 && backup7 == 0) break;
                            int value1 = filteredSpaces.get(i);
                            int value2 = filteredSpaces.get(j);
                            int value3 = filteredSpaces.get(k);
                            int value4 = filteredSpaces.get(h);
                            int totalSum = sum(new int[]{value1, value2, value3, value4});

                            if (!used7.contains(i) && !used7.contains(j) && !used7.contains(k) && !used7.contains(h) &&
                                    totalSum >= (2 * 7 + 6) &&
                                    totalSum - Math.min(Math.min(Math.min(value1, value2), value3), value4) < (2 * 7 + 6) &&
                                    (six7 > 0 && backup7 > 1)) {

                                backup7 -= 2;
                                six7 -= 1;
                                used7.add(h);
                                used7.add(i);
                                used7.add(j);
                                used7.add(k);
                                fours7.add(new int[]{h+1, i + 1, k + 1, j + 1});
                                init1.add(new int[]{2, 1});
                                if (six7==0 && backup7==0){
                                    return Arrays.asList(fours7, init1);
                                }
                                List<Object> trial2 = threesFlipped(filteredAllocations, filteredSpaces, new int[]{backup7, six7}, backupSize, used7);
                                if (!((List<?>) trial2.get(1)).isEmpty()) {
                                    finalCombos = mergeLists((List<int[]>) trial2.get(0),fours7);
                                    finalInit = mergeLists((List<int[]>) trial2.get(1),init1);
                                    return Arrays.asList(finalCombos, finalInit);
                                }
                            }
                        }
                    }
                }
            }
        }

        int six8 = six7;
        int backup8 = backup7;
        List<int[]> fours8 = new ArrayList<>(fours7);
        Set<Integer> used8 = new HashSet<>(used7);
        List<int[]> init2 = new ArrayList<>(init1);

        for (int h = filteredSpaces.size() - 4; h >= 0; h--) {
            if (six8 == 0 && backup8 == 0) break;
            for (int i = filteredSpaces.size() - 3; i > h; i--) {
                if (six8 == 0 && backup8 == 0) break;
                for (int j = filteredSpaces.size() - 2; j > i; j--) {
                    if (six8 == 0 && backup8 == 0) break;
                    for (int k = filteredSpaces.size() - 1; k > j; k--) {
                        if (six8 == 0 && backup8 == 0) break;
                        int value1 = filteredSpaces.get(i);
                        int value2 = filteredSpaces.get(j);
                        int value3 = filteredSpaces.get(k);
                        int value4 = filteredSpaces.get(h);
                        int totalSum = sum(new int[]{value1, value2, value3, value4});

                        if (!used8.contains(i) && !used8.contains(j) && !used8.contains(k) &&!used8.contains(h) &&
                                totalSum >= (Math.max(backupSize, 6)+ 2* Math.min(backupSize, 6)) &&
                                totalSum-Math.min(Math.min(Math.min(value1,value2),value3),value4)  < (Math.max(backupSize, 6)+ 2*Math.min(backupSize,6)) &&
                                (six8 > 0 || backup8 > 0)) {

                            if (backupSize == 7 && six8 >= 2 && backup8 >= 1) {
                                six8 -= 2;
                                backup8-=1;
                                used8.add(h);
                                used8.add(i);
                                used8.add(j);
                                used8.add(k);
                                fours8.add(new int[]{h+1, i + 1, k + 1, j + 1});
                                init2.add(new int[]{1, 2});
                            } else if (backupSize == 5 && backup8 >= 2 && six8>=1) {
                                backup8 -= 2;
                                six8-=1;
                                used8.add(h);
                                used8.add(i);
                                used8.add(j);
                                used8.add(k);
                                fours8.add(new int[]{h+1, i + 1, k + 1, j + 1});
                                init2.add(new int[]{2, 1});
                            }
                            if (six8==0 && backup8==0){
                                return Arrays.asList(fours8, init2);
                            }
                            List<Object> trial3 = threesFlipped(filteredAllocations, filteredSpaces, new int[]{backup8, six8}, backupSize, used8);
                            if (!((List<?>) trial3.get(1)).isEmpty()) {
                                finalCombos = mergeLists((List<int[]>) trial3.get(0),fours8);
                                finalInit = mergeLists((List<int[]>) trial3.get(1),init2);
                                return Arrays.asList(finalCombos, finalInit);
                            }
                        }
                    }
                }
            }
        }

        int six9 = six8;
        int backup9 = backup8;
        List<int[]> fours9 = new ArrayList<>(fours8);
        Set<Integer> used9 = new HashSet<>(used8);
        List<int[]> init3 = new ArrayList<>(init2);

        for (int h = filteredSpaces.size() - 4; h >= 0; h--) {
            if (six9 == 0 && backup9 == 0) break;
            for (int i = filteredSpaces.size() - 3; i > h; i--) {
                if (six9 == 0 && backup9 == 0) break;
                for (int j = filteredSpaces.size() - 2; j > i; j--) {
                    if (six9 == 0 && backup9 == 0) break;
                    for (int k = filteredSpaces.size() - 1; k > j; k--) {
                        if (six9 == 0 && backup9 == 0) break;
                        int value1= filteredSpaces.get(i);
                        int value2= filteredSpaces.get(j);
                        int value3= filteredSpaces.get(k);
                        int value4= filteredSpaces.get(h);
                        int totalSum=sum(new int[]{value1, value2, value3, value4});

                        if (!used9.contains(i) && !used9.contains(j) && !used9.contains(k) &&!used9.contains(h) &&
                                totalSum >= (3 * Math.min(backupSize, 6)) &&
                                totalSum-Math.min(Math.min(Math.min(value1,value2),value3),value4)  < (3 * Math.min(backupSize, 6)) &&
                                (six9 > 2 || backup9 > 2)){

                            if (backupSize == 7 && six9 >= 3) {
                                six9 -= 3;
                                used9.add(h);
                                used9.add(i);
                                used9.add(j);
                                used9.add(k);
                                fours9.add(new int[]{h+1, i + 1, k + 1, j + 1});
                                init3.add(new int[]{0, 3});
                            } else if (backupSize == 5 && backup9 >= 3) {
                                backup9 -= 3;
                                used9.add(h);
                                used9.add(i);
                                used9.add(j);
                                used9.add(k);
                                fours9.add(new int[]{h+1, i + 1, k + 1, j + 1});
                                init3.add(new int[]{3, 0});
                            }
                            if (six9==0 && backup9==0){
                                return Arrays.asList(fours9, init3);
                            }
                            List<Object> trial4 = threesFlipped(filteredAllocations, filteredSpaces, new int[]{backup9, six9}, backupSize, used9);
                            if (!((List<?>) trial4.get(1)).isEmpty()) {
                                finalCombos = mergeLists((List<int[]>) trial4.get(0),fours9);
                                finalInit = mergeLists((List<int[]>) trial4.get(1),init3);
                                return Arrays.asList(finalCombos, finalInit);
                            }
                        }
                    }
                }
            }
        }

        int six10 = six9;
        int backup10 = backup9;
        List<int[]> fours10 = new ArrayList<>(fours9);
        Set<Integer> used10 = new HashSet<>(used9);
        List<int[]> init4 = new ArrayList<>(init3);

        for (int h = filteredSpaces.size() - 4; h >= 0; h--) {
            if (six10 == 0 && backup10 == 0) break;
            for (int i = filteredSpaces.size() - 3; i > h; i--) {
                if (six10 == 0 && backup10 == 0) break;
                for (int j = filteredSpaces.size() - 2; j > i; j--) {
                    if (six10 == 0 && backup10 == 0) break;
                    for (int k = filteredSpaces.size() - 1; k > j; k--) {
                        if (six10 == 0 && backup10 == 0) break;
                        int value1= filteredSpaces.get(i);
                        int value2= filteredSpaces.get(j);
                        int value3= filteredSpaces.get(k);
                        int value4= filteredSpaces.get(h);
                        int totalSum=sum(new int[]{value1, value2, value3, value4});

                        if (!used10.contains(i) && !used10.contains(j) && !used10.contains(k) &&!used10.contains(h) &&
                                totalSum >= ( Math.max(backupSize, 6)) &&
                                totalSum-Math.min(Math.min(Math.min(value1,value2),value3),value4)  < ( Math.max(backupSize, 6)) &&
                                (six10 > 0 || backup10 > 0)){

                            if (backupSize == 7 && backup10 >= 1) {
                                backup10 -= 1;
                                used10.add(h);
                                used10.add(i);
                                used10.add(j);
                                used10.add(k);
                                fours10.add(new int[]{h+1, i + 1, k + 1, j + 1});
                                init4.add(new int[]{1, 0});
                            } else if (backupSize == 5 && six10 >= 1) {
                                six10 -= 1;
                                used10.add(h);
                                used10.add(i);
                                used10.add(j);
                                used10.add(k);
                                fours10.add(new int[]{h+1, i + 1, k + 1, j + 1});
                                init4.add(new int[]{0, 1});
                            }
                            if (six10==0 && backup10==0){
                                return Arrays.asList(fours10, init4);
                            }
                            List<Object> trial5 = threesFlipped(filteredAllocations, filteredSpaces, new int[]{backup10, six10}, backupSize, used10);
                            if (!((List<?>) trial5.get(1)).isEmpty()) {
                                finalCombos = mergeLists((List<int[]>) trial5.get(0),fours10);
                                finalInit = mergeLists((List<int[]>) trial5.get(1),init4);
                                return Arrays.asList(finalCombos, finalInit);
                            }
                        }
                    }
                }
            }
        }

        int six11 = six10;
        int backup11 = backup10;
        List<int[]> fours11 = new ArrayList<>(fours10);
        Set<Integer> used11 = new HashSet<>(used10);
        List<int[]> init5 = new ArrayList<>(init4);

        for (int h = filteredSpaces.size() - 4; h >= 0; h--) {
            if (six11 == 0 && backup11 == 0) break;
            for (int i = filteredSpaces.size() - 3; i > h; i--) {
                if (six11 == 0 && backup11 == 0) break;
                for (int j = filteredSpaces.size() - 2; j > i; j--) {
                    if (six11 == 0 && backup11 == 0) break;
                    for (int k = filteredSpaces.size() - 1; k > j; k--) {
                        if (six11 == 0 && backup11 == 0) break;
                        int value1= filteredSpaces.get(i);
                        int value2= filteredSpaces.get(j);
                        int value3= filteredSpaces.get(k);
                        int value4= filteredSpaces.get(h);
                        int totalSum=sum(new int[]{value1, value2, value3, value4});

                        if (!used11.contains(i) && !used11.contains(j) && !used11.contains(k) &&!used11.contains(h) &&
                                totalSum >= ( Math.min(backupSize, 6)) &&
                                totalSum-Math.min(Math.min(Math.min(value1,value2),value3),value4)  < (Math.min(backupSize, 6)) &&
                                (six11 > 0 || backup11 > 0)){

                            if (backupSize == 7 && six11 >= 1) {
                                six11 -= 1;
                                used11.add(h);
                                used11.add(i);
                                used11.add(j);
                                used11.add(k);
                                fours11.add(new int[]{h+1, i + 1, k + 1, j + 1});
                                init5.add(new int[]{1, 0});
                            } else if (backupSize == 5 && six11 >= 1) {
                                backup11 -= 1;
                                used11.add(h);
                                used11.add(i);
                                used11.add(j);
                                used11.add(k);
                                fours11.add(new int[]{h+1, i + 1, k + 1, j + 1});
                                init5.add(new int[]{0, 1});
                            }
                            if (six11==0 && backup11==0){
                                return Arrays.asList(fours11, init5);
                            }
                            List<Object> trial6 = threesFlipped(filteredAllocations, filteredSpaces, new int[]{backup11, six11}, backupSize, used11);
                            if (!((List<?>) trial6.get(1)).isEmpty()) {
                                finalCombos = mergeLists((List<int[]>) trial6.get(0),fours11);
                                finalInit = mergeLists((List<int[]>) trial6.get(1),init5);
                                return Arrays.asList(finalCombos, finalInit);
                            }
                        }
                    }
                }
            }
        }

        return Arrays.asList(new ArrayList<>(), new ArrayList<>());
    }

    public static List<Object> fivesFlipped(List<int[]> allocations, List<Integer> spaces, int[] shortfall, int backupSize, Set<Integer> used5) {
        if (used5 == null) {
            used5 = new HashSet<>();
        }

        if (sum1(spaces)<backupSize*shortfall[0]+6*shortfall[1]) {
            return Arrays.asList(new ArrayList<>(), new ArrayList<>());
        }

        List<int[]> filteredAllocations = new ArrayList<>();
        List<Integer> filteredSpaces = new ArrayList<>();

        List<int[]> finalCombos = new ArrayList<>();
        List<int[]> finalInit = new ArrayList<>();

        for (int i = 0; i < spaces.size(); i++) {
            if (spaces.get(i) != 0) {
                filteredAllocations.add(allocations.get(i));
                filteredSpaces.add(spaces.get(i));
            }
        }
        int six6 = shortfall[1];
        int backup6 = shortfall[0];
        List<int[]> fives6 = new ArrayList<>();
        Set<Integer> used6 = new HashSet<>(used5);
        List<int[]> init = new ArrayList<>();

        List<Object> trial = foursFlipped(filteredAllocations, filteredSpaces, new int[]{backup6, six6}, backupSize, used6);
        if (!((List<?>) trial.get(1)).isEmpty()) {
            finalCombos = (List<int[]>) trial.get(0);
            finalInit = (List<int[]>) trial.get(1);
            return Arrays.asList(finalCombos, finalInit);
        }

        for (int g = filteredSpaces.size() - 5; g >= 0; g--) {
            if (six6 == 0 && backup6 == 0) break;
            for (int h = filteredSpaces.size() - 4; h > g; h--) {
                if (six6 == 0 && backup6 == 0) break;
                for (int i = filteredSpaces.size() - 3; i > h; i--) {
                    if (six6 == 0 && backup6 == 0) break;
                    for (int j = filteredSpaces.size() - 2; j > i; j--) {
                        if (six6 == 0 && backup6 == 0) break;
                        for (int k = filteredSpaces.size() - 1; k > j; k--) {
                            if (six6 == 0 && backup6 == 0) break;
                            int value1 = filteredSpaces.get(i);
                            int value2 = filteredSpaces.get(j);
                            int value3 = filteredSpaces.get(k);
                            int value4 = filteredSpaces.get(h);
                            int value5= filteredSpaces.get(g);
                            int totalSum = sum(new int[]{value1, value2, value3, value4, value5});

                            if (!used6.contains(i) && !used6.contains(j) && !used6.contains(k) && !used6.contains(h) && !used6.contains(g) &&
                                    totalSum >= (4 * Math.max(backupSize, 6)) &&
                                    totalSum - Math.min(Math.min(Math.min(Math.min(value1, value2), value3), value4), value5) < (4 * Math.max(backupSize, 6)) &&
                                    (six6 > 3 || backup6 > 3)) {

                                if (backupSize == 7 && backup6 >= 4) {
                                    backup6 -= 4;
                                    used6.add(g);
                                    used6.add(h);
                                    used6.add(i);
                                    used6.add(j);
                                    used6.add(k);
                                    fives6.add(new int[]{g+1, h + 1, i + 1, k + 1, j + 1});
                                    init.add(new int[]{4, 0});
                                } else if (backupSize == 5 && six6 >= 4) {
                                    six6 -= 4;
                                    used6.add(g);
                                    used6.add(h);
                                    used6.add(i);
                                    used6.add(j);
                                    used6.add(k);
                                    fives6.add(new int[]{g+1, h + 1, i + 1, k + 1, j + 1});
                                    init.add(new int[]{0, 4});
                                }
                                if (six6==0 && backup6==0){
                                    return Arrays.asList(fives6, init);
                                }
                                List<Object> trial1 = foursFlipped(filteredAllocations, filteredSpaces, new int[]{backup6, six6}, backupSize, used6);
                                if (!((List<?>) trial1.get(1)).isEmpty()) {
                                    finalCombos = mergeLists((List<int[]>) trial1.get(0),fives6);
                                    finalInit = mergeLists((List<int[]>) trial1.get(1),init);
                                    return Arrays.asList(finalCombos, finalInit);
                                }
                            }
                        }
                    }
                }
            }
        }
        int six7 = six6;
        int backup7 = backup6;
        List<int[]> fives7 = new ArrayList<>(fives6);
        Set<Integer> used7 = new HashSet<>(used6);
        List<int[]> init1 = new ArrayList<>(init);

        if (backupSize==7) {

            for (int g = filteredSpaces.size() - 5; g >= 0; g--) {
                if (six7 == 0 && backup7 == 0) break;
                for (int h = filteredSpaces.size() - 4; h > g; h--) {
                    if (six7 == 0 && backup7 == 0) break;
                    for (int i = filteredSpaces.size() - 3; i > h; i--) {
                        if (six7 == 0 && backup7 == 0) break;
                        for (int j = filteredSpaces.size() - 2; j > i; j--) {
                            if (six7 == 0 && backup7 == 0) break;
                            for (int k = filteredSpaces.size() - 1; k > j; k--) {
                                if (six7 == 0 && backup7 == 0) break;
                                int value1 = filteredSpaces.get(i);
                                int value2 = filteredSpaces.get(j);
                                int value3 = filteredSpaces.get(k);
                                int value4 = filteredSpaces.get(h);
                                int value5 = filteredSpaces.get(g);
                                int totalSum = sum(new int[]{value1, value2, value3, value4, value5});

                                if (!used7.contains(i) && !used7.contains(j) && !used7.contains(k) && !used7.contains(h) && !used7.contains(g) &&
                                        totalSum >= (3 * 7 + 6) &&
                                        totalSum - Math.min(Math.min(Math.min(Math.min(value1, value2), value3), value4),value5) < (3 * 7 + 6) &&
                                        (six7 > 0 && backup7 > 2)) {
                                    backup7 -= 3;
                                    six7 -= 1;
                                    used7.add(g);
                                    used7.add(h);
                                    used7.add(i);
                                    used7.add(j);
                                    used7.add(k);
                                    fives7.add(new int[]{g+1, h + 1, i + 1, k + 1, j + 1});
                                    init1.add(new int[]{3, 1});

                                    if (six7==0 && backup7==0){
                                        return Arrays.asList(fives7, init1);
                                    }
                                    List<Object> trial2 = foursFlipped(filteredAllocations, filteredSpaces, new int[]{backup7, six7}, backupSize, used7);
                                    if (!((List<?>) trial2.get(1)).isEmpty()) {
                                        finalCombos = mergeLists((List<int[]>) trial2.get(0),fives7);
                                        finalInit = mergeLists((List<int[]>) trial2.get(1),init1);
                                        return Arrays.asList(finalCombos, finalInit);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        int six8 = six7;
        int backup8 = backup7;
        List<int[]> fives8 = new ArrayList<>(fives7);
        Set<Integer> used8 = new HashSet<>(used7);
        List<int[]> init2 = new ArrayList<>(init1);

        for (int g = filteredSpaces.size() - 5; g >= 0; g--) {
            if (six8 == 0 && backup8 == 0) break;
            for (int h = filteredSpaces.size() - 4; h >g; h--) {
                if (six8 == 0 && backup8 == 0) break;
                for (int i = filteredSpaces.size() - 3; i > h; i--) {
                    if (six8 == 0 && backup8 == 0) break;
                    for (int j = filteredSpaces.size() - 2; j > i; j--) {
                        if (six8 == 0 && backup8 == 0) break;
                        for (int k = filteredSpaces.size() - 1; k > j; k--) {
                            if (six8 == 0 && backup8 == 0) break;
                            int value1 = filteredSpaces.get(i);
                            int value2 = filteredSpaces.get(j);
                            int value3 = filteredSpaces.get(k);
                            int value4 = filteredSpaces.get(h);
                            int value5 = filteredSpaces.get(g);
                            int totalSum = sum(new int[]{value1, value2, value3, value4,value5});

                            if (!used8.contains(i) && !used8.contains(j) && !used8.contains(k) && !used8.contains(h) && !used8.contains(g) &&
                                    totalSum >= 4* Math.min(backupSize, 6) &&
                                    totalSum - Math.min(Math.min(Math.min(Math.min(value1, value2), value3), value4),value5) < (4 * Math.min(backupSize, 6)) &&
                                    (six8 > 3 || backup8 > 3)) {

                                if (backupSize == 7 && six8 >= 4) {
                                    six8 -= 4;
                                    used8.add(g);
                                    used8.add(h);
                                    used8.add(i);
                                    used8.add(j);
                                    used8.add(k);
                                    fives8.add(new int[]{g+1, h + 1, i + 1, k + 1, j + 1});
                                    init2.add(new int[]{0, 4});
                                } else if (backupSize == 5 && backup8 >= 4) {
                                    backup8 -= 4;
                                    used8.add(g);
                                    used8.add(h);
                                    used8.add(i);
                                    used8.add(j);
                                    used8.add(k);
                                    fives8.add(new int[]{g+1, h + 1, i + 1, k + 1, j + 1});
                                    init2.add(new int[]{4, 0});
                                }
                                if (six8==0 && backup8==0){
                                    return Arrays.asList(fives8, init2);
                                }
                                List<Object> trial3 = foursFlipped(filteredAllocations, filteredSpaces, new int[]{backup8, six8}, backupSize, used8);
                                if (!((List<?>) trial3.get(1)).isEmpty()) {
                                    finalCombos = mergeLists((List<int[]>) trial3.get(0),fives8);
                                    finalInit = mergeLists((List<int[]>) trial3.get(1),init2);
                                    return Arrays.asList(finalCombos, finalInit);
                                }
                            }
                        }
                    }
                }
            }
        }


        int six9 = six8;
        int backup9 = backup8;
        List<int[]> fives9 = new ArrayList<>(fives8);
        Set<Integer> used9 = new HashSet<>(used8);
        List<int[]> init3 = new ArrayList<>(init2);

        for (int g = filteredSpaces.size() - 5; g >= 0; g--) {
            if (six9 == 0 && backup9 == 0) break;
            for (int h = filteredSpaces.size() - 4; h > g; h--) {
                if (six9 == 0 && backup9 == 0) break;
                for (int i = filteredSpaces.size() - 3; i > h; i--) {
                    if (six9 == 0 && backup9 == 0) break;
                    for (int j = filteredSpaces.size() - 2; j > i; j--) {
                        if (six9 == 0 && backup9 == 0) break;
                        for (int k = filteredSpaces.size() - 1; k > j; k--) {
                            if (six9 == 0 && backup9 == 0) break;
                            int value1 = filteredSpaces.get(i);
                            int value2 = filteredSpaces.get(j);
                            int value3 = filteredSpaces.get(k);
                            int value4 = filteredSpaces.get(h);
                            int value5 = filteredSpaces.get(g);
                            int totalSum = sum(new int[]{value1, value2, value3, value4, value5});

                            if (!used9.contains(i) && !used9.contains(j) && !used9.contains(k) && !used9.contains(h) && !used9.contains(g) &&
                                    totalSum >= (Math.max(backupSize, 6)) &&
                                    totalSum - Math.min(Math.min(Math.min(Math.min(value1, value2), value3), value4), value5) < (Math.max(backupSize, 6)) &&
                                    (six9 > 0 || backup9 > 0)) {

                                if (backupSize == 7 && backup9 >= 1) {
                                    backup9 -= 1;
                                    used9.add(g);
                                    used9.add(h);
                                    used9.add(i);
                                    used9.add(j);
                                    used9.add(k);
                                    fives9.add(new int[]{g+1, h + 1, i + 1, k + 1, j + 1});
                                    init3.add(new int[]{1, 0});
                                } else if (backupSize == 5 && six9 >= 1) {
                                    six9 -= 1;
                                    used9.add(g);
                                    used9.add(h);
                                    used9.add(i);
                                    used9.add(j);
                                    used9.add(k);
                                    fives9.add(new int[]{g+1, h + 1, i + 1, k + 1, j + 1});
                                    init3.add(new int[]{0, 1});
                                }
                                if (six9==0 && backup9==0){
                                    return Arrays.asList(fives9, init3);
                                }
                                List<Object> trial4 = foursFlipped(filteredAllocations, filteredSpaces, new int[]{backup9, six9}, backupSize, used9);
                                if (!((List<?>) trial4.get(1)).isEmpty()) {
                                    finalCombos = mergeLists((List<int[]>) trial4.get(0),fives9);
                                    finalInit = mergeLists((List<int[]>) trial4.get(1),init3);
                                    return Arrays.asList(finalCombos, finalInit);
                                }
                            }
                        }
                    }
                }
            }
        }


        int six10 = six9;
        int backup10 = backup9;
        List<int[]> fives10 = new ArrayList<>(fives9);
        Set<Integer> used10 = new HashSet<>(used9);
        List<int[]> init4 = new ArrayList<>(init3);

        for (int g = filteredSpaces.size() - 5; g >= 0; g--) {
            if (six10 == 0 && backup10 == 0) break;
            for (int h = filteredSpaces.size() - 4; h > g; h--) {
                if (six10 == 0 && backup10 == 0) break;
                for (int i = filteredSpaces.size() - 3; i > h; i--) {
                    if (six10 == 0 && backup10 == 0) break;
                    for (int j = filteredSpaces.size() - 2; j > i; j--) {
                        if (six10 == 0 && backup10 == 0) break;
                        for (int k = filteredSpaces.size() - 1; k > j; k--) {
                            if (six10 == 0 && backup10 == 0) break;
                            int value1 = filteredSpaces.get(i);
                            int value2 = filteredSpaces.get(j);
                            int value3 = filteredSpaces.get(k);
                            int value4 = filteredSpaces.get(h);
                            int value5 = filteredSpaces.get(g);
                            int totalSum = sum(new int[]{value1, value2, value3, value4, value5});

                            if (!used10.contains(i) && !used10.contains(j) && !used10.contains(k) && !used10.contains(h) && !used10.contains(g) &&
                                    totalSum >= (Math.min(backupSize, 6)) &&
                                    totalSum - Math.min(Math.min(Math.min(Math.min(value1, value2), value3), value4), value5) < (Math.min(backupSize, 6)) &&
                                    (six10 > 0 || backup10 > 0)) {

                                if (backupSize == 7 && six10 >= 1) {
                                    six10 -= 1;
                                    used10.add(g);
                                    used10.add(h);
                                    used10.add(i);
                                    used10.add(j);
                                    used10.add(k);
                                    fives10.add(new int[]{g+1, h + 1, i + 1, k + 1, j + 1});
                                    init4.add(new int[]{0, 1});
                                } else if (backupSize == 5 && backup10 >= 1) {
                                    backup10 -= 1;
                                    used10.add(g);
                                    used10.add(h);
                                    used10.add(i);
                                    used10.add(j);
                                    used10.add(k);
                                    fives10.add(new int[]{g+1, h + 1, i + 1, k + 1, j + 1});
                                    init4.add(new int[]{1, 0});
                                }
                                if (six10==0 && backup10==0){
                                    return Arrays.asList(fives10, init4);
                                }
                                List<Object> trial5 = foursFlipped(filteredAllocations, filteredSpaces, new int[]{backup10, six10}, backupSize, used10);
                                if (!((List<?>) trial5.get(1)).isEmpty()) {
                                    finalCombos = mergeLists((List<int[]>) trial5.get(0),fives10);
                                    finalInit = mergeLists((List<int[]>) trial5.get(1),init4);
                                    return Arrays.asList(finalCombos, finalInit);
                                }
                            }
                        }
                    }
                }
            }
        }
        return Arrays.asList(new ArrayList<>(), new ArrayList<>());
    }

    public static List<Object> sixesFlipped(List<int[]> allocations, List<Integer> spaces, int[] shortfall, int backupSize, Set<Integer> used5) {
        if (used5 == null) {
            used5 = new HashSet<>();
        }

        if (sum1(spaces)<backupSize*shortfall[0]+6*shortfall[1]) {
            return Arrays.asList(new ArrayList<>(), new ArrayList<>());
        }

        List<int[]> filteredAllocations = new ArrayList<>();
        List<Integer> filteredSpaces = new ArrayList<>();

        List<int[]> finalCombos = new ArrayList<>();
        List<int[]> finalInit = new ArrayList<>();

        for (int i = 0; i < spaces.size(); i++) {
            if (spaces.get(i) != 0) {
                filteredAllocations.add(allocations.get(i));
                filteredSpaces.add(spaces.get(i));
            }
        }
        int six6 = shortfall[1];
        int backup6 = shortfall[0];
        List<int[]> sixes6 = new ArrayList<>();
        Set<Integer> used6 = new HashSet<>(used5);
        List<int[]> init = new ArrayList<>();

        List<Object> trial = fivesFlipped(filteredAllocations, filteredSpaces, new int[]{backup6, six6}, backupSize, used6);
        if (!((List<?>) trial.get(1)).isEmpty()) {
            finalCombos = (List<int[]>) trial.get(0);
            finalInit = (List<int[]>) trial.get(1);
            return Arrays.asList(finalCombos, finalInit);
        }

        for (int f = filteredSpaces.size() - 6; f >= 0; f--) {
            if (six6 == 0 && backup6 == 0) break;
            for (int g = filteredSpaces.size() - 5; g > f; g--) {
                if (six6 == 0 && backup6 == 0) break;
                for (int h = filteredSpaces.size() - 4; h > g; h--) {
                    if (six6 == 0 && backup6 == 0) break;
                    for (int i = filteredSpaces.size() - 3; i > h; i--) {
                        if (six6 == 0 && backup6 == 0) break;
                        for (int j = filteredSpaces.size() - 2; j > i; j--) {
                            if (six6 == 0 && backup6 == 0) break;
                            for (int k = filteredSpaces.size() - 1; k > j; k--) {
                                if (six6 == 0 && backup6 == 0) break;
                                int value1 = filteredSpaces.get(i);
                                int value2 = filteredSpaces.get(j);
                                int value3 = filteredSpaces.get(k);
                                int value4 = filteredSpaces.get(h);
                                int value5 = filteredSpaces.get(g);
                                int value6 = filteredSpaces.get(f);
                                int totalSum = sum(new int[]{value1, value2, value3, value4, value5, value6});

                                if (!used6.contains(i) && !used6.contains(j) && !used6.contains(k) && !used6.contains(h) && !used6.contains(g) && !used6.contains(f) &&
                                        totalSum >= (5 * Math.max(backupSize, 6)) &&
                                        totalSum - Math.min(Math.min(Math.min(Math.min(Math.min(value1, value2), value3), value4), value5), value6) < (5 * Math.max(backupSize, 6)) &&
                                        (six6 > 4 || backup6 > 4)) {
                                    if (backupSize == 7 && backup6 >= 5) {
                                        backup6 -= 5;
                                        used6.add(f);
                                        used6.add(g);
                                        used6.add(h);
                                        used6.add(i);
                                        used6.add(j);
                                        used6.add(k);
                                        sixes6.add(new int[]{f+1, g + 1, h + 1, i + 1, k + 1, j + 1});
                                        init.add(new int[]{5, 0});
                                    } else if (backupSize == 5 && six6 >= 5) {
                                        six6 -= 5;
                                        used6.add(f);
                                        used6.add(g);
                                        used6.add(h);
                                        used6.add(i);
                                        used6.add(j);
                                        used6.add(k);
                                        sixes6.add(new int[]{f+1, g + 1, h + 1, i + 1, k + 1, j + 1});
                                        init.add(new int[]{0, 5});
                                    }
                                    if (six6==0 && backup6==0){
                                        return Arrays.asList(sixes6, init);
                                    }
                                    List<Object> trial1 = fivesFlipped(filteredAllocations, filteredSpaces, new int[]{backup6, six6}, backupSize, used6);
                                    if (!((List<?>) trial1.get(1)).isEmpty()) {
                                        finalCombos = mergeLists((List<int[]>) trial1.get(0),sixes6);
                                        finalInit = mergeLists((List<int[]>) trial1.get(1),init);
                                        return Arrays.asList(finalCombos, finalInit);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        int six7 = six6;
        int backup7 = backup6;
        List<int[]> sixes7 = new ArrayList<>(sixes6);
        Set<Integer> used7 = new HashSet<>(used6);
        List<int[]> init1 = new ArrayList<>(init);

        if (backupSize==7) {
            for (int f = filteredSpaces.size() - 6; f >= 0; f--) {
                if (six7 == 0 && backup7 == 0) break;
                for (int g = filteredSpaces.size() - 5; g > f; g--) {
                    if (six7 == 0 && backup7 == 0) break;
                    for (int h = filteredSpaces.size() - 4; h > g; h--) {
                        if (six7 == 0 && backup7 == 0) break;
                        for (int i = filteredSpaces.size() - 3; i > h; i--) {
                            if (six7 == 0 && backup7 == 0) break;
                            for (int j = filteredSpaces.size() - 2; j > i; j--) {
                                if (six7 == 0 && backup7 == 0) break;
                                for (int k = filteredSpaces.size() - 1; k > j; k--) {
                                    if (six7 == 0 && backup7 == 0) break;
                                    int value1 = filteredSpaces.get(i);
                                    int value2 = filteredSpaces.get(j);
                                    int value3 = filteredSpaces.get(k);
                                    int value4 = filteredSpaces.get(h);
                                    int value5 = filteredSpaces.get(g);
                                    int value6 = filteredSpaces.get(f);
                                    int totalSum = sum(new int[]{value1, value2, value3, value4, value5, value6});

                                    if (!used7.contains(i) && !used7.contains(j) && !used7.contains(k) && !used7.contains(h) && !used7.contains(g) && !used7.contains(f) &&
                                            totalSum >= 30 &&
                                            totalSum - Math.min(Math.min(Math.min(Math.min(Math.min(value1, value2), value3), value4), value5), value6) < 30 &&
                                            (six7 > 4 || backup7 >=0)) {
                                        six7 -= 5;
                                        used7.add(f);
                                        used7.add(g);
                                        used7.add(h);
                                        used7.add(i);
                                        used7.add(j);
                                        used7.add(k);
                                        sixes7.add(new int[]{f+1, g + 1, h + 1, i + 1, k + 1, j + 1});
                                        init1.add(new int[]{0, 5});

                                        if (six7==0 && backup7==0){
                                            return Arrays.asList(sixes7, init1);
                                        }
                                        List<Object> trial2 = fivesFlipped(filteredAllocations, filteredSpaces, new int[]{backup7, six7}, backupSize, used7);
                                        if (!((List<?>) trial2.get(1)).isEmpty()) {
                                            finalCombos = mergeLists((List<int[]>) trial2.get(0),sixes6);
                                            finalInit = mergeLists((List<int[]>) trial2.get(1),init);
                                            return Arrays.asList(finalCombos, finalInit);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        int six8 = six7;
        int backup8 = backup7;
        List<int[]> sixes8 = new ArrayList<>(sixes7);
        Set<Integer> used8 = new HashSet<>(used7);
        List<int[]> init2 = new ArrayList<>(init1);

        for (int f = filteredSpaces.size() - 6; f >= 0; f--) {
            if (six8 == 0 && backup8 == 0) break;
            for (int g = filteredSpaces.size() - 5; g > f; g--) {
                if (six8 == 0 && backup8 == 0) break;
                for (int h = filteredSpaces.size() - 4; h > g; h--) {
                    if (six8 == 0 && backup8 == 0) break;
                    for (int i = filteredSpaces.size() - 3; i > h; i--) {
                        if (six8 == 0 && backup8 == 0) break;
                        for (int j = filteredSpaces.size() - 2; j > i; j--) {
                            if (six8 == 0 && backup8 == 0) break;
                            for (int k = filteredSpaces.size() - 1; k > j; k--) {
                                if (six8 == 0 && backup8 == 0) break;
                                int value1 = filteredSpaces.get(i);
                                int value2 = filteredSpaces.get(j);
                                int value3 = filteredSpaces.get(k);
                                int value4 = filteredSpaces.get(h);
                                int value5 = filteredSpaces.get(g);
                                int value6 = filteredSpaces.get(f);
                                int totalSum = sum(new int[]{value1, value2, value3, value4, value5, value6});

                                if (!used8.contains(i) && !used8.contains(j) && !used8.contains(k) && !used8.contains(h) && !used8.contains(g) && !used8.contains(f) &&
                                        totalSum >= Math.max(backupSize, 6) &&
                                        totalSum - Math.min(Math.min(Math.min(Math.min(Math.min(value1, value2), value3), value4), value5),value6) < ( Math.max(backupSize, 6)) &&
                                        (six8 > 0 || backup8 > 0)) {

                                    if (backupSize == 7 && backup8 >= 0) {
                                        backup8 -= 1;
                                        used8.add(f);
                                        used8.add(g);
                                        used8.add(h);
                                        used8.add(i);
                                        used8.add(j);
                                        used8.add(k);
                                        sixes8.add(new int[]{f+1, g + 1, h + 1, i + 1, k + 1, j + 1});
                                        init2.add(new int[]{1, 0});
                                    } else if (backupSize == 5 && six8 >= 1) {
                                        six8 -= 1;
                                        used8.add(f);
                                        used8.add(g);
                                        used8.add(h);
                                        used8.add(i);
                                        used8.add(j);
                                        used8.add(k);
                                        sixes8.add(new int[]{f+1, g + 1, h + 1, i + 1, k + 1, j + 1});
                                        init2.add(new int[]{0, 1});
                                    }
                                    if (six8==0 && backup8==0){
                                        return Arrays.asList(sixes8, init2);
                                    }
                                    List<Object> trial3 = fivesFlipped(filteredAllocations, filteredSpaces, new int[]{backup8, six8}, backupSize, used8);
                                    if (!((List<?>) trial3.get(1)).isEmpty()) {
                                        finalCombos = mergeLists((List<int[]>) trial3.get(0),sixes6);
                                        finalInit = mergeLists((List<int[]>) trial3.get(1),init);
                                        return Arrays.asList(finalCombos, finalInit);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (backupSize == 7) {
            int six9 = six8;
            int backup9 = backup8;
            List<int[]> sixes9 = new ArrayList<>(sixes8);
            Set<Integer> used9 = new HashSet<>(used8);
            List<int[]> init3 = new ArrayList<>(init2);

            for (int f = filteredSpaces.size() - 6; f >= 0; f--) {
                if (six9 == 0 && backup9 == 0) break;
                for (int g = filteredSpaces.size() - 5; g >f ; g--) {
                    if (six9 == 0 && backup9 == 0) break;
                    for (int h = filteredSpaces.size() - 4; h > g; h--) {
                        if (six9 == 0 && backup9 == 0) break;
                        for (int i = filteredSpaces.size() - 3; i > h; i--) {
                            if (six9 == 0 && backup9 == 0) break;
                            for (int j = filteredSpaces.size() - 2; j > i; j--) {
                                if (six9 == 0 && backup9 == 0) break;
                                for (int k = filteredSpaces.size() - 1; k > j; k--) {
                                    if (six9 == 0 && backup9 == 0) break;
                                    int value1 = filteredSpaces.get(i);
                                    int value2 = filteredSpaces.get(j);
                                    int value3 = filteredSpaces.get(k);
                                    int value4 = filteredSpaces.get(h);
                                    int value5 = filteredSpaces.get(g);
                                    int value6 = filteredSpaces.get(f);
                                    int totalSum = sum(new int[]{value1, value2, value3, value4, value5, value6});

                                    if (!used9.contains(i) && !used9.contains(j) && !used9.contains(k) && !used9.contains(h) && !used9.contains(g) && !used9.contains(f) &&
                                            totalSum >= (Math.min(backupSize, 6)) &&
                                            totalSum - Math.min(Math.min(Math.min(Math.min(Math.min(value1, value2), value3), value4),value5),value6) < (Math.min(backupSize, 6)) &&
                                            (six9 > 0 || backup9 > 0)) {

                                        if (backupSize == 7 && six9 >= 1) {
                                            six9 -= 1;
                                            used9.add(f);
                                            used9.add(g);
                                            used9.add(h);
                                            used9.add(i);
                                            used9.add(j);
                                            used9.add(k);
                                            sixes9.add(new int[]{f+1, g + 1, h + 1, i + 1, k + 1, j + 1});
                                            init3.add(new int[]{0, 1});
                                        } else if (backupSize == 5 && backup9 >= 1) {
                                            backup9 -= 1;
                                            used9.add(f);
                                            used9.add(g);
                                            used9.add(h);
                                            used9.add(i);
                                            used9.add(j);
                                            used9.add(k);
                                            sixes9.add(new int[]{f+1, g + 1, h + 1, i + 1, k + 1, j + 1});
                                            init3.add(new int[]{1, 0});
                                        }
                                        if (six9==0 && backup9==0){
                                            return Arrays.asList(sixes9, init3);
                                        }
                                        List<Object> trial4 = fivesFlipped(filteredAllocations, filteredSpaces, new int[]{backup9, six9}, backupSize, used9);
                                        if (!((List<?>) trial4.get(1)).isEmpty()) {
                                            finalCombos = mergeLists((List<int[]>) trial4.get(0),sixes6);
                                            finalInit = mergeLists((List<int[]>) trial4.get(1),init);
                                            return Arrays.asList(finalCombos, finalInit);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return Arrays.asList(new ArrayList<>(), new ArrayList<>());
    }

    public static List<Object> sevensFlipped(List<int[]> allocations, List<Integer> spaces, int[] shortfall, int backupSize, Set<Integer> used5) {
        if (used5 == null) {
            used5 = new HashSet<>();
        }
        if (sum1(spaces)<backupSize*shortfall[0]+6*shortfall[1]) {
            return Arrays.asList(new ArrayList<>(), new ArrayList<>());
        }

        List<int[]> filteredAllocations = new ArrayList<>();
        List<Integer> filteredSpaces = new ArrayList<>();

        List<int[]> finalCombos = new ArrayList<>();
        List<int[]> finalInit = new ArrayList<>();

        for (int i = 0; i < spaces.size(); i++) {
            if (spaces.get(i) != 0) {
                filteredAllocations.add(allocations.get(i));
                filteredSpaces.add(spaces.get(i));
            }
        }

        int six6 = shortfall[1];
        int backup6 = shortfall[0];
        List<int[]> sevens6 = new ArrayList<>();
        Set<Integer> used6 = new HashSet<>(used5);
        List<int[]> init = new ArrayList<>();

        List<Object> trial = sixesFlipped(filteredAllocations, filteredSpaces, new int[]{backup6, six6}, backupSize, used6);
        if (!((List<?>) trial.get(1)).isEmpty()) {
            finalCombos = (List<int[]>) trial.get(0);
            finalInit = (List<int[]>) trial.get(1);
            return Arrays.asList(finalCombos, finalInit);
        }

        if (backupSize == 7) {
            for (int e = filteredSpaces.size() - 6; e >= 0; e--) {
                if (six6 == 0 && backup6 == 0) break;
                for (int f = filteredSpaces.size() - 6; f > e; f--) {
                    if (six6 == 0 && backup6 == 0) break;
                    for (int g = filteredSpaces.size() - 5; g > f; g--) {
                        if (six6 == 0 && backup6 == 0) break;
                        for (int h = filteredSpaces.size() - 4; h > g; h--) {
                            if (six6 == 0 && backup6 == 0) break;
                            for (int i = filteredSpaces.size() - 3; i > h; i--) {
                                if (six6 == 0 && backup6 == 0) break;
                                for (int j = filteredSpaces.size() - 2; j > i; j--) {
                                    if (six6 == 0 && backup6 == 0) break;
                                    for (int k = filteredSpaces.size() - 1; k > j; k--) {
                                        if (six6 == 0 && backup6 == 0) break;
                                        int value1 = filteredSpaces.get(i);
                                        int value2 = filteredSpaces.get(j);
                                        int value3 = filteredSpaces.get(k);
                                        int value4 = filteredSpaces.get(h);
                                        int value5 = filteredSpaces.get(g);
                                        int value6 = filteredSpaces.get(f);
                                        int value7 = filteredSpaces.get(e);
                                        int totalSum = sum(new int[]{value1, value2, value3, value4, value5, value6, value7});

                                        if (!used6.contains(i) && !used6.contains(j) && !used6.contains(k) && !used6.contains(h) && !used6.contains(g) && !used6.contains(f) && !used6.contains(e) &&
                                                totalSum >= (42) &&
                                                totalSum - Math.min(Math.min(Math.min(Math.min(Math.min(Math.min(value1, value2), value3), value4), value5), value6), value7) < (42) &&
                                                (six6 >= 0 && backup6 > 5)) {
                                            if (backupSize == 7) {
                                                backup6 -= 6;
                                                used6.add(e);
                                                used6.add(f);
                                                used6.add(g);
                                                used6.add(h);
                                                used6.add(i);
                                                used6.add(j);
                                                used6.add(k);
                                                sevens6.add(new int[]{e + 1, f + 1, g + 1, h + 1, i + 1, k + 1, j + 1});
                                                init.add(new int[]{6, 0});
                                            }
                                            if (six6==0 && backup6==0){
                                                return Arrays.asList(sevens6, init);
                                            }
                                            List<Object> trial1 = sixesFlipped(filteredAllocations, filteredSpaces, new int[]{backup6, six6}, backupSize, used6);
                                            if (!((List<?>) trial1.get(1)).isEmpty()) {
                                                finalCombos = mergeLists((List<int[]>) trial1.get(0),sevens6);
                                                finalInit = mergeLists((List<int[]>) trial1.get(1),init);
                                                return Arrays.asList(finalCombos, finalInit);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            int six7 = six6;
            int backup7 = backup6;
            List<int[]> sevens7 = new ArrayList<>(sevens6);
            Set<Integer> used7 = new HashSet<>(used6);
            List<int[]> init1 = new ArrayList<>(init);

            for (int e = filteredSpaces.size() - 7; e >= 0; e--) {
                if (six7 == 0 && backup7 == 0) break;
                for (int f = filteredSpaces.size() - 6; f > e; f--) {
                    if (six7 == 0 && backup7 == 0) break;
                    for (int g = filteredSpaces.size() - 5; g > f; g--) {
                        if (six7 == 0 && backup7 == 0) break;
                        for (int h = filteredSpaces.size() - 4; h > g; h--) {
                            if (six7 == 0 && backup7 == 0) break;
                            for (int i = filteredSpaces.size() - 3; i > h; i--) {
                                if (six7 == 0 && backup7 == 0) break;
                                for (int j = filteredSpaces.size() - 2; j > i; j--) {
                                    if (six7 == 0 && backup7 == 0) break;
                                    for (int k = filteredSpaces.size() - 1; k > j; k--) {
                                        if (six7 == 0 && backup7 == 0) break;
                                        int value1 = filteredSpaces.get(i);
                                        int value2 = filteredSpaces.get(j);
                                        int value3 = filteredSpaces.get(k);
                                        int value4 = filteredSpaces.get(h);
                                        int value5 = filteredSpaces.get(g);
                                        int value6 = filteredSpaces.get(f);
                                        int value7 = filteredSpaces.get(e);
                                        int totalSum = sum(new int[]{value1, value2, value3, value4, value5, value6, value7});

                                        if (!used7.contains(i) && !used7.contains(j) && !used7.contains(k) && !used7.contains(h) && !used7.contains(g) && !used7.contains(f) && !used7.contains(e) &&
                                                totalSum >= 7 &&
                                                totalSum - Math.min(Math.min(Math.min(Math.min(Math.min(Math.min(value1, value2), value3), value4), value5), value6), value7) < 7 &&
                                                (six7 >= 0 && backup7 >= 1)) {
                                            backup7 -= 1;
                                            used7.add(e);
                                            used7.add(f);
                                            used7.add(g);
                                            used7.add(h);
                                            used7.add(i);
                                            used7.add(j);
                                            used7.add(k);
                                            sevens7.add(new int[]{e + 1, f + 1, g + 1, h + 1, i + 1, k + 1, j + 1});
                                            init1.add(new int[]{1, 0});

                                            if (six7==0 && backup7==0){
                                                return Arrays.asList(sevens7, init1);
                                            }

                                            List<Object> trial2 = sixesFlipped(filteredAllocations, filteredSpaces, new int[]{backup7, six7}, backupSize, used7);
                                            if (!((List<?>) trial2.get(1)).isEmpty()) {
                                                finalCombos = mergeLists((List<int[]>) trial2.get(0),sevens6);
                                                finalInit = mergeLists((List<int[]>) trial2.get(1),init);
                                                return Arrays.asList(finalCombos, finalInit);
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
        return Arrays.asList(new ArrayList<>(), new ArrayList<>());
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
                                    if (item[0]*backupSize + item[1]*6>=space1After && backupSize*otherSide[0] + 6*otherSide[1]>=space2After) {
                                        allocations.set(i,item);
                                        allocations.set(j,otherSide);
                                        indexCombos.set(i, newCombo1);
                                        indexCombos.set(j, newCombo2);
                                        progress = true;
                                        break;
                                    }
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
                                            if (item[0]*backupSize + item[1]*6>=space1After && backupSize*otherSide[0] + 6*otherSide[1]>=space2After) {
                                                allocations.set(i,item);
                                                allocations.set(j,otherSide);
                                                indexCombos.set(i, newCombo1);
                                                indexCombos.set(j, newCombo2);
                                                secondaryProgress = true;
                                                break;
                                            }
                                        }
                                    }
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
        return result;
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

}