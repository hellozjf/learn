package com.hellozjf.learn.company.zrar.arbdpress;

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

    private String arbdIp;
    private int arbdPort;
    private String receivePath;
    private String callPath;
    private int threadNum;
    private int minCallInterval;
    private int maxCallInterval;
    private int minSendInterval;
    private int maxSendInterval;
    private int minRecognizeInterval;
    private int maxRecognizeInterval;
    private int minSendCount;
    private int maxSendCount;
    private int minCsadCallNumber;
    private int maxCsadCallNumber;
    private int minCustomerCallNumber;
    private int maxCustomerCallNumber;
}
