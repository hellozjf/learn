package com.hellozjf.learn.company.zrar.csmonitor_data_generator;

import com.hellozjf.learn.company.zrar.csmonitor_data_generator.config.CustomConfig;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.domain.Csadstate;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.repository.CsadstateRepository;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.repository.CustomserviceRepository;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.repository.MessagetempRepository;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.repository.ServicelogRepository;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.service.InitService;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.thread.CsadRunnable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Jingfeng Zhou
 */
@SpringBootApplication
@Slf4j
public class Main implements CommandLineRunner {

    @Autowired
    private CsadstateRepository csadstateRepository;

    @Autowired
    private MessagetempRepository messagetempRepository;

    @Autowired
    private ServicelogRepository servicelogRepository;

    @Autowired
    private CustomserviceRepository customserviceRepository;

    @Autowired
    private CustomConfig customConfig;

    @Autowired
    private Random random;

    @Autowired
    private InitService initService;

    @Override
    public void run(String... args) throws Exception {
        log.debug("customConfig = {}", customConfig);
        List<Csadstate> csadstateList = csadstateRepository.findAll();
        if (csadstateList.size() == 0) {
            // 生成随机坐席列表
            initService.initAll();
            csadstateList = csadstateRepository.findAll();
        }
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (Csadstate csadstate : csadstateList) {
            executorService.execute(new CsadRunnable(csadstate.getCsadid(),
                    csadstateRepository,
                    messagetempRepository,
                    servicelogRepository,
                    customserviceRepository,
                    customConfig,
                    random));
        }
    }

    public static void main(String[] args) {
        // springboot默认不开启图形界面，通过设置headless(false)开启图形界面
        new SpringApplicationBuilder(Main.class)
                .headless(false)
                .run(args);
    }
}
