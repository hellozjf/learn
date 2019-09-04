package com.hellozjf.learn.projects.order12306;

import com.hellozjf.learn.projects.order12306.constant.TicketStateEnum;
import com.hellozjf.learn.projects.order12306.domain.TicketInfoEntity;
import com.hellozjf.learn.projects.order12306.repository.TicketInfoRepository;
import com.hellozjf.learn.projects.order12306.runnable.OrderRunnable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 这里扫描com.hellozjf.learn是因为SpringContextConfig类在common包下面
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.hellozjf.learn"})
public class Order12306Application implements CommandLineRunner {

    @Autowired
    private TicketInfoRepository ticketInfoRepository;

    @Autowired
    private ExecutorService executorService;

    public static void main(String[] args) {
        // springboot默认不开启图形界面，通过设置headless(false)开启图形界面
        new SpringApplicationBuilder(Order12306Application.class)
                .headless(false)
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 启动的时候，找到所有还在抢票的项目，然后让它们继续去抢票
        List<TicketInfoEntity> ticketInfoEntityList = ticketInfoRepository.findByState(TicketStateEnum.GRABBING.getCode());
        for (TicketInfoEntity ticketInfoEntity : ticketInfoEntityList) {
            // 重新再开启购票线程
            executorService.execute(new OrderRunnable(ticketInfoEntity));
        }
    }
}
