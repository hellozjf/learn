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
    public <T> T send(HttpClient httpClient, UriEnum uriEnum, Map<Object, Object> params, String regex, TypeReference typeReference) throws IOException, InterruptedException {
        String strParams = MoreBodyPublisherUtils.getStringFromFormData(params);
        return send(httpClient, uriEnum, strParams, regex, typeReference);
    }

    @Override
    public <T> T send(HttpClient httpClient, UriEnum uriEnum, String params, String regex, TypeReference typeReference) throws IOException, InterruptedException {
        String result = send(httpClient, uriEnum, params);
        result = RegexUtils.getMatch(result, regex);
        return objectMapper.readValue(result, typeReference);
    }

    @Override
    public <T> T send(HttpClient httpClient, UriEnum uriEnum, Map<Object, Object> params, TypeReference typeReference) throws IOException, InterruptedException {
        String strParams = MoreBodyPublisherUtils.getStringFromFormData(params);
        return send(httpClient, uriEnum, strParams, typeReference);
    }

    @Override
    public <T> T send(HttpClient httpClient, UriEnum uriEnum, String params, TypeReference typeReference) throws IOException, InterruptedException {
        String result = send(httpClient, uriEnum, params);
        return objectMapper.readValue(result, typeReference);
    }

    @Override
    public String send(HttpClient httpClient, UriEnum uriEnum, Map<Object, Object> params) throws IOException, InterruptedException {
        String strParams = MoreBodyPublisherUtils.getStringFromFormData(params);
        return send(httpClient, uriEnum, strParams);
    }

    @Override
    public String send(HttpClient httpClient, UriEnum uriEnum, String params) throws IOException, InterruptedException {
        URI uri = uriService.getUri(uriEnum, params);
        HttpMethod httpMethod = uriEnum.getHttpMethod();
        String referer = uriEnum.getReferer();
        return send(httpClient, uri, httpMethod, referer, params);
    }

    @Override
    public String send(HttpClient httpClient, URI uri, HttpMethod httpMethod, String referer, String params) throws IOException, InterruptedException {
        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder(uri);
        httpRequestBuilder.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
        httpRequestBuilder.header("Referer", referer);
        if (httpMethod.equals(HttpMethod.POST)) {
            httpRequestBuilder
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString(params));
        }
        HttpRequest httpRequest = httpRequestBuilder.build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        return body;
    }
}
