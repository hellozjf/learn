package com.hellozjf.learn.projects.order12306.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.hellozjf.learn.projects.order12306.config.CustomConfig;
import com.hellozjf.learn.projects.order12306.constant.ResultEnum;
import com.hellozjf.learn.projects.order12306.constant.TicketStateEnum;
import com.hellozjf.learn.projects.order12306.custom.FileCookieStore;
import com.hellozjf.learn.projects.order12306.domain.TicketInfoEntity;
import com.hellozjf.learn.projects.order12306.dto.NormalPassengerDTO;
import com.hellozjf.learn.projects.order12306.dto.ResultDTO;
import com.hellozjf.learn.projects.order12306.dto.TicketInfoDTO;
import com.hellozjf.learn.projects.order12306.exception.Order12306Exception;
import com.hellozjf.learn.projects.order12306.repository.TicketInfoRepository;
import com.hellozjf.learn.projects.order12306.service.Client12306Service;
import com.hellozjf.learn.projects.order12306.util.HttpClientUtils;
import com.hellozjf.learn.projects.order12306.util.RegexUtils;
import com.hellozjf.learn.projects.order12306.util.ResultDTOUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Jingfeng Zhou
 */
@Slf4j
@Service
public class Client12306ServiceImpl implements Client12306Service {

    @Autowired
    private Random random;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TicketInfoRepository ticketInfoRepository;

    @Autowired
    @Qualifier("mapSeatConf")
    private Map<String, Integer> mapSeatConf;

    @Autowired
    private CustomConfig customConfig;

    @Override
    public CloseableHttpClient login(String username, String password) throws IOException, URISyntaxException {
        CookieStore cookieStore = new FileCookieStore(customConfig.getCookieFolderName(), username);
        CloseableHttpClient closeableHttpClient = HttpClientUtils.getHttpClient(cookieStore);
        login(closeableHttpClient, cookieStore, username, password);
        return closeableHttpClient;
    }

    @Override
    public List<NormalPassengerDTO> queryTicketPeopleList(CloseableHttpClient httpClient) throws IOException, URISyntaxException {
        List<NormalPassengerDTO> normalPassengerDTOList = otnConfirmPassengerGetPassengerDTOs(httpClient);
        return normalPassengerDTOList;
    }

