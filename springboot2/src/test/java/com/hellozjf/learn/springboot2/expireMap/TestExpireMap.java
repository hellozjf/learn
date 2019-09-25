package com.hellozjf.learn.springboot2.expireMap;

import lombok.extern.slf4j.Slf4j;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Jingfeng Zhou
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class TestExpireMap {

    public static final String CONN = "conn";

    @Test
    public void test() throws InterruptedException {

//        Map<String, String> map = ExpiringMap.builder()
//                .maxSize(5000)
//                .expiration(1, TimeUnit.SECONDS)
//                .expirationPolicy(ExpirationPolicy.ACCESSED)
//                .variableExpiration()
//                .build();
//        map.put("sun", "xiaofei");
//        Thread.sleep(500);
//        //String sun1 = map.get("sun");
//        Thread.sleep(1000);
//        String sun = map.get("sun");
//        System.err.println(sun);

        Map<String, String> map = ExpiringMap.builder()
                .maxSize(123)
                .expiration(1, TimeUnit.SECONDS)
                .expirationPolicy(ExpirationPolicy.ACCESSED)
                .variableExpiration()
                .build();

        map.put(CONN, "hello");
        String s1 = map.get(CONN);
        TimeUnit.MILLISECONDS.sleep(500);
        String s2 = map.get(CONN);
        TimeUnit.SECONDS.sleep(2);
        String s3 = map.get(CONN);

        log.error("s1 = {}, s2 = {}, s3 = {}", s1, s2, s3);
    }
}
