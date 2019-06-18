package com.hellozjf.learn.projects.order12306.runnable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellozjf.learn.projects.common.config.SpringContextConfig;
import com.hellozjf.learn.projects.order12306.dto.OrderTicketDTO;
import com.hellozjf.learn.projects.order12306.util.HttpClientUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 购票线程体
 * @author Jingfeng Zhou
 */
public class OrderRunnable implements Runnable {

    private CookieStore cookieStore;
    private CloseableHttpClient httpclient;
    private ObjectMapper objectMapper;
    private Random random;
    private OrderTicketDTO orderTicketDTO;
    private Map<String, Integer> mapSeatConf;

    public OrderRunnable() {
        cookieStore = new BasicCookieStore();
        httpclient = HttpClientUtils.getHttpClient(cookieStore);
        objectMapper = SpringContextConfig.getBean(ObjectMapper.class);
        random = SpringContextConfig.getBean(Random.class);
        orderTicketDTO = getDefaultOrderTicketDTO();
        mapSeatConf = getMapSeatConf();
    }

    public void setOrderTicketDTO(OrderTicketDTO orderTicketDTO) {
        this.orderTicketDTO = orderTicketDTO;
    }

    @Override
    public void run() {

    }

    /**
     * 获取默认的OrderTicketDTO
     * @return
     */
    private OrderTicketDTO getDefaultOrderTicketDTO() {
        OrderTicketDTO orderTicketDTO = new OrderTicketDTO();
        orderTicketDTO.setTrainDate("2019-07-01");
        orderTicketDTO.setBackTrainDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd"));
        orderTicketDTO.setStationTrain("D655");
        orderTicketDTO.setFromStation("杭州");
        orderTicketDTO.setToStation("宁波");
        orderTicketDTO.setSeatType("二等座");
        orderTicketDTO.setTicketPeople("周靖峰");
        orderTicketDTO.setUsername("15158037019");
        orderTicketDTO.setPassword("Zjf@1234");
        orderTicketDTO.setEmail("908686171@qq.com");
        return orderTicketDTO;
    }

    /**
     * 初始化mapSeatConf
     */
    private HashMap<String, Integer> getMapSeatConf() {
        HashMap<String, Integer> mapSeatConf = new HashMap<>();
        mapSeatConf.put("商务座", 32);
        mapSeatConf.put("一等座", 31);
        mapSeatConf.put("二等座", 30);
        mapSeatConf.put("特等座", 25);
        mapSeatConf.put("高级软卧", 21);
        mapSeatConf.put("软卧", 23);
        mapSeatConf.put("动卧", 33);
        mapSeatConf.put("硬卧", 28);
        mapSeatConf.put("软座", 24);
        mapSeatConf.put("硬座", 29);
        mapSeatConf.put("无座", 26);
        mapSeatConf.put("其他", 22);
        return mapSeatConf;
    }
}
