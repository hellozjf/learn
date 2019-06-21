package com.hellozjf.learn.java8inaction.chapter5;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jingfeng Zhou
 */
public class Test5_3 {
    public static void main(String[] args) {
        Trader raoul = new Trader("Raoul", "Cambridge");
        Trader mario = new Trader("Mario","Milan");
        Trader alan = new Trader("Alan","Cambridge");
        Trader brian = new Trader("Brian","Cambridge");
        List<Transaction> transactions = Arrays.asList(
                new Transaction(brian, 2011, 300),
                new Transaction(raoul, 2012, 1000),
                new Transaction(raoul, 2011, 400),
                new Transaction(mario, 2012, 710),
                new Transaction(mario, 2012, 700),
                new Transaction(alan, 2012, 950)
        );

        // 找出2011年发生的所有交易，并按交易额排序（从低到高）。
        List<Transaction> q1 = transactions.stream()
                .filter(transaction -> transaction.getYear() == 2011)
                .sorted(Comparator.comparingInt(Transaction::getValue))
                .collect(Collectors.toList());
        System.out.println(q1);
        Set<Transaction> q11 = transactions.stream()
                .filter(transaction -> transaction.getYear() == 2011)
                .collect(Collectors.toSet());
        System.out.println(q11);

        // 交易员都在哪些不同的城市工作过？
        List<String> q2 = transactions.stream()
                .map(transaction -> transaction.getTrader().getCity())
                .distinct()
                .collect(Collectors.toList());
        System.out.println(q2);

        // 查找所有来自于剑桥的交易员，并按姓名排序。
        List<Trader> q3 = transactions.stream()
                .map(transaction -> transaction.getTrader())
                .filter(trader -> trader.getCity().equalsIgnoreCase("Cambridge"))
                .distinct()
                .sorted(Comparator.comparing(Trader::getName))
                .collect(Collectors.toList());
        System.out.println(q3);

        // 返回所有交易员的姓名字符串，按字母顺序排序。
        String q4 = transactions.stream()
                .map(transaction -> transaction.getTrader().getName())
                .distinct()
                .sorted(Comparator.comparing(String::toString))
                .reduce("", (String a, String b) -> a + b);
        System.out.println(q4);
        q4 = transactions.stream()
                .map(transaction -> transaction.getTrader().getName())
                .distinct()
                .sorted(Comparator.comparing(String::toString))
                .collect(Collectors.joining());
        System.out.println(q4);

        // 有没有交易员是在米兰工作的？
        boolean q5 = transactions.stream()
                .anyMatch(transaction -> transaction.getTrader().getCity().equalsIgnoreCase("Milan"));
        System.out.println(q5);

        // 打印生活在剑桥的交易员的所有交易额。
        transactions.stream()
                .filter(transaction -> transaction.getTrader().getCity().equalsIgnoreCase("Cambridge"))
                .map(transaction -> transaction.getValue())
                .forEach(System.out::println);

        // 所有交易中，最高的交易额是多少？
        int q7 = transactions.stream()
                .map(transaction -> transaction.getValue())
                .reduce(0, Integer::max);
        System.out.println(q7);

        // 找到交易额最小的交易。
        Optional<Transaction> q8 = transactions.stream()
                .reduce((Transaction t1, Transaction t2) -> t1.getValue() < t2.getValue() ? t1 : t2);
        q8.ifPresent(System.out::println);
        q8 = transactions.stream()
                .min(Comparator.comparing(Transaction::getValue));
        q8.ifPresent(System.out::println);
    }
}
