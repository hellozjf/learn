package com.hellozjf.learn.projects.testftp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 调用百度翻译api，实现翻译
 * @author hellozjf
 */
@Service
@Slf4j
public class TranslateService {

    @Autowired
    private BaiduConfig baiduConfig;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 将q翻译成中文
     * @param q
     * @return
     */
    public String translate(String q) {
        int retryCount = 3;
        for (int i = 0; i < retryCount; i++) {
            try {
                CloseableHttpClient httpClient = HttpClients.createDefault();
                String urlEncodeQ = null;
                try {
                    urlEncodeQ = URLEncoder.encode(q, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    log.error("e = {}", e);
                }
                String md5 = null;
                try {
                    md5 = DigestUtils.md5DigestAsHex((baiduConfig.getAppid() + q + baiduConfig.getSalt() + baiduConfig.getKey()).getBytes("utf-8")).toLowerCase();
                } catch (UnsupportedEncodingException e) {
                    log.error("e = {}", e);
                    return q;
                }
                URI uri = new URIBuilder()
                        .setScheme("https")
                        .setHost("fanyi-api.baidu.com")
                        .setPath("/api/trans/vip/translate")
                        .build();
                HttpPost httpPost = new HttpPost(uri);
                List<NameValuePair> formparams = new ArrayList<>();
                formparams.add(new BasicNameValuePair("q", urlEncodeQ));
                formparams.add(new BasicNameValuePair("from", "en"));
                formparams.add(new BasicNameValuePair("to", "zh"));
                formparams.add(new BasicNameValuePair("appid", baiduConfig.getAppid()));
                formparams.add(new BasicNameValuePair("salt", baiduConfig.getSalt()));
                formparams.add(new BasicNameValuePair("sign", md5));
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
                httpPost.setEntity(entity);
                CloseableHttpResponse response = httpClient.execute(httpPost);
                String body = null;
                try {
                    HttpEntity resEntity = response.getEntity();
                    if (resEntity != null) {
                        body = EntityUtils.toString(resEntity);
                    }
                } catch (Exception e) {
                    log.error("e = {}", e);
                    return q;
                }
                JsonNode node = null;
                try {
                    log.debug("body = {}", body);
                    node = objectMapper.readTree(body);
                    log.debug("node = {}", node);
                    if (node.has("error_code") &&
                            node.get("error_code").asText().equalsIgnoreCase("52001")) {
                        log.debug("try again");
                        continue;
                    }
                } catch (IOException e) {
                    log.error("e = {}", e);
                    return q;
                }
                ArrayNode arrayNode = (ArrayNode) node.get("trans_result");
                if (arrayNode == null) {
                    return q;
                }
                String dst = arrayNode.get(0).get("dst").asText();
                log.debug("dst = {}", dst);
                return dst;
            } catch (Exception e) {
                log.error("e = {}", e);
            }
        }
        return "翻译服务请求超时";
    }
}
