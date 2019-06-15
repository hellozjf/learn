package com.hellozjf.learn.projects.scoresystem.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * @author hellozjf
 */
@Entity
@Data
public class StudentEntity extends BaseEntity {
    private String name;
    private Integer sex;
    private Boolean isMarried;
    @Temporal(TemporalType.TIMESTAMP)
    private Date birthday;
    private String classId;
}
