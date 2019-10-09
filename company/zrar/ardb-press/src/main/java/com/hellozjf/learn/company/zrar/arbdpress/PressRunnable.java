package com.hellozjf.learn.company.zrar.arbdpress;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Jingfeng Zhou
 */
@Slf4j
public class PressRunnable implements Runnable {

    private List<String> csadSentence = Arrays.asList(
            "很高心为您服务，有什么可以帮您？",
            "您好，请问有什么可以帮您",
            "您好，请问您需要什么帮助？",
            "女士，这边需要帮您查询一会，请稍等，可以吗？",
            "先生，这边需要帮您查询一会，请稍等，可以吗？",
            "先生，非常抱歉，让您久等了",
            "女士，非常抱歉，让您久等了",
            "感谢您的来电，欢迎再次拨打12366",
            "稍后请对我的服务做出满意度评价，谢谢，再见；"
    );
    private List<String> customerSentence = Arrays.asList(
            "增值税的税率是多少",
            "车辆的单位和个人，为车辆购置税的纳税人应当依照本条例缴纳车辆购置税，单位包括国有企业集体企业私营企业股份制企业外商投资企业外国企业以及其他企业和事业单位社会团体国家机关部队以及其他单位所称个人包括个体工商户以及其他个人。",
            "是非营利性医疗机构的药房分离为独立的药品零售企业应按规定征收各项税收。",
            "根据财政部国家税务总局关于医疗卫生机构有关税收政策的通知财税二千四十二号规定一关于非赢利性医疗机构的税收政策，二对非赢利性医疗机构从事非医疗服务取得的收入，由租赁收入财产转让收入培训收入，对外投资收入等应按规定征收各项税收，非营利性医疗机构将取得的非医疗服务收入直接用于改善医疗卫生服务条件的部分，经税务部门审核批准，其应纳税所得额，就其余额征收企业所得税，三对非营利性医疗机构自产自用的制剂免征增值税。"
    );
    private int csadCallNumber;
    private int customerCallNumber;

    public PressRunnable(int csadCallNumber, int customerCallNumber) {
        // 初始化座席分机号，客户电话号码
        this.csadCallNumber = csadCallNumber;
        this.customerCallNumber = customerCallNumber;
    }

    @Override
    public void run() {
        CustomConfig customConfig = SpringContextConfig.getBean(CustomConfig.class);
        Random random = SpringContextConfig.getBean(Random.class);
        CloseableHttpClient httpClient = SpringContextConfig.getBean(CloseableHttpClient.class);

        while (true) {
            try {
                // 两次通话之间需要有一段时间间隔
                int callInterval = RandomUtils.getCallInterval(customConfig, random);
                TimeUnit.SECONDS.sleep(callInterval);

                // 初始化callid
                String callid = UUID.randomUUID().toString();

                // 来电
                callUp(customConfig, httpClient, callid);

                int sendCount = RandomUtils.getSendCount(customConfig, random);
                for (int i = 0; i < sendCount; i++) {
                    int sendInterval = RandomUtils.getSendInterval(customConfig, random);
                    int recognizeInterval = RandomUtils.getRecognizeInterval(customConfig, random);
                    TimeUnit.SECONDS.sleep(sendInterval);

                    String uuid = UUID.randomUUID().toString();
                    if (RandomUtils.getWhoSay(customConfig, random) == 0) {
                        // 座席说
                        String content = RandomUtils.getRandomCsadSentence(random, csadSentence);
                        csadSay(customConfig, httpClient, callid, csadCallNumber, customerCallNumber, uuid, "...");
                        TimeUnit.SECONDS.sleep(recognizeInterval);
                        csadSay(customConfig, httpClient, callid, csadCallNumber, customerCallNumber, uuid, content);
                    } else {
                        // 客户说
                        String content = RandomUtils.getRandomCustomerSentence(random, customerSentence);
                        customerSay(customConfig, httpClient, callid, csadCallNumber, customerCallNumber, uuid, "...");
                        TimeUnit.SECONDS.sleep(recognizeInterval);
                        customerSay(customConfig, httpClient, callid, csadCallNumber, customerCallNumber, uuid, content);
                    }
                }

                // 挂机
                callDown(customConfig, httpClient, callid);

            } catch (InterruptedException e) {
                log.error("e = {}", e);
            } catch (URISyntaxException e) {
                log.error("e = {}", e);
            }
        }
    }

