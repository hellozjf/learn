package com.hellozjf.learn.projects.order12306.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author Jingfeng Zhou
 */
public class HttpClientUtils {

    /**
     * 获取charles代理的httpClient
     * @param cookieStore
     * @return
     */
    public static CloseableHttpClient getProxyHttpClient(CookieStore cookieStore) {
        return HttpClients.custom()
                .setProxy(new HttpHost("localhost", 8888))
                .setDefaultCookieStore(cookieStore)
                .build();
    }

    /**
     * 获取普通httpClient
     * @param cookieStore
     * @return
     */
    public static CloseableHttpClient getHttpClient(CookieStore cookieStore) {
        return HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
    }

    /**
     * 将httpClient的返回结果转化为字符串
     * @param response
     * @return
     * @throws IOException
     */
    public static String getResponse(CloseableHttpResponse response) throws IOException {
        try {
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                String responseString = EntityUtils.toString(httpEntity);
                return responseString;
            }
        } finally {
            response.close();
        }
        return null;
    }

    /**
     * 将key为name，value为value的cookie存储到cookieStore中
     * @param cookieStore
     * @param name
     * @param value
     */
    public static void addCookie(CookieStore cookieStore, String name, String value) {
        BasicClientCookie basicClientCookie = new BasicClientCookie(name, value);
        basicClientCookie.setDomain("kyfw.12306.cn");
        basicClientCookie.setPath("/");
        cookieStore.addCookie(basicClientCookie);
    }
}
