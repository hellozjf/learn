package com.hellozjf.learn.springboot2.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @author Jingfeng Zhou
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MultiThreadServiceTest {

    @Autowired
    private MultiThreadService multiThreadService;

    @Test
    public void testMultiThreads() {
        multiThreadService.testMultiThreads(360, 100);
    }
}