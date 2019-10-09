package com.hellozjf.learn.springboot2.bo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Jingfeng Zhou
 */
@Data
@Entity
@Table(name = "hello_bo")
public class HelloBO {

    @Id
    private Long id;

    private String name;

    private Integer age;

    private Double money;

    private Date birthday;
}
