package com.hellozjf.learn.company.zrar.testbadlanguagemodel;

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

    /**
     * 保存cookie的文件夹名称
     */
    private Integer threadNum;
}
