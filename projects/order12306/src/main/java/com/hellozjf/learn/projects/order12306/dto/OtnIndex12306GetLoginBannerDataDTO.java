package com.hellozjf.learn.projects.order12306.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author hellozjf
 */
@Data
public class OtnIndex12306GetLoginBannerDataDTO {

    @JsonProperty("index_banner_url")
    private List<IndexBannerUrlDTO> indexBannerUrl;

}
