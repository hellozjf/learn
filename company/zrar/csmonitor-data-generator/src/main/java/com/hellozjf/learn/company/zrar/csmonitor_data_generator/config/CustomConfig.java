package com.hellozjf.learn.company.zrar.csmonitor_data_generator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Jingfeng Zhou
 */
@Data
@Component
@ConfigurationProperties("custom")
public class CustomConfig {

    private Integer startWeekDay;
    private Integer stopWeekDay;
    private Integer startHour;
    private Integer stopHour;

}
