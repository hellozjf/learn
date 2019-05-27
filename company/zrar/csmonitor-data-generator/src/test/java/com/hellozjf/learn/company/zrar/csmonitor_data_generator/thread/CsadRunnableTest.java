package com.hellozjf.learn.company.zrar.csmonitor_data_generator.thread;

import com.hellozjf.learn.company.zrar.csmonitor_data_generator.config.CustomConfig;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.domain.Csadstate;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.repository.*;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.util.SleepUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Jingfeng Zhou
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class CsadRunnableTest {

    @Autowired
    private CsadstateRepository csadstateRepository;

    @Autowired
    private MessagetempRepository messagetempRepository;

    @Autowired
    private ServicelogRepository servicelogRepository;

    @Autowired
    private CustomserviceRepository customserviceRepository;

    @Autowired
    private CacsiresultRepository cacsiresultRepository;

    @Autowired
    private CustomConfig customConfig;

    @Autowired
    private Random random;

    @Test
    public void runTest() {
        List<Csadstate> csadstateList = csadstateRepository.findAll();
        String csadid = csadstateList.get(0).getCsadid();
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Runnable runnable = new CsadRunnable(csadid,
                csadstateRepository,
                messagetempRepository,
                servicelogRepository,
                customserviceRepository,
                cacsiresultRepository,
                customConfig,
                random);
        executorService.execute(runnable);
        executorService.shutdown();
        while (! executorService.isTerminated()) {
            SleepUtils.sleep(TimeUnit.MINUTES, 1);
        }
    }
}