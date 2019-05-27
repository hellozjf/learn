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
public class Servicelog implements Serializable {

    @PrimaryKeyClass
    @Data
    public static class Key implements Serializable {

        @PrimaryKeyColumn(name = "csadid", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
        private String csadid;

        @PrimaryKeyColumn(name = "clientid", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
        @Indexed
        private String clientid;

        @PrimaryKeyColumn(name = "servicetime", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
        private Long servicetime;
    }

    @PrimaryKey
    private Servicelog.Key key;

    @Column("client_info")
    private String clientInfo;

    private Integer endtype;

    @Column("service_endtime")
    private Long serviceEndtime;

    @Column("service_id")
    @Indexed
    private String serviceId;

    @Indexed
    private String servicedate;

    @Column("session_id")
    @Indexed
    private String sessionId;
}
