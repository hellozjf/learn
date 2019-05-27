package com.hellozjf.learn.company.zrar.csmonitor_data_generator.domain;

import lombok.Data;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;

import java.io.Serializable;

/**
 * @author Jingfeng Zhou
 */
@Table
@Data
public class Cacsiresult implements Serializable {

    @PrimaryKeyClass
    @Data
    public static class Key implements Serializable {

        @PrimaryKeyColumn(name = "clientid", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
        private String clientid;

        @PrimaryKeyColumn(name = "savetime", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
        private Long savetime;
    }

    @PrimaryKey
    private Cacsiresult.Key key;

    private String cacsi;

    @Indexed
    private String sessionid;
}
