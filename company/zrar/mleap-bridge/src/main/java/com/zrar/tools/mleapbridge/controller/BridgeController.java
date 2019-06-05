package com.zrar.tools.mleapbridge.controller;

import com.zrar.tools.mleapbridge.util.UrlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

/**
 * @author Jingfeng Zhou
 *
 * 参考：https://www.dozer.cc/2014/03/use-spring-mvc-and-resttemplate-impl-corsproxy.html
 */
@Slf4j
@RestController
public class BridgeController {

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("{mleap}/{uri}")
    public ResponseEntity<byte[]> redirect(HttpServletRequest request,
                                           @RequestHeader MultiValueMap<String, String> headers,
                                           @RequestBody(required = false) byte[] body,
                                           @PathVariable("mleap") String mleap,
                                           @PathVariable("uri") String uri) throws UnsupportedEncodingException {

        String url = UrlUtils.getUrl(request.getScheme(), mleap, 65327, uri);
        String queryString = request.getQueryString();
        if (!StringUtils.isEmpty(queryString)) {
            url = url + "?" + queryString;
        }

        ResponseEntity<byte[]> result = null;
        try {
            result = restTemplate.exchange(url, HttpMethod.valueOf(request.getMethod()), new HttpEntity<>(body, headers), byte[].class);
        } catch (HttpClientErrorException exp) {
            return new ResponseEntity<>(exp.getResponseBodyAsByteArray(), exp.getResponseHeaders(), exp.getStatusCode());
        } catch (HttpServerErrorException exp) {
            return new ResponseEntity<>(exp.getResponseBodyAsByteArray(), exp.getResponseHeaders(), exp.getStatusCode());
        } catch (Exception exp) {
            return new ResponseEntity<>(exp.getMessage().getBytes("utf-8"), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(result.getBody(), result.getHeaders(), result.getStatusCode());
    }
}
