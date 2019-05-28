package com.hellozjf.learn.projects.order12306.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author hellozjf
 */
@Configuration
@Slf4j
public class BeanConfig {

    /**
     * 构建一个ObjectMapper，用于解析json
     * @return
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    /**
     * 存储自定义cookie所需要用到的URI
     * @return
     */
    @Bean
    @Qualifier("customCookieUri")
    public URI customCookieUri() {
        try {
            URI uri = new URI("http://aliyun.hellozjf.com");
            return uri;
        } catch (URISyntaxException e) {
            log.error("e = {}", e);
            return null;
        }
    }

    /**
     * 存储12306 cookie所需要用到的URI
     * @return
     */
    @Bean
    @Qualifier("site12306CookieUri")
    public URI site12306CookieUri() {
        try {
            URI uri = new URI("https://kyfw.12306.cn");
            return uri;
        } catch (URISyntaxException e) {
            log.error("e = {}", e);
            return null;
        }
    }
}
