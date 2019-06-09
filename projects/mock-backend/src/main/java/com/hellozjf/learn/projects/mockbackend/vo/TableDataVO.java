package com.hellozjf.learn.projects.mockbackend.vo;

import lombok.Data;

/**
 * @author hellozjf
 */
@Data
public class TableDataVO {
    private Integer id;
    private String userName;
    private Integer sex;
    private Integer state;
    private Integer interest;
    private Integer isMarried;
    private String birthday;
    private String address;
    private String time;
}
