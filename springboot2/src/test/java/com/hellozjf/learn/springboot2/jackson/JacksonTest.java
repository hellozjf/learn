package com.hellozjf.learn.springboot2.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

/**
 * @author Jingfeng Zhou
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class JacksonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testChangeStringToList() throws IOException {
        String listString = "[\"hello\", \"world\"]";
        List<String> list = objectMapper.readValue(listString, new TypeReference<List<String>>(){});
        log.debug("list = {}", list);
    }
}
