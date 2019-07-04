package com.hellozjf.learn.springboot2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Jingfeng Zhou
 */
@Data
@AllArgsConstructor
public class Elf {
    private int buyMoney;
    private int incLumber;

    public static Elf elf10() {
        return new Elf(18000, 10);
    }

    public static Elf elf48() {
        return new Elf(80000, 48);
    }

    public static Elf elf255() {
        return new Elf(255000, 255);
    }

    public static Elf elf2200() {
        return new Elf(1000000, 2200);
    }
}
