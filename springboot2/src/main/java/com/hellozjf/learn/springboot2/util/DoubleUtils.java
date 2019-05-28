package com.hellozjf.learn.springboot2.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * @author Jingfeng Zhou
 */
public class DoubleUtils {

    /**
     * 将0.1234转化为12.34%
     * @param d
     * @return
     */
    public static String parseDoubleToPercentString(Double d) {
        DecimalFormat df = new DecimalFormat("0.00");
        String str = df.format(d * 100);
        return str + "%";
    }
}
