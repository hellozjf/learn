package com.hellozjf.learn.springboot2.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jingfeng Zhou
 */
@Data
@Slf4j
public class MoneyLumber {
    private static final int MAX = 1000000;
    private int moneyInc = 512;
    private int lumberInc = 6;
    private int currentMoney = 0;
    private int currentLumber = 0;
    private int seconds = 0;

    private List<Elf> elfList = new ArrayList<>();
    private List<Glod> goldList = new ArrayList<>();

    private int getTotalMoneyInc() {
        int moneyInc = goldList.stream().mapToInt(Glod::getIncMoney).sum();
        return moneyInc + this.moneyInc;
    }

    private int getTotalLumberInc() {
        int lumberInc = elfList.stream().mapToInt(Elf::getIncLumber).sum();
        return lumberInc + this.lumberInc;
    }

    public void inc() {
        currentMoney += getTotalMoneyInc();
        currentLumber += getTotalLumberInc();
        if (currentMoney > MAX) {
            currentMoney = MAX;
        }
        if (currentLumber > MAX) {
            currentLumber = MAX;
        }
        seconds++;
    }

    public void print() {
        log.debug("seconds = {}", getSeconds());
        log.debug("elf10 = {}", getElfNum(Elf.elf10()));
        log.debug("elf48 = {}", getElfNum(Elf.elf48()));
        log.debug("elf255 = {}", getElfNum(Elf.elf255()));
        log.debug("elf2200 = {}", getElfNum(Elf.elf2200()));
        log.debug("gold128 = {}", getGlodNum(Glod.glod128()));
        log.debug("gold512 = {}", getGlodNum(Glod.glod512()));
        log.debug("gold2048 = {}", getGlodNum(Glod.glod2048()));
        log.debug("gold8192 = {}", getGlodNum(Glod.glod8192()));
        log.debug("gold50000 = {}", getGlodNum(Glod.glod50000()));
    }

    public void tryBuy(Elf elf) {
        if (currentMoney >= elf.getBuyMoney()) {
            currentMoney -= elf.getBuyMoney();
            elfList.add(elf);
        }
    }

    public void tryBuy(Glod glod) {
        if (currentLumber >= glod.getBuyLumber()) {
            currentLumber -= glod.getBuyLumber();
            goldList.add(glod);
        }
    }

    public long getElfNum(Elf wantElf) {
        return elfList.stream().filter(elf -> elf.equals(wantElf)).count();
    }

    public long getGlodNum(Glod wantGlod) {
        return goldList.stream().filter(gold -> gold.equals(wantGlod)).count();
    }

    public void calcMaxTime(int moneyTimeToMax, int lumberTimeToMax,
                            int maxElf10Count, int maxElf48Count, int maxElf255Count, int maxElf2200Count,
                            int maxGlod128Count, int maxGlod512Count, int maxGlod2048Count, int maxGlod8192Count, int maxGlod50000Count) {
        while (true) {
            inc();
            if (getTotalMoneyInc() < MAX / moneyTimeToMax) {
                if (maxGlod128Count == -1 || getGlodNum(Glod.glod128()) < maxGlod128Count) {
                    tryBuy(Glod.glod128());
                } else if (maxGlod512Count == -1 || getGlodNum(Glod.glod512()) < maxGlod512Count) {
                    tryBuy(Glod.glod512());
                } else if (maxGlod2048Count == -1 || getGlodNum(Glod.glod2048()) < maxGlod2048Count) {
                    tryBuy(Glod.glod2048());
                } else if (maxGlod8192Count == -1 || getGlodNum(Glod.glod8192()) < maxGlod8192Count) {
                    tryBuy(Glod.glod8192());
                } else if (maxGlod50000Count == -1 || getGlodNum(Glod.glod50000()) < maxGlod50000Count) {
                    tryBuy(Glod.glod50000());
                }
            }
            if (getTotalLumberInc() < MAX / lumberTimeToMax) {
                if (maxElf10Count == -1 || getElfNum(Elf.elf10()) < maxElf10Count) {
                    tryBuy(Elf.elf10());
                } else if (maxElf48Count == -1 || getElfNum(Elf.elf48()) < maxElf48Count) {
                    tryBuy(Elf.elf48());
                } else if (maxElf255Count == -1 || getElfNum(Elf.elf255()) < maxElf255Count) {
                    tryBuy(Elf.elf255());
                } else if (maxElf2200Count == -1 || getElfNum(Elf.elf2200()) < maxElf2200Count) {
                    tryBuy(Elf.elf2200());
                }
            }
            if (currentMoney == MAX && currentLumber == MAX) {
                break;
            }
        }
    }

    public static void main(String[] args) {
        MoneyLumber maxMoneyLumber = new MoneyLumber();
        maxMoneyLumber.setSeconds(Integer.MAX_VALUE);
//        for (int maxElf10Count = 0; maxElf10Count <= 20; maxElf10Count++) {
//            for (int maxElf48Count = 0; maxElf48Count <= 20; maxElf48Count++) {
//                for (int maxElf255Count = 0; maxElf255Count < 20; maxElf255Count++) {
//                    MoneyLumber moneyLumber = new MoneyLumber();
//                    moneyLumber.calcMaxTime(10, 60,
//                            maxElf10Count, maxElf48Count, maxElf255Count, -1,
//                            4, 4, 4, 0, -1);
//                    if (maxMoneyLumber.getSeconds() > moneyLumber.getSeconds()) {
//                        maxMoneyLumber = moneyLumber;
//                    }
//                }
//            }
//        }

//        for (int maxGold128Count = 0; maxGold128Count <= 10; maxGold128Count++) {
//            for (int maxGold512Count = 0; maxGold512Count <= 10; maxGold512Count++) {
//                for (int maxGold2048Count = 0; maxGold2048Count <= 10; maxGold2048Count++) {
//                    for (int maxGold8192Count = 0; maxGold8192Count <= 10; maxGold8192Count++) {
//                        MoneyLumber moneyLumber = new MoneyLumber();
//                        moneyLumber.calcMaxTime(10, 60,
//                                13, 0, 2, -1,
//                                maxGold128Count, maxGold512Count, maxGold2048Count, maxGold8192Count, -1);
//                        if (maxMoneyLumber.getSeconds() > moneyLumber.getSeconds()) {
//                            maxMoneyLumber = moneyLumber;
//                        }
//                    }
//                }
//            }
//        }

        MoneyLumber moneyLumber = new MoneyLumber();
        moneyLumber.calcMaxTime(10, 60,
                4, 0, 1, -1,
                -1, 0, 0, 0, -1);
        moneyLumber.print();
    }
}
