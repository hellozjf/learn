package com.hellozjf.learn.projects.order12306.dto;

import lombok.Data;

/**
 * 车票信息
 *
 *  0: yNeV9DSvHkgreA8P1IKJGQ61Eyfq7S2HxEHCfzd5sx36NH3FYQ%2B%2FeS%2FR531fk4JETOgR4x4tKKwH%0Ar9gzxOHCgHBqaJPJFRxsmfkCY%2B2Q%2FWIXXOr22GMrQuXFH2dwgL%2FXkjaGShjbnX2JujsMUCAeQYmf%0ATa5Kq1eNOARWCFXQujCdilqTo%2BAW9vwcExWdbaJQDWd1Zl1QsO%2BrGQXbPRkKrAJI74DhHQ%2FAYQ8%2B%0A9wImYjI57AQZ8x8XJq37gktYBV09n%2B3EHJyqcwUhsIetWkkteYyzQvh3hKiEvaMZLpPuvpu5hmsP%0Ak%2Bb6Rw%3D%3D|			secret
 *  1: 预订|
 *  2: 53000K849902|
 *  3: K8499|		stationTrain
 *  4: FYH|		    起点
 *  5: NGH|		    终点
 *  6: HZH|		    杭州
 *  7: NGH|		    宁波
 *  8: 02:46|		beginTime
 *  9: 05:45|		arriveTime
 * 10: 02:59|		duringTime
 * 11: Y|
 * 12: vrYGyfBE6pJ%2FhH2oB%2By8bGISe0FjK3Tf7zNa95fWwgsUh1Ai1nZWuAy8HkM%3D|
 * 13: 20190630|	时间
 * 14: 3|
 * 15: H2|
 * 16: 09|
 * 17: 12|
 * 18: 0|
 * 19: 0|
 * 20: |
 * 21: |			高级软卧
 * 22: |			其他
 * 23: 7|			软卧
 * 24: |			软座
 * 25: |			特等座
 * 26: 有|			无座
 * 27: |
 * 28: 20|			硬卧
 * 29: 有|			硬座
 * 30: |			二等座
 * 31: |			一等座
 * 32: |			商务座
 * 33: |			动卧
 * 34: 10401030|
 * 35: 1413|
 * 36: 0|
 * 37: 0|
 *
 * @author Jingfeng Zhou
 */
@Data
public class TicketInfoDTO {

    /**
     * 加密信息，index:0
     */
    private String secret;

    /**
     * 列车车次，index:3
     */
    private String stationTrain;

    /**
     * 起点站代码，index:4
     */
    private String beginStationCode;

    /**
     * 终点站代码，index:5
     */
    private String endStationCode;

    /**
     * 出发站代码，index:6
     */
    private String fromStationCode;

    /**
     * 到达站代码，index:7
     */
    private String toStationCode;

    /**
     * 出发时间，index:8
     */
    private String fromTime;

    /**
     * 到达时间，index:9
     */
    private String toTime;

    /**
     * 持续时间，index:10
     */
    private String duringTime;

    /**
     * 出发日期，index:13
     */
    private String trainDate;

    /**
     * 高级软卧，index:21
     */
    private String advancedSoftSleeper;

    /**
     * 其它，index:22
     */
    private String other;

    /**
     * 软卧，index:23
     */
    private String softSleeper;

    /**
     * 软座，index:24
     */
    private String softSeat;

    /**
     * 特等座，index:25
     */
    private String principalSeat;

    /**
     * 无座，index:26
     */
    private String noSeat;

    /**
     * 硬卧，index:28
     */
    private String hardSleeper;

    /**
     * 硬座，index:29
     */
    private String hardSeat;

    /**
     * 二等座，index:30
     */
    private String secondClass;

    /**
     * 一等座，index:31
     */
    private String firstClass;

    /**
     * 商务座，index:32
     */
    private String businessClass;

    /**
     * 动卧，index:33
     */
    private String stillLie;
}
