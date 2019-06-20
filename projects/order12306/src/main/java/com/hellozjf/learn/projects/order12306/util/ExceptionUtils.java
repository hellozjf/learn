package com.hellozjf.learn.projects.order12306.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 参考：https://blog.csdn.net/zhxdick/article/details/78955576
 *
 * @author hellozjf
 */
public class ExceptionUtils {

    public static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.close();
        try {
            sw.close();
        } catch (IOException e1) {
            //ignore
        }
        return sw.toString();
    }
}
