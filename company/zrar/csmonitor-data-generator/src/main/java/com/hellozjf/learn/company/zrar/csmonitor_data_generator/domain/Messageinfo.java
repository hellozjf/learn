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
public class Messageinfo implements Serializable {

    @PrimaryKeyClass
    @Data
    public static class Key implements Serializable {

        @PrimaryKeyColumn(name = "specify_pk", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
        private String specifyPk;

        @PrimaryKeyColumn(name = "time_save", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
        private Long timeSave;

        @PrimaryKeyColumn(name = "message_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
        private String messageId;
    }

    @PrimaryKey
    private Messageinfo.Key key;

    @Column("channel_from")
    private String channelFrom;

    @Column("channel_to")
    private String channelTo;

    private String content;

    private String fromuser;

    private Integer msgtype;

    @Column("num_send")
    private Integer numSend;

    @Column("session_id")
    @Indexed
    private String sessionId;

    @Indexed
    private Integer state;

    private Integer stype;

    @Column("time_receipt")
    private Long timeReceipt;

    @Column("time_send")
    private Long timeSend;

    @Indexed
    private String touser;

    private Integer type;
}
