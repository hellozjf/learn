package com.hellozjf.learn.springboot2.util;

import java.util.UUID;

/**
 * @author Jingfeng Zhou
 */
public class UUIDUtils {
    public static String genUUIDNoSplit() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
