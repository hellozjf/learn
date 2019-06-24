package com.hellozjf.learn.projects.order12306.util;

import com.hellozjf.learn.projects.order12306.constant.ResultEnum;
import com.hellozjf.learn.projects.order12306.domain.TicketInfoEntity;
import com.hellozjf.learn.projects.order12306.exception.Order12306Exception;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 参考：https://blog.csdn.net/zhxdick/article/details/78955576
 *
 * @author hellozjf
 */
public class ExceptionUtils {

    /**
     * 获取异常堆栈字符串
     * @param e
     * @return
     */
    public static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.close();
        try {
            sw.close();
        } catch (IOException e1) {
            //ignore
        }
        return sw.toString();
    }

    /**
     * 设置失败原因
     * @param ticketInfoEntity
     * @param e
     */
    public static void setFaileReason(TicketInfoEntity ticketInfoEntity, Exception e) {
        if (e instanceof Order12306Exception) {
            Order12306Exception order12306Exception = (Order12306Exception) e;
            ticketInfoEntity.setFailedReason(order12306Exception.getMessage());
            ResultEnum resultEnum = EnumUtils.getByCode(order12306Exception.getCode(), ResultEnum.class);
            ticketInfoEntity.setResultCode(resultEnum.getCode());
            ticketInfoEntity.setResultMessage(resultEnum.getMessage());
        } else {
            ticketInfoEntity.setFailedReason(ExceptionUtils.getStackTrace(e));
        }
    }
}
