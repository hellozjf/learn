package com.hellozjf.learn.springboot2.service.impl;

import com.hellozjf.learn.springboot2.service.DateTimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * @author Jingfeng Zhou
 */
@Service
public class DateTimeServiceImpl implements DateTimeService {

    private Logger logger = LoggerFactory.getLogger(DateTimeServiceImpl.class);

    public static final String FORMAT_YYYYMMDDHH = "yyyy-MM-dd HH";
    public static final String SAVED_LOCAL_DATE_FILE_FILE_NAME = "savedLocalDateTime";

    /**
     * 获取当前的LocalDateTime
     * @return
     */
    @Override
    public LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }

    @Override
    public LocalDateTime getHourStartTime(LocalDateTime base) {
        return base.withNano(0).withSecond(0).withMinute(0);
    }

    @Override
    public LocalDateTime getDayStartTime(LocalDateTime base) {
        return getHourStartTime(base).withHour(0);
    }

    @Override
    public LocalDateTime getPrevDayStartTime(LocalDateTime base) {
        return getDayStartTime(base).minusDays(1);
    }

    @Override
    public LocalDateTime getNextDayStartTime(LocalDateTime base) {
        return getDayStartTime(base).plusDays(1);
    }

    @Override
    public LocalDateTime getWeekStartTime(LocalDateTime base) {
        // 参考：https://stackoverflow.com/questions/28450720/get-date-of-first-day-of-week-based-on-localdate-now-in-java-8
        TemporalField dayOfWeek = WeekFields.of(DayOfWeek.MONDAY, 1).dayOfWeek();
        return getDayStartTime(base).with(dayOfWeek, 1);
    }

    @Override
    public LocalDateTime getMonthStartTime(LocalDateTime base) {
        return getDayStartTime(base).withDayOfMonth(1);
    }

    @Override
    public LocalDateTime getNextMonthStartTime(LocalDateTime base) {
        return getMonthStartTime(base).plusMonths(1);
    }

    @Override
    public long getTimestamp(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    @Override
    public String getString(LocalDateTime localDateTime, String format) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        String dateStr = df.format(localDateTime);
        return dateStr;
    }

    @Override
    public LocalDateTime getLocalDateTime(String str, String format) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        LocalDateTime localDateTime = LocalDateTime.parse(str, df);
        return localDateTime;
    }

    @Override
    public LocalDateTime getSavedLocalDateTime() {
        File file = new File(SAVED_LOCAL_DATE_FILE_FILE_NAME);
        try (FileInputStream fileInputStream = new FileInputStream(file);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            return (LocalDateTime) objectInputStream.readObject();
        } catch (Exception e) {
            logger.error("e = {}", e);
            return null;
        }
    }

    @Override
    public void updateSavedLocalDateTime(LocalDateTime localDateTime) {
        File file = new File(SAVED_LOCAL_DATE_FILE_FILE_NAME);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(localDateTime);
        } catch (Exception e) {
            logger.error("e = {}", e);
        }
    }

}
