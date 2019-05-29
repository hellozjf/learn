package com.hellozjf.learn.springboot2.domain;

import lombok.Data;

/**
 * @author Jingfeng Zhou
 */
@Data
public class Test {

    public Integer add(Integer a, Integer b) {
        return a + b;
    }
}
