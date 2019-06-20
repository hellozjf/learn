package com.hellozjf.learn.projects.order12306.repository;

import com.hellozjf.learn.projects.order12306.domain.TicketInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author hellozjf
 */
public interface TicketInfoRepository extends JpaRepository<TicketInfoEntity, String> {
    List<TicketInfoEntity> findByState(Integer state);
    Optional<TicketInfoEntity> findTopByUsernameOrderByGmtCreateDesc(String username);
    Optional<TicketInfoEntity> findByStateAndUsername(Integer state, String username);
}
