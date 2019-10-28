package com.zrar.test.simulatearassistrecognition.runnable;

import com.zrar.test.simulatearassistrecognition.config.ArassistConfig;
import com.zrar.test.simulatearassistrecognition.config.SimulateConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 模拟抓包端的运行体
 * @author Jingfeng Zhou
 */
@Slf4j
public class CallRunnable implements Runnable {

    /**
     * 分机号
     */
    private String callNumber;
    private String clientNumber;
    private ArassistConfig arassistConfig;
    private SimulateConfig simulateConfig;
    private RestTemplate restTemplate;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public CallRunnable(String callNumber, String clientNumber, ArassistConfig arassistConfig, SimulateConfig simulateConfig, RestTemplate restTemplate) {
        this.callNumber = callNumber;
        this.clientNumber = clientNumber;
        this.arassistConfig = arassistConfig;
        this.simulateConfig = simulateConfig;
        this.restTemplate = restTemplate;
    }

    @Override
    public void run() {
        // 会话编号
        String callId = UUID.randomUUID().toString();

        // 1. 来电
        Map<String, String> params = new HashMap<>();
        params.put("callid", callId);
        params.put("sendTime", String.valueOf(System.currentTimeMillis()));
        params.put("czlx", "1");
        restTemplate.getForObject(arassistConfig.getCzCallLog(), String.class, params);

        // 2. 来电
        restTemplate.getForObject(simulateConfig.getWebsocketClient().getCallUp(), String.class);

        // 生成随机线程数（消息数）
        int threadCount = RandomUtils.nextInt(simulateConfig.getRecognition().getCall().getMinThreadCount(),
                simulateConfig.getRecognition().getCall().getMaxThreadCount() + 1);
        int currentThreadCount = 0;

        // 先发送，您好xxx为您服务


        // 等待线程全部结束
        executorService.shutdown();
        while (true) {
            if (executorService.isTerminated()) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("e = ", e);
            }
        }

        // 7. 挂机
        params = new HashMap<>();
        params.put("callid", callId);
        params.put("sendTime", String.valueOf(System.currentTimeMillis()));
        params.put("czlx", "2");
        restTemplate.getForObject(arassistConfig.getCzCallLog(), String.class, params);

        // 8. 挂机
        restTemplate.getForObject(simulateConfig.getWebsocketClient().getCallDown(), String.class);
    }
}
