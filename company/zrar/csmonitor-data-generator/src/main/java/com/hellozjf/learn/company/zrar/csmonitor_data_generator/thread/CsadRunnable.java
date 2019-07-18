package com.hellozjf.learn.company.zrar.csmonitor_data_generator.thread;

import com.hellozjf.learn.company.zrar.csmonitor_data_generator.config.CustomConfig;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.constant.CsadStateEnum;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.domain.*;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.repository.*;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.util.SleepUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Jingfeng Zhou
 */
@Slf4j
@AllArgsConstructor
public class CsadRunnable implements Runnable {

    private String csadid;
    private CsadstateRepository csadstateRepository;
    private ServicelogRepository servicelogRepository;
    private CustomserviceRepository customserviceRepository;
    private CacsiresultRepository cacsiresultRepository;
    private MessageinfoRepository messageinfoRepository;
    private CustomConfig customConfig;
    private Random random;

    @Override
    public void run() {
        Csadstate csadstate = csadstateRepository.findById(csadid).orElse(null);
        String groupId = csadstate.getGroupid();
        while (true) {
            try {
                // 首先获取时间，如果时间在工作日9点-17点，才允许工作
                LocalDateTime localDateTime = LocalDateTime.now();
                int hour = localDateTime.getHour();
                int weekday = localDateTime.getDayOfWeek().getValue();
                if (customConfig.getStartHour() <= hour && hour < customConfig.getStopHour()
                        && customConfig.getStartWeekDay() <= weekday && weekday < customConfig.getStopWeekDay()) {
                    // 说明是工作时间
                    int rand = random.nextInt(4);
                    if (rand < 2) {
                        // 1/2概率，说明是签入
                        int currentHour = hour;
                        // 坐席仅在本小时内，要么是应答，要么是空闲
                        while (currentHour == hour) {
                            // 应答或空闲60-300秒
                            int randTime = random.nextInt(300 - 60 + 1) + 60;
                            rand = random.nextInt(3);
                            changeState(CsadStateEnum.SIGN_IN);
                            if (rand < 1) {
                                // 1/3概率，说明是空闲状态
                                SleepUtils.sleep(TimeUnit.SECONDS, randTime);
                            } else {
                                // 2/3概率，说明是应答状态
                                int totalTime = randTime;
                                int startTime = 0;
                                String sessionId = UUID.randomUUID().toString();
                                long servicetime = System.currentTimeMillis();
                                String clientId = UUID.randomUUID().toString();
                                // 往customservice表插入一条记录
                                Customservice customservice = new Customservice();
                                Customservice.Key customserviceKey = new Customservice.Key();
                                customserviceKey.setClientId(clientId);
                                customserviceKey.setCsadId(csadid);
                                customservice.setKey(customserviceKey);
                                customservice.setApplyType(1);
                                customservice.setCacsiInformState(1);
                                customservice.setClientType("WEB");
                                customservice.setOvertimeClientInformState(1);
                                customservice.setServiceTime(servicetime);
                                customservice.setSessionClientInformState(1);
                                customservice.setSessionCsadInformState(1);
                                customservice.setSessionId(sessionId);
                                customservice.setTimeClientLastest(System.currentTimeMillis());
                                customservice.setTimeCsadLastest(System.currentTimeMillis());
                                customserviceRepository.save(customservice);
                                // 每1-10秒插入一条人工坐席消息
                                while (startTime < totalTime) {
                                    Messageinfo messageinfo = new Messageinfo();
                                    Messageinfo.Key messageinfoKey = new Messageinfo.Key();
                                    messageinfoKey.setSpecifyPk(UUID.randomUUID().toString());
                                    messageinfoKey.setTimeSave(System.currentTimeMillis());
                                    messageinfoKey.setMessageId(UUID.randomUUID().toString());
                                    messageinfo.setKey(messageinfoKey);
                                    messageinfo.setChannelFrom("WEB");
                                    messageinfo.setChannelTo("WEB");
                                    messageinfo.setContent(RandomStringUtils.randomAlphanumeric(10, 51));
                                    messageinfo.setFromuser(csadid);
                                    messageinfo.setMsgtype(getMsgType());
                                    messageinfo.setNumSend(0);
                                    messageinfo.setSessionId(sessionId);
                                    messageinfo.setState(1);
                                    messageinfo.setStype(0);
                                    messageinfo.setTimeReceipt(null);
                                    messageinfo.setTimeSend(System.currentTimeMillis());
                                    messageinfo.setTouser(clientId);
                                    messageinfo.setType(1);
                                    log.debug("{} messagetemp: {}", csadid, messageinfo);
                                    messageinfoRepository.save(messageinfo);
                                    randTime = random.nextInt(10) + 1;
                                    SleepUtils.sleep(TimeUnit.SECONDS, randTime);
                                    startTime += randTime;
                                }
                                // 从customservice表移除之前插入的记录
                                customserviceRepository.deleteById(customserviceKey);
                                // 所有消息都发送完毕了，插入一条服务日志
                                Servicelog servicelog = new Servicelog();
                                Servicelog.Key servicelogKey = new Servicelog.Key();
                                servicelogKey.setCsadid(csadid);
                                servicelogKey.setClientid(clientId);
                                servicelogKey.setServicetime(servicetime);
                                servicelog.setKey(servicelogKey);
                                // 来源五等分
                                int clientInfo = random.nextInt(5);
                                switch (clientInfo) {
                                    case 0:
                                        servicelog.setClientInfo("{\"source\":\"WEB\"}");
                                        break;
                                    case 1:
                                        servicelog.setClientInfo("{\"source\":\"wap\"}");
                                        break;
                                    case 2:
                                        servicelog.setClientInfo("{\"source\":\"ios\"}");
                                        break;
                                    case 3:
                                        servicelog.setClientInfo("{\"source\":\"android\"}");
                                        break;
                                    case 4:
                                        servicelog.setClientInfo("{\"source\":\"miniprogram\"}");
                                        break;
                                }
                                servicelog.setEndtype(1);
                                servicelog.setServiceEndtime(System.currentTimeMillis());
                                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                servicelog.setServicedate(dateTimeFormatter.format(localDateTime));
                                servicelog.setSessionId(sessionId);
                                servicelog.setServiceId(String.valueOf(System.currentTimeMillis()));
                                log.debug("{} servicelog: {}", csadid, servicelog);
                                servicelogRepository.save(servicelog);

                                // 有10%的概率是会话转移
                                int sessionTransfer = random.nextInt(10);
                                if (sessionTransfer == 0) {
                                    Servicelog servicelogTransfer = new Servicelog();
                                    BeanUtils.copyProperties(servicelog, servicelogTransfer);
                                    servicelogTransfer.getKey().setServicetime(System.currentTimeMillis());
                                    servicelogTransfer.setEndtype(4);
                                    log.debug("{} servicelogTransfer: {}", csadid, servicelog);
                                    servicelogRepository.save(servicelogTransfer);
                                }

                                if (willAnswerSurvey()) {
                                    // 如果会答复满意率调查表，则插入满意率调查结果
                                    Cacsiresult cacsiresult = new Cacsiresult();
                                    Cacsiresult.Key cacsiresultKey = new Cacsiresult.Key();
                                    cacsiresultKey.setClientid(clientId);
                                    cacsiresultKey.setSavetime(System.currentTimeMillis());
                                    cacsiresult.setKey(cacsiresultKey);
                                    cacsiresult.setCacsi(getSurveyResult());
                                    cacsiresult.setSessionid(sessionId);
                                    cacsiresultRepository.save(cacsiresult);
                                }
                            }
                            localDateTime = LocalDateTime.now();
                            hour = localDateTime.getHour();
                        }
                    } else if (rand < 3) {
                        // 1/4概率，说明是示忙，将坐席状态修改为示忙，然后等待1小时之后再次尝试
                        changeState(CsadStateEnum.BUSY);
                        int currentHour = hour;
                        while (currentHour == hour) {
                            SleepUtils.sleep(TimeUnit.MINUTES, 1);
                            localDateTime = LocalDateTime.now();
                            hour = localDateTime.getHour();
                        }
                    } else {
                        // 1/4概率，说明是离线，将坐席状态修改为离线，然后等待1小时之后再次尝试
                        changeState(CsadStateEnum.SIGN_OUT);
                        int currentHour = hour;
                        while (currentHour == hour) {
                            SleepUtils.sleep(TimeUnit.MINUTES, 1);
                            localDateTime = LocalDateTime.now();
                            hour = localDateTime.getHour();
                        }
                    }
                } else {
                    // 说明不是工作时间，将坐席状态修改为离线，然后等待1分钟之后再次尝试
                    changeState(CsadStateEnum.SIGN_OUT);
                    SleepUtils.sleep(TimeUnit.MINUTES, 1);
                }
            } catch (Exception e) {
                // 不知道啥原因就报错了，休眠1分钟后再次启动，防止线程退出
                log.error("e = {}", e);
                SleepUtils.sleep(TimeUnit.MINUTES, 1);
            }
        }
    }

    private void changeState(CsadStateEnum csadStateEnum) {
        Csadstate csadstate = csadstateRepository.findById(csadid).orElse(null);
        csadstate.setCsadstate(csadStateEnum.getCode());
        log.debug("{} changeState: {}", csadid, csadStateEnum.getCode());
        csadstateRepository.save(csadstate);
    }

    private Integer getMsgType() {
        int rand = random.nextInt(10);
        if (rand < 5) {
            // 5/10概率是文本消息
            return 1;
        } else if (rand < 8) {
            // 3/10概率是图片消息
            return 2;
        } else if (rand < 9) {
            // 1/10概率是视频消息
            return 3;
        } else {
            // 1/10概率是音频消息
            return 4;
        }
    }

    private boolean willAnswerSurvey() {
        int rand = random.nextInt(4);
        if (rand < 3) {
            return true;
        } else {
            return false;
        }
    }

    private String getSurveyResult() {
        int rand = random.nextInt(10);
        if (rand < 1) {
            // 10%的用户非常满意
            return "1";
        } else if (rand < 3) {
            // 20%的用户满意
            return "2";
        } else if (rand < 9) {
            // 60%的用户一般
            return "3";
        } else {
            // 10%的用户不满意
            return "4";
        }
    }
}
