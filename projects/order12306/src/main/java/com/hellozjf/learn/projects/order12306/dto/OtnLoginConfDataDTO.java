package com.hellozjf.learn.projects.order12306.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author hellozjf
 */
@Data
public class OtnLoginConfDataDTO {
    private boolean isstudentDate;
    @JsonProperty("is_login_passCode")
    private String isLoginPassCode;
    @JsonProperty("is_sweep_login")
    private String isSweepLogin;
    @JsonProperty("psr_qr_code_result")
    private String psrQrCodeResult;
    @JsonProperty("login_url")
    private String loginUrl;
    private List<String> studentDate;
    @JsonProperty("stu_control")
    private Integer stuControl;
    @JsonProperty("is_uam_login")
    private String isUamLogin;
    @JsonProperty("is_login")
    private String isLogin;
    @JsonProperty("other_control")
    private Integer otherControl;
}
