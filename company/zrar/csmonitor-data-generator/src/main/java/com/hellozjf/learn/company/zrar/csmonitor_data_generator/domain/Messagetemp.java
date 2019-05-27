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
public class Messagetemp implements Serializable {

    @PrimaryKeyClass
    @Data
    public static class Key implements Serializable {

        @PrimaryKeyColumn(name = "uid", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
        private String uid;

        @PrimaryKeyColumn(name = "time", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
        private Long time;

        @PrimaryKeyColumn(name = "message_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
        private String messageId;
    }

    @PrimaryKey
    private Messagetemp.Key key;

    @Column("channel_id")
    private String channelId;

    private String content;

    @Indexed
    private String groupid;

    private Integer msgtype;

    @Column("session_id")
    @Indexed
    private String sessionId;
}
