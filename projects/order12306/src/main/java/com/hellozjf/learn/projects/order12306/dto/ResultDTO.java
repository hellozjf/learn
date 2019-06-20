package com.hellozjf.learn.projects.order12306.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

/**
 * @author Jingfeng Zhou
 */
@Data
public class ResultDTO {

    @JsonProperty("result_message")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String resultMessage;

    @JsonProperty("uamtk")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String uamtk;

    @JsonProperty("result_code")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String resultCode;

    @JsonProperty("exp")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String exp;

    @JsonProperty("cookieCode")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cookieCode;

    @JsonProperty("dfp")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dfp;

    @JsonProperty("code")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String code;

    @JsonProperty("msg")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String msg;

    @JsonProperty("data")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private JsonNode data;

    @JsonProperty("image")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String image;

    @JsonProperty("apptk")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String apptk;

    @JsonProperty("newapptk")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String newapptk;

    @JsonProperty("userName")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String userName;

    @JsonProperty("username")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String username;

    @JsonProperty("name")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;

    @JsonProperty("validateMessagesShowId")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String validateMessagesShowId;

    @JsonProperty("status")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String status;

    @JsonProperty("httpstatus")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String httpstatus;

    @JsonProperty("messages")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private JsonNode messages;

    @JsonProperty("validateMessages")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private JsonNode validateMessages;

}
