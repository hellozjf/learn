package com.zrar.test.simulatearassistrecognition.repository;

import com.zrar.test.simulatearassistrecognition.domain.RecognitionLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Jingfeng Zhou
 */
public interface RecognitionLogRepository extends JpaRepository<RecognitionLog, Long> {
}
