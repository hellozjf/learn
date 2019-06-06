package com.hellozjf.learn.projects.order12306;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellozjf.learn.projects.order12306.dto.ResultDTO;
import com.hellozjf.learn.projects.order12306.util.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
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
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class Order12306ApplicationTests {

    private CookieStore cookieStore;
    private CloseableHttpClient httpclient;
    private ObjectMapper objectMapper;
    private Random random;

    @Before
    public void before() {
        cookieStore = cookieStore();
        httpclient = getProxyHttpClient(cookieStore);
        objectMapper = new ObjectMapper();
        random = new Random();
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

        addCookie(cookieStore, "RAIL_EXPIRATION", resultDTO.getExp());
        addCookie(cookieStore, "RAIL_DEVICEID", resultDTO.getDfp());
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
                .setParameter(String.valueOf(System.currentTimeMillis()), null)
                .setParameter("callback", "jQuery19104205457370736725_1559731489947")
                .setParameter("_", String.valueOf(System.currentTimeMillis()))
                .build();
        HttpGet httpget = new HttpGet(uri);

        CloseableHttpResponse response = httpclient.execute(httpget);
        String responseString = getResponse(response);
        log.debug("responseString = {}", responseString);
        String image = RegexUtils.getMatch(responseString, ".*\\((.*)\\)");
        ResultDTO resultDTO = objectMapper.readValue(image, new TypeReference<ResultDTO>(){});
        return resultDTO.getImage();
    }

    private String getCheck(String image) throws IOException, URISyntaxException {
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
        ResultDTO resultDTO = objectMapper.readValue(responseString, new TypeReference<ResultDTO>() {});
        return resultDTO.getData().asText();
    }

    private void passportCaptchaCaptchaCheck(String check) throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/passport/captcha/captcha-check")
                .setParameter("callback", "jQuery19104205457370736725_1559731489947")
                .setParameter("answer", check)
                .setParameter("rand", "sjrand")
                .setParameter("login_site", "E")
                .setParameter("_", String.valueOf(System.currentTimeMillis()))
                .build();
        HttpGet httpget = new HttpGet(uri);
        CloseableHttpResponse response = httpclient.execute(httpget);
        String responseString = getResponse(response);
        log.debug("responseString = {}", responseString);
    }

    private void passportWebLogin(String username, String password, String answer) throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/passport/web/login")
                .build();
        HttpPost httppost = new HttpPost(uri);

        List<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("username", username));
        formparams.add(new BasicNameValuePair("password", password));
        formparams.add(new BasicNameValuePair("appid", "otn"));
        formparams.add(new BasicNameValuePair("answer", answer));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httppost.setEntity(entity);

        CloseableHttpResponse response = httpclient.execute(httppost);
        String responseString = getResponse(response);
        log.debug("responseString = {}", responseString);
    }

    private void otnLoginUserLogin() throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/login/userLogin")
                .build();
        HttpGet httpget = new HttpGet(uri);

        CloseableHttpResponse response = httpclient.execute(httpget);
        getResponse(response);
    }

    private String passportWebAuthUamtk() throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/passport/web/auth/uamtk")
                .build();
        HttpPost httppost = new HttpPost(uri);

        List<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("appid", "otn"));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httppost.setEntity(entity);

        CloseableHttpResponse response = httpclient.execute(httppost);
        String responseString = getResponse(response);
        log.debug("responseString = {}", responseString);
        ResultDTO resultDTO = objectMapper.readValue(responseString, new TypeReference<ResultDTO>(){});
        return resultDTO.getNewapptk();
    }

    private void otnUamauthclient(String newapptk) throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/uamauthclient")
                .build();
        HttpPost httppost = new HttpPost(uri);

        List<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("tk", newapptk));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httppost.setEntity(entity);

        CloseableHttpResponse response = httpclient.execute(httppost);
        String responseString = getResponse(response);
        log.debug("responseString = {}", responseString);
    }

    private void otnIndexInitMy12306Api() throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/index/initMy12306Api")
                .build();
        HttpPost httppost = new HttpPost(uri);

        List<NameValuePair> formparams = new ArrayList<>();
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httppost.setEntity(entity);

        CloseableHttpResponse response = httpclient.execute(httppost);
        String responseString = getResponse(response);
        log.debug("responseString = {}", responseString);
    }

    private void otnLeftTicketInit() throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/leftTicket/init")
                .setParameter("linktypeid", "dc")
                .build();
        HttpGet httpget = new HttpGet(uri);

        CloseableHttpResponse response = httpclient.execute(httpget);
        String responseString = getResponse(response);
    }

    private void otnLeftTicketQuery(String trainDate, String fromStation, String toStation) throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/leftTicket/query")
                .setParameter("leftTicketDTO.train_date", trainDate)
                .setParameter("leftTicketDTO.from_station", fromStation)
                .setParameter("leftTicketDTO.to_station", toStation)
                .setParameter("purpose_codes", "ADULT")
                .build();
        HttpGet httpget = new HttpGet(uri);

        CloseableHttpResponse response = httpclient.execute(httpget);
        String responseString = getResponse(response);
        log.debug("responseString = {}", responseString);
        ResultDTO resultDTO = objectMapper.readValue(responseString, new TypeReference<ResultDTO>(){});
        log.debug("result = {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultDTO.getData().get("result")));
    }

    private void otnPasscodeNewGetPassCodeNew() throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/passcodeNew/getPassCodeNew")
                .setParameter("module", "passenger")
                .setParameter("rand", "randp")
                .setParameter(String.valueOf(random.nextDouble()), null)
                .build();
        HttpGet httpget = new HttpGet(uri);

        CloseableHttpResponse response = httpclient.execute(httpget);
        String responseString = getResponse(response);
