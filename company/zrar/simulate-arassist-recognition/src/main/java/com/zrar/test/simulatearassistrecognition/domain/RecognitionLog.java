package com.zrar.test.simulatearassistrecognition.domain;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author Jingfeng Zhou
 */
@Entity
@Data
@Table(name = "recognition_log")
public class RecognitionLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "create_time")
    @CreationTimestamp
    private Timestamp createTime;

    @Column(name = "update_time")
    @UpdateTimestamp
    private Timestamp updateTime;

    /**
     * 类型
     * @see com.zrar.test.simulatearassistrecognition.constant.RecognitionLogType
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 会话编号
     */
    @Column(name = "call_id")
    private String callId;

    /**
     * 消息发送人
     */
    @Column(name = "msg_from")
    private String msgFrom;

    /**
     * 消息接收人
     */
    @Column(name = "msg_to")
    private String msgTo;

    /**
     * 消息内容
     */
    @Column(name = "content")
    private String content;
}
