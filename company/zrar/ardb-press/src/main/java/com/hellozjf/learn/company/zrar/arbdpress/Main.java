package com.hellozjf.learn.company.zrar.arbdpress;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.ArrayList;
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
    private CustomConfig customConfig;

    @Autowired
    private Random random;

    /**
     * springboot初始化完毕执行的函数
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        int threadNum = customConfig.getThreadNum();
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        List<Integer> csadCallNumberList = new ArrayList<>();
        List<Integer> customerCallNumberList = new ArrayList<>();
        for (int i = 0; i < threadNum; i++) {
            int csadCallNumber;
            int customerCallNumber;

            // 找到一个未被分配的座席分机号
            while (true) {
                csadCallNumber = RandomUtils.getCsadCallNumber(customConfig, random);
                if (! csadCallNumberList.contains(csadCallNumber)) {
                    csadCallNumberList.add(csadCallNumber);
                    break;
                }
            }

            // 找到一个未被分配的客户分机号
            while (true) {
                customerCallNumber = RandomUtils.getCustomerCallNumber(customConfig, random);
                if (! customerCallNumberList.contains(customerCallNumber)) {
                    customerCallNumberList.add(customerCallNumber);
                    break;
                }
            }

            // 开启压测线程
            executorService.submit(new PressRunnable(csadCallNumber, customerCallNumber));
        }
    }

    public static void main(String[] args) {
        // springboot默认不开启图形界面，通过设置headless(false)开启图形界面
        new SpringApplicationBuilder(Main.class)
                .headless(false)
                .run(args);
    }
}
