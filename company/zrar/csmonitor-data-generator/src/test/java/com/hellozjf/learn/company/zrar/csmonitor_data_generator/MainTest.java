package com.hellozjf.learn.company.zrar.csmonitor_data_generator;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Jingfeng Zhou
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MainTest {

    @Test
    public void sleepTest() {

        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 100; i++) {
            executorService.execute(() -> {
                int n = 0;
                while (true) {
                    n++;
                }
            });
        }

        long t1 = System.currentTimeMillis();
        try {
            TimeUnit.MINUTES.sleep(1);
        } catch (InterruptedException e) {
            log.error("e = {}", e);
        }
        long t2 = System.currentTimeMillis();
        log.debug("{}", t2 - t1);
    }
}