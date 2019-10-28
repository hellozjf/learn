package com.zrar.test.simulatearassistrecognition.runnable;

import com.zrar.test.simulatearassistrecognition.config.ArassistConfig;
import com.zrar.test.simulatearassistrecognition.config.SimulateConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

/**
 * 模拟发送消息的运行体
 *
 * @author Jingfeng Zhou
 */
@Slf4j
public class MessageRunnable implements Runnable {

    private String callId;
    private String msgFrom;
    private String msgTo;
    private String content;
    private ArassistConfig arassistConfig;
    private SimulateConfig simulateConfig;
    private RestTemplate restTemplate;

    public MessageRunnable(String callId, String msgFrom, String msgTo, String content, ArassistConfig arassistConfig, SimulateConfig simulateConfig, RestTemplate restTemplate) {
        this.callId = callId;
        this.msgFrom = msgFrom;
        this.msgTo = msgTo;
        this.content = content;
        this.arassistConfig = arassistConfig;
        this.simulateConfig = simulateConfig;
        this.restTemplate = restTemplate;
    }

    @Override
    public void run() {
        // 先发送...
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> springEntity = new HttpEntity<>("...", headers);
        String url = arassistConfig.getRecive() + "?use=thinkit&callid=" + callId + "&telNum=" + msgFrom + "," + msgTo + "&scene=2&uuid=123435456465434&sendTime=1551409675511&role=1&dialect=Mandarin&voiceTime=1235"
        restTemplate.postForObject()

        // 再发送实际内容

    }
}
