package com.hellozjf.learn.projects.order12306.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hellozjf.learn.projects.order12306.constant.UriEnum;
import org.springframework.http.HttpMethod;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.Map;

/**
 * @author hellozjf
 */
public interface SendService {

    <T> T send(HttpClient httpClient, UriEnum uriEnum, Map<Object, Object> params, String regex, TypeReference typeReference);

    <T> T send(HttpClient httpClient, UriEnum uriEnum, String params, String regex, TypeReference typeReference);

    <T> T send(HttpClient httpClient, UriEnum uriEnum, Map<Object, Object> params, TypeReference typeReference);

    <T> T send(HttpClient httpClient, UriEnum uriEnum, String params, TypeReference typeReference);

    String send(HttpClient httpClient, UriEnum uriEnum, Map<Object, Object> params);

    String send(HttpClient httpClient, UriEnum uriEnum, String params);

    String send(HttpClient httpClient, URI uri, HttpMethod httpMethod, String params);
}
