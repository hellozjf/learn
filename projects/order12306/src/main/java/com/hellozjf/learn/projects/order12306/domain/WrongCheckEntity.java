package com.hellozjf.learn.projects.order12306.domain;

import com.hellozjf.learn.projects.common.domain.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Lob;

/**
 * @author Jingfeng Zhou
 */
@Entity
@Data
public class WrongCheckEntity extends BaseEntity {
    @Lob
    private String image;
    private String code;
}
