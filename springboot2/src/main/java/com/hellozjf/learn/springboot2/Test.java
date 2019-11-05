package com.hellozjf.learn.springboot2;

import cn.hutool.core.util.RandomUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jingfeng Zhou
 */
public class Test {

    private static List<String> list = new ArrayList<>();

    public static void main(String[] args) {
        // 建立一百个发送线程
        for (int i = 0; i < 512; i++) {
            new Thread(() -> {
                list.add(RandomUtil.randomString(1024 * 32));
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        // 建立一个处理线程
        new Thread(() -> {
            list.clear();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        while (true) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
