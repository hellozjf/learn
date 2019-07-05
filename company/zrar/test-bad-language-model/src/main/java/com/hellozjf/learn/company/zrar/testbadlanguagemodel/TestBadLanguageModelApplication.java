package com.hellozjf.learn.company.zrar.testbadlanguagemodel;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URI;

@Slf4j
@SpringBootApplication
public class TestBadLanguageModelApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(TestBadLanguageModelApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        ObjectMapper objectMapper = new ObjectMapper();

        Resource resource = new ClassPathResource("sentence.txt");
        FileSystemResource outResource = new FileSystemResource("badLanguage.txt");
        try (InputStream inputStream = resource.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
             OutputStream outputStream = outResource.getOutputStream();
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
             PrintWriter printWriter = new PrintWriter(outputStreamWriter)) {
            String line = null;
            int lineNum = 0;
            while (true) {
                // 首先将sentence.txt里面的话都提取出来
                line = bufferedReader.readLine();
                if (StringUtils.isEmpty(line)) {
                    break;
                }

                lineNum++;
                log.debug("lineNum = {}", lineNum);

                // 先获取参数
                String params = null;
                URI uri = new URIBuilder()
                        .setScheme("http")
                        .setHost("192.168.2.149")
                        .setPort(5000)
                        .setPath("/getParams")
                        .setParameter("sentence", line)
                        .build();
                HttpGet httpGet = new HttpGet(uri);
                try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                    HttpEntity entity = response.getEntity();
                    params = EntityUtils.toString(entity);
                } catch (Exception e) {
                    log.error("e = {}", e);
                }

                // 再获取结果
                String result = null;
                uri = new URIBuilder()
                        .setScheme("http")
                        .setHost("192.168.2.149")
                        .setPort(8501)
                        .setPath("/v1/models/result_pb:predict")
                        .build();
                HttpPost httpPost = new HttpPost(uri);
                StringEntity stringEntity = new StringEntity("{\"instances\": [" + params + "]}", ContentType.TEXT_PLAIN);
                httpPost.setEntity(stringEntity);
                try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                    HttpEntity entity = response.getEntity();
                    result = EntityUtils.toString(entity);
                } catch (Exception e) {
                    log.error("e = {}", e);
                }

                // 解析结果
                JsonNode jsonNode = objectMapper.readTree(result);
                ArrayNode predictions = (ArrayNode) jsonNode.get("predictions");
                ArrayNode predict = (ArrayNode) predictions.get(0);
                double notBadLanguage = predict.get(0).asDouble();
                double badLanguage = predict.get(1).asDouble();
                log.debug("line = {}, not = {}, is = {}", line, notBadLanguage, badLanguage);

                // 把非脏话概率小于0.5的句子保存到脏话文本中
                if (notBadLanguage < 0.5) {
                    printWriter.println(line);
                }
            }
        } catch (Exception e) {
            log.error("e = {}", e);
        }
    }
}
