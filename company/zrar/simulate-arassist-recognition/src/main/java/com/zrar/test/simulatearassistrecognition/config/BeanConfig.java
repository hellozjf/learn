package com.zrar.test.simulatearassistrecognition.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Jingfeng Zhou
 */
@Configuration
public class BeanConfig {

    /**
     * 用于发送http请求的工具
     * @return
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * 线程池
     * @return
     */
    @Bean
    public ExecutorService executorService() {
        return Executors.newCachedThreadPool();
    }
}
