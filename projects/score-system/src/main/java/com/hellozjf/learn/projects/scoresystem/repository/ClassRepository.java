package com.hellozjf.learn.projects.scoresystem.repository;

import com.hellozjf.learn.projects.scoresystem.domain.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

/**
 * @author hellozjf
 */
@RepositoryRestResource(collectionResourceRel = "class", path = "class")
public interface ClassRepository extends JpaRepository<ClassEntity, String> {

    @RestResource(path = "nameLike", rel = "nameLike")
    List<ClassEntity> findByNameLike(@Param("name") String name);
}
