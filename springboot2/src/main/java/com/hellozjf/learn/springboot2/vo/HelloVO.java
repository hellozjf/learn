package com.hellozjf.learn.springboot2.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author Jingfeng Zhou
 */
@Data
public class HelloVO {

    private Long id;

    private String name;

    private Integer age;

    private Double money;

    private Date birthday;
}
