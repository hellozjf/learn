package com.hellozjf.learn.springboot2.controller;

import com.hellozjf.learn.projects.common.vo.ResultVO;
import com.hellozjf.learn.springboot2.util.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * @author Jingfeng Zhou
 */
@Slf4j
@RestController
@RequestMapping(value="/t")
public class TestController {

    @RequestMapping(value="/test", method = RequestMethod.GET)
    public ResultVO test() {
        synchronized (this) {
            log.debug("enter test");
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("leave test");
            return ResultUtils.success();
        }
    }

    @RequestMapping(value="/test2", method = RequestMethod.POST)
    public ResultVO test2() {
        return ResultUtils.success();
    }

    @RequestMapping(value = "/test3", method = RequestMethod.DELETE)
    public ResultVO test3() {
        return ResultUtils.success();
    }

    @RequestMapping(value = "/test4", method = RequestMethod.PUT)
    public ResultVO test4() {
        return ResultUtils.success();
    }
}
