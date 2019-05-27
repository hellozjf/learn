package com.hellozjf.learn.company.zrar.csmonitor_data_generator.service;

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
public class InitServiceTest {

    @Autowired
    private InitService initService;

    @Test
    public void initAll() {
        initService.initAll();
    }
}