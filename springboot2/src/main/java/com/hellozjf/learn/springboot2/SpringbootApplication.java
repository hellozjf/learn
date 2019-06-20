package com.hellozjf.learn.springboot2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * @author hellozjf
 */
@SpringBootApplication
@EnableJpaAuditing
@Slf4j
public class SpringbootApplication implements CommandLineRunner {

    public static void main(String[] args) {
        // springboot默认不开启图形界面，通过设置headless(false)开启图形界面
        new SpringApplicationBuilder(SpringbootApplication.class)
                .headless(false)
                .run(args);
    }

    /**
     * springboot启动完会回调的函数
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {

    }
}
