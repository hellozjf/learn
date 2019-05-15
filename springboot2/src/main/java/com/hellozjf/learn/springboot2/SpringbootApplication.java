package com.hellozjf.learn.springboot2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author hellozjf
 */
@SpringBootApplication
@Slf4j
public class SpringbootApplication {

    public static void main(String[] args) {
        // springboot默认不开启图形界面，通过设置headless(false)开启图形界面
        new SpringApplicationBuilder(SpringbootApplication.class)
                .headless(false)
                .run(args);
    }

}
