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
public class Customservice implements Serializable {

    @PrimaryKeyClass
    @Data
    public static class Key implements Serializable {

        @PrimaryKeyColumn(name = "client_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
        private String clientId;

        @PrimaryKeyColumn(name = "csad_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
        @Indexed
        private String csadId;
    }

    @PrimaryKey
    private Customservice.Key key;

    @Column("apply_type")
    private Integer applyType;

    @Column("cacsi_inform_state")
    private Integer cacsiInformState;

    @Column("client_info")
    private String clientInfo;

    @Column("client_type")
    private String clientType;

    private String nid;

    @Column("overtime_client_inform_state")
    private Integer overtimeClientInformState;

    @Column("service_time")
    private Long serviceTime;

    @Column("session_client_inform_state")
    @Indexed
    private Integer sessionClientInformState;

    @Column("session_csad_inform_state")
    @Indexed
    private Integer sessionCsadInformState;

    @Column("session_id")
    private String sessionId;

    @Column("time_client_lastest")
    private Long timeClientLastest;

    @Column("time_csad_lastest")
    private Long timeCsadLastest;
}