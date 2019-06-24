package com.hellozjf.learn.projects.order12306.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hellozjf
 */
@AllArgsConstructor
@Getter
public enum TicketStateEnum implements CodeEnum {

    GRABBING(1, "抢票中"),
    SUCCESS(2, "抢票成功"),
    FAILED(3, "抢票失败"),
    PAUSE(4, "暂停抢票"),

    QUERY_INFO(11, "查询信息中"),
    QUERY_INFO_SUCCESS(12, "查询信息成功"),
    QUERY_INFO_FAILED(13, "查询信息失败"),
    ;

    private Integer code;
    private String desc;
}
