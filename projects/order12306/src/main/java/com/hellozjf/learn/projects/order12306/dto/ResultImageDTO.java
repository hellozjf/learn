package com.hellozjf.learn.projects.order12306.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author hellozjf
 */
@Data
public class ResultImageDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String image;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String uamtk;

    @JsonProperty("result_message")
    private String resultMessage;

    @JsonProperty("result_code")
    private String resultCode;
}
