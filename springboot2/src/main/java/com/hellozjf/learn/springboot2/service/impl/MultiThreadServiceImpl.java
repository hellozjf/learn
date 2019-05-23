package com.hellozjf.learn.springboot2.service.impl;

import com.hellozjf.learn.springboot2.domain.MultiThreadEntity;
import com.hellozjf.learn.springboot2.repository.MultiThreadRepository;
import com.hellozjf.learn.springboot2.service.MultiThreadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Jingfeng Zhou
 */
@Service
@Slf4j
public class MultiThreadServiceImpl implements MultiThreadService {

    @Autowired
    private MultiThreadRepository multiThreadRepository;

    @Override
    public void testMultiThreads(int threadNum, int loopNum) {

        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        for (int i = 0; i < threadNum; i++) {
            executorService.execute(() -> {
                MultiThreadEntity multiThreadEntity = multiThreadRepository.findTopByNameOrderByCountDesc(Thread.currentThread().getName());
                int startCount = multiThreadEntity == null ? 0 : multiThreadEntity.getCount() + 1;
                for (int count = startCount; ; count++) {
                    multiThreadEntity = new MultiThreadEntity();
                    multiThreadEntity.setName(Thread.currentThread().getName());
                    multiThreadEntity.setCount(count);
                    log.debug("{}", multiThreadEntity);
                    multiThreadRepository.save(multiThreadEntity);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        log.error("e = {}", e);
                    }
                }
            });
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                log.error("e = {}", e);
            }
        }
    }
}
