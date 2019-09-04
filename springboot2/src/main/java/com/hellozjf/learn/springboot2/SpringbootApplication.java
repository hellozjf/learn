package com.hellozjf.learn.springboot2;

import com.hellozjf.learn.springboot2.config.CustomConfig;
import com.hellozjf.learn.springboot2.service.DateTimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private CustomConfig customConfig;

    @Autowired
    private DateTimeService dateTimeService;

    /**
     * springboot启动完会回调的函数
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        log.debug("hello = {}", customConfig.getHello());
        log.debug("test = {}", dateTimeService.getWeekStartTime(dateTimeService.getCurrentTime()));
    }
}
