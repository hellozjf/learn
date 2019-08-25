package com.hellozjf.learn.springboot2.pool;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Jingfeng Zhou
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class PoolTest {

    @Data
    public static class MyClass {
        private Integer age;
        private String name;
        private Date date;
        private Double value;
    }

    @Test
    public void main() {
        long t1 = System.currentTimeMillis();
        List<MyClass> myClassList = new ArrayList<>();
        for (int i = 0; i < 1000 * 365; i++) {
            MyClass myClass = new MyClass();
            myClass.setAge(i);
            myClass.setDate(new Date());
            myClass.setName(String.valueOf(i));
            myClass.setValue(Double.valueOf(i));
            myClassList.add(myClass);
        }
        long t2 = System.currentTimeMillis();
        log.debug("{}", myClassList);
        log.debug("t2 - t1 = {}", t2 - t1);
    }
}