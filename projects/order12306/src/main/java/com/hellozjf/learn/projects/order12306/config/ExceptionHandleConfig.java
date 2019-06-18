package com.hellozjf.learn.projects.order12306.config;

import com.hellozjf.learn.projects.common.vo.ResultVO;
import com.hellozjf.learn.projects.order12306.constant.ResultEnum;
import com.hellozjf.learn.projects.order12306.exception.Order12306Exception;
import com.hellozjf.learn.projects.order12306.util.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Order12306Exception统一异常处理
 * 2017-01-21 13:59
 */
@ControllerAdvice
@Slf4j
public class ExceptionHandleConfig {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultVO handle(Exception e) {
        if (e instanceof Order12306Exception) {
            Order12306Exception exception = (Order12306Exception) e;
            return ResultUtils.error(exception.getCode(), exception.getMessage());
        } else {
            log.error("【系统异常】{}", e);
            return ResultUtils.error(ResultEnum.UNKNOWN_ERROR);
        }
    }
}
