package com.hellozjf.learn.java8inaction.chapter5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hellozjf
 */
public class Test5_2 {

    private static void question2() {
        List<Integer> list1 = Arrays.asList(1, 2, 3);
        List<Integer> list2 = Arrays.asList(3, 4);
        List<int[]> result = list1.stream()
                .flatMap(i -> list2.stream()
                        .filter(j -> (i + j) % 3 == 0)
                        .map(j -> new int[] {i, j})
                )
                .collect(Collectors.toList());

        result.stream().map(e -> e[0] + "," + e[1]).forEach(System.out::println);
    }

    public static void main(String[] args) {
//        List<List<Integer>> result = mmDikaerji(Arrays.asList(1, 2), Arrays.asList(3, 4), Arrays.asList(5, 6));
//        System.out.println(result);
        question2();
    }

    public static List<List<Integer>> mmDikaerji(List<Integer>... lists) {
        List<List<Integer>> result = dikaerji(lists[0], lists[1]);
        for (int i = 2; i < lists.length; i++) {
            result = multiDikaerji(result, lists[2]);
        }
        return result;
    }

    public static List<List<Integer>> multiDikaerji(List<List<Integer>> a, List<Integer> b) {
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < a.size(); i++) {
            for (int j = 0; j < b.size(); j++) {
                List<Integer> t = new ArrayList<>();
                t.addAll(a.get(i));
                t.add(b.get(j));
                result.add(t);
            }
        }
        return result;
    }

    public static List<List<Integer>> dikaerji(List<Integer> a, List<Integer> b) {
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < a.size(); i++) {
            for (int j = 0; j < b.size(); j++) {
                List<Integer> t = new ArrayList<>();
                t.add(a.get(i));
                t.add(b.get(i));
                result.add(t);
            }
        }
        return result;
    }
}
