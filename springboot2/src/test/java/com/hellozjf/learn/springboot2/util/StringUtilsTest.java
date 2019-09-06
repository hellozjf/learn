package com.hellozjf.learn.springboot2.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jingfeng Zhou
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class StringUtilsTest {

    @Test
    public void join() {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        log.debug("{}", String.join(",", list));
    }
}
