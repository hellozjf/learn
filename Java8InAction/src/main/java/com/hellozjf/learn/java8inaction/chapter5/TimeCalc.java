package com.hellozjf.learn.java8inaction.chapter5;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author Jingfeng Zhou
 */
public class TimeCalc {

    static class Person implements Serializable {
        String id;
        Date gmtCreate;
        Date gmtModified;
        Integer score;
        int score2;

        public Person(String id, Integer score) {
            this.id = id;
            this.score = score;
            this.score2 = score.intValue();
            this.gmtCreate = new Date();
            this.gmtModified = new Date();
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Date getGmtCreate() {
            return gmtCreate;
        }

        public void setGmtCreate(Date gmtCreate) {
            this.gmtCreate = gmtCreate;
        }

        public Date getGmtModified() {
            return gmtModified;
        }

        public void setGmtModified(Date gmtModified) {
            this.gmtModified = gmtModified;
        }

        public Integer getScore() {
            return score;
        }

        public void setScore(Integer score) {
            this.score = score;
        }

        public int getScore2() {
            return score2;
        }

        public void setScore2(int score2) {
            this.score2 = score2;
        }
    }

    static void integerListSum(List<Person> list) {
        long t1;
        long t2;

        // 普通加法
        t1 = System.currentTimeMillis();
        Integer sum = 0;
        for (Integer i = 0; i < list.size(); i++) {
            sum += list.get(i).getScore();
        }
        t2 = System.currentTimeMillis();
        System.out.println(sum + ":" + (t2 - t1));

        // map-reduce
        t1 = System.currentTimeMillis();
        Integer sum2 = list.stream()
                .map(person -> person.getScore())
                .reduce(0, Integer::sum);
        t2 = System.currentTimeMillis();
        System.out.println(sum2 + ":" + (t2 - t1));
    }

    static void intListSum(List<Person> list) {
        long t1;
        long t2;

        // 普通加法
        t1 = System.currentTimeMillis();
        int sum = 0;
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i).getScore2();
        }
        t2 = System.currentTimeMillis();
        System.out.println(sum + ":" + (t2 - t1));

        // map-reduce
        t1 = System.currentTimeMillis();
        int sum2 = list.parallelStream()
                .map(person -> person.getScore2())
                .reduce(0, Integer::sum);
        t2 = System.currentTimeMillis();
        System.out.println(sum2 + ":" + (t2 - t1));
    }

    public static void main(String[] args) {

        long t1 = System.currentTimeMillis();
        List<Person> list = new ArrayList<>();
        for (int i = 0; i < 10000000; i++) {
            list.add(new Person(UUID.randomUUID().toString(), i));
        }
        long t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);

        integerListSum(list);
        intListSum(list);
    }
}
