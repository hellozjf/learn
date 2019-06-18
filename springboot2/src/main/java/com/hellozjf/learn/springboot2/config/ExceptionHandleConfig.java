package com.hellozjf.learn.springboot2.config;

import com.hellozjf.learn.projects.common.vo.ResultVO;
import com.hellozjf.learn.springboot2.exception.SpringBoot2Exception;
import com.hellozjf.learn.springboot2.util.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常处理
 * Created by 廖师兄
 * 2017-01-21 13:59
 */
@Slf4j
@ControllerAdvice
public class ExceptionHandleConfig {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultVO handle(Exception e) {
        if (e instanceof SpringBoot2Exception) {
            SpringBoot2Exception exception = (SpringBoot2Exception) e;
            return ResultUtils.error(exception.getCode(), exception.getMessage());
        } else {
            log.error("【系统异常】{}", e);
            return ResultUtils.error(-1, "未知错误");
        }
    }
}
