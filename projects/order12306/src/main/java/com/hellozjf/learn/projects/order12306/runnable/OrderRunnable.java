package com.hellozjf.learn.projects.order12306.runnable;

import com.hellozjf.learn.projects.common.config.SpringContextConfig;
import com.hellozjf.learn.projects.order12306.constant.ResultEnum;
import com.hellozjf.learn.projects.order12306.constant.TicketStateEnum;
import com.hellozjf.learn.projects.order12306.domain.TicketInfoEntity;
import com.hellozjf.learn.projects.order12306.exception.Order12306Exception;
import com.hellozjf.learn.projects.order12306.repository.TicketInfoRepository;
import com.hellozjf.learn.projects.order12306.service.Client12306Service;
import com.hellozjf.learn.projects.order12306.util.EnumUtils;
import com.hellozjf.learn.projects.order12306.util.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 购票线程体
 *
 * @author Jingfeng Zhou
 */
@Slf4j
public class OrderRunnable implements Runnable {

    private TicketInfoEntity ticketInfoEntity;
    private TicketInfoRepository ticketInfoRepository;
    private Client12306Service client12306Service;

    public OrderRunnable(TicketInfoEntity ticketInfoEntity) {
        ticketInfoRepository = SpringContextConfig.getBean(TicketInfoRepository.class);
        client12306Service = SpringContextConfig.getBean(Client12306Service.class);

        // 把需要购票的信息存起来
        this.ticketInfoEntity = ticketInfoEntity;
    }

    @Override
    public void run() {

        // 如果传入的ticketInfoEntity为空，不允许开线程
        if (ticketInfoEntity == null) {
            log.error("ticketInfoEntity == null");
            return;
        }

        // 如果当前该用户已经在抢票了，不允许开线程
        TicketInfoEntity see = ticketInfoRepository.findTopByUsernameOrderByGmtCreateDesc(ticketInfoEntity.getUsername()).get();
        if (see != null && see.getState().equals(TicketStateEnum.GRABBING.getCode())) {
            // 已经在抢票中了，不允许再次抢票
            log.error("{}", ResultEnum.ALREADY_GRABBING.getMessage());
            return;
        }

        // 如果id为空，则数据库中添加一条记录，开始抢票状态
        // 如果id不为空，则更新数据库中的记录，恢复抢票状态
        ticketInfoEntity.setState(TicketStateEnum.GRABBING.getCode());
        ticketInfoEntity = ticketInfoRepository.save(ticketInfoEntity);

        try {
            // 抢票
            client12306Service.order(ticketInfoEntity);

            // 抢票成功
            ticketInfoEntity.setState(TicketStateEnum.SUCCESS.getCode());
            ticketInfoRepository.save(ticketInfoEntity);
        } catch (Exception e) {
            // 抢票失败
            ticketInfoEntity.setState(TicketStateEnum.FAILED.getCode());
            if (e instanceof Order12306Exception) {
                Order12306Exception order12306Exception = (Order12306Exception) e;
                ticketInfoEntity.setFailedReason(order12306Exception.getMessage());
                ResultEnum resultEnum = EnumUtils.getByCode(order12306Exception.getCode(), ResultEnum.class);
                ticketInfoEntity.setResultCode(resultEnum.getCode());
                ticketInfoEntity.setResultMessage(resultEnum.getMessage());
            } else {
                ticketInfoEntity.setFailedReason(ExceptionUtils.getStackTrace(e));
            }
            ticketInfoRepository.save(ticketInfoEntity);
        }
    }

}
