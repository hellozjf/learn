package com.hellozjf.learn.projects.order12306.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hellozjf
 */
@Getter
@AllArgsConstructor
public enum ResultEnum implements CodeEnum {
    UNKNOWN_ERROR(-1, "未知异常"),
    CAN_NOT_FIND_THIS_ID_OBJECT(1, "无法找到此ID对应的对象"),
    LOGIN_12306_ERROR(2, "尝试登陆失败1000次，登录12306失败"),
    FORM_ERROR(3, "表单验证失败"),
    LEFT_TICKET_SUBMIT_ORDER_ERROR(4, "LEFT_TICKET_SUBMIT_ORDER_ERROR"),
    OTN_LOGIN_CONF_ERROR(5, "OTN_LOGIN_CONF_ERROR"),
    OTN_INDEX12306_GET_LOGIN_BANNER_ERROR(6, "OTN_INDEX12306_GET_LOGIN_BANNER_ERROR"),
    OTN_INDEX_INITMY12306API_ERROR(7, "OTN_INDEX_INITMY12306API_ERROR"),
    OTN_LEFT_TICKET_QUERY_ERROR(8, "OTN_LEFT_TICKET_QUERY_ERROR"),
    OTN_LOGIN_CHECK_USER_ERROR(9, "OTN_LOGIN_CHECK_USER_ERROR"),
    OTN_CONFIRM_PASSENGER_GET_PASSENGER_DTOS_ERROR(10, "OTN_CONFIRM_PASSENGER_GET_PASSENGER_DTOS_ERROR"),
    OTN_CONFIRM_PASSENGER_CHECK_ORDER_INFO_ERROR(11, "OTN_CONFIRM_PASSENGER_CHECK_ORDER_INFO_ERROR"),
    OTN_CONFIRM_PASSENGER_GET_QUEUE_COUNT_ERROR(12, "OTN_CONFIRM_PASSENGER_GET_QUEUE_COUNT_ERROR"),
    OTN_CONFIRM_PASSENGER_CONFIRM_SINGLE_FOR_QUEUE_ERROR(13, "OTN_CONFIRM_PASSENGER_CONFIRM_SINGLE_FOR_QUEUE_ERROR"),
    OTN_CONFIRM_PASSENGER_QUERY_ORDER_WAIT_TIME_ERROR(14, "OTN_CONFIRM_PASSENGER_QUERY_ORDER_WAIT_TIME_ERROR"),
    OTN_CONFIRM_PASSENGER_RESULT_ORDER_FOR_DC_QUEUE_ERROR(15, "OTN_CONFIRM_PASSENGER_RESULT_ORDER_FOR_DC_QUEUE_ERROR"),
    PASSPORT_CAPTCHA_CAPTCHA_IMAGE64_ERROR(16, "PASSPORT_CAPTCHA_CAPTCHA_IMAGE64_ERROR"),
    ALREADY_GRABBING(17, "已经在抢票中了，无法再开启抢票"),
    NOT_GRABBING_ANY_TICKET(18, "未曾抢过任何票"),
    STOP_GRABBING(19, "停止抢票"),
    QUERY_TICKET_PEOPLE_INFO_FAILED(20, "查询乘车人信息失败"),
    QUERY_LEFT_TICKET_INFO_FAILED(21, "查询余票信息失败"),
    GRABBING_STOPED_BY_HAND(22, "抢票已被手动停止"),
    ALREADY_GRABBING_CAN_NOT_GET_INFO(23, "已经在抢票中了，无法再获取信息"),
    ;

    Integer code;
    String message;
}
