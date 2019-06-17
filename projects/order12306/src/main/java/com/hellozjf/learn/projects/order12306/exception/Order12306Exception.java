package com.hellozjf.learn.projects.order12306.exception;

import com.hellozjf.learn.projects.order12306.constant.ResultEnum;
import lombok.Getter;

/**
 * @author Jingfeng Zhou
 */
@Getter
public class Order12306Exception extends RuntimeException {

    private Integer code;

    public Order12306Exception(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Order12306Exception(ResultEnum resultEnum) {
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }
}
