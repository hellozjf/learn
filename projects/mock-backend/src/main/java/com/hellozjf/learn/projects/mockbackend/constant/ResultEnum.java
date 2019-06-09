package com.hellozjf.learn.projects.mockbackend.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hellozjf
 */
@Getter
@AllArgsConstructor
public enum ResultEnum {
    CAN_NOT_FIND_THIS_ID_OBJECT(1, "无法找到此ID对应的对象"),
    ;

    Integer code;
    String message;
}
