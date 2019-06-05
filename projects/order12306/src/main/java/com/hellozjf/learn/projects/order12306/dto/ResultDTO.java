package com.hellozjf.learn.projects.order12306.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Jingfeng Zhou
 */
@Data
public class ResultDTO {

    @JsonProperty("result_message")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String resultMessage;

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
    private String data;

    @JsonProperty("image")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String image;
}
