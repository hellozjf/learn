package com.hellozjf.learn.java8inaction.chapter2;

/**
 * @author Jingfeng Zhou
 */
public class AppleSimpleFormatter implements AppleFormatter {

    @Override
    public String accept(Apple apple) {
        return "An apple of " + apple.getWeight() + "g";
    }
}
