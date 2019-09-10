package com.hellozjf.learn.springboot2.test;

import com.hellozjf.learn.springboot2.util.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * @author Jingfeng Zhou
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class WebsocketSendTest {

    public static final String HOST = "localhost";

    /**
     * 来电
     * @param httpClient
     * @param callid
     * @throws Exception
     */
    private void callUp(CloseableHttpClient httpClient, String callid) throws Exception {
        HttpGet httpGet = new HttpGet();
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost(HOST)
                .setPort(8080)
                .setPath("/bd/call/asr/czCallLog")
                .setParameter("callid", callid)
                .setParameter("czlx", "1")
                .build();
        httpGet.setURI(uri);
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        httpResponse.close();
        log.debug("callUp callid={}", callid);
    }

    /**
     * 挂机
     * @param httpClient
     * @param callid
     * @throws Exception
     */
    private void callDown(CloseableHttpClient httpClient, String callid) throws Exception {
        HttpGet httpGet = new HttpGet();
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost(HOST)
                .setPort(8080)
                .setPath("/bd/call/asr/czCallLog")
                .setParameter("callid", callid)
                .setParameter("czlx", "2")
                .build();
        httpGet.setURI(uri);
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        httpResponse.close();
        log.debug("callDown callid={}", callid);
    }

    /**
     * 座席或者纳税人说
     * @param httpClient
     * @param callid
     * @throws Exception
     */
    private void say(CloseableHttpClient httpClient, String callid, String from, String to, String uuid, String content) throws Exception {
        HttpPost httpPost = new HttpPost();
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost(HOST)
                .setPort(8080)
                .setPath("/bd/call/asr/websocketRecive")
                .setParameter("use", "thinkit")
                .setParameter("callid", callid)
                .setParameter("telNum", from + "," + to)
                .setParameter("scene", "2")
                .setParameter("uuid", uuid)
                .setParameter("role", "2")
                .setParameter("dialect", "Mandarin")
                .setParameter("voiceTime", "1000")
                .setParameter("isKh", "0")
                .build();
        httpPost.setURI(uri);
        StringEntity stringEntity = new StringEntity(content, HTTP.UTF_8);
        httpPost.setEntity(stringEntity);
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        httpResponse.close();
        log.debug("say callid={} from={} to={} uuid={} content={}", callid, from, to, uuid, content);
    }

    /**
     * 让websocket快速发送，以检验
     */
    @Test
    public void quickSend() throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String callid = UUIDUtils.genUUIDNoSplit();

        // 先来电
        callUp(httpClient, callid);

        // 座席或者纳税人说
        String uuid1 = UUIDUtils.genUUIDNoSplit();
        String uuid2 = UUIDUtils.genUUIDNoSplit();
        String uuid3 = UUIDUtils.genUUIDNoSplit();
        String uuid4 = UUIDUtils.genUUIDNoSplit();
        String uuid5 = UUIDUtils.genUUIDNoSplit();
        say(httpClient, callid, "10000", "20000", uuid1, "...");
//        Thread.sleep(1000);
        say(httpClient, callid, "20000", "10000", uuid2, "...");
//        Thread.sleep(1000);
        say(httpClient, callid, "10000", "20000", uuid1, "你好，很高兴为你服务");
//        Thread.sleep(1000);
        say(httpClient, callid, "10000", "20000", uuid3, "...");
//        Thread.sleep(1000);
        say(httpClient, callid, "20000", "10000", uuid2, "增值税的税率是多少");
//        Thread.sleep(1000);
        say(httpClient, callid, "10000", "20000", uuid3, "增值税的税率是5%");
//        Thread.sleep(1000);
        say(httpClient, callid, "20000", "10000", uuid4, "...");
//        Thread.sleep(1000);
        say(httpClient, callid, "10000", "20000", uuid5, "...");
//        Thread.sleep(1000);
        say(httpClient, callid, "20000", "10000", uuid4, "好的，谢谢了");
//        Thread.sleep(1000);
        say(httpClient, callid, "10000", "20000", uuid5, "不客气");

        TimeUnit.HOURS.sleep(1);

        // 最后挂断
        callDown(httpClient, callid);
    }
}
