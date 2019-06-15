package com.hellozjf.learn.projects.scoresystem.repository;

import com.hellozjf.learn.projects.scoresystem.domain.ExamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * @author hellozjf
 */
@RepositoryRestResource(collectionResourceRel = "exam", path = "exam")
public interface ExamRepository extends JpaRepository<ExamEntity, String> {
}
