package com.hellozjf.learn.company.zrar.testbadlanguagemodel;

import java.lang.annotation.*;

/**
 * 参考：https://www.jianshu.com/p/3a89e19a1bc3
 * @author Jingfeng Zhou
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelColumn {

    String value() default "";

    int col() default 0;
}