    @Override
    public List<TicketInfoDTO> queryLeftTicketList(CloseableHttpClient httpClient, String trainDate, String fromStation, String toStation) throws IOException, URISyntaxException {
        List<String> otnLeftTicketInitList = otnLeftTicketInit(httpClient);
        String leftTicketQueryUrl = otnLeftTicketInitList.get(0);
        String stationVersionUrl = otnLeftTicketInitList.get(1);
        Map<String, String> nameCodeMap = otnResourcesJsFrameworkStationName(httpClient, stationVersionUrl);
        String fromStationCode = nameCodeMap.get(fromStation);
        String toStationCode = nameCodeMap.get(toStation);
        Map<String, String> codeNameMap = exchangeMap(nameCodeMap);
        ArrayNode arrayNode = otnLeftTicketQuery(httpClient, leftTicketQueryUrl, trainDate, fromStationCode, toStationCode);
        log.debug("arrayNode = {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode));

        List<TicketInfoDTO> ticketInfoDTOList = new ArrayList<>();
        for (JsonNode jsonNode : arrayNode) {
            TicketInfoDTO ticketInfoDTO = new TicketInfoDTO();
            String[] parts = jsonNode.asText().split("\\|");
            ticketInfoDTO.setSecret(parts[0]);
            ticketInfoDTO.setStationTrain(parts[3]);
            ticketInfoDTO.setBeginStationCode(parts[4]);
            ticketInfoDTO.setEndStationCode(parts[5]);
            ticketInfoDTO.setFromStationCode(parts[6]);
            ticketInfoDTO.setFromStation(codeNameMap.get(parts[6]));
            ticketInfoDTO.setToStationCode(parts[7]);
            ticketInfoDTO.setToStation(codeNameMap.get(parts[7]));
            ticketInfoDTO.setFromTime(parts[8]);
            ticketInfoDTO.setToTime(parts[9]);
            ticketInfoDTO.setDuringTime(parts[10]);
            ticketInfoDTO.setTrainDate(parts[13]);
            ticketInfoDTO.setAdvancedSoftSleeper(parts[21]);
            ticketInfoDTO.setOther(parts[22]);
            ticketInfoDTO.setSoftSleeper(parts[23]);
            ticketInfoDTO.setSoftSeat(parts[24]);
            ticketInfoDTO.setPrincipalSeat(parts[25]);
            ticketInfoDTO.setNoSeat(parts[26]);
            ticketInfoDTO.setHardSleeper(parts[28]);
            ticketInfoDTO.setHardSeat(parts[29]);
            ticketInfoDTO.setSecondClass(parts[30]);
            ticketInfoDTO.setFirstClass(parts[31]);
            ticketInfoDTO.setBusinessClass(parts[32]);
            ticketInfoDTO.setStillLie(parts[33]);
            ticketInfoDTOList.add(ticketInfoDTO);
        }
        return ticketInfoDTOList;
    }

    private Map<String, String> exchangeMap(Map<String, String> nameCodeMap) {
        Map<String, String> codeNameMap = new HashMap<>();
        for (Map.Entry<String, String> entry : nameCodeMap.entrySet()) {
            codeNameMap.put(entry.getValue(), entry.getKey());
        }
        return codeNameMap;
    }

    @Override
    public String order(TicketInfoEntity ticketInfoEntity) throws IOException, URISyntaxException, InterruptedException {
        // 抢票前先要登录
        CookieStore cookieStore = new FileCookieStore(customConfig.getCookieFolderName(), ticketInfoEntity.getUsername());
        CloseableHttpClient httpClient = HttpClientUtils.getHttpClient(cookieStore);
        login(httpClient, cookieStore, ticketInfoEntity.getUsername(), ticketInfoEntity.getPassword());

        List<String> otnLeftTicketInitResult = otnLeftTicketInit(httpClient);
        String leftTicketQueryUrl = otnLeftTicketInitResult.get(0);
        String stationVersionUrl = otnLeftTicketInitResult.get(1);
        log.debug("leftTicketQueryUrl = {}", leftTicketQueryUrl);
        log.debug("stationVersionUrl = {}", stationVersionUrl);
        Map<String, String> nameCodeMap = otnResourcesJsFrameworkStationName(httpClient, stationVersionUrl);
        log.debug("nameCodeMap = {}", nameCodeMap);
        otnPasscodeNewGetPassCodeNew(httpClient);
        String secret = queryTicket(httpClient, leftTicketQueryUrl, nameCodeMap, ticketInfoEntity, mapSeatConf);

        otnLoginCheckUser(httpClient);
        otnLeftTicketSubmitOrderRequest(httpClient, secret, ticketInfoEntity);
        List<String> otnConfirmPassengerInitDcResult = otnConfirmPassengerInitDc(httpClient);
        String globalRepeatSubmitToken = otnConfirmPassengerInitDcResult.get(0);
        String ticketInfoForPassengerForm = otnConfirmPassengerInitDcResult.get(1);
        JsonNode ticketInfoForPassengerFormNode = objectMapper.readTree(ticketInfoForPassengerForm);
        NormalPassengerDTO normalPassengerDTO = otnConfirmPassengerGetPassengerDTOs(httpClient, globalRepeatSubmitToken, ticketInfoEntity);
        otnPasscodeNewGetPassCodeNew(httpClient);

        String passengerTicketStr = getPassengerTicketStr(ticketInfoForPassengerFormNode, normalPassengerDTO);
        String oldPassengerStr = getOldPassengerStr(normalPassengerDTO);
        otnConfirmPassengerCheckOrderInfo(httpClient, globalRepeatSubmitToken, passengerTicketStr, oldPassengerStr);
        otnConfirmPassengerGetQueueCount(httpClient, ticketInfoForPassengerFormNode, globalRepeatSubmitToken);

        // 确认订单需要等待3秒，否则会报-100错误
        TimeUnit.SECONDS.sleep(3);

        otnConfirmPassengerConfirmSingleForQueue(httpClient, passengerTicketStr, oldPassengerStr, ticketInfoForPassengerFormNode, globalRepeatSubmitToken);
        String orderId = otnConfirmPassengerQueryOrderWaitTime(httpClient, globalRepeatSubmitToken);
        otnConfirmPassengerResultOrderForDcQueue(httpClient);

        return orderId;
    }

    private void login(CloseableHttpClient httpClient, CookieStore cookieStore, String username, String password) throws IOException, URISyntaxException {
        otnHttpZFGetJS(httpClient);
        otnHttpZFLogdevice(httpClient, cookieStore);
        String newapptk = passportWebAuthUamtkStatic(httpClient);
        if (StringUtils.isEmpty(newapptk)) {
            otnLoginConf(httpClient);
            otnIndex12306GetLoginBanner(httpClient);
            passportWebAuthUamtkStatic(httpClient);
            String image = passportCaptchaCaptchaImage64(httpClient);
            String check = getCheck(httpClient, image);
            passportCaptchaCaptchaCheck(httpClient, check);
            passportWebLogin(httpClient, username, password, check);
            otnLoginUserLogin(httpClient);
            newapptk = passportWebAuthUamtk(httpClient);
        }
        otnUamauthclient(httpClient, newapptk);
        otnLoginUserLogin(httpClient);
        otnLoginConf(httpClient);
        otnIndexInitMy12306Api(httpClient);
    }

    private void otnHttpZFGetJS(CloseableHttpClient httpClient) throws IOException, URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/HttpZF/GetJS")
                .build();
        HttpGet httpget = new HttpGet(uri);

        CloseableHttpResponse response = httpClient.execute(httpget);
        String responseString = HttpClientUtils.getResponse(response);
    }

