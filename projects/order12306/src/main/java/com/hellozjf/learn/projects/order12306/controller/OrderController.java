package com.hellozjf.learn.projects.order12306.controller;

import com.hellozjf.learn.projects.common.vo.ResultVO;
import com.hellozjf.learn.projects.order12306.constant.ResultEnum;
import com.hellozjf.learn.projects.order12306.constant.TicketStateEnum;
import com.hellozjf.learn.projects.order12306.domain.TicketInfoEntity;
import com.hellozjf.learn.projects.order12306.form.TicketInfoForm;
import com.hellozjf.learn.projects.order12306.repository.TicketInfoRepository;
import com.hellozjf.learn.projects.order12306.runnable.OrderRunnable;
import com.hellozjf.learn.projects.order12306.util.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.concurrent.ExecutorService;

/**
 * @author hellozjf
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private TicketInfoRepository ticketInfoRepository;

    @PostMapping("/grabbing")
    public ResultVO grabbing(@Valid TicketInfoForm ticketInfoForm,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errMsg = bindingResult.getFieldError().getDefaultMessage();
            log.error("{}", errMsg);
            return ResultUtils.error(ResultEnum.FORM_ERROR.getCode(), errMsg);
        }
        TicketInfoEntity ticketInfoEntity = new TicketInfoEntity();
        BeanUtils.copyProperties(ticketInfoForm, ticketInfoEntity);
        ticketInfoEntity.setTryLoginTimes(0);
        ticketInfoEntity.setTryLeftTicketTimes(0);
        executorService.execute(new OrderRunnable(ticketInfoEntity));
        return ResultUtils.success();
    }

    @GetMapping("/search")
    public ResultVO search(String username) {
        TicketInfoEntity ticketInfoEntity = ticketInfoRepository.findByStateAndUsername(TicketStateEnum.GRABBING.getCode(), username).get();
        if (ticketInfoEntity != null) {
            return ResultUtils.success(ticketInfoEntity);
        } else {
            return ResultUtils.success();
        }
    }
}
