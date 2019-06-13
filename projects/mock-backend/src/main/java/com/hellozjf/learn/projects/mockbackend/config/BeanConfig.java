package com.hellozjf.learn.projects.mockbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

/**
 * @author Jingfeng Zhou
 */
@Configuration
public class BeanConfig {

    @Bean
    public Random random() {
        return new Random();
    }
}
