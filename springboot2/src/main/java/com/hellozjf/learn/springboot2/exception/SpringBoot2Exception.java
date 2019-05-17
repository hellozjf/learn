package com.hellozjf.learn.springboot2.exception;

import com.hellozjf.learn.springboot2.constant.ResultEnum;
import lombok.Getter;

/**
 * @author Jingfeng Zhou
 */
@Getter
public class SpringBoot2Exception extends RuntimeException {

    private Integer code;

    public SpringBoot2Exception(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public SpringBoot2Exception(ResultEnum resultEnum) {
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }
}
