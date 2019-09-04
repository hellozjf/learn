package com.hellozjf.learn.springboot2.service;

import java.time.LocalDateTime;

/**
 * @author Jingfeng Zhou
 */
public interface DateTimeService {

    /////////////////////时间相关/////////////////////////////////////

    /**
     * 获取当前的LocalDateTime
     * @return
     */
    LocalDateTime getCurrentTime();

    /**
     * 获取基于某个时间的小时开始时间
     * @param base
     * @return
     */
    LocalDateTime getHourStartTime(LocalDateTime base);

    /**
     * 获取基于某个时间的日开始时间
     * @return
     */
    LocalDateTime getDayStartTime(LocalDateTime base);

    /**
     * 获取基于某个时间的前一天开始时间
     * @return
     */
    LocalDateTime getPrevDayStartTime(LocalDateTime base);

    /**
     * 获取基于某个时间的后一天开始时间
     * @return
     */
    LocalDateTime getNextDayStartTime(LocalDateTime localDateTime);

    /**
     * 获取基于某个时间的星期开始时间，周一
     * @return
     */
    LocalDateTime getWeekStartTime(LocalDateTime localDateTime);

    /**
     * 获取基于某个时间的月开始时间
     * @return
     */
    LocalDateTime getMonthStartTime(LocalDateTime localDateTime);

    /**
     * 获取基于某个时间的下月开始时间
     * @return
     */
    LocalDateTime getNextMonthStartTime(LocalDateTime localDateTime);









    /////////////////////转化相关/////////////////////////////////////

    /**
     * 获取时间戳
     * @param localDateTime
     * @return
     */
    long getTimestamp(LocalDateTime localDateTime);

    /**
     * 获取某个时间的字符串表示
     * @param localDateTime
     * @param format
     * @return
     */
    String getString(LocalDateTime localDateTime, String format);

    /**
     * 将format格式的字符串转化为LocalDateTime
     * @param str
     * @return
     */
    LocalDateTime getLocalDateTime(String str, String format);














    ///////////////////存储相关/////////////////////////////////////////

    /**
     * 获取保存的时间
     * @return
     */
    LocalDateTime getSavedLocalDateTime();

    /**
     * 设置保存的时间
     * @param localDateTime
     */
    void updateSavedLocalDateTime(LocalDateTime localDateTime);
}
