package com.hellozjf.learn.company.zrar.testbadlanguagemodel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import java.io.PrintWriter;
import java.net.URI;
import java.text.DecimalFormat;

/**
 * @author Jingfeng Zhou
 */
@Slf4j
public class JudgeBadSentenceRunnable implements Runnable {

    private CloseableHttpClient httpClient;
    private ObjectMapper objectMapper;
    private int lineNum;
    private String sentence;
    private PrintWriter printWriter;

    public JudgeBadSentenceRunnable(CloseableHttpClient httpClient,
                                    ObjectMapper objectMapper,
                                    int lineNum,
                                    String sentence,
                                    PrintWriter printWriter) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.lineNum = lineNum;
        this.sentence = sentence;
        this.printWriter = printWriter;
    }

    private void old() {
        // 最多尝试10次
        boolean bSuccess = false;
        for (int i = 0; i < 10; i++) {
            try {
                // 先获取参数
                String params;
                URI uri = new URIBuilder()
                        .setScheme("http")
                        .setHost("192.168.2.149")
                        .setPort(5000)
                        .setPath("/getParams")
                        .setParameter("sentence", sentence)
                        .build();
                HttpGet httpGet = new HttpGet(uri);
                CloseableHttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                params = EntityUtils.toString(entity);
                if (StringUtils.isEmpty(params)) {
                    continue;
                }

                // 再获取结果
                String result = null;
                uri = new URIBuilder()
                        .setScheme("http")
                        .setHost("192.168.2.149")
                        .setPort(8501)
                        .setPath("/v1/models/resultPb_7.1:predict")
                        .build();
                HttpPost httpPost = new HttpPost(uri);
                StringEntity stringEntity = new StringEntity("{\"instances\": [" + params + "]}", ContentType.TEXT_PLAIN);
                httpPost.setEntity(stringEntity);
                response = httpClient.execute(httpPost);
                entity = response.getEntity();
                result = EntityUtils.toString(entity);
                if (StringUtils.isEmpty(result)) {
                    continue;
                }


                // 解析结果
                JsonNode jsonNode = objectMapper.readTree(result);
                ArrayNode predictions = (ArrayNode) jsonNode.get("predictions");
                ArrayNode predict = (ArrayNode) predictions.get(0);
                double notDirty = predict.get(0).asDouble();
                double dirty = predict.get(1).asDouble();
                String s = String.format("lineNum = %d, sentence = %s, not = %f, is = %f", lineNum, sentence, notDirty, dirty);
                log.debug(s);

                // 把非脏话概率小于0.5的句子保存到脏话文本中
                printWriter.println("lineNum\tsentence\tnot\tis");
                if (notDirty < 0.5) {
                    s = String.format("%d\t%s\t%f\t%f", lineNum, sentence, notDirty, dirty);
                    printWriter.println(s);
                }
            } catch (Exception e) {
                log.error("e = {}", e);
                continue;
            }

            bSuccess = true;
            break;
        }

        if (!bSuccess) {
            log.error("lineNum = {}, sentence = {}, 解析失败", lineNum, sentence);
        }
    }

    @Override
    public void run() {
        try {
            URI uri = new URIBuilder()
                    .setScheme("http")
                    .setHost("localhost")
                    .setPort(8081)
                    .setPath("/dirtyWord/predict")
                    .build();
            HttpPost httpPost = new HttpPost(uri);
            StringEntity stringEntity = new StringEntity(sentence, ContentType.APPLICATION_JSON);
            httpPost.setEntity(stringEntity);
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            if (StringUtils.isEmpty(result)) {
                log.error("sentence {}, result is empty", sentence);
            }

            // 解析结果
            JsonNode jsonNode = objectMapper.readTree(result);
            ArrayNode data = (ArrayNode) jsonNode.get("data");
            double probability = data.get("probability").doubleValue();
            double notDirty = 1 - probability;
            double dirty = probability;
            String s = String.format("lineNum = %d, sentence = %s, not = %f, is = %f", lineNum, sentence, notDirty, dirty);
            log.debug(s);

            // 把非脏话概率小于0.5的句子保存到脏话文本中
            printWriter.println("lineNum\tsentence\tnot\tis");
            if (notDirty < 0.5) {
                s = String.format("%d\t%s\t%f\t%f", lineNum, sentence, notDirty, dirty);
                printWriter.println(s);
            }
        } catch (Exception e) {
            log.error("e = {}", e);
        }
    }
}
