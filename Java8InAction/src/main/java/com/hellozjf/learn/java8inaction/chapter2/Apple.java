package com.hellozjf.learn.java8inaction.chapter2;

import java.util.Arrays;
import java.util.List;

/**
 * @author Jingfeng Zhou
 */
public class Apple {
    private String color;
    private Integer weight;

    public Apple(String color, Integer weight) {
        this.color = color;
        this.weight = weight;
    }

    public Apple() {
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public static void prettyPrintApple(List<Apple> inventory, AppleFormatter appleFormatter) {
        for (Apple apple : inventory) {
            String output = appleFormatter.accept(apple);
            System.out.println(output);
        }
    }

    public static void main(String[] args) {
        List<Apple> appleList = Arrays.asList(
                new Apple("green", 50),
                new Apple("red", 100),
                new Apple("yellow", 150),
                new Apple("black", 200)
        );
        prettyPrintApple(appleList, new AppleFancyFormatter());
        prettyPrintApple(appleList, new AppleSimpleFormatter());
    }

}
