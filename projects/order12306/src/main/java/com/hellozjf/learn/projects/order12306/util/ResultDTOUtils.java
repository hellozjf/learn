package com.hellozjf.learn.projects.order12306.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.hellozjf.learn.projects.order12306.constant.ResultEnum;
import com.hellozjf.learn.projects.order12306.dto.ResultDTO;
import com.hellozjf.learn.projects.order12306.exception.Order12306Exception;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author Jingfeng Zhou
 */
@Slf4j
public class ResultDTOUtils {

    /**
     * 解析12306报文中的messages字段，将其解析为字符串
     *
     * @param messages
     * @return
     */
    public static String getMessagesString(JsonNode messages) {
        if (messages.isArray()) {
            ArrayNode arrayNode = (ArrayNode) messages;
            StringBuilder stringBuilder = new StringBuilder();
            for (JsonNode jsonNode : arrayNode) {
                stringBuilder.append(jsonNode.textValue());
                stringBuilder.append(",");
            }
            return stringBuilder.toString();
        } else {
            return messages.textValue();
        }
    }

    /**
     * 检测报文中的status字段是否为false，如果为false则要抛出Order12306Exception
     * @param resultEnum
     * @param responseString
     * @throws IOException
     */
    public static ResultDTO checkStatus(ObjectMapper objectMapper, ResultEnum resultEnum, String responseString) throws IOException {
        ResultDTO resultDTO = objectMapper.readValue(responseString, new TypeReference<ResultDTO>() {
        });
        if (resultDTO.getStatus().equalsIgnoreCase("false")) {
            log.error("{}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultDTO));
            String errmsg = getMessagesString(resultDTO.getMessages());
            throw new Order12306Exception(resultEnum.getCode(), errmsg);
        }
        return resultDTO;
    }

    /**
     * 检查result_code是否正确
     * @param objectMapper
     * @param text
     * @param rightResultCode
     * @return
     * @throws IOException
     */
    public static ResultDTO checkResultCode(ObjectMapper objectMapper, String text, String rightResultCode) throws IOException {
        ResultDTO resultDTO = objectMapper.readValue(text, new TypeReference<ResultDTO>() {
        });
        if (! resultDTO.getResultCode().equalsIgnoreCase(rightResultCode)) {
            log.error(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultDTO));
            throw new Order12306Exception(ResultEnum.PASSPORT_CAPTCHA_CAPTCHA_IMAGE64_ERROR.getCode(), resultDTO.getResultMessage());
        }
        return resultDTO;
    }
}
