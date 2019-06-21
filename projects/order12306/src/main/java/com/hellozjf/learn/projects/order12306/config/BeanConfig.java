package com.hellozjf.learn.projects.order12306.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author hellozjf
 */
@Configuration
@Slf4j
public class BeanConfig {

    @Bean
    public ExecutorService executorService() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        return executorService;
    }

    /**
     * 初始化mapSeatConf
     */
    @Bean("mapSeatConf")
    public HashMap<String, Integer> getMapSeatConf() {
        HashMap<String, Integer> mapSeatConf = new HashMap<>();
        mapSeatConf.put("高级软卧", 21);
        mapSeatConf.put("其他", 22);
        mapSeatConf.put("软卧", 23);
        mapSeatConf.put("软座", 24);
        mapSeatConf.put("特等座", 25);
        mapSeatConf.put("无座", 26);
        mapSeatConf.put("硬卧", 28);
        mapSeatConf.put("硬座", 29);
        mapSeatConf.put("二等座", 30);
        mapSeatConf.put("一等座", 31);
        mapSeatConf.put("商务座", 32);
        mapSeatConf.put("动卧", 33);
        return mapSeatConf;
    }
}
