package com.hellozjf.learn.projects.order12306;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

/**
 * 这里扫描com.hellozjf.learn是因为SpringContextConfig类在common包下面
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.hellozjf.learn"})
public class Order12306Application {

    public static void main(String[] args) {
        // springboot默认不开启图形界面，通过设置headless(false)开启图形界面
        new SpringApplicationBuilder(Order12306Application.class)
                .headless(false)
                .run(args);
    }

}
