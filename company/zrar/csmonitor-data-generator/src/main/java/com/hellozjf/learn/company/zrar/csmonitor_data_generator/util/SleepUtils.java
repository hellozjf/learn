package com.hellozjf.learn.company.zrar.csmonitor_data_generator.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author Jingfeng Zhou
 */
@Slf4j
public class SleepUtils {

    /**
     * 睡眠timeout个timeUnit
     * @param timeUnit
     * @param timeout
     */
    public static void sleep(TimeUnit timeUnit, long timeout) {
        try {
            timeUnit.sleep(timeout);
        } catch (InterruptedException e) {
            log.error("e = {}", e);
        }
    }
}
