package com.hellozjf.learn.company.zrar.csmonitor_data_generator.domain;

import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Indexed;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;

/**
 * @author Jingfeng Zhou
 */
@Table
@Data
public class Csadstate implements Serializable {

    @PrimaryKey
    private String csadid;

    @Indexed
    private Integer csadstate;

    @Indexed
    private String groupid;

    private Integer maxservicenum;

    private String protocol;

    private Integer servicenum;
}
