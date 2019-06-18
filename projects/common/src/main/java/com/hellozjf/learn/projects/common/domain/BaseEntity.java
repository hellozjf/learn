package com.hellozjf.learn.projects.common.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @author hellozjf
 *
 * 详见
 * https://blog.csdn.net/tianyaleixiaowu/article/details/77931903
 * https://www.jianshu.com/p/14cb69646195
 *
 * UUID生成器：
 * https://blog.csdn.net/vary_/article/details/8557043
 *
 * 时间用Long是因为这样在不同的服务器上才能生成相同的数据，用Date或Instant在中国的服务器和在美国的服务器会产生不同的时间
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    /**
     * ID
     */
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name="idGenerator", strategy="uuid")
    @Column(length = 32)
    private String id;

    /**
     * 创建时间
     */
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date gmtCreate;

    /**
     * 更新时间
     */
    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date gmtModified;
}