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
import java.util.List;
import java.util.Optional;
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
            String field = bindingResult.getFieldError().getField();
            String errMsg = bindingResult.getFieldError().getDefaultMessage();
            log.error("{}", errMsg);
            return ResultUtils.error(ResultEnum.FORM_ERROR.getCode(), field + errMsg);
        }
        TicketInfoEntity see = ticketInfoRepository.findTopByUsernameOrderByGmtCreateDesc(ticketInfoForm.getUsername()).get();
        if (see != null && see.getState().equals(TicketStateEnum.GRABBING.getCode())) {
            // 已经在抢票中了，不允许再次抢票
            return ResultUtils.error(ResultEnum.ALREADY_GRABBING);
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
        TicketInfoEntity ticketInfoEntity = ticketInfoRepository.findTopByUsernameOrderByGmtCreateDesc(username).get();
        if (ticketInfoEntity == null) {
            return ResultUtils.error(ResultEnum.NOT_GRABBING_ANY_TICKET);
        } else {
            return ResultUtils.success(ticketInfoEntity);
        }
    }
}
