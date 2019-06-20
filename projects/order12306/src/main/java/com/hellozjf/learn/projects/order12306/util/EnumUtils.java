package com.hellozjf.learn.projects.order12306.util;

import com.hellozjf.learn.projects.order12306.constant.CodeEnum;

/**
 * Created by 廖师兄
 * 2017-07-16 18:36
 */
public class EnumUtils {

    public static <T extends CodeEnum> T getByCode(Integer code, Class<T> enumClass) {
        for (T each: enumClass.getEnumConstants()) {
            if (code.equals(each.getCode())) {
                return each;
            }
        }
        return null;
    }
}
