package com.hellozjf.learn.company.zrar.testbadlanguagemodel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.writer.ArraysMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URI;

/**
 * @author Jingfeng Zhou
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class TestBadLanguageModelApplicationTest {

    @Test
    public void getBadLanguage() {
    }

    @Test
    public void changeCsvToSentences() {
        Resource resource = new ClassPathResource("test.tsv");
        FileSystemResource outputResource = new FileSystemResource("d:\\sentence.txt");
        try (InputStream inputStream = resource.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
             OutputStream outputStream = outputResource.getOutputStream();
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
             PrintWriter printWriter = new PrintWriter(outputStreamWriter)) {
            // 跳过第一行
            bufferedReader.readLine();
            while (true) {
                String line = bufferedReader.readLine();
                if (StringUtils.isEmpty(line)) {
                    break;
                }
                String sentence = line.split("\t")[1];
                System.out.println(sentence);
                printWriter.println(sentence);
            }
        } catch (Exception e) {
            log.error("e = {}", e);
        }
    }
}