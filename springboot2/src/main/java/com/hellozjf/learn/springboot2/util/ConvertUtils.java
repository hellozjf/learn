package com.hellozjf.learn.springboot2.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jingfeng Zhou
 */
@Slf4j
public class ConvertUtils {

    /**
     * 单个类型转换
     * @param u         转换前的对象
     * @param clazz     待转换的类别
     * @return
     */
    public static <T, U> T convert(U u, Class<T> clazz) {
        if (u == null) {
            return null;
        }
        T t = null;
        try {
            t = clazz.newInstance();
        } catch (Exception e) {
            log.error("初始化{}对象失败。", clazz, e);
        }
        BeanUtils.copyProperties(u, t);
        return t;
    }

    /**
     * 单个类型列表转换
     * @param uList     转换前的对象列表
     * @param clazz     待转换的类别
     * @return
     */
    public static <T, U> List<T> convert(List<U> uList, Class<T> clazz) {
        if (uList == null) {
            return null;
        }
        List<T> tList = new ArrayList<>();
        for (U u : uList) {
            tList.add(convert(u, clazz));
        }
        return tList;
    }
}
