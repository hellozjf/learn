package com.hellozjf.learn.springboot2.repository;

import com.hellozjf.learn.springboot2.domain.MultiThreadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * @author Jingfeng Zhou
 */
@RepositoryRestResource(collectionResourceRel = "class", path = "class")
public interface MultiThreadRepository extends JpaRepository<MultiThreadEntity, String> {

    @RestResource(path = "findTopByNameOrderByCountDesc", rel = "findTopByNameOrderByCountDesc")
    MultiThreadEntity findTopByNameOrderByCountDesc(@Param("name") String name);
}
