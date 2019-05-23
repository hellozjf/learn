package com.hellozjf.learn.springboot2.domain;

import lombok.Data;

import javax.persistence.Entity;

/**
 * @author Jingfeng Zhou
 */
@Entity
@Data
public class MultiThreadEntity extends BaseEntity {
    private String name;
    private Integer count;
}
