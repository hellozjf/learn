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
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jingfeng Zhou
 */
@Slf4j
public class GetJavaTensorflowParamsRunnable implements Runnable {

    private CloseableHttpClient httpClient;
    private ObjectMapper objectMapper;
    private int lineNum;
    private String sentence;
    private PrintWriter printWriter;

    public GetJavaTensorflowParamsRunnable(CloseableHttpClient httpClient,
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

    @Override
    public void run() {
        try {
            URI uri = new URIBuilder()
                    .setScheme("http")
                    .setHost("localhost")
                    .setPort(8081)
                    .setPath("/getRawTensorflowParams")
                    .setParameter("sentence", sentence)
                    .setParameter("paramCode", String.valueOf(ModelParamEnum.TENSORFLOW_NORMAL.getCode()))
                    .build();
            HttpGet httpGet = new HttpGet(uri);
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            if (StringUtils.isEmpty(result)) {
                log.error("{} sentence {}, result is empty", lineNum, sentence);
            } else {
                log.debug("{} sentence {} success", lineNum, sentence);
                JsonNode jsonNode = objectMapper.readTree(result);
//                printWriter.println(objectMapper.writeValueAsString(jsonNode));
                Map<String, Object> map = new HashMap<>();
                map.put("input_ids", jsonNode.get("input_ids"));
                map.put("input_mask", jsonNode.get("input_mask"));
                map.put("segment_ids", jsonNode.get("segment_ids"));
                map.put("label_ids", jsonNode.get("label_ids"));
                printWriter.println(objectMapper.writeValueAsString(map));
            }
        } catch (Exception e) {
            log.error("{} sentence {} error", lineNum, sentence);
            log.error("{}", e);
        }
    }
}
