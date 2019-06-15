package com.hellozjf.learn.projects.scoresystem.repository;

import com.hellozjf.learn.projects.scoresystem.domain.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * @author hellozjf
 */
@RepositoryRestResource(collectionResourceRel = "student", path = "student")
public interface StudentRepository extends JpaRepository<StudentEntity, String> {
}
