package com.hellozjf.learn.projects.order12306.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author hellozjf
 */
@Data
public class TicketInfoForm {

    /**
     * 哪天出发，例如2019-04-26
     */
    @NotEmpty
    private String trainDate;

    /**
     * 哪天返回，如果是单程，就填写今天的日期
     */
    private String backTrainDate;

    /**
     * 哪辆火车，例如D777
     */
    @NotEmpty
    private String stationTrain;

    /**
     * 从哪个站出发，例如杭州东
     */
    @NotEmpty
    private String fromStation;

    /**
     * 达到哪个站，例如宁波
     */
    @NotEmpty
    private String toStation;

    /**
     * 座位类型，例如二等座
     */
    @NotEmpty
    private String seatType;

    /**
     * 购票人姓名，例如周靖峰
     */
    @NotEmpty
    private String ticketPeople;

    /**
     * 12306账号
     */
    @NotEmpty
    private String username;

    /**
     * 12306密码
     */
    @NotEmpty
    private String password;

    /**
     * 需要通知的邮箱
     */
    private String email;
}
