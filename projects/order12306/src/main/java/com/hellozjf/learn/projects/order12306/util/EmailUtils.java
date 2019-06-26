package com.hellozjf.learn.projects.order12306.util;

import com.hellozjf.learn.projects.order12306.domain.TicketInfoEntity;
import com.hellozjf.learn.projects.order12306.exception.Order12306Exception;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * @author Jingfeng Zhou
 */
public class EmailUtils {

    /**
     * 发送邮件
     * @param mailSender
     * @param orderId
     * @param ticketInfoEntity
     */
    public static void sendSuccessEmail(MailSender mailSender, String orderId, TicketInfoEntity ticketInfoEntity) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("908686171@qq.com");
        message.setTo(ticketInfoEntity.getEmail());
        message.setSubject("火车票抢票成功");
        // 恭喜您订票成功，订单号为：E898599288, 请立即打开浏览器登录12306，访问‘未完成订单’，在30分钟内完成支付!
        String text = String.format("%s，从%s到%s，列车%s，乘车人%s，已抢票成功，订单编号%s，请尽快登录12306支付购买",
                ticketInfoEntity.getTrainDate(),
                ticketInfoEntity.getFromStation(),
                ticketInfoEntity.getToStation(),
                ticketInfoEntity.getStationTrain(),
                ticketInfoEntity.getTicketPeople(),
                orderId);
        message.setText(text);

        mailSender.send(message);
    }

    /**
     * 发送失败邮件
     * @param mailSender
     * @param ticketInfoEntity
     */
    public static void sendFailedEmail(MailSender mailSender, TicketInfoEntity ticketInfoEntity, Exception e) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("908686171@qq.com");
        message.setTo(ticketInfoEntity.getEmail());
        message.setSubject("火车票抢票失败");
        // 恭喜您订票成功，订单号为：E898599288, 请立即打开浏览器登录12306，访问‘未完成订单’，在30分钟内完成支付!
        String text = String.format("%s，从%s到%s，列车%s，乘车人%s，抢票失败，请重新抢票\n%s",
                ticketInfoEntity.getTrainDate(),
                ticketInfoEntity.getFromStation(),
                ticketInfoEntity.getToStation(),
                ticketInfoEntity.getStationTrain(),
                ticketInfoEntity.getTicketPeople(),
                ExceptionUtils.getFailedReason(e));
        message.setText(text);

        mailSender.send(message);
    }
}
