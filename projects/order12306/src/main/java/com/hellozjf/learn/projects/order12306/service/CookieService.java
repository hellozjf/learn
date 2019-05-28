package com.hellozjf.learn.projects.order12306.service;

import com.hellozjf.learn.projects.order12306.constant.UriEnum;

import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.List;

/**
 * @author hellozjf
 */
public interface CookieService {

    String RAIL_EXPIRATION = "RAIL_EXPIRATION";
    String RAIL_DEVICEID = "RAIL_DEVICEID";

    String IMAGE = "image";
    String ANSWER = "answer";
    String TK = "tk";

    List<HttpCookie> getAll(HttpClient httpClient, UriEnum uriEnum);
    List<HttpCookie> getAll(HttpClient httpClient, URI uri);
    void add(HttpClient httpClient, URI uri, String name, String value);

    void add(HttpClient httpClient, URI uri, HttpCookie httpCookie);

    String get(HttpClient httpClient, URI uri, String name);
}