    private void otnHttpZFLogdevice(CloseableHttpClient httpClient, CookieStore cookieStore) throws IOException, URISyntaxException {
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

        CloseableHttpResponse response = httpClient.execute(httpget);
        String responseString = HttpClientUtils.getResponse(response);
        responseString = RegexUtils.getMatch(responseString, ".*\\('(.*)'\\)");
        ResultDTO resultDTO = objectMapper.readValue(responseString, new TypeReference<ResultDTO>() {
        });
        log.debug("resultDTO = {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultDTO));

        log.debug("cookies = {}", cookieStore.getCookies());

        HttpClientUtils.addCookie(cookieStore, "RAIL_EXPIRATION", resultDTO.getExp());
        HttpClientUtils.addCookie(cookieStore, "RAIL_DEVICEID", resultDTO.getDfp());
    }

    private String passportWebAuthUamtkStatic(CloseableHttpClient httpClient) throws IOException, URISyntaxException {
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

        CloseableHttpResponse response = httpClient.execute(httppost);
        String responseString = HttpClientUtils.getResponse(response);
        ResultDTO resultDTO = objectMapper.readValue(responseString, new TypeReference<ResultDTO>(){});
        return resultDTO.getNewapptk();
    }

    private void otnLoginConf(CloseableHttpClient httpClient) throws IOException, URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/login/conf")
                .build();
        HttpPost httppost = new HttpPost(uri);

        List<NameValuePair> formparams = new ArrayList<>();
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httppost.setEntity(entity);

        CloseableHttpResponse response = httpClient.execute(httppost);
        String responseString = HttpClientUtils.getResponse(response);
        ResultDTOUtils.checkStatus(objectMapper, ResultEnum.OTN_LOGIN_CONF_ERROR, responseString);
    }

    private void otnIndex12306GetLoginBanner(CloseableHttpClient httpClient) throws IOException, URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/index12306/getLoginBanner")
                .build();
        HttpGet httpget = new HttpGet(uri);

