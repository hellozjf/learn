package com.hellozjf.learn.projects.order12306.controller;

import com.hellozjf.learn.projects.common.vo.ResultVO;
import com.hellozjf.learn.projects.order12306.constant.ResultEnum;
import com.hellozjf.learn.projects.order12306.constant.TicketStateEnum;
import com.hellozjf.learn.projects.order12306.domain.TicketInfoEntity;
import com.hellozjf.learn.projects.order12306.dto.NormalPassengerDTO;
import com.hellozjf.learn.projects.order12306.dto.TicketInfoDTO;
import com.hellozjf.learn.projects.order12306.exception.Order12306Exception;
import com.hellozjf.learn.projects.order12306.form.TicketInfoForm;
import com.hellozjf.learn.projects.order12306.repository.TicketInfoRepository;
import com.hellozjf.learn.projects.order12306.runnable.OrderRunnable;
import com.hellozjf.learn.projects.order12306.service.Client12306Service;
import com.hellozjf.learn.projects.order12306.util.EnumUtils;
import com.hellozjf.learn.projects.order12306.util.ExceptionUtils;
import com.hellozjf.learn.projects.order12306.util.ResultUtils;
import com.hellozjf.learn.projects.order12306.vo.TicketInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URISyntaxException;
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

    @Autowired
    private Client12306Service client12306Service;

    /**
     * 抢票接口
     * @param ticketInfoForm
     * @param bindingResult
     * @return
     */
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

    /**
     * 查询购票状态
     * @param username
     * @return
     */
    @GetMapping("/queryState")
    public ResultVO queryState(String username) {
        TicketInfoEntity ticketInfoEntity = ticketInfoRepository.findTopByUsernameOrderByGmtCreateDesc(username).get();
        if (ticketInfoEntity == null) {
            return ResultUtils.error(ResultEnum.NOT_GRABBING_ANY_TICKET);
        } else {
            TicketInfoVO ticketInfoVO = new TicketInfoVO();
            BeanUtils.copyProperties(ticketInfoEntity, ticketInfoVO);
            ticketInfoVO.setStateString(EnumUtils.getByCode(ticketInfoVO.getState(), TicketStateEnum.class).getDesc());
            return ResultUtils.success(ticketInfoVO);
        }
    }

    /**
     * 停止抢票
     * @param username
     * @return
     */
    @PostMapping("/stopGrabbing")
    public ResultVO stopGrabbing(String username) {
        TicketInfoEntity ticketInfoEntity = ticketInfoRepository.findTopByUsernameOrderByGmtCreateDesc(username).get();
        if (ticketInfoEntity == null) {
            return ResultUtils.error(ResultEnum.NOT_GRABBING_ANY_TICKET);
        } else {
            ticketInfoEntity.setState(TicketStateEnum.STOP_BY_HAND.getCode());
            ticketInfoRepository.save(ticketInfoEntity);
            return ResultUtils.success();
        }
    }

    /**
     * 查询购票人列表
     * @param username
     * @return
     */
    @GetMapping("/queryTicketPeopleList")
    public ResultVO queryTicketPeopleList(String username, String password) throws IOException, URISyntaxException {

        TicketInfoEntity ticketInfoEntity = new TicketInfoEntity();
        ticketInfoEntity.setUsername(username);
        ticketInfoEntity.setPassword(password);
        ticketInfoEntity.setTryLoginTimes(0);
        ticketInfoEntity.setTryLeftTicketTimes(0);
        ticketInfoEntity.setState(TicketStateEnum.QUERY_INFO.getCode());

        try {
            // 保存一条查询信息的记录，防止覆盖了正常抢票的记录
            ticketInfoEntity = ticketInfoRepository.save(ticketInfoEntity);

            CloseableHttpClient httpClient = client12306Service.login(username, password);
            List<NormalPassengerDTO> normalPassengerDTOList = client12306Service.queryTicketPeopleList(httpClient);

            // 查询信息成功
            ticketInfoEntity.setState(TicketStateEnum.QUERY_INFO_SUCCESS.getCode());
            ticketInfoRepository.save(ticketInfoEntity);
            return ResultUtils.success(normalPassengerDTOList);
        } catch (Exception e) {
            // 查询信息失败
            ticketInfoEntity.setState(TicketStateEnum.QUERY_INFO_FAILED.getCode());
            ExceptionUtils.setFaileReason(ticketInfoEntity, e);
            ticketInfoRepository.save(ticketInfoEntity);
            return ResultUtils.error(ResultEnum.QUERY_TICKET_PEOPLE_INFO_FAILED);
        }
    }

    /**
     * 查出所有trainDate下fromStation到toStation的火车票
     * @param username
     * @param password
     * @param trainDate
     * @param fromStation
     * @param toStation
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    @GetMapping("/queryLeftTicketList")
    public ResultVO queryLeftTicketList(String username,
                       String password,
                       String trainDate,
                       String fromStation,
                       String toStation) throws IOException, URISyntaxException {

        // 保存一条查询信息的记录，防止覆盖了正常抢票的记录
        TicketInfoEntity ticketInfoEntity = new TicketInfoEntity();
        ticketInfoEntity.setUsername(username);
        ticketInfoEntity.setPassword(password);
        ticketInfoEntity.setTryLoginTimes(0);
        ticketInfoEntity.setTryLeftTicketTimes(0);
        ticketInfoEntity.setState(TicketStateEnum.QUERY_INFO.getCode());

        try {
            ticketInfoRepository.save(ticketInfoEntity);

            CloseableHttpClient httpClient = client12306Service.login(username, password);
            List<TicketInfoDTO> ticketInfoDTOList = client12306Service.queryLeftTicketList(httpClient, trainDate, fromStation, toStation);

            // 查询信息成功
            ticketInfoEntity.setState(TicketStateEnum.QUERY_INFO_SUCCESS.getCode());
            ticketInfoRepository.save(ticketInfoEntity);
            return ResultUtils.success(ticketInfoDTOList);
        } catch (Exception e) {
            // 查询信息失败
            ticketInfoEntity.setState(TicketStateEnum.QUERY_INFO_FAILED.getCode());
            ExceptionUtils.setFaileReason(ticketInfoEntity, e);
            ticketInfoRepository.save(ticketInfoEntity);
            return ResultUtils.error(ResultEnum.QUERY_LEFT_TICKET_INFO_FAILED);
        }
    }
}
