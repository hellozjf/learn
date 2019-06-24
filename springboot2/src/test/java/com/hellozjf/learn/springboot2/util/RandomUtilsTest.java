package com.hellozjf.learn.springboot2.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @author Jingfeng Zhou
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class RandomUtilsTest {

    @Test
    public void getChineseName() {
        String cname = RandomUtils.getChineseName();
        log.debug("cname = {}", cname);
    }
}