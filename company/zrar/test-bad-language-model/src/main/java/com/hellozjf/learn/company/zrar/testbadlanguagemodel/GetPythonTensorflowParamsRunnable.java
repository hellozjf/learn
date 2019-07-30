package com.hellozjf.learn.company.zrar.testbadlanguagemodel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jingfeng Zhou
 */
@Slf4j
public class GetPythonTensorflowParamsRunnable implements Runnable {

    private CloseableHttpClient httpClient;
    private ObjectMapper objectMapper;
    private int lineNum;
    private String sentence;
    private PrintWriter printWriter;

    public GetPythonTensorflowParamsRunnable(CloseableHttpClient httpClient,
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
                    .setHost("192.168.56.111")
                    .setPort(8084)
                    .setPath("/tensorflow/params/transformer")
                    .build();
            HttpPost httpPost = new HttpPost(uri);
            List<NameValuePair> formparams = new ArrayList<>();
            formparams.add(new BasicNameValuePair("sentence", sentence));
            formparams.add(new BasicNameValuePair("paramCode", String.valueOf(ModelParamEnum.TENSORFLOW_NORMAL.getCode())));
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
            httpPost.setEntity(formEntity);
            HttpResponse response = httpClient.execute(httpPost);
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
