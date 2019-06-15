package com.hellozjf.learn.projects.scoresystem.repository;

import com.hellozjf.learn.projects.scoresystem.domain.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * @author hellozjf
 */
@RepositoryRestResource(collectionResourceRel = "class", path = "class")
public interface ClassRepository extends JpaRepository<ClassEntity, String> {
}
