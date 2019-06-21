package com.hellozjf.learn.projects.order12306.service;

import com.hellozjf.learn.projects.order12306.dto.TicketInfoDTO;
import org.apache.http.client.HttpClient;

import java.util.List;

/**
 * 12306服务接口，用于提取公有接口，方便查询用户列表，查询车票信息
 * @author Jingfeng Zhou
 */
public interface Client12306Service {

    /**
     * 输入用户名和密码，返回12306连接
     * @param username
     * @param password
     * @return
     */
    HttpClient login(String username, String password);

    /**
     * 通过12306连接，查询该用户底下能够抢票的用户
     * @param httpClient
     * @return
     */
    List<String> queryUsernameList(HttpClient httpClient);

    /**
     * 通过12306连接，查询trainDate从fromStation到toStation的车票信息
     * @param httpClient
     * @param trainDate
     * @param fromStation
     * @param toStation
     * @return
     */
    List<TicketInfoDTO> queryLeftTicketList(HttpClient httpClient, String trainDate, String fromStation, String toStation);
}
