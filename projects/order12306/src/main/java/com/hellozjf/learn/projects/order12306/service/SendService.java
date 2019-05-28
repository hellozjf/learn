package com.hellozjf.learn.projects.order12306.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hellozjf.learn.projects.order12306.constant.UriEnum;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.Map;

/**
 * @author hellozjf
 */
public interface SendService {

    <T> T send(HttpClient httpClient, UriEnum uriEnum, Map<Object, Object> params, String regex, TypeReference typeReference) throws IOException, InterruptedException;

    <T> T send(HttpClient httpClient, UriEnum uriEnum, String params, String regex, TypeReference typeReference) throws IOException, InterruptedException;

    <T> T send(HttpClient httpClient, UriEnum uriEnum, Map<Object, Object> params, TypeReference typeReference) throws IOException, InterruptedException;

    <T> T send(HttpClient httpClient, UriEnum uriEnum, String params, TypeReference typeReference) throws IOException, InterruptedException;

    String send(HttpClient httpClient, UriEnum uriEnum, Map<Object, Object> params) throws IOException, InterruptedException;

    String send(HttpClient httpClient, UriEnum uriEnum, String params) throws IOException, InterruptedException;

    String send(HttpClient httpClient, URI uri, HttpMethod httpMethod, String referer, String params) throws IOException, InterruptedException;
}
