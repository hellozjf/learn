package com.hellozjf.learn.projects.order12306.util;

import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

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
}
