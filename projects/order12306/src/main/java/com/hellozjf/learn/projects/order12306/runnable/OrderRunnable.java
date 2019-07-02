package com.hellozjf.learn.projects.order12306.runnable;

import com.hellozjf.learn.projects.common.config.SpringContextConfig;
import com.hellozjf.learn.projects.order12306.constant.ResultEnum;
import com.hellozjf.learn.projects.order12306.constant.TicketStateEnum;
import com.hellozjf.learn.projects.order12306.domain.TicketInfoEntity;
import com.hellozjf.learn.projects.order12306.exception.Order12306Exception;
import com.hellozjf.learn.projects.order12306.repository.TicketInfoRepository;
import com.hellozjf.learn.projects.order12306.service.Client12306Service;
import com.hellozjf.learn.projects.order12306.util.EmailUtils;
import com.hellozjf.learn.projects.order12306.util.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.concurrent.TimeUnit;

/**
 * 购票线程体
 *
 * @author Jingfeng Zhou
 */
@Slf4j
public class OrderRunnable implements Runnable {

    private JavaMailSender mailSender;
    private TicketInfoEntity ticketInfoEntity;
    private TicketInfoRepository ticketInfoRepository;
    private Client12306Service client12306Service;

    public OrderRunnable(TicketInfoEntity ticketInfoEntity) {
        mailSender = SpringContextConfig.getBean(JavaMailSender.class);
        ticketInfoRepository = SpringContextConfig.getBean(TicketInfoRepository.class);
        client12306Service = SpringContextConfig.getBean(Client12306Service.class);

        // 把需要购票的信息存起来
        this.ticketInfoEntity = ticketInfoEntity;
    }

    @Override
    public void run() {

        while (true) {
            // 如果传入的ticketInfoEntity为空，不允许开线程
            if (ticketInfoEntity == null) {
                log.error("ticketInfoEntity == null");
                return;
            }

            // 如果当前该用户已经在抢票了，不允许开线程
            TicketInfoEntity see = ticketInfoRepository.findTopByUsernameOrderByGmtCreateDesc(ticketInfoEntity.getUsername());
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
                String orderId = client12306Service.order(ticketInfoEntity);

                // 如果填写了邮箱的话，就发送通知邮件
                if (!StringUtils.isEmpty(ticketInfoEntity.getEmail())) {
                    EmailUtils.sendSuccessEmail(mailSender, orderId, ticketInfoEntity);
                }

                // 抢票成功
                ticketInfoEntity.setState(TicketStateEnum.SUCCESS.getCode());
                ticketInfoRepository.save(ticketInfoEntity);
                break;
            } catch (Exception e) {

                // 抢票失败
                if (e instanceof Order12306Exception) {
                    Order12306Exception order12306Exception = (Order12306Exception) e;
                    if (order12306Exception.getCode().intValue() == TicketStateEnum.STOP_BY_HAND.getCode()) {
                        // 如果是手动关闭的话，就不用再发送邮件和更新错误日志了
                        return;
                    }
                }

                // 如果填写了邮箱的话，就发送通知邮件
                if (!StringUtils.isEmpty(ticketInfoEntity.getEmail())) {
                    EmailUtils.sendFailedEmail(mailSender, ticketInfoEntity, e);
                }

                ticketInfoEntity.setState(TicketStateEnum.FAILED.getCode());
                ExceptionUtils.setFaileReason(ticketInfoEntity, e);
                ticketInfoRepository.save(ticketInfoEntity);

                // 发生异常了，关五分钟小黑屋
                try {
                    TimeUnit.MINUTES.sleep(5);
                } catch (InterruptedException e1) {
                    log.error("e1 = {}", e1);
                }
            }
        }
    }

}
