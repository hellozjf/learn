package com.hellozjf.learn.projects.order12306.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hellozjf
 */
@Getter
@AllArgsConstructor
public enum ResultEnum {
    UNKNOWN_ERROR(-1, "未知异常"),
    CAN_NOT_FIND_THIS_ID_OBJECT(1, "无法找到此ID对应的对象"),
    LOGIN_12306_ERROR(2, "登录12306失败"),
    ;

    Integer code;
    String message;
}
