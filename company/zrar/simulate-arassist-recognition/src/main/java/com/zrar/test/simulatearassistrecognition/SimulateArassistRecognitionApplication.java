package com.zrar.test.simulatearassistrecognition;

import com.zrar.test.simulatearassistrecognition.config.ArassistConfig;
import com.zrar.test.simulatearassistrecognition.config.SimulateConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@Slf4j
@SpringBootApplication
public class SimulateArassistRecognitionApplication {

    @Autowired
    private ArassistConfig arassistConfig;

    @Autowired
    private SimulateConfig simulateConfig;

    public static void main(String[] args) {
        SpringApplication.run(SimulateArassistRecognitionApplication.class, args);
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("arassistConfig = {}", arassistConfig);
        log.debug("simulateConfig = {}", simulateConfig);
    }

}
