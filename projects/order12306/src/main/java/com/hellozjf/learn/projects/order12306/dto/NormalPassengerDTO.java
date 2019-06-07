package com.hellozjf.learn.projects.order12306.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author hellozjf
 */
@Data
public class NormalPassengerDTO {

    @JsonProperty("code")
    private String code;

    @JsonProperty("passenger_name")
    private String passengerName;

    @JsonProperty("sex_code")
    private String sexCode;

    @JsonProperty("sex_name")
    private String sexName;

    @JsonProperty("born_date")
    private String bornDate;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("passenger_id_type_code")
    private String passengerIdTypeCode;

    @JsonProperty("passenger_id_type_name")
    private String passengerIdTypeName;

    @JsonProperty("passenger_id_no")
    private String passengerIdNo;

    @JsonProperty("passenger_type")
    private String passengerType;

    @JsonProperty("passenger_flag")
    private String passengerFlag;

    @JsonProperty("passenger_type_name")
    private String passengerTypeName;

    @JsonProperty("mobile_no")
    private String mobileNo;

    @JsonProperty("phone_no")
    private String phoneNo;

    @JsonProperty("email")
    private String email;

    @JsonProperty("address")
    private String address;

    @JsonProperty("postalcode")
    private String postalcode;

    @JsonProperty("first_letter")
    private String firstLetter;

    @JsonProperty("recordCount")
    private String recordCount;

    @JsonProperty("total_times")
    private String totalTimes;

    @JsonProperty("index_id")
    private String indexId;

    @JsonProperty("gat_born_date")
    private String gatBornDate;

    @JsonProperty("gat_valid_date_start")
    private String gatValidDateStart;

    @JsonProperty("gat_valid_date_end")
    private String gatValidDateEnd;

    @JsonProperty("gat_version")
    private String gatVersion;
}
