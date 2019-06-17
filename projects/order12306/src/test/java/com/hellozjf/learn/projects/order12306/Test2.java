package com.hellozjf.learn.projects.order12306;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.hellozjf.learn.projects.order12306.dto.NormalPassengerDTO;
import com.hellozjf.learn.projects.order12306.dto.OrderTicketDTO;
import com.hellozjf.learn.projects.order12306.dto.ResultDTO;
import com.hellozjf.learn.projects.order12306.util.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.DateUtils;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class Test2 {

    private CookieStore cookieStore;
    private CloseableHttpClient httpclient;
    private ObjectMapper objectMapper;
    private Random random;
    private OrderTicketDTO orderTicketDTO;
    private Map<String, Integer> mapSeatConf;

    @Autowired
    private JavaMailSender mailSender;

    @Before
    public void before() {
        cookieStore = cookieStore();
//        httpclient = getHttpClient(cookieStore);
        httpclient = getProxyHttpClient(cookieStore);
        objectMapper = new ObjectMapper();
        random = new Random();

        // 初始化OrderTicketDTO
        orderTicketDTO = new OrderTicketDTO();
        orderTicketDTO.setTrainDate("2019-07-01");
        orderTicketDTO.setBackTrainDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd"));
        orderTicketDTO.setStationTrain("D655");
        orderTicketDTO.setFromStation("杭州");
        orderTicketDTO.setToStation("宁波");
        orderTicketDTO.setSeatType("二等座");
        orderTicketDTO.setTicketPeople("周靖峰");
        orderTicketDTO.setUsername("15158037019");
        orderTicketDTO.setPassword("Zjf@1234");
        orderTicketDTO.setEmail("908686171@qq.com");

        // 初始化mapSeatConf
        mapSeatConf = new HashMap<>();
        mapSeatConf.put("商务座", 32);
        mapSeatConf.put("一等座", 31);
        mapSeatConf.put("二等座", 30);
        mapSeatConf.put("特等座", 25);
        mapSeatConf.put("高级软卧", 21);
        mapSeatConf.put("软卧", 23);
        mapSeatConf.put("动卧", 33);
        mapSeatConf.put("硬卧", 28);
        mapSeatConf.put("软座", 24);
        mapSeatConf.put("硬座", 29);
        mapSeatConf.put("无座", 26);
        mapSeatConf.put("其他", 22);
    }

    private List<String> otnLeftTicketInit() throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/leftTicket/init")
                .build();
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) 12306-electron/1.0.1 Chrome/59.0.3071.115 Electron/1.8.4 Safari/537.36");

        CloseableHttpResponse response = httpclient.execute(httpGet);
        String responseString = getResponse(response);
        String leftTicketQueryUrl = RegexUtils.getMatch(responseString, "var CLeftTicketUrl = '(.*)';");
        String stationVersionUrl = RegexUtils.getMatch(responseString, "<script .* src=\"(/otn/resources/js/framework/station_name.js\\?station_version=.*)\" .*</script>");
        return Arrays.asList(leftTicketQueryUrl, stationVersionUrl);
    }

    private void otnLoginConf() throws IOException, URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/login/conf")
                .build();
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) 12306-electron/1.0.1 Chrome/59.0.3071.115 Electron/1.8.4 Safari/537.36");
        httpGet.setHeader(HttpHeaders.REFERER, "https://kyfw.12306.cn/otn/leftTicket/init");
        httpGet.setHeader("Origin", "https://kyfw.12306.cn");

        CloseableHttpResponse response = httpclient.execute(httpGet);
        String responseString = getResponse(response);
        log.debug("responseString = {}", responseString);
    }

    private void otnResourcesLoginHtml() throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/resources/login.html")
                .build();
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) 12306-electron/1.0.1 Chrome/59.0.3071.115 Electron/1.8.4 Safari/537.36");
        httpGet.setHeader(HttpHeaders.REFERER, "https://kyfw.12306.cn/otn/view/index.html");
        httpGet.setHeader("Origin", "https://kyfw.12306.cn");

        CloseableHttpResponse response = httpclient.execute(httpGet);
        String responseString = getResponse(response);
        log.debug("responseString = {}", responseString);
    }

    private String passportWebAuthUamtkStatic() throws IOException, URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/passport/web/auth/uamtk-static")
                .build();
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) 12306-electron/1.0.1 Chrome/59.0.3071.115 Electron/1.8.4 Safari/537.36");
        httpPost.setHeader(HttpHeaders.REFERER, "https://kyfw.12306.cn/otn/resources/login.html");
        httpPost.setHeader("Origin", "https://kyfw.12306.cn");

        List<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("appid", "otn"));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httpPost.setEntity(entity);

        log.debug("cookies = {}", cookieStore.getCookies());
        CloseableHttpResponse response = httpclient.execute(httpPost);
        String responseString = getResponse(response);
        ResultDTO resultDTO = objectMapper.readValue(responseString, new TypeReference<ResultDTO>(){});
        return resultDTO.getNewapptk();
    }

    private void otnHttpZFLogdevice() throws IOException, URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/HttpZF/logdevice")
                .setParameter("algID", "3yxNoRW8BM")
                .setParameter("hashCode", "8EFUGZrjK3cO8VdDugvPxyyiUqMNmKhl6pbW1ftnEVI")
                .setParameter("FMQw", "0")
                .setParameter("q4f3", "zh-CN")
                .setParameter("VPIf", "1")
                .setParameter("custID", "133")
                .setParameter("VEek", "unknown")
                .setParameter("dzuS", "0")
                .setParameter("yD16", "0")
                .setParameter("EOQP", "c227b88b01f5c513710d4b9f16a5ce52")
                .setParameter("lEnu", "2887005765")
                .setParameter("jp76", "52d67b2a5aa5e031084733d5006cc664")
                .setParameter("hAqN", "MacIntel")
                .setParameter("platform", "WEB")
                .setParameter("ks0Q", "d22ca0b81584fbea62237b14bd04c866")
                .setParameter("TeRS", "1013x1920")
                .setParameter("tOHY", "24xx1080x1920")
                .setParameter("Fvje", "i1l1o1s1")
                .setParameter("q5aJ", "-8")
                .setParameter("wNLf", "99115dfb07133750ba677d055874de87")
                .setParameter("0aew", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36")
                .setParameter("E3gR", "d4c1ccb1725a4a45cc350f16ac26f32b")
                .setParameter("timestamp", String.valueOf(System.currentTimeMillis()))
                .build();
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) 12306-electron/1.0.1 Chrome/59.0.3071.115 Electron/1.8.4 Safari/537.36");
        httpGet.setHeader(HttpHeaders.REFERER, "https://kyfw.12306.cn/otn/passport?redirect=/otn/");
        httpGet.setHeader("Origin", "https://kyfw.12306.cn");

        CloseableHttpResponse response = httpclient.execute(httpGet);
        String responseString = getResponse(response);
        responseString = RegexUtils.getMatch(responseString, ".*\\('(.*)'\\)");
        ResultDTO resultDTO = objectMapper.readValue(responseString, new TypeReference<ResultDTO>() {
        });
        log.debug("resultDTO = {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultDTO));

        log.debug("cookies = {}", cookieStore.getCookies());

        addCookie(cookieStore, "RAIL_EXPIRATION", resultDTO.getExp());
        addCookie(cookieStore, "RAIL_DEVICEID", resultDTO.getDfp());
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
                .setParameter("callback", "jQuery19108016482864806321_1554298927290")
                .setParameter("_", String.valueOf(System.currentTimeMillis()))
                .build();
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) 12306-electron/1.0.1 Chrome/59.0.3071.115 Electron/1.8.4 Safari/537.36");
        httpGet.setHeader(HttpHeaders.REFERER, "https://kyfw.12306.cn/otn/resources/login.html");
        httpGet.setHeader("Origin", "https://kyfw.12306.cn");

        CloseableHttpResponse response = httpclient.execute(httpGet);
        String responseString = getResponse(response);
        log.debug("responseString = {}", responseString);
        String image = RegexUtils.getMatch(responseString, ".*\\((.*)\\)");
        ResultDTO resultDTO = objectMapper.readValue(image, new TypeReference<ResultDTO>() {
        });
        return resultDTO.getImage();
    }

    private void passportCaptchaCaptchaCheck(String check) throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/passport/captcha/captcha-check")
                .setParameter("callback", "jQuery19108016482864806321_1554298927290")
                .setParameter("answer", check)
                .setParameter("rand", "sjrand")
                .setParameter("login_site", "E")
                .setParameter("_", String.valueOf(System.currentTimeMillis()))
                .build();
        HttpGet httpget = new HttpGet(uri);
        httpget.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) 12306-electron/1.0.1 Chrome/59.0.3071.115 Electron/1.8.4 Safari/537.36");
        httpget.setHeader(HttpHeaders.REFERER, "https://kyfw.12306.cn/otn/resources/login.html");
        httpget.setHeader("Origin", "https://kyfw.12306.cn");

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
        httppost.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) 12306-electron/1.0.1 Chrome/59.0.3071.115 Electron/1.8.4 Safari/537.36");
        httppost.setHeader(HttpHeaders.REFERER, "https://kyfw.12306.cn/otn/resources/login.html");
        httppost.setHeader("Origin", "https://kyfw.12306.cn");

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

    private void otnUamauthclient(String newapptk) throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/uamauthclient")
                .build();
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) 12306-electron/1.0.1 Chrome/59.0.3071.115 Electron/1.8.4 Safari/537.36");
        httpPost.setHeader(HttpHeaders.REFERER, "https://kyfw.12306.cn/otn/passport?redirect=/otn/login/userLogin");
        httpPost.setHeader("Origin", "https://kyfw.12306.cn");

        List<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("tk", newapptk));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httpPost.setEntity(entity);

        CloseableHttpResponse response = httpclient.execute(httpPost);
        String responseString = getResponse(response);
        log.debug("responseString = {}", responseString);
    }

    private NormalPassengerDTO otnConfirmPassengerGetPassengerDTOs(OrderTicketDTO orderTicketDTO) throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/confirmPassenger/getPassengerDTOs")
                .build();
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) 12306-electron/1.0.1 Chrome/59.0.3071.115 Electron/1.8.4 Safari/537.36");
        httpPost.setHeader(HttpHeaders.REFERER, "https://kyfw.12306.cn/otn/leftTicket/init");
        httpPost.setHeader("Origin", "https://kyfw.12306.cn");

        List<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("_json_att", ""));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httpPost.setEntity(entity);

        CloseableHttpResponse response = httpclient.execute(httpPost);
        String responseString = getResponse(response);
        log.debug("responseString = {}", responseString);
        ResultDTO resultDTO = objectMapper.readValue(responseString, new TypeReference<ResultDTO>() {
        });
        JsonNode normalPassengers = resultDTO.getData().get("normal_passengers");
        String normalPassengersString = objectMapper.writeValueAsString(normalPassengers);
        log.debug("normalPassengers = {}", normalPassengersString);
        List<NormalPassengerDTO> normalPassengerList = objectMapper.readValue(normalPassengersString, new TypeReference<List<NormalPassengerDTO>>() {
        });
        for (NormalPassengerDTO normalPassengerDTO : normalPassengerList) {
            if (normalPassengerDTO.getPassengerName().equalsIgnoreCase(orderTicketDTO.getTicketPeople())) {
                return normalPassengerDTO;
            }
        }
        return null;
    }

    private void otnLoginCheckUser() throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/login/checkUser")
                .build();
        HttpPost httppost = new HttpPost(uri);
        httppost.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) 12306-electron/1.0.1 Chrome/59.0.3071.115 Electron/1.8.4 Safari/537.36");
        httppost.setHeader(HttpHeaders.REFERER, "https://kyfw.12306.cn/otn/leftTicket/init");
        httppost.setHeader("Origin", "https://kyfw.12306.cn");

        List<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("_json_att", ""));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httppost.setEntity(entity);

        CloseableHttpResponse response = httpclient.execute(httppost);
        String responseString = getResponse(response);
        log.debug("responseString = {}", responseString);
    }

    private ArrayNode otnLeftTicketQuery(String leftTicketQueryUrl, String trainDate, String fromStation, String toStation) throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/" + leftTicketQueryUrl)
                .setParameter("leftTicketDTO.train_date", trainDate)
                .setParameter("leftTicketDTO.from_station", fromStation)
                .setParameter("leftTicketDTO.to_station", toStation)
                .setParameter("purpose_codes", "ADULT")
                .build();
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) 12306-electron/1.0.1 Chrome/59.0.3071.115 Electron/1.8.4 Safari/537.36");
        httpGet.setHeader(HttpHeaders.REFERER, "https://kyfw.12306.cn/otn/leftTicket/init");
        httpGet.setHeader("Origin", "https://kyfw.12306.cn");

        CloseableHttpResponse response = httpclient.execute(httpGet);
        String responseString = getResponse(response);
        log.debug("responseString = {}", responseString);
        ResultDTO resultDTO = objectMapper.readValue(responseString, new TypeReference<ResultDTO>() {
        });
        return (ArrayNode) resultDTO.getData().get("result");
    }

    private String queryTicket(String leftTicketQueryUrl, Map<String, String> nameCodeMap) throws IOException, URISyntaxException {
        String trainDate = orderTicketDTO.getTrainDate();
        String fromStationCode = nameCodeMap.get(orderTicketDTO.getFromStation());
        String toStationCode = nameCodeMap.get(orderTicketDTO.getToStation());
        while (true) {
            ArrayNode arrayNode = otnLeftTicketQuery(leftTicketQueryUrl, trainDate, fromStationCode, toStationCode);
            String secret = getWantedTicketSecret(orderTicketDTO, arrayNode);
            if (StringUtils.isEmpty(secret)) {
                log.debug("目前{}无票，等待5s后重试", orderTicketDTO.getStationTrain());
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    log.error("e = {}", e);
                }
            } else {
                log.debug("查询到{}有票，即将开始锁定票", orderTicketDTO.getStationTrain());
                return secret;
            }
        }
    }

    private void otnLeftTicketSubmitOrderRequest(String secret, OrderTicketDTO orderTicketDTO) throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/leftTicket/submitOrderRequest")
                .build();
        HttpPost httppost = new HttpPost(uri);
        httppost.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) 12306-electron/1.0.1 Chrome/59.0.3071.115 Electron/1.8.4 Safari/537.36");
        httppost.setHeader(HttpHeaders.REFERER, "https://kyfw.12306.cn/otn/leftTicket/init");
        httppost.setHeader("Origin", "https://kyfw.12306.cn");

        List<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("secretStr", secret));
        formparams.add(new BasicNameValuePair("train_date", orderTicketDTO.getTrainDate()));
        formparams.add(new BasicNameValuePair("back_train_date", orderTicketDTO.getBackTrainDate()));
        formparams.add(new BasicNameValuePair("tour_flag", "dc"));
        formparams.add(new BasicNameValuePair("purpose_codes", "ADULT"));
        formparams.add(new BasicNameValuePair("query_from_station_name", orderTicketDTO.getFromStation()));
        formparams.add(new BasicNameValuePair("query_to_station_name", orderTicketDTO.getToStation()));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httppost.setEntity(entity);

        CloseableHttpResponse response = httpclient.execute(httppost);
        String responseString = getResponse(response);
        log.debug("responseString = {}", responseString);
    }

    private List<String> otnConfirmPassengerInitDc() throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/confirmPassenger/initDc")
                .build();
        HttpPost httppost = new HttpPost(uri);
        httppost.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) 12306-electron/1.0.1 Chrome/59.0.3071.115 Electron/1.8.4 Safari/537.36");
        httppost.setHeader(HttpHeaders.REFERER, "https://kyfw.12306.cn/otn/leftTicket/init");
        httppost.setHeader("Origin", "https://kyfw.12306.cn");

        CloseableHttpResponse response = httpclient.execute(httppost);
        String responseString = getResponse(response);
        String globalRepeatSubmitToken = RegexUtils.getMatch(responseString, "var globalRepeatSubmitToken = '(.*)';");
        String ticketInfoForPassengerForm = RegexUtils.getMatch(responseString, "var ticketInfoForPassengerForm=(.*);");
        ticketInfoForPassengerForm = ticketInfoForPassengerForm.replaceAll("'", "\"");
        log.debug("globalRepeatSubmitToken = {}", globalRepeatSubmitToken);
        log.debug("ticketInfoForPassengerForm = {}", ticketInfoForPassengerForm);
        return Arrays.asList(globalRepeatSubmitToken, ticketInfoForPassengerForm);
    }

    private void otnHttpZFGetJS() throws IOException, URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/HttpZF/GetJS")
                .build();
        HttpGet httpget = new HttpGet(uri);
        httpget.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.80 Safari/537.36");
        httpget.setHeader(HttpHeaders.REFERER, "https://www.12306.cn/index/");

        CloseableHttpResponse response = httpclient.execute(httpget);
        getResponse(response);
    }

    private void otnIndex12306GetLoginBanner() throws IOException, URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/index12306/getLoginBanner")
                .build();
        HttpGet httpget = new HttpGet(uri);
        httpget.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.80 Safari/537.36");
        httpget.setHeader(HttpHeaders.REFERER, "https://kyfw.12306.cn/otn/resources/login.html");

        CloseableHttpResponse response = httpclient.execute(httpget);
        String responseString = getResponse(response);
        log.debug("responseString = {}", responseString);
    }

    private String getCheck(String image) throws IOException, URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("aliyun.hellozjf.com")
                .setPort(12306)
                .setPath("/result/base64")
                .build();
        HttpPost httppost = new HttpPost(uri);
        httppost.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.80 Safari/537.36");

        List<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("base64String", image));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httppost.setEntity(entity);

        CloseableHttpResponse response = httpclient.execute(httppost);
        String responseString = getResponse(response);
        log.debug("responseString = {}", responseString);
        ResultDTO resultDTO = objectMapper.readValue(responseString, new TypeReference<ResultDTO>() {
        });
        return resultDTO.getData().asText();
    }

    private void otnLoginUserLogin() throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/login/userLogin")
                .build();
        HttpGet httpget = new HttpGet(uri);
        httpget.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.80 Safari/537.36");
        httpget.setHeader(HttpHeaders.REFERER, "https://kyfw.12306.cn/otn/resources/login.html");

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
        httppost.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.80 Safari/537.36");
        httppost.setHeader(HttpHeaders.REFERER, "https://kyfw.12306.cn/otn/passport?redirect=/otn/login/userLogin");

        List<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("appid", "otn"));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httppost.setEntity(entity);

        CloseableHttpResponse response = httpclient.execute(httppost);
        String responseString = getResponse(response);
        log.debug("responseString = {}", responseString);
        ResultDTO resultDTO = objectMapper.readValue(responseString, new TypeReference<ResultDTO>() {
        });
        return resultDTO.getNewapptk();
    }

    private void otnIndexInitMy12306Api() throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/index/initMy12306Api")
                .build();
        HttpPost httppost = new HttpPost(uri);
        httppost.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.80 Safari/537.36");
        httppost.setHeader(HttpHeaders.REFERER, "https://kyfw.12306.cn/otn/view/index.html");

        List<NameValuePair> formparams = new ArrayList<>();
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httppost.setEntity(entity);

        CloseableHttpResponse response = httpclient.execute(httppost);
        String responseString = getResponse(response);
        log.debug("responseString = {}", responseString);
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
        httpget.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.80 Safari/537.36");
        httpget.setHeader(HttpHeaders.REFERER, "https://kyfw.12306.cn/otn/leftTicket/init?linktypeid=dc");

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

    private Map<String, String> otnResourcesJsFrameworkStationName(String stationVersionUrl) throws URISyntaxException, IOException {
        String[] parts = stationVersionUrl.split("\\?");
        String[] params = parts[1].split("=");
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath(parts[0])
                .setParameter(params[0], params[1])
                .build();
        HttpGet httpget = new HttpGet(uri);
        httpget.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.80 Safari/537.36");

        CloseableHttpResponse response = httpclient.execute(httpget);
        String responseString = getResponse(response);
        String stationNames = RegexUtils.getMatch(responseString, "var station_names ='(.*)';");
        String[] infos = stationNames.split("@");
        Map<String, String> nameCodeMap = new HashMap<>();
        for (int i = 1; i < infos.length; i++) {
            String info = infos[i];
            String[] rs = info.split("\\|");
            nameCodeMap.put(rs[1], rs[2]);
        }
        return nameCodeMap;
    }

    private String getPassengerTicketStr(JsonNode ticketInfoForPassengerFormNode, NormalPassengerDTO normalPassengerDTO) throws IOException {
        String seatType = ticketInfoForPassengerFormNode.get("queryLeftNewDetailDTO").get("WZ_seat_type_code").textValue();
        String ticketType = "1";
        String cardType = "1";
        String passengerTicketStr = String.join(",",
                seatType,
                "0",
                ticketType,
                normalPassengerDTO.getPassengerName(),
                cardType,
                normalPassengerDTO.getPassengerIdNo(),
                normalPassengerDTO.getMobileNo(),
                "N");
        return passengerTicketStr;
    }

    private String getOldPassengerStr(NormalPassengerDTO normalPassengerDTO) {
        String cardType = "1";
        String oldPassengerStr = String.join(",",
                normalPassengerDTO.getPassengerName(),
                cardType,
                normalPassengerDTO.getPassengerIdNo(),
                "1") + "_";
        return oldPassengerStr;
    }

    private void otnConfirmPassengerCheckOrderInfo(String repeatSubmitToken, String passengerTicketStr, String oldPassengerStr) throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/confirmPassenger/checkOrderInfo")
                .build();
        HttpPost httppost = new HttpPost(uri);
        httppost.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) 12306-electron/1.0.1 Chrome/59.0.3071.115 Electron/1.8.4 Safari/537.36");
        httppost.setHeader(HttpHeaders.REFERER, "https://kyfw.12306.cn/otn/confirmPassenger/initDc");
        httppost.setHeader("Origin", "https://kyfw.12306.cn");

        List<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("passengerTicketStr", passengerTicketStr));
        formparams.add(new BasicNameValuePair("oldPassengerStr", oldPassengerStr));
        formparams.add(new BasicNameValuePair("REPEAT_SUBMIT_TOKEN", repeatSubmitToken));
        formparams.add(new BasicNameValuePair("randCode", ""));
        formparams.add(new BasicNameValuePair("cancel_flag", "2"));
        formparams.add(new BasicNameValuePair("bed_level_order_num", "000000000000000000000000000000"));
        formparams.add(new BasicNameValuePair("tour_flag", "dc"));
        formparams.add(new BasicNameValuePair("_json_att", ""));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httppost.setEntity(entity);

        CloseableHttpResponse response = httpclient.execute(httppost);
        String responseString = getResponse(response);
        log.debug("responseString = {}", responseString);
    }

    private void otnConfirmPassengerGetQueueCount(JsonNode ticketInfoForPassengerFormNode, String repeatSubmitToken) throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/confirmPassenger/getQueueCount")
                .build();
        HttpPost httppost = new HttpPost(uri);
        httppost.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) 12306-electron/1.0.1 Chrome/59.0.3071.115 Electron/1.8.4 Safari/537.36");
        httppost.setHeader(HttpHeaders.REFERER, "https://kyfw.12306.cn/otn/confirmPassenger/initDc");
        httppost.setHeader("Origin", "https://kyfw.12306.cn");

        String trainDate = DateUtils.formatDate(new Date(), "EEE MMM dd yyyy") + " 00:00:00 GMT+0800 (中国标准时间)";
        String trainNo = ticketInfoForPassengerFormNode.get("orderRequestDTO").get("train_no").textValue();
        String stationTrainCode = ticketInfoForPassengerFormNode.get("orderRequestDTO").get("station_train_code").textValue();
        String seatType = ticketInfoForPassengerFormNode.get("queryLeftNewDetailDTO").get("WZ_seat_type_code").textValue();
        String fromStationTelecode = ticketInfoForPassengerFormNode.get("orderRequestDTO").get("from_station_telecode").textValue();
        String toStationTelecode = ticketInfoForPassengerFormNode.get("orderRequestDTO").get("to_station_telecode").textValue();
        String leftTicket = ticketInfoForPassengerFormNode.get("leftTicketStr").textValue();
        String purposeCodes = ticketInfoForPassengerFormNode.get("purpose_codes").textValue();
        String trainLocation = ticketInfoForPassengerFormNode.get("train_location").textValue();

        List<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("train_date", trainDate));
        formparams.add(new BasicNameValuePair("train_no", trainNo));
        formparams.add(new BasicNameValuePair("stationTrainCode", stationTrainCode));
        formparams.add(new BasicNameValuePair("seatType", seatType));
        formparams.add(new BasicNameValuePair("fromStationTelecode", fromStationTelecode));
        formparams.add(new BasicNameValuePair("toStationTelecode", toStationTelecode));
        formparams.add(new BasicNameValuePair("leftTicket", leftTicket));
        formparams.add(new BasicNameValuePair("purpose_codes", purposeCodes));
        formparams.add(new BasicNameValuePair("train_location", trainLocation));
        formparams.add(new BasicNameValuePair("REPEAT_SUBMIT_TOKEN", repeatSubmitToken));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httppost.setEntity(entity);

        CloseableHttpResponse response = httpclient.execute(httppost);
        String responseString = getResponse(response);
        log.debug("responseString = {}", responseString);
    }

    private void otnConfirmPassengerConfirmSingleForQueue(String passengerTicketStr,
                                                          String oldPassengerStr,
                                                          JsonNode ticketInfoForPassengerFormNode,
                                                          String repeatSubmitToken) throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/confirmPassenger/confirmSingleForQueue")
                .build();
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) 12306-electron/1.0.1 Chrome/59.0.3071.115 Electron/1.8.4 Safari/537.36");
        httpPost.setHeader(HttpHeaders.REFERER, "https://kyfw.12306.cn/otn/confirmPassenger/initDc");
        httpPost.setHeader("Origin", "https://kyfw.12306.cn");

        List<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("passengerTicketStr", passengerTicketStr));
        formparams.add(new BasicNameValuePair("oldPassengerStr", oldPassengerStr));
        formparams.add(new BasicNameValuePair("purpose_codes", ticketInfoForPassengerFormNode.get("purpose_codes").textValue()));
        formparams.add(new BasicNameValuePair("key_check_isChange", ticketInfoForPassengerFormNode.get("key_check_isChange").textValue()));
        formparams.add(new BasicNameValuePair("leftTicketStr", ticketInfoForPassengerFormNode.get("leftTicketStr").textValue()));
        formparams.add(new BasicNameValuePair("train_location", ticketInfoForPassengerFormNode.get("train_location").textValue()));
        formparams.add(new BasicNameValuePair("seatDetailType", ""));
        formparams.add(new BasicNameValuePair("roomType", "00"));
        formparams.add(new BasicNameValuePair("dwAll", "N"));
        formparams.add(new BasicNameValuePair("whatsSelect", "1"));
        formparams.add(new BasicNameValuePair("_json_att", ""));
        formparams.add(new BasicNameValuePair("randCode", ""));
        formparams.add(new BasicNameValuePair("choose_seats", ""));
        formparams.add(new BasicNameValuePair("REPEAT_SUBMIT_TOKEN", repeatSubmitToken));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httpPost.setEntity(entity);

        CloseableHttpResponse response = httpclient.execute(httpPost);
        String responseString = getResponse(response);
        log.debug("responseString = {}", responseString);
    }

    private String otnConfirmPassengerQueryOrderWaitTime(String repeatSubmitToken) throws URISyntaxException, IOException, InterruptedException {
        while (true) {
            URI uri = new URIBuilder()
                    .setScheme("https")
                    .setHost("kyfw.12306.cn")
                    .setPath("/otn/confirmPassenger/queryOrderWaitTime")
                    .setParameter("random", String.valueOf(System.currentTimeMillis()))
                    .setParameter("tourFlag", "dc")
                    .setParameter("json_att", "")
                    .build();
            HttpGet httpGet = new HttpGet(uri);
            httpGet.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) 12306-electron/1.0.1 Chrome/59.0.3071.115 Electron/1.8.4 Safari/537.36");
            httpGet.setHeader(HttpHeaders.REFERER, "https://kyfw.12306.cn/otn/confirmPassenger/initDc");
            httpGet.setHeader("Origin", "https://kyfw.12306.cn");

            CloseableHttpResponse response = httpclient.execute(httpGet);
            String responseString = getResponse(response);
            ResultDTO resultDTO = objectMapper.readValue(responseString, new TypeReference<ResultDTO>() {
            });
            log.debug("resultDTO = {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultDTO));

            JsonNode orderIdNode = resultDTO.getData().get("orderId");
            if (orderIdNode.isNull()) {
                int waitTime = resultDTO.getData().get("waitTime").intValue();
                if (waitTime > 0) {
                    TimeUnit.SECONDS.sleep(waitTime);
                } else {
                    log.error("waitTime = {}", waitTime);
                    TimeUnit.SECONDS.sleep(5);
                }
            } else {
                return orderIdNode.asText();
            }
        }
    }

    private void otnConfirmPassengerResultOrderForDcQueue() throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/confirmPassenger/resultOrderForDcQueue")
                .build();
        HttpPost httppost = new HttpPost(uri);
        httppost.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.80 Safari/537.36");
        httppost.setHeader(HttpHeaders.REFERER, "https://kyfw.12306.cn/otn/confirmPassenger/initDc");
        httppost.setHeader("x-requested-with", "XMLHttpRequest");

        List<NameValuePair> formparams = new ArrayList<>();
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

    private String getWantedTicketSecret(OrderTicketDTO orderTicketDTO, ArrayNode arrayNode) throws IOException {

        for (JsonNode jsonNode : arrayNode) {
            String r = jsonNode.textValue();
            String[] rs = r.split("\\|");
            if (rs[3].equalsIgnoreCase(orderTicketDTO.getStationTrain())) {
                int seatType = mapSeatConf.get(orderTicketDTO.getSeatType());
                log.debug("列车: {}, 余票: {}", orderTicketDTO.getStationTrain(), rs[seatType]);
                if (!rs[seatType].equalsIgnoreCase("无") &&
                        !rs[seatType].equalsIgnoreCase("") &&
                        !rs[seatType].equalsIgnoreCase("*")) {
                    // 查询到符合条件的票存入cookie
                    String secret = URLDecoder.decode(rs[0], StandardCharsets.UTF_8);
                    log.debug("secret = {}", secret);
                    return secret;
                }
            }
        }
        return null;
    }

    private List<Object> login() throws IOException, URISyntaxException {
        List<String> otnLeftTicketInitResult = otnLeftTicketInit();
        otnLoginConf();
        otnResourcesLoginHtml();
        passportWebAuthUamtkStatic();
        otnHttpZFLogdevice();
        String image = passportCaptchaCaptchaImage64();
        String check = getCheck(image);
        otnResourcesLoginHtml();
        passportWebAuthUamtkStatic();
        passportCaptchaCaptchaCheck(check);
        passportWebLogin(orderTicketDTO.getUsername(), orderTicketDTO.getPassword(), check);
        otnResourcesLoginHtml();
        String newapptk = passportWebAuthUamtkStatic();
        otnUamauthclient(newapptk);
        NormalPassengerDTO normalPassengerDTO = otnConfirmPassengerGetPassengerDTOs(orderTicketDTO);
        otnLoginCheckUser();

        List<Object> loginResult = new ArrayList<>();
        loginResult.add(otnLeftTicketInitResult.get(0));
        loginResult.add(otnLeftTicketInitResult.get(1));
        loginResult.add(normalPassengerDTO);
        return loginResult;
    }

    private void sendEmail(String orderId, OrderTicketDTO orderTicketDTO) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("908686171@qq.com");
        message.setTo("908686171@qq.com");
        message.setSubject("火车票抢票成功");
        // 恭喜您订票成功，订单号为：E898599288, 请立即打开浏览器登录12306，访问‘未完成订单’，在30分钟内完成支付!
        String text = String.format("%s，从%s到%s，列车%s，乘车人%s，已抢票成功，订单编号%s，请尽快登录12306支付购买",
                orderTicketDTO.getTrainDate(),
                orderTicketDTO.getFromStation(),
                orderTicketDTO.getToStation(),
                orderTicketDTO.getStationTrain(),
                orderTicketDTO.getTicketPeople(),
                orderId);
        message.setText(text);

        mailSender.send(message);
    }

    public static String string2Unicode(String string) {
        StringBuilder unicode = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            // 取出每一个字符
            char c = string.charAt(i);
            // 转换为unicode
            unicode.append("%u" + Integer.toHexString(c));
        }
        return unicode.toString();
    }

    private void addJcSaveCookie(CookieStore cookieStore, OrderTicketDTO orderTicketDTO, Map<String, String> nameCodeMap) {
        addCookie(cookieStore, "_jc_save_fromStation", string2Unicode(orderTicketDTO.getFromStation()) + "%2C" + nameCodeMap.get(orderTicketDTO.getFromStation()));
        addCookie(cookieStore, "_jc_save_toStation", string2Unicode(orderTicketDTO.getToStation()) + "%2C" + nameCodeMap.get(orderTicketDTO.getToStation()));
        addCookie(cookieStore, "_jc_save_fromDate", orderTicketDTO.getTrainDate());
        addCookie(cookieStore, "_jc_save_toDate", orderTicketDTO.getBackTrainDate());
        addCookie(cookieStore, "_jc_save_wfdc_flag", "dc");
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

    @Test
    public void order() throws IOException, URISyntaxException, InterruptedException {
        List<Object> otnLeftTicketInitResult = login();
        String leftTicketQueryUrl = (String) otnLeftTicketInitResult.get(0);
        String stationVersionUrl = (String) otnLeftTicketInitResult.get(1);
        NormalPassengerDTO normalPassengerDTO = (NormalPassengerDTO) otnLeftTicketInitResult.get(2);
        log.debug("leftTicketQueryUrl = {}", leftTicketQueryUrl);
        log.debug("stationVersionUrl = {}", stationVersionUrl);
        Map<String, String> nameCodeMap = otnResourcesJsFrameworkStationName(stationVersionUrl);
        log.debug("nameCodeMap = {}", nameCodeMap);
        String secret = queryTicket(leftTicketQueryUrl, nameCodeMap);
        otnLeftTicketSubmitOrderRequest(secret, orderTicketDTO);
        List<String> otnConfirmPassengerInitDcResult = otnConfirmPassengerInitDc();
        String globalRepeatSubmitToken = otnConfirmPassengerInitDcResult.get(0);
        String ticketInfoForPassengerForm = otnConfirmPassengerInitDcResult.get(1);
        JsonNode ticketInfoForPassengerFormNode = objectMapper.readTree(ticketInfoForPassengerForm);
        String passengerTicketStr = getPassengerTicketStr(ticketInfoForPassengerFormNode, normalPassengerDTO);
        String oldPassengerStr = getOldPassengerStr(normalPassengerDTO);
        otnConfirmPassengerCheckOrderInfo(globalRepeatSubmitToken, passengerTicketStr, oldPassengerStr);
        otnConfirmPassengerGetQueueCount(ticketInfoForPassengerFormNode, globalRepeatSubmitToken);
        TimeUnit.SECONDS.sleep(3);
        otnConfirmPassengerConfirmSingleForQueue(passengerTicketStr, oldPassengerStr, ticketInfoForPassengerFormNode, globalRepeatSubmitToken);
        String orderId = otnConfirmPassengerQueryOrderWaitTime(globalRepeatSubmitToken);
        sendEmail(orderId, orderTicketDTO);
    }
}