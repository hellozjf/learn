package com.hellozjf.learn.springboot2.repository;

import com.hellozjf.learn.springboot2.domain.MultiThreadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Jingfeng Zhou
 */
public interface MultiThreadRepository extends JpaRepository<MultiThreadEntity, String> {
    MultiThreadEntity findTopByNameOrderByCountDesc(String name);
}
