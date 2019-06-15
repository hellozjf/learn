package com.hellozjf.learn.projects.scoresystem.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * @author hellozjf
 */
@Data
@Entity
public class ExamEntity extends BaseEntity {
    private String name;
    @Temporal(TemporalType.TIMESTAMP)
    private Date examDate;
    private String description;
}
