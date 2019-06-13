package com.hellozjf.learn.projects.mockbackend.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Jingfeng Zhou
 */
@Data
public class TablePageVO {
    private List<TableDataVO> list;
    private Integer page;
    @JsonProperty("page_size")
    private Integer pageSize;
    private Integer total;
}
