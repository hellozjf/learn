package com.hellozjf.learn.company.zrar.csmonitor_data_generator.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Jingfeng Zhou
 */
@AllArgsConstructor
@Getter
public enum CsadStateEnum {

    SIGN_IN(1, "签入"),
    BUSY(2, "示忙"),
    SIGN_OUT(0, "签出"),
    ANSWER(11, "应答"),
    IDLE(12, "空闲"),
    ;

    Integer code;
    String desc;
}
