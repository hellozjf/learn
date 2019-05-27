package com.hellozjf.learn.company.zrar.csmonitor_data_generator.thread;

import com.hellozjf.learn.company.zrar.csmonitor_data_generator.config.CustomConfig;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.constant.CsadStateEnum;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.domain.Csadstate;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.domain.Customservice;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.domain.Messagetemp;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.domain.Servicelog;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.repository.CsadstateRepository;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.repository.CustomserviceRepository;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.repository.MessagetempRepository;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.repository.ServicelogRepository;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.util.SleepUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

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
    private MessagetempRepository messagetempRepository;
    private ServicelogRepository servicelogRepository;
    private CustomserviceRepository customserviceRepository;
    private CustomConfig customConfig;
    private Random random;

    @Override
    public void run() {
        Csadstate csadstate = csadstateRepository.findById(csadid).orElse(null);
        String groupId = csadstate.getGroupid();
        while (true) {
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
                        if (rand < 1) {
                            // 1/3概率，说明是空闲状态
                            changeState(CsadStateEnum.IDLE);
                            SleepUtils.sleep(TimeUnit.SECONDS, randTime);
                        } else {
                            // 2/3概率，说明是应答状态
                            changeState(CsadStateEnum.ANSWER);
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
                                Messagetemp messagetemp = new Messagetemp();
                                Messagetemp.Key messagetempKey = new Messagetemp.Key();
                                messagetempKey.setUid(UUID.randomUUID().toString());
                                messagetempKey.setTime(System.currentTimeMillis());
                                messagetempKey.setMessageId(UUID.randomUUID().toString());
                                messagetemp.setKey(messagetempKey);
                                messagetemp.setChannelId("WEB");
                                messagetemp.setContent(RandomStringUtils.randomAlphanumeric(10, 51));
                                messagetemp.setGroupid(groupId);
                                messagetemp.setMsgtype(getMsgType());
                                messagetemp.setSessionId(sessionId);
                                log.debug("{} messagetemp: {}", csadid, messagetemp);
                                messagetempRepository.save(messagetemp);
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
                            servicelog.setClientInfo("{\"phone\":\"\"}");
                            servicelog.setEndtype(1);
                            servicelog.setServiceEndtime(System.currentTimeMillis());
                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            servicelog.setServicedate(dateTimeFormatter.format(localDateTime));
                            servicelog.setSessionId(sessionId);
                            servicelog.setServiceId(String.valueOf(System.currentTimeMillis()));
                            log.debug("{} servicelog: {}", csadid, servicelog);
                            servicelogRepository.save(servicelog);
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
        if (rand < 6) {
            // 6/10概率是文本消息
            return 1;
        } else if (rand < 9) {
            // 3/10概率是图片消息
            return 2;
        } else {
            // 1/10概率是视频消息
            return 3;
        }
    }
}
