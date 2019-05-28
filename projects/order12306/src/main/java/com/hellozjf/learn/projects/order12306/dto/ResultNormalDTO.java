package com.hellozjf.learn.projects.order12306.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Data;

/**
 * @author Jingfeng Zhou
 */
@Data
public class ResultNormalDTO<T> {
    private String validateMessagesShowId;
    private Boolean status;
    private Integer httpstatus;
    private T data;
    private ArrayNode messages;
    private JsonNode validateMessages;
}
