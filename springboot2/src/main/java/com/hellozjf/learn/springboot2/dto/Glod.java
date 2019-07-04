package com.hellozjf.learn.springboot2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Jingfeng Zhou
 */
@Data
@AllArgsConstructor
public class Glod {
    private int buyLumber = 0;
    private int incMoney = 0;

    public static Glod glod128() {
        return new Glod(3200, 128);
    }

    public static Glod glod512() {
        return new Glod(12800, 512);
    }

    public static Glod glod2048() {
        return new Glod(51200, 2048);
    }

    public static Glod glod8192() {
        return new Glod(204800, 8192);
    }

    public static Glod glod50000() {
        return new Glod(1000000, 50000);
    }
}
