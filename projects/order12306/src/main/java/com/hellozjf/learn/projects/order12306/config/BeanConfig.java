package com.hellozjf.learn.projects.order12306.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
