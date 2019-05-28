package com.hellozjf.learn.springboot2.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Jingfeng Zhou
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DoubleUtilsTest {

    @Test
    public void parseDoubleToString() {
        Double d = 0.0034;
        String sd = DoubleUtils.parseDoubleToPercentString(d);
        log.debug("sd = {}", sd);
    }
}