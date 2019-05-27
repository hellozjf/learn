package com.hellozjf.learn.company.zrar.csmonitor_data_generator.service.impl;

import com.hellozjf.learn.company.zrar.csmonitor_data_generator.constant.CsadStateEnum;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.domain.Csadstate;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.dto.GroupUnitDTO;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.repository.*;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.service.GroupService;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.service.InitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author Jingfeng Zhou
 */
@Service
public class InitServiceImpl implements InitService {

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
    private GroupService groupService;

    @Autowired
    private Random random;

    @Override
    public void initAll() {
        initCsadState();
        initMessagetemp();
        initServicelog();
        initCustomservice();
        initCacsiresult();
    }

    @Override
    public void initCsadState() {

        // 先将csadstate表清空
        csadstateRepository.deleteAll();

        // 为每个地区都增加坐席
        List<GroupUnitDTO> groupUnitDTOList = groupService.getGroupUnitDTOList();
        for (GroupUnitDTO groupUnitDTO : groupUnitDTOList) {
            // 从1-20之间获取一个随机数，该数是该地区的坐席数
            int csadCount = random.nextInt(20) + 1;
            for (int i = 0; i < csadCount; i++) {
                Csadstate csadstate = new Csadstate();
                csadstate.setCsadid("9000_" + UUID.randomUUID().toString().replace("-", ""));
                csadstate.setGroupid(groupUnitDTO.getUnitCode());
                csadstate.setCsadstate(CsadStateEnum.SIGN_OUT.getCode());
                csadstate.setProtocol("HTTP");
                csadstate.setMaxservicenum(5);
                csadstate.setServicenum(0);
                csadstateRepository.save(csadstate);
            }
        }
    }

    @Override
    public void initMessagetemp() {
        messagetempRepository.deleteAll();
    }

    @Override
    public void initServicelog() {
        servicelogRepository.deleteAll();
    }

    @Override
    public void initCustomservice() {
        customserviceRepository.deleteAll();
    }

    @Override
    public void initCacsiresult() {
        cacsiresultRepository.deleteAll();
    }
}
