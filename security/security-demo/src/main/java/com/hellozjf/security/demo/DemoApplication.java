package com.hellozjf.security.demo;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jingfeng Zhou
 */
@SpringBootApplication
@RestController
public class DemoApplication {
    public static void main(String[] args) {
        // springboot默认不开启图形界面，通过设置headless(false)开启图形界面
        new SpringApplicationBuilder(DemoApplication.class)
                .headless(false)
                .run(args);
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello spring security";
    }
}
