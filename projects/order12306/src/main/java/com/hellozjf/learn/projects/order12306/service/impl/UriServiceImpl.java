package com.hellozjf.learn.projects.order12306.service.impl;

import com.hellozjf.learn.projects.order12306.constant.UriEnum;
import com.hellozjf.learn.projects.order12306.service.UriService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author hellozjf
 */
@Service
@Slf4j
public class UriServiceImpl implements UriService {

    @Override
    public URI getUri(String scheme, String host, int port, String path, String query) {
        try {
            return new URI(scheme,
                    null,
                    host,
                    port,
                    path,
                    query,
                    null);
        } catch (URISyntaxException e) {
            log.error("e = {}", e);
            System.exit(-1);
            return null;
        }
    }

    @Override
    public URI getUri(UriEnum uriEnum, String params) {
        String scheme = uriEnum.getScheme();
        String host = uriEnum.getHost();
        int port = uriEnum.getPort();
        String path = uriEnum.getPath();
        String query = null;
        if (uriEnum.getHttpMethod().equals(HttpMethod.GET)) {
            query = params;
        }
        return getUri(scheme, host, port, path, query);
    }
}
