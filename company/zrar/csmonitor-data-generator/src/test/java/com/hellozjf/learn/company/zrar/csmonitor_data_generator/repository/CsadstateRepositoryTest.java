package com.hellozjf.learn.company.zrar.csmonitor_data_generator.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.domain.Csadstate;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

/**
 * @author Jingfeng Zhou
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class CsadstateRepositoryTest {

    @Autowired
    private CsadstateRepository csadstateRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void findAll() throws JsonProcessingException {
        List<Csadstate> csadstateList = csadstateRepository.findAll();
        log.debug("{}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(csadstateList));
    }

    @Test
    public void findById() {
        Csadstate csadstate = csadstateRepository.findById("f4392921-2437-4dfe-86f3-8996ce8dee04").orElse(null);
        log.debug("{}", csadstate);
    }

    @Test
    public void save() {
        Csadstate csadstate = new Csadstate();
        csadstate.setCsadid(UUID.randomUUID().toString());
        csadstate.setCsadstate(0);
        csadstate.setGroupid("1");
        csadstate.setMaxservicenum(1);
        csadstate.setProtocol("http");
        csadstate.setServicenum(0);
        csadstateRepository.save(csadstate);
    }
}