    /**
     * 座席说，先发...，再发内容
     * @param customConfig
     * @param httpClient
     * @param callid
     * @param csadCallNumber
     * @param customerCallNumber
     */
    private void csadSay(CustomConfig customConfig,
                         CloseableHttpClient httpClient,
                         String callid,
                         int csadCallNumber,
                         int customerCallNumber,
                         String uuid,
                         String content) throws URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost(customConfig.getArbdIp())
                .setPort(customConfig.getArbdPort())
                .setPath(customConfig.getReceivePath())
                .setParameter("use", "thinkit")
                .setParameter("callid", callid)
                .setParameter("telNum", csadCallNumber + "," + customerCallNumber)
                .setParameter("scene", "2")
                .setParameter("uuid", uuid)
                .setParameter("role", "1")
                .setParameter("dialect", "Mandarin")
                .setParameter("voiceTime", "1000")
                .build();

        HttpPost httpPost = new HttpPost(uri);
        StringEntity stringEntity = new StringEntity(content, "utf-8");
        httpPost.setEntity(stringEntity);
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            String responseString = EntityUtils.toString(response.getEntity(), "utf-8");
            log.debug("csadSay: {}", responseString);
        } catch (Exception e) {
            log.error("e = {}", e);
        }
    }

    /**
     * 座席说
     * @param customConfig
     * @param httpClient
     * @param callid
     * @param csadCallNumber
     * @param customerCallNumber
     */
    private void customerSay(CustomConfig customConfig,
                         CloseableHttpClient httpClient,
                         String callid,
                         int csadCallNumber,
                         int customerCallNumber,
                         String uuid,
                         String content) throws URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost(customConfig.getArbdIp())
                .setPort(customConfig.getArbdPort())
                .setPath(customConfig.getReceivePath())
                .setParameter("use", "thinkit")
                .setParameter("callid", callid)
                .setParameter("telNum", customerCallNumber + "," + csadCallNumber)
                .setParameter("scene", "2")
                .setParameter("uuid", uuid)
                .setParameter("role", "2")
                .setParameter("dialect", "Mandarin")
                .setParameter("voiceTime", "1000")
                .build();

        HttpPost httpPost = new HttpPost(uri);
        StringEntity stringEntity = new StringEntity(content, "utf-8");
        httpPost.setEntity(stringEntity);
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            String responseString = EntityUtils.toString(response.getEntity(), "utf-8");
            log.debug("customerSay: {}", responseString);
        } catch (Exception e) {
            log.error("e = {}", e);
        }
    }

    /**
     * 来电
     * @param httpClient
     */
    private void callUp(CustomConfig customConfig, CloseableHttpClient httpClient, String callid) throws URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost(customConfig.getArbdIp())
                .setPort(customConfig.getArbdPort())
                .setPath(customConfig.getCallPath())
                .setParameter("callid", callid)
                .setParameter("czlx", "1")
                .build();
        HttpGet httpget = new HttpGet(uri);
        try (CloseableHttpResponse response = httpClient.execute(httpget)) {
            String responseString = EntityUtils.toString(response.getEntity(), "utf-8");
            log.debug("callUp: {}", responseString);
        } catch (Exception e) {
            log.error("e = {}", e);
        }
    }

    /**
     * 来电
     * @param httpClient
     */
    private void callDown(CustomConfig customConfig, CloseableHttpClient httpClient, String callid) throws URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost(customConfig.getArbdIp())
                .setPort(customConfig.getArbdPort())
                .setPath(customConfig.getCallPath())
                .setParameter("callid", callid)
                .setParameter("czlx", "2")
                .build();
        HttpGet httpget = new HttpGet(uri);
        try (CloseableHttpResponse response = httpClient.execute(httpget)) {
            String responseString = EntityUtils.toString(response.getEntity(), "utf-8");
            log.debug("callDown: {}", responseString);
        } catch (Exception e) {
            log.error("e = {}", e);
        }
    }
}
