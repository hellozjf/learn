package com.hellozjf.learn.springboot2.util;

import com.hellozjf.learn.springboot2.dto.ClazzA;
import com.hellozjf.learn.springboot2.dto.ClazzB;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jingfeng Zhou
 */
@Slf4j
public class TestDup {

    public Long[] calcA(List<ClazzA> clazzAList) {
        Long[] nums = {0L, 0L, 0L};
        clazzAList.stream().forEach(clazzA -> {
            nums[0] += clazzA.getNum1();
            nums[1] += clazzA.getNum2();
            nums[2] += clazzA.getNum3();
        });
        return nums;
    }

    public Long[] calcB(List<ClazzB> clazzBList) {
        Long[] nums = {0L, 0L, 0L};
        clazzBList.stream().forEach(clazzB -> {
            nums[0] += clazzB.getNum1();
            nums[1] += clazzB.getNum2();
            nums[2] += clazzB.getNum3();
        });
        return nums;
    }

    public static void main(String[] args) {

        // 1-10
        List<ClazzA> clazzAList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ClazzA clazzA = new ClazzA();
            clazzA.setNum1(i);
            clazzA.setNum2(i + 1);
            clazzA.setNum3(i + 2);
            clazzAList.add(clazzA);
        }

        // 11-20
        List<ClazzB> clazzBList = new ArrayList<>();
        for (int i = 11; i < 20; i++) {
            ClazzB clazzB = new ClazzB();
            clazzB.setNum1(i);
            clazzB.setNum2(i + 1);
            clazzB.setNum3(i + 2);
            clazzBList.add(clazzB);
        }

        TestDup testDup = new TestDup();
        Long[] ra = testDup.calcA(clazzAList);
        Long[] rb = testDup.calcB(clazzBList);

        for (int i = 0; i < ra.length; i++) {
            log.debug("{}", ra[i]);
        }

        for (int i = 0; i < rb.length; i++) {
            log.debug("{}", rb[i]);
        }
    }
}
