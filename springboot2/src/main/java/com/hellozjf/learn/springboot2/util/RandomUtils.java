package com.hellozjf.learn.springboot2.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author Jingfeng Zhou
 */
@Slf4j
public class RandomUtils {

    public static String getChineseName() {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        Resource resource = new ClassPathResource("mock-min.js");

        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = resource.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
                line = bufferedReader.readLine();
            }
        } catch (Exception e) {
            log.error("e = {}", e);
        }
        try {
            engine.eval(stringBuilder.toString());
            Object eval = engine.eval("Mock.Random.cname()");
            return eval.toString();
        } catch (ScriptException e) {
            log.error("e = {}", e);
            return "";
        }
    }
}
