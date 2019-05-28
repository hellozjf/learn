package com.hellozjf.learn.projects.order12306.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author hellozjf
 */
@Data
public class ResultLoginDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String image;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String uamtk;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String apptk;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String newapptk;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String username;

    @JsonProperty("result_message")
    private String resultMessage;

    @JsonProperty("result_code")
    private Integer resultCode;
}
