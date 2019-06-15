package com.hellozjf.learn.projects.scoresystem.repository;

import com.hellozjf.learn.projects.scoresystem.domain.ScoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * @author hellozjf
 */
@RepositoryRestResource(collectionResourceRel = "score", path = "score")
public interface ScoreRepository extends JpaRepository<ScoreEntity, String> {
}