//        log.debug("responseString = {}", responseString);

//        addCookie(cookieStore, "_jc_save_fromStation", "杭州,HZH");
//        addCookie(cookieStore, "_jc_save_fromStation", "%u676D%u5DDE%2CHZH");
//        addCookie(cookieStore, "_jc_save_toStation", "宁波,NGH");
//        addCookie(cookieStore, "_jc_save_toStation", "%u5B81%u6CE2%2CNGH");
//        addCookie(cookieStore, "_jc_save_fromDate", "2019-06-12");
//        addCookie(cookieStore, "_jc_save_toDate", "2019-06-06");
//        addCookie(cookieStore, "_jc_save_wfdc_flag", "dc");
    }

    private void otnLoginCheckUser() throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/login/checkUser")
                .build();
        HttpPost httppost = new HttpPost(uri);

        List<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("_json_att", ""));
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

    private void login() throws IOException, URISyntaxException {
        otnHttpZFGetJS();
        otnHttpZFLogdevice();
        passportWebAuthUamtkStatic();
        otnLoginConf();
        otnIndex12306GetLoginBanner();
        passportWebAuthUamtkStatic();
        String image = passportCaptchaCaptchaImage64();
        String check = getCheck(image);
        passportCaptchaCaptchaCheck(check);
        passportWebLogin("15158037019", "Zjf@1234", check);
        otnLoginUserLogin();
        String newapptk = passportWebAuthUamtk();
        otnUamauthclient(newapptk);
        otnLoginUserLogin();
        otnLoginConf();
        otnIndexInitMy12306Api();
    }

    @Test
    public void order() throws IOException, URISyntaxException {
        login();
        otnLeftTicketInit();
        otnPasscodeNewGetPassCodeNew();
        otnLeftTicketQuery("2019-06-12", "HZH", "NGH");
        otnLoginCheckUser();
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

    private void addCookie(CookieStore cookieStore, String name, String value) {
        BasicClientCookie basicClientCookie = new BasicClientCookie(name, value);
        basicClientCookie.setDomain("kyfw.12306.cn");
        basicClientCookie.setPath("/");
        cookieStore.addCookie(basicClientCookie);
    }
}
