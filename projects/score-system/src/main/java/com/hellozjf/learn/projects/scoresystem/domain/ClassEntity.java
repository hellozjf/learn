package com.hellozjf.learn.projects.scoresystem.domain;

import lombok.Data;

import javax.persistence.Entity;

/**
 * @author hellozjf
 */
@Entity
@Data
public class ClassEntity extends BaseEntity {
    private String name;
    private String description;
}
