package com.hellozjf.learn.projects.order12306;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellozjf.learn.projects.common.config.SpringContextConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author Jingfeng Zhou
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class TestApplicationContext {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetByName() {
        log.debug("objectMapper = {}", objectMapper);
        log.debug("springContext = {}", SpringContextConfig.getApplicationContext());
        log.debug("random = {}", SpringContextConfig.getBean("random"));
        log.debug("objectMapper = {}", SpringContextConfig.getBean("objectMapper", ObjectMapper.class));
        log.debug("random = {}", SpringContextConfig.getBean(Random.class));
    }

    @Data
    class Person {
        private List<Integer> productIdList;
    }

    @Test
    public void testLambda() {
        Person p1 = new Person();
        p1.setProductIdList(List.of(1, 2, 3));
        Person p2 = new Person();
        p2.setProductIdList(List.of(4, 5, 6));
        List<Person> personList = List.of(p1, p2);

        List<Integer> integerList = personList.stream()
                .map(person -> person.getProductIdList())
                .flatMap(List::stream)
                .collect(Collectors.toList());
        log.debug("integerList = {}", integerList);
    }
}
