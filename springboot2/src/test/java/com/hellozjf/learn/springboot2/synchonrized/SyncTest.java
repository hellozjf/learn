package com.hellozjf.learn.springboot2.synchonrized;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author hellozjf
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class SyncTest {

    @Test
    public void test() {
        String a = "15158037019";
        String b = "15158037019";

        new Thread(new TRunnable(a)).start();
        new Thread(new TRunnable(b)).start();

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class TRunnable implements Runnable {

        private String str;

        public TRunnable(String str) {
            this.str = str;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (str) {
                    log.debug("{} start", str);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    log.debug("{} stop", str);
                }
            }
        }
    }
}
