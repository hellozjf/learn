package com.hellozjf.learn.projects.scoresystem.domain;

import lombok.Data;

import javax.persistence.Entity;

/**
 * @author hellozjf
 */
@Data
@Entity
public class ScoreEntity extends BaseEntity {
    private String studentId;
    private String examId;
    private Integer score;
    private String description;
}
