package com.hellozjf.learn.company.zrar.csmonitor_data_generator.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.dto.GroupUnitDTO;
import com.hellozjf.learn.company.zrar.csmonitor_data_generator.service.GroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jingfeng Zhou
 */
@Service
@Slf4j
public class GroupServiceImpl implements GroupService {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<GroupUnitDTO> getGroupUnitDTOList() {
        try {
            Resource resource = new ClassPathResource("unitConfig.js");
            StringBuilder stringBuilder = new StringBuilder();
            try (InputStream inputStream = resource.getInputStream();
                 InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                 BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                while (true) {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                    stringBuilder.append(line);
                }
            } catch (Exception e) {
                log.error("e = {}", e);
                return new ArrayList<>();
            }
            String text = stringBuilder.toString();
            List<GroupUnitDTO> groupUnitDTOList = objectMapper.readValue(text,
                    new TypeReference<List<GroupUnitDTO>>() {
                    });
            return groupUnitDTOList;
        } catch (IOException e) {
            log.error("e = {}", e);
            return new ArrayList<>();
        }
    }
}
