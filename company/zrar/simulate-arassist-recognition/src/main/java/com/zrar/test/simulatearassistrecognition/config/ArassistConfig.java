package com.zrar.test.simulatearassistrecognition.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Jingfeng Zhou
 */
@Component
@ConfigurationProperties(prefix = "arassist")
@Data
public class ArassistConfig {
    private String czCallLog;
    private String recive;
}