        CloseableHttpResponse response = httpClient.execute(httpget);
        String responseString = HttpClientUtils.getResponse(response);
        ResultDTOUtils.checkStatus(objectMapper, ResultEnum.OTN_INDEX12306_GET_LOGIN_BANNER_ERROR, responseString);
    }

    private String passportCaptchaCaptchaImage64(CloseableHttpClient httpClient) throws IOException, URISyntaxException {
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

        CloseableHttpResponse response = httpClient.execute(httpget);
        String responseString = HttpClientUtils.getResponse(response);
        String text = RegexUtils.getMatch(responseString, ".*\\((.*)\\)");
        ResultDTO resultDTO = ResultDTOUtils.checkResultCode(objectMapper, text, "0");
        return resultDTO.getImage();
    }

    private String getCheck(CloseableHttpClient httpClient, String image) throws IOException, URISyntaxException {
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

        CloseableHttpResponse response = httpClient.execute(httppost);
        String responseString = HttpClientUtils.getResponse(response);
        ResultDTO resultDTO = objectMapper.readValue(responseString, new TypeReference<ResultDTO>() {
        });
        return resultDTO.getData().asText();
    }

    private void passportCaptchaCaptchaCheck(CloseableHttpClient httpClient, String check) throws URISyntaxException, IOException {
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

        CloseableHttpResponse response = httpClient.execute(httpget);
        String responseString = HttpClientUtils.getResponse(response);
        responseString = RegexUtils.getMatch(responseString, ".*\\((.*)\\)");
        ResultDTOUtils.checkResultCode(objectMapper, responseString, "4");
    }

    private void passportWebLogin(CloseableHttpClient httpClient, String username, String password, String answer) throws URISyntaxException, IOException {
        for (int i = 0; i < 1000; i++) {
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

            // 把尝试登陆的次数+1，再存回数据库中
            TicketInfoEntity ticketInfoEntity = ticketInfoRepository.findTopByUsernameOrderByGmtCreateDesc(username).get();
            ticketInfoEntity.setTryLoginTimes(ticketInfoEntity.getTryLoginTimes() + 1);
            ticketInfoRepository.save(ticketInfoEntity);

            CloseableHttpResponse response = httpClient.execute(httppost);
            String responseString = HttpClientUtils.getResponse(response);
            if (!StringUtils.isEmpty(responseString)) {
                ResultDTOUtils.checkResultCode(objectMapper, responseString, "0");
                return;
            }
        }
        throw new Order12306Exception(ResultEnum.LOGIN_12306_ERROR);
    }

    private void otnLoginUserLogin(CloseableHttpClient httpClient) throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/login/userLogin")
                .build();
        HttpGet httpget = new HttpGet(uri);

        CloseableHttpResponse response = httpClient.execute(httpget);
        HttpClientUtils.getResponse(response);
    }

    private String passportWebAuthUamtk(CloseableHttpClient httpClient) throws URISyntaxException, IOException {
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

        CloseableHttpResponse response = httpClient.execute(httppost);
        String responseString = HttpClientUtils.getResponse(response);
        ResultDTO resultDTO = ResultDTOUtils.checkResultCode(objectMapper, responseString, "0");
        return resultDTO.getNewapptk();
    }

    private void otnUamauthclient(CloseableHttpClient httpClient, String newapptk) throws URISyntaxException, IOException {
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

        CloseableHttpResponse response = httpClient.execute(httppost);
        String responseString = HttpClientUtils.getResponse(response);
        ResultDTOUtils.checkResultCode(objectMapper, responseString, "0");
    }

    private void otnIndexInitMy12306Api(CloseableHttpClient httpClient) throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/index/initMy12306Api")
                .build();
        HttpPost httppost = new HttpPost(uri);

        List<NameValuePair> formparams = new ArrayList<>();
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httppost.setEntity(entity);

        CloseableHttpResponse response = httpClient.execute(httppost);
        String responseString = HttpClientUtils.getResponse(response);
        ResultDTOUtils.checkStatus(objectMapper, ResultEnum.OTN_INDEX_INITMY12306API_ERROR, responseString);
    }

    private List<NormalPassengerDTO> otnConfirmPassengerGetPassengerDTOs(CloseableHttpClient httpClient) throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/confirmPassenger/getPassengerDTOs")
                .build();
        HttpPost httppost = new HttpPost(uri);

        List<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("_json_att", ""));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httppost.setEntity(entity);

        CloseableHttpResponse response = httpClient.execute(httppost);
        String responseString = HttpClientUtils.getResponse(response);
        ResultDTO resultDTO = ResultDTOUtils.checkStatus(objectMapper, ResultEnum.OTN_CONFIRM_PASSENGER_GET_PASSENGER_DTOS_ERROR, responseString);

        JsonNode normalPassengers = resultDTO.getData().get("normal_passengers");
        String normalPassengersString = objectMapper.writeValueAsString(normalPassengers);
        log.debug("normalPassengers = {}", normalPassengersString);
        List<NormalPassengerDTO> normalPassengerList = objectMapper.readValue(normalPassengersString, new TypeReference<List<NormalPassengerDTO>>() {
        });
        return normalPassengerList;
    }

    private NormalPassengerDTO otnConfirmPassengerGetPassengerDTOs(CloseableHttpClient httpClient, String repeatSubmitToken, TicketInfoEntity ticketInfoEntity) throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/confirmPassenger/getPassengerDTOs")
                .build();
        HttpPost httppost = new HttpPost(uri);

        List<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("_json_att", ""));
        formparams.add(new BasicNameValuePair("REPEAT_SUBMIT_TOKEN", repeatSubmitToken));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httppost.setEntity(entity);

        CloseableHttpResponse response = httpClient.execute(httppost);
        String responseString = HttpClientUtils.getResponse(response);
        ResultDTO resultDTO = ResultDTOUtils.checkStatus(objectMapper, ResultEnum.OTN_CONFIRM_PASSENGER_GET_PASSENGER_DTOS_ERROR, responseString);

        JsonNode normalPassengers = resultDTO.getData().get("normal_passengers");
        String normalPassengersString = objectMapper.writeValueAsString(normalPassengers);
        log.debug("normalPassengers = {}", normalPassengersString);
        List<NormalPassengerDTO> normalPassengerList = objectMapper.readValue(normalPassengersString, new TypeReference<List<NormalPassengerDTO>>() {
        });
        for (NormalPassengerDTO normalPassengerDTO : normalPassengerList) {
            if (normalPassengerDTO.getPassengerName().equalsIgnoreCase(ticketInfoEntity.getTicketPeople())) {
                return normalPassengerDTO;
            }
        }
        return null;
    }

    private List<String> otnLeftTicketInit(CloseableHttpClient closeableHttpClient) throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/leftTicket/init")
                .setParameter("linktypeid", "dc")
                .build();
        HttpGet httpget = new HttpGet(uri);

        CloseableHttpResponse response = closeableHttpClient.execute(httpget);
        String responseString = HttpClientUtils.getResponse(response);
        String leftTicketQueryUrl = RegexUtils.getMatch(responseString, "var CLeftTicketUrl = '(.*)';");
        String stationVersionUrl = RegexUtils.getMatch(responseString, "<script .* src=\"(/otn/resources/js/framework/station_name.js\\?station_version=.*)\" .*</script>");
        return Arrays.asList(leftTicketQueryUrl, stationVersionUrl);
    }

    private ArrayNode otnLeftTicketQuery(CloseableHttpClient httpClient, String leftTicketQueryUrl, String trainDate, String fromStation, String toStation) throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/" + leftTicketQueryUrl)
                .setParameter("leftTicketDTO.train_date", trainDate)
                .setParameter("leftTicketDTO.from_station", fromStation)
                .setParameter("leftTicketDTO.to_station", toStation)
                .setParameter("purpose_codes", "ADULT")
                .build();
        HttpGet httpget = new HttpGet(uri);

        CloseableHttpResponse response = httpClient.execute(httpget);
        String responseString = HttpClientUtils.getResponse(response);
        ResultDTO resultDTO = ResultDTOUtils.checkStatus(objectMapper, ResultEnum.OTN_LEFT_TICKET_QUERY_ERROR, responseString);

        return (ArrayNode) resultDTO.getData().get("result");
    }

    private Map<String, String> otnResourcesJsFrameworkStationName(CloseableHttpClient httpClient, String stationVersionUrl) throws URISyntaxException, IOException {
        String[] parts = stationVersionUrl.split("\\?");
        String[] params = parts[1].split("=");
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath(parts[0])
                .setParameter(params[0], params[1])
                .build();
        HttpGet httpget = new HttpGet(uri);

        CloseableHttpResponse response = httpClient.execute(httpget);
        String responseString = HttpClientUtils.getResponse(response);
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

    private void otnPasscodeNewGetPassCodeNew(CloseableHttpClient httpClient) throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/passcodeNew/getPassCodeNew")
                .setParameter("module", "passenger")
                .setParameter("rand", "randp")
                .setParameter(String.valueOf(random.nextDouble()), null)
                .build();
        HttpGet httpget = new HttpGet(uri);

        CloseableHttpResponse response = httpClient.execute(httpget);
        HttpClientUtils.getResponse(response);
    }

    private String getWantedTicketSecret(TicketInfoEntity ticketInfoEntity, ArrayNode arrayNode, Map<String, Integer> mapSeatConf) throws IOException {

        for (JsonNode jsonNode : arrayNode) {
            String r = jsonNode.textValue();
            String[] rs = r.split("\\|");
            if (rs[3].equalsIgnoreCase(ticketInfoEntity.getStationTrain())) {
                int seatType = mapSeatConf.get(ticketInfoEntity.getSeatType());
                log.debug("列车: {}, 余票: {}", ticketInfoEntity.getStationTrain(), rs[seatType]);
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

    private String queryTicket(CloseableHttpClient closeableHttpClient, String leftTicketQueryUrl, Map<String, String> nameCodeMap, TicketInfoEntity ticketInfoEntity, Map<String, Integer> mapSeatConf) throws IOException, URISyntaxException {
        String trainDate = ticketInfoEntity.getTrainDate();
        String fromStationCode = nameCodeMap.get(ticketInfoEntity.getFromStation());
        String toStationCode = nameCodeMap.get(ticketInfoEntity.getToStation());
        while (true) {
            ArrayNode arrayNode = otnLeftTicketQuery(closeableHttpClient, leftTicketQueryUrl, trainDate, fromStationCode, toStationCode);
            String secret = getWantedTicketSecret(ticketInfoEntity, arrayNode, mapSeatConf);
            ticketInfoEntity = ticketInfoRepository.findByStateAndUsername(TicketStateEnum.GRABBING.getCode(), ticketInfoEntity.getUsername()).get();
            if (ticketInfoEntity == null) {
                // 说明抢票被终止了，那就返回空字符串表示异常
                throw new Order12306Exception(ResultEnum.GRABBING_STOPED_BY_HAND);
            }
            // 将查询次数写入数据库中
            ticketInfoEntity.setTryLeftTicketTimes(ticketInfoEntity.getTryLeftTicketTimes() + 1);
            ticketInfoRepository.save(ticketInfoEntity);
            if (StringUtils.isEmpty(secret)) {
                log.debug("目前{}无票，等待5s后重试", ticketInfoEntity.getStationTrain());
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    log.error("e = {}", e);
                }
            } else {
                log.debug("查询到{}有票，即将开始锁定票", ticketInfoEntity.getStationTrain());
                return secret;
            }
        }
    }

    private void otnLoginCheckUser(CloseableHttpClient closeableHttpClient) throws URISyntaxException, IOException {
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

        CloseableHttpResponse response = closeableHttpClient.execute(httppost);
        String responseString = HttpClientUtils.getResponse(response);
        ResultDTOUtils.checkStatus(objectMapper, ResultEnum.OTN_LOGIN_CHECK_USER_ERROR, responseString);
    }

    private void otnLeftTicketSubmitOrderRequest(CloseableHttpClient httpClient, String secret, TicketInfoEntity ticketInfoEntity) throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/leftTicket/submitOrderRequest")
                .build();
        HttpPost httppost = new HttpPost(uri);

        List<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("secretStr", secret));
        formparams.add(new BasicNameValuePair("train_date", ticketInfoEntity.getTrainDate()));
        formparams.add(new BasicNameValuePair("back_train_date", ticketInfoEntity.getBackTrainDate()));
        formparams.add(new BasicNameValuePair("tour_flag", "dc"));
        formparams.add(new BasicNameValuePair("purpose_codes", "ADULT"));
        formparams.add(new BasicNameValuePair("query_from_station_name", ticketInfoEntity.getFromStation()));
        formparams.add(new BasicNameValuePair("query_to_station_name", ticketInfoEntity.getToStation()));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httppost.setEntity(entity);

        CloseableHttpResponse response = httpClient.execute(httppost);
        String responseString = HttpClientUtils.getResponse(response);
        ResultDTOUtils.checkStatus(objectMapper, ResultEnum.LEFT_TICKET_SUBMIT_ORDER_ERROR, responseString);
    }

    private List<String> otnConfirmPassengerInitDc(CloseableHttpClient httpClient) throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/confirmPassenger/initDc")
                .build();
        HttpPost httppost = new HttpPost(uri);

        List<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("_json_att", ""));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httppost.setEntity(entity);

        CloseableHttpResponse response = httpClient.execute(httppost);
        String responseString = HttpClientUtils.getResponse(response);
        String globalRepeatSubmitToken = RegexUtils.getMatch(responseString, "var globalRepeatSubmitToken = '(.*)';");
        String ticketInfoForPassengerForm = RegexUtils.getMatch(responseString, "var ticketInfoForPassengerForm=(.*);");
        ticketInfoForPassengerForm = ticketInfoForPassengerForm.replaceAll("'", "\"");
        log.debug("globalRepeatSubmitToken = {}", globalRepeatSubmitToken);
        log.debug("ticketInfoForPassengerForm = {}", ticketInfoForPassengerForm);
        return Arrays.asList(globalRepeatSubmitToken, ticketInfoForPassengerForm);
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

    private void otnConfirmPassengerCheckOrderInfo(CloseableHttpClient httpClient, String repeatSubmitToken, String passengerTicketStr, String oldPassengerStr) throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/confirmPassenger/checkOrderInfo")
                .build();
        HttpPost httppost = new HttpPost(uri);

        List<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("cancel_flag", "2"));
        formparams.add(new BasicNameValuePair("bed_level_order_num", "000000000000000000000000000000"));
        formparams.add(new BasicNameValuePair("passengerTicketStr", passengerTicketStr));
        formparams.add(new BasicNameValuePair("oldPassengerStr", oldPassengerStr));
        formparams.add(new BasicNameValuePair("tour_flag", "dc"));
        formparams.add(new BasicNameValuePair("randCode", ""));
        formparams.add(new BasicNameValuePair("whatsSelect", "1"));
        formparams.add(new BasicNameValuePair("_json_att", ""));
        formparams.add(new BasicNameValuePair("REPEAT_SUBMIT_TOKEN", repeatSubmitToken));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httppost.setEntity(entity);

        CloseableHttpResponse response = httpClient.execute(httppost);
        String responseString = HttpClientUtils.getResponse(response);
        ResultDTOUtils.checkStatus(objectMapper, ResultEnum.OTN_CONFIRM_PASSENGER_CHECK_ORDER_INFO_ERROR, responseString);
    }

    private void otnConfirmPassengerGetQueueCount(CloseableHttpClient httpClient, JsonNode ticketInfoForPassengerFormNode, String repeatSubmitToken) throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/confirmPassenger/getQueueCount")
                .build();
        HttpPost httppost = new HttpPost(uri);

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
        formparams.add(new BasicNameValuePair("_json_att", ""));
        formparams.add(new BasicNameValuePair("REPEAT_SUBMIT_TOKEN", repeatSubmitToken));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httppost.setEntity(entity);

        CloseableHttpResponse response = httpClient.execute(httppost);
        String responseString = HttpClientUtils.getResponse(response);
        ResultDTOUtils.checkStatus(objectMapper, ResultEnum.OTN_CONFIRM_PASSENGER_GET_QUEUE_COUNT_ERROR, responseString);
    }

    private void otnConfirmPassengerConfirmSingleForQueue(CloseableHttpClient httpClient,
                                                          String passengerTicketStr,
                                                          String oldPassengerStr,
                                                          JsonNode ticketInfoForPassengerFormNode,
                                                          String repeatSubmitToken) throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/confirmPassenger/confirmSingleForQueue")
                .build();
        HttpPost httppost = new HttpPost(uri);

        List<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("passengerTicketStr", passengerTicketStr));
        formparams.add(new BasicNameValuePair("oldPassengerStr", oldPassengerStr));
        formparams.add(new BasicNameValuePair("randCode", ""));
        formparams.add(new BasicNameValuePair("purpose_codes", ticketInfoForPassengerFormNode.get("purpose_codes").textValue()));
        formparams.add(new BasicNameValuePair("key_check_isChange", ticketInfoForPassengerFormNode.get("key_check_isChange").textValue()));
        formparams.add(new BasicNameValuePair("leftTicketStr", ticketInfoForPassengerFormNode.get("leftTicketStr").textValue()));
        formparams.add(new BasicNameValuePair("train_location", ticketInfoForPassengerFormNode.get("train_location").textValue()));
        formparams.add(new BasicNameValuePair("choose_seats", "1F"));
        formparams.add(new BasicNameValuePair("seatDetailType", "000"));
        formparams.add(new BasicNameValuePair("whatsSelect", "1"));
        formparams.add(new BasicNameValuePair("roomType", "00"));
        formparams.add(new BasicNameValuePair("dwAll", "N"));
        formparams.add(new BasicNameValuePair("_json_att", ""));
        formparams.add(new BasicNameValuePair("REPEAT_SUBMIT_TOKEN", repeatSubmitToken));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httppost.setEntity(entity);

        CloseableHttpResponse response = httpClient.execute(httppost);
        String responseString = HttpClientUtils.getResponse(response);
        ResultDTOUtils.checkStatus(objectMapper, ResultEnum.OTN_CONFIRM_PASSENGER_CONFIRM_SINGLE_FOR_QUEUE_ERROR, responseString);
    }

    private String otnConfirmPassengerQueryOrderWaitTime(CloseableHttpClient httpClient, String repeatSubmitToken) throws URISyntaxException, IOException, InterruptedException {
        while (true) {
            URI uri = new URIBuilder()
                    .setScheme("https")
                    .setHost("kyfw.12306.cn")
                    .setPath("/otn/confirmPassenger/queryOrderWaitTime")
                    .setParameter("random", String.valueOf(System.currentTimeMillis()))
                    .setParameter("tourFlag", "dc")
                    .setParameter("json_att", "")
                    .setParameter("REPEAT_SUBMIT_TOKEN", repeatSubmitToken)
                    .build();
            HttpGet httpget = new HttpGet(uri);

            CloseableHttpResponse response = httpClient.execute(httpget);
            String responseString = HttpClientUtils.getResponse(response);
            ResultDTO resultDTO = ResultDTOUtils.checkStatus(objectMapper, ResultEnum.OTN_CONFIRM_PASSENGER_QUERY_ORDER_WAIT_TIME_ERROR, responseString);

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

    private void otnConfirmPassengerResultOrderForDcQueue(CloseableHttpClient httpClient) throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/confirmPassenger/resultOrderForDcQueue")
                .build();
        HttpPost httppost = new HttpPost(uri);

        List<NameValuePair> formparams = new ArrayList<>();
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httppost.setEntity(entity);

        CloseableHttpResponse response = httpClient.execute(httppost);
        String responseString = HttpClientUtils.getResponse(response);
        ResultDTOUtils.checkStatus(objectMapper, ResultEnum.OTN_CONFIRM_PASSENGER_RESULT_ORDER_FOR_DC_QUEUE_ERROR, responseString);
    }
}
