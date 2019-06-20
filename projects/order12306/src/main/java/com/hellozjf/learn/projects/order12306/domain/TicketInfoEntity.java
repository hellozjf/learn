package com.hellozjf.learn.projects.order12306.domain;

import com.hellozjf.learn.projects.common.domain.BaseEntity;
import com.hellozjf.learn.projects.order12306.constant.TicketStateEnum;
import lombok.Data;

import javax.persistence.*;

/**
 * @author Jingfeng Zhou
 */
@Entity
@Data
@Table(indexes = {
        @Index(columnList = "state,username"),
        @Index(columnList = "username,gmtCreate")
})
public class TicketInfoEntity extends BaseEntity {

    /**
     * 哪天出发，例如2019-04-26
     */
    @Column
    private String trainDate;

    /**
     * 哪天返回，如果是单程，就填写今天的日期
     */
    private String backTrainDate;

    /**
     * 哪辆火车，例如D777
     */
    private String stationTrain;

    /**
     * 从哪个站出发，例如杭州东
     */
    private String fromStation;

    /**
     * 达到哪个站，例如宁波
     */
    private String toStation;

    /**
     * 座位类型，例如二等座
     */
    private String seatType;

    /**
     * 购票人姓名，例如周靖峰
     */
    private String ticketPeople;

    /**
     * 12306账号
     */
    private String username;

    /**
     * 12306密码
     */
    private String password;

    /**
     * 需要通知的邮箱
     */
    private String email;

    /**
     * 购票状态
     * @see TicketStateEnum
     */
    private Integer state;

    /**
     * 购票失败原因
     */
    @Lob
    private String failedReason;

    /**
     * 失败的代码
     */
    private Integer resultCode;

    /**
     * 失败代码对应的描述
     */
    private String resultMessage;

    /**
     * 尝试登陆的次数
     */
    private Integer tryLoginTimes;

    /**
     * 余票查询的次数
     */
    private Integer tryLeftTicketTimes;
}
