package com.hellozjf.learn.projects.order12306.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellozjf.learn.projects.order12306.constant.UriEnum;
import com.hellozjf.learn.projects.order12306.service.SendService;
import com.hellozjf.learn.projects.order12306.service.UriService;
import com.hellozjf.learn.projects.order12306.util.MoreBodyPublisherUtils;
import com.hellozjf.learn.projects.order12306.util.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

/**
 * @author hellozjf
 */
@Service
@Slf4j
public class SendServiceImpl implements SendService {

    @Autowired
    private UriService uriService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public <T> T send(HttpClient httpClient, UriEnum uriEnum, Map<Object, Object> params, String regex, TypeReference typeReference) {
        String strParams = MoreBodyPublisherUtils.getStringFromFormData(params);
        return send(httpClient, uriEnum, strParams, regex, typeReference);
    }

    @Override
    public <T> T send(HttpClient httpClient, UriEnum uriEnum, String params, String regex, TypeReference typeReference) {
        String result = send(httpClient, uriEnum, params);
        try {
            result = RegexUtils.getMatch(result, regex);
            return objectMapper.readValue(result, typeReference);
        } catch (IOException e) {
            log.error("e = {}", e);
            return null;
        }
    }

    @Override
    public <T> T send(HttpClient httpClient, UriEnum uriEnum, Map<Object, Object> params, TypeReference typeReference) {
        String strParams = MoreBodyPublisherUtils.getStringFromFormData(params);
        return send(httpClient, uriEnum, strParams, typeReference);
    }

    @Override
    public <T> T send(HttpClient httpClient, UriEnum uriEnum, String params, TypeReference typeReference) {
        String result = send(httpClient, uriEnum, params);
        try {
            return objectMapper.readValue(result, typeReference);
        } catch (IOException e) {
            log.error("e = {}", e);
            return null;
        }
    }

    @Override
    public String send(HttpClient httpClient, UriEnum uriEnum, Map<Object, Object> params) {
        String strParams = MoreBodyPublisherUtils.getStringFromFormData(params);
        return send(httpClient, uriEnum, strParams);
    }

    @Override
    public String send(HttpClient httpClient, UriEnum uriEnum, String params) {
        URI uri = uriService.getUri(uriEnum, params);
        HttpMethod httpMethod = uriEnum.getHttpMethod();
        return send(httpClient, uri, httpMethod, params);
    }

    @Override
    public String send(HttpClient httpClient, URI uri, HttpMethod httpMethod, String params) {
        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder(uri);
        if (httpMethod.equals(HttpMethod.POST)) {
            httpRequestBuilder
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(params));
        }
        HttpRequest httpRequest = httpRequestBuilder.build();
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            return body;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
    }
}
