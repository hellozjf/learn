package com.hellozjf.learn.projects.order12306.repository;

import com.hellozjf.learn.projects.order12306.domain.WrongCheckEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Jingfeng Zhou
 */
public interface WrongCheckRepository extends JpaRepository<WrongCheckEntity, String> {

}
