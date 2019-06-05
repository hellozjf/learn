package com.hellozjf.learn.projects.order12306;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellozjf.learn.projects.order12306.dto.ResultDTO;
import com.hellozjf.learn.projects.order12306.util.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class Order12306ApplicationTests {

    private CookieStore cookieStore;
    private CloseableHttpClient httpclient;
    private ObjectMapper objectMapper;

    @Before
    public void before() {
        cookieStore = cookieStore();
        httpclient = getProxyHttpClient(cookieStore);
        objectMapper = new ObjectMapper();
    }

    private void otnHttpZFGetJS() throws IOException, URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/HttpZF/GetJS")
                .build();
        HttpGet httpget = new HttpGet(uri);
        CloseableHttpResponse response = httpclient.execute(httpget);
        getResponse(response);
    }

    private void otnHttpZFLogdevice() throws IOException, URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/HttpZF/logdevice")
                .setParameter("algID", "B1yZP5nZFD")
                .setParameter("hashCode", "FGzlY5ZPKbBlR5vAJscQioAhxZM_aEjZmEJBooIXYHI")
                .setParameter("FMQw", "1")
                .setParameter("q4f3", "zh-CN")
                .setParameter("VPIf", "1")
                .setParameter("custID", "133")
                .setParameter("VEek", "unknown")
                .setParameter("dzuS", "0")
                .setParameter("yD16", "0")
                .setParameter("EOQP", "8f58b1186770646318a429cb33977d8c")
                .setParameter("lEnu", "3232236319")
                .setParameter("jp76", "52d67b2a5aa5e031084733d5006cc664")
                .setParameter("hAqN", "Win32")
                .setParameter("platform", "WEB")
                .setParameter("ks0Q", "d22ca0b81584fbea62237b14bd04c866")
                .setParameter("TeRS", "860x1600")
                .setParameter("tOHY", "24xx900x1600")
                .setParameter("Fvje", "i1l1o1s1")
                .setParameter("q5aJ", "-8")
                .setParameter("wNLf", "99115dfb07133750ba677d055874de87")
                .setParameter("0aew", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36")
                .setParameter("E3gR", "d57736fc5698a73751a8dedad5cb8c3c")
                .setParameter("timestamp", String.valueOf(System.currentTimeMillis()))
                .build();
        HttpGet httpget = new HttpGet(uri);
        CloseableHttpResponse response = httpclient.execute(httpget);
        String responseString = getResponse(response);
        responseString = RegexUtils.getMatch(responseString, ".*\\('(.*)'\\)");
        ResultDTO resultDTO = objectMapper.readValue(responseString, new TypeReference<ResultDTO>(){});
        log.debug("resultDTO = {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultDTO));

        log.debug("cookies = {}", cookieStore.getCookies());

        BasicClientCookie railExpiration = new BasicClientCookie("RAIL_EXPIRATION", resultDTO.getExp());
        railExpiration.setVersion(0);
        railExpiration.setDomain("kyfw.12306.cn");
        railExpiration.setPath("/");
        BasicClientCookie railDeviceid = new BasicClientCookie("RAIL_DEVICEID", resultDTO.getDfp());
        railDeviceid.setVersion(0);
        railDeviceid.setDomain("kyfw.12306.cn");
        railDeviceid.setPath("/");
        cookieStore.addCookie(railExpiration);
        cookieStore.addCookie(railDeviceid);
    }

    private void passportWebAuthUamtkStatic() throws IOException, URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/passport/web/auth/uamtk-static")
                .build();
        HttpPost httppost = new HttpPost(uri);

        List<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("appid", "otn"));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httppost.setEntity(entity);

        log.debug("cookies = {}", cookieStore.getCookies());
        CloseableHttpResponse response = httpclient.execute(httppost);
        getResponse(response);
    }

    private void otnLoginConf() throws IOException, URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/login/conf")
                .build();
        HttpPost httppost = new HttpPost(uri);

        List<NameValuePair> formparams = new ArrayList<>();
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httppost.setEntity(entity);

        CloseableHttpResponse response = httpclient.execute(httppost);
        String responseString = getResponse(response);
        log.debug("responseString = {}", responseString);
    }

    private void otnIndex12306GetLoginBanner() throws IOException, URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/index12306/getLoginBanner")
                .build();
        HttpGet httpget = new HttpGet(uri);

        CloseableHttpResponse response = httpclient.execute(httpget);
        String responseString = getResponse(response);
        log.debug("responseString = {}", responseString);
    }

    private String passportCaptchaCaptchaImage64() throws IOException, URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/passport/captcha/captcha-image64")
                .setParameter("login_site", "E")
                .setParameter("module", "login")
                .setParameter("rand", "sjrand")
                .setParameter("1559731492080", null)
                .setParameter("callback", "jQuery19104205457370736725_1559731489947")
                .setParameter("_", "1559731489948")
                .build();
        HttpGet httpget = new HttpGet(uri);

        CloseableHttpResponse response = httpclient.execute(httpget);
        String responseString = getResponse(response);
        log.debug("responseString = {}", responseString);
        String image = RegexUtils.getMatch(responseString, ".*\\((.*)\\)");
        ResultDTO resultDTO = objectMapper.readValue(image, new TypeReference<ResultDTO>(){});
        return resultDTO.getImage();
    }

    private void getCheck(String image) throws IOException, URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("aliyun.hellozjf.com")
                .setPort(12306)
                .setPath("/result/base64")
                .build();
        HttpPost httppost = new HttpPost(uri);

        List<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("base64String", image));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httppost.setEntity(entity);

        CloseableHttpResponse response = httpclient.execute(httppost);
        String responseString = getResponse(response);
        log.debug("responseString = {}", responseString);
    }

    private String getResponse(CloseableHttpResponse response) throws IOException {
        try {
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                String responseString = EntityUtils.toString(httpEntity);
//                log.debug("{}", responseString);
                return responseString;
            }
        } finally {
            response.close();
        }
        return null;
    }

    @Test
    public void login() throws IOException, URISyntaxException {
        otnHttpZFGetJS();
        otnHttpZFLogdevice();
        passportWebAuthUamtkStatic();
        otnLoginConf();
        otnIndex12306GetLoginBanner();
        passportWebAuthUamtkStatic();
        String image = passportCaptchaCaptchaImage64();
        getCheck(image);
    }

    private CloseableHttpClient getProxyHttpClient(CookieStore cookieStore) {
        return HttpClients.custom()
                .setProxy(new HttpHost("localhost", 8888))
                .setDefaultCookieStore(cookieStore)
                .build();
    }

    private CloseableHttpClient getHttpClient(CookieStore cookieStore) {
        return HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
    }

    private CookieStore cookieStore() {
        return new BasicCookieStore();
    }
}
