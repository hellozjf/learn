package com.hellozjf.learn.springboot2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jingfeng Zhou
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class SpringbootApplicationTest {

    @Test
    public void main() {
        log.debug("hello world");
    }

    @Test
    public void calcFast() {
        int moneyInc = 512;
        int lumberInc = 0;
        int currentMoney = 0;
        int currentLumber = 0;
        int seconds = 0;

        for (int i = 0; i < 60 * 10; i++) {
            currentMoney += moneyInc;
            currentLumber += lumberInc;
            seconds++;

            if (currentMoney > 18000) {
                currentMoney -= 18000;
                lumberInc += 10;
            }
            if (currentLumber > 3200) {
                currentLumber -= 3200;
                moneyInc += 128;
            }
        }

        log.debug("moneyInc={}, lumberInc={}, currentMoney={}, currentLumber={}, seconds={}",
                moneyInc, lumberInc, currentMoney, currentLumber, seconds);
    }

    @Test
    public void invokeJS() {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        Resource resource = new ClassPathResource("jsss.js");
        try (InputStream inputStream = resource.getInputStream();
             InputStreamReader reader = new InputStreamReader(inputStream)) {
            engine.eval(reader);
            if (engine instanceof Invocable) {
                Invocable invoke = (Invocable) engine;    // 调用merge方法，并传入两个参数
                Double c = (Double) invoke.invokeFunction("aa", 2, 3); //调用了js的aa方法
                System.out.println(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void invokeGetJS() {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        Resource resource = new ClassPathResource("GetJS.js");
        try (InputStream inputStream = resource.getInputStream();
             InputStreamReader reader = new InputStreamReader(inputStream)) {
            engine.eval(reader);
            if (engine instanceof Invocable) {
                Invocable invoke = (Invocable) engine;    // 调用merge方法，并传入两个参数
                invoke.invokeFunction("Pa");
//                engine.get("")
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Data
    @AllArgsConstructor
    public static class ST {
        public int a;
        public int b;
        public int c;
    }

    @Test
    public void stTest() {
        List<ST> stList = new ArrayList<>();
        stList.add(new ST(1, 2, 3));
        stList.add(new ST(2, 3, 4));
        stList.add(new ST(3, 4, 5));
        ST st = new ST(0, 0, 0);
        stList.stream().forEach(t -> {
            st.a += t.a;
            st.b += t.b;
            st.c += t.c;
        });
        log.debug("{}", st);
    }
}