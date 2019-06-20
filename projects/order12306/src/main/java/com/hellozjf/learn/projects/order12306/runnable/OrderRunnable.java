package com.hellozjf.learn.projects.order12306.runnable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.hellozjf.learn.projects.common.config.SpringContextConfig;
import com.hellozjf.learn.projects.order12306.constant.ResultEnum;
import com.hellozjf.learn.projects.order12306.constant.TicketStateEnum;
import com.hellozjf.learn.projects.order12306.domain.TicketInfoEntity;
import com.hellozjf.learn.projects.order12306.dto.NormalPassengerDTO;
import com.hellozjf.learn.projects.order12306.dto.ResultDTO;
import com.hellozjf.learn.projects.order12306.exception.Order12306Exception;
import com.hellozjf.learn.projects.order12306.repository.TicketInfoRepository;
import com.hellozjf.learn.projects.order12306.util.EnumUtils;
import com.hellozjf.learn.projects.order12306.util.ExceptionUtils;
import com.hellozjf.learn.projects.order12306.util.HttpClientUtils;
import com.hellozjf.learn.projects.order12306.util.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 购票线程体
 *
 * @author Jingfeng Zhou
 */
@Slf4j
public class OrderRunnable implements Runnable {

    private CookieStore cookieStore;
    private CloseableHttpClient httpclient;
    private ObjectMapper objectMapper;
    private Random random;
    private TicketInfoEntity ticketInfoEntity;
    private Map<String, Integer> mapSeatConf;
    private JavaMailSender mailSender;
    private TicketInfoRepository ticketInfoRepository;

    public OrderRunnable(TicketInfoEntity ticketInfoEntity) {
        cookieStore = new BasicCookieStore();
        httpclient = HttpClientUtils.getHttpClient(cookieStore);
        objectMapper = SpringContextConfig.getBean(ObjectMapper.class);
        random = SpringContextConfig.getBean(Random.class);
        mapSeatConf = getMapSeatConf();
        mailSender = SpringContextConfig.getBean(JavaMailSender.class);
        ticketInfoRepository = SpringContextConfig.getBean(TicketInfoRepository.class);

        // 把需要购票的信息存起来
        this.ticketInfoEntity = ticketInfoEntity;
    }

    @Override
    public void run() {
        if (ticketInfoEntity == null) {
            log.error("ticketInfoEntity == null");
            return;
        }

        // 数据库中存储一条记录，表示正在抢票
        if (ticketInfoEntity.getId() == null) {
            ticketInfoEntity.setState(TicketStateEnum.GRABBING.getCode());
            ticketInfoEntity = ticketInfoRepository.save(ticketInfoEntity);
        }

        try {
            order();
            // 抢票成功
            ticketInfoEntity.setState(TicketStateEnum.SUCCESS.getCode());
            ticketInfoRepository.save(ticketInfoEntity);
        } catch (Exception e) {
            // 抢票失败
            ticketInfoEntity.setState(TicketStateEnum.FAILED.getCode());
            if (e instanceof Order12306Exception) {
                Order12306Exception order12306Exception = (Order12306Exception) e;
                ticketInfoEntity.setFailedReason(order12306Exception.getMessage());
                ResultEnum resultEnum = EnumUtils.getByCode(order12306Exception.getCode(), ResultEnum.class);
                ticketInfoEntity.setResultCode(resultEnum.getCode());
                ticketInfoEntity.setResultMessage(resultEnum.getMessage());
            } else {
                ticketInfoEntity.setFailedReason(ExceptionUtils.getStackTrace(e));
            }
            ticketInfoRepository.save(ticketInfoEntity);
        }
    }

    /**
     * 获取默认的OrderTicketDTO
     *
     * @return
     */
    private TicketInfoEntity getDefaultOrderTicketDTO() {
        TicketInfoEntity ticketInfoEntity = new TicketInfoEntity();
        ticketInfoEntity.setTrainDate("2019-07-01");
        ticketInfoEntity.setBackTrainDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd"));
        ticketInfoEntity.setStationTrain("D655");
        ticketInfoEntity.setFromStation("杭州");
        ticketInfoEntity.setToStation("宁波");
        ticketInfoEntity.setSeatType("二等座");
        ticketInfoEntity.setTicketPeople("周靖峰");
        ticketInfoEntity.setUsername("15158037019");
        ticketInfoEntity.setPassword("Zjf@1234");
        ticketInfoEntity.setEmail("908686171@qq.com");
        return ticketInfoEntity;
    }

    /**
     * 初始化mapSeatConf
     */
    private HashMap<String, Integer> getMapSeatConf() {
        HashMap<String, Integer> mapSeatConf = new HashMap<>();
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
        return mapSeatConf;
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
        ResultDTO resultDTO = objectMapper.readValue(responseString, new TypeReference<ResultDTO>() {
        });
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
        checkStatus(ResultEnum.OTN_LOGIN_CONF_ERROR, responseString);
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
        checkStatus(ResultEnum.OTN_INDEX12306_GET_LOGIN_BANNER_ERROR, responseString);
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
        String text = RegexUtils.getMatch(responseString, ".*\\((.*)\\)");
        ResultDTO resultDTO = checkResultCode(text, "0");
        return resultDTO.getImage();
    }

    private ResultDTO checkResultCode(String text, String rightResultCode) throws IOException {
        ResultDTO resultDTO = objectMapper.readValue(text, new TypeReference<ResultDTO>() {
        });
        if (! resultDTO.getResultCode().equalsIgnoreCase(rightResultCode)) {
            log.error(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultDTO));
            throw new Order12306Exception(ResultEnum.PASSPORT_CAPTCHA_CAPTCHA_IMAGE64_ERROR.getCode(), resultDTO.getResultMessage());
        }
        return resultDTO;
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
        ResultDTO resultDTO = objectMapper.readValue(responseString, new TypeReference<ResultDTO>() {
        });
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
        checkResultCode(responseString, "4");
    }

    private void passportWebLogin(String username, String password, String answer) throws URISyntaxException, IOException {
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

            CloseableHttpResponse response = httpclient.execute(httppost);
            String responseString = getResponse(response);
            if (!StringUtils.isEmpty(responseString)) {
                checkResultCode(responseString, "0");
                return;
            }

            // 把尝试登陆的次数+1，再存回数据库中
            ticketInfoEntity.setTryLoginTimes(ticketInfoEntity.getTryLoginTimes() + 1);
            ticketInfoRepository.save(ticketInfoEntity);
        }
        throw new Order12306Exception(ResultEnum.LOGIN_12306_ERROR);
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
        ResultDTO resultDTO = checkResultCode(responseString, "0");
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
        checkResultCode(responseString, "0");
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
        checkStatus(ResultEnum.OTN_INDEX_INITMY12306API_ERROR, responseString);
    }

    private List<String> otnLeftTicketInit() throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/leftTicket/init")
                .setParameter("linktypeid", "dc")
                .build();
        HttpGet httpget = new HttpGet(uri);

        CloseableHttpResponse response = httpclient.execute(httpget);
        String responseString = getResponse(response);
        String leftTicketQueryUrl = RegexUtils.getMatch(responseString, "var CLeftTicketUrl = '(.*)';");
        String stationVersionUrl = RegexUtils.getMatch(responseString, "<script .* src=\"(/otn/resources/js/framework/station_name.js\\?station_version=.*)\" .*</script>");
        return Arrays.asList(leftTicketQueryUrl, stationVersionUrl);
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
        HttpGet httpget = new HttpGet(uri);

        CloseableHttpResponse response = httpclient.execute(httpget);
        String responseString = getResponse(response);
        ResultDTO resultDTO = checkStatus(ResultEnum.OTN_LEFT_TICKET_QUERY_ERROR, responseString);

        return (ArrayNode) resultDTO.getData().get("result");
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
        getResponse(response);
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
        checkStatus(ResultEnum.OTN_LOGIN_CHECK_USER_ERROR, responseString);
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

        CloseableHttpResponse response = httpclient.execute(httppost);
        String responseString = getResponse(response);
        checkStatus(ResultEnum.OTN_CONFIRM_PASSENGER_CHECK_ORDER_INFO_ERROR, responseString);
    }

    private void otnLeftTicketSubmitOrderRequest(String secret, TicketInfoEntity ticketInfoEntity) throws URISyntaxException, IOException {
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

        CloseableHttpResponse response = httpclient.execute(httppost);
        String responseString = getResponse(response);
        checkStatus(ResultEnum.LEFT_TICKET_SUBMIT_ORDER_ERROR, responseString);
    }

    /**
     * 检测报文中的status字段是否为false，如果为false则要抛出Order12306Exception
     * @param resultEnum
     * @param responseString
     * @throws IOException
     */
    private ResultDTO checkStatus(ResultEnum resultEnum, String responseString) throws IOException {
        ResultDTO resultDTO = objectMapper.readValue(responseString, new TypeReference<ResultDTO>() {
        });
        if (resultDTO.getStatus().equalsIgnoreCase("false")) {
            log.error("{}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultDTO));
            String errmsg = getMessagesString(resultDTO.getMessages());
            throw new Order12306Exception(resultEnum.getCode(), errmsg);
        }
        return resultDTO;
    }

    /**
     * 解析12306报文中的messages字段，将其解析为字符串
     *
     * @param messages
     * @return
     */
    private String getMessagesString(JsonNode messages) {
        if (messages.isArray()) {
            ArrayNode arrayNode = (ArrayNode) messages;
            StringBuilder stringBuilder = new StringBuilder();
            for (JsonNode jsonNode : arrayNode) {
                stringBuilder.append(jsonNode.textValue());
                stringBuilder.append(",");
            }
            return stringBuilder.toString();
        } else {
            return messages.textValue();
        }
    }

    private List<String> otnConfirmPassengerInitDc() throws URISyntaxException, IOException {
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

        CloseableHttpResponse response = httpclient.execute(httppost);
        String responseString = getResponse(response);
        String globalRepeatSubmitToken = RegexUtils.getMatch(responseString, "var globalRepeatSubmitToken = '(.*)';");
        String ticketInfoForPassengerForm = RegexUtils.getMatch(responseString, "var ticketInfoForPassengerForm=(.*);");
        ticketInfoForPassengerForm = ticketInfoForPassengerForm.replaceAll("'", "\"");
        log.debug("globalRepeatSubmitToken = {}", globalRepeatSubmitToken);
        log.debug("ticketInfoForPassengerForm = {}", ticketInfoForPassengerForm);
        return Arrays.asList(globalRepeatSubmitToken, ticketInfoForPassengerForm);
    }

    private void otnConfirmPassengerGetQueueCount(JsonNode ticketInfoForPassengerFormNode, String repeatSubmitToken) throws URISyntaxException, IOException {
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

        CloseableHttpResponse response = httpclient.execute(httppost);
        String responseString = getResponse(response);
        checkStatus(ResultEnum.OTN_CONFIRM_PASSENGER_GET_QUEUE_COUNT_ERROR, responseString);
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

        CloseableHttpResponse response = httpclient.execute(httppost);
        String responseString = getResponse(response);
        checkStatus(ResultEnum.OTN_CONFIRM_PASSENGER_CONFIRM_SINGLE_FOR_QUEUE_ERROR, responseString);
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
                    .setParameter("REPEAT_SUBMIT_TOKEN", repeatSubmitToken)
                    .build();
            HttpGet httpget = new HttpGet(uri);

            CloseableHttpResponse response = httpclient.execute(httpget);
            String responseString = getResponse(response);
            ResultDTO resultDTO = checkStatus(ResultEnum.OTN_CONFIRM_PASSENGER_QUERY_ORDER_WAIT_TIME_ERROR, responseString);

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

        List<NameValuePair> formparams = new ArrayList<>();
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httppost.setEntity(entity);

        CloseableHttpResponse response = httpclient.execute(httppost);
        String responseString = getResponse(response);
        checkStatus(ResultEnum.OTN_CONFIRM_PASSENGER_RESULT_ORDER_FOR_DC_QUEUE_ERROR, responseString);
    }

    private NormalPassengerDTO otnConfirmPassengerGetPassengerDTOs(String repeatSubmitToken, TicketInfoEntity ticketInfoEntity) throws URISyntaxException, IOException {
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

        CloseableHttpResponse response = httpclient.execute(httppost);
        String responseString = getResponse(response);
        ResultDTO resultDTO = checkStatus(ResultEnum.OTN_CONFIRM_PASSENGER_GET_PASSENGER_DTOS_ERROR, responseString);

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

    private String getWantedTicketSecret(TicketInfoEntity ticketInfoEntity, ArrayNode arrayNode) throws IOException {

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
        passportWebLogin(ticketInfoEntity.getUsername(), ticketInfoEntity.getPassword(), check);
        otnLoginUserLogin();
        String newapptk = passportWebAuthUamtk();
        otnUamauthclient(newapptk);
        otnLoginUserLogin();
        otnLoginConf();
        otnIndexInitMy12306Api();
    }

    private String queryTicket(String leftTicketQueryUrl, Map<String, String> nameCodeMap) throws IOException, URISyntaxException {
        String trainDate = ticketInfoEntity.getTrainDate();
        String fromStationCode = nameCodeMap.get(ticketInfoEntity.getFromStation());
        String toStationCode = nameCodeMap.get(ticketInfoEntity.getToStation());
        while (true) {
            ArrayNode arrayNode = otnLeftTicketQuery(leftTicketQueryUrl, trainDate, fromStationCode, toStationCode);
            String secret = getWantedTicketSecret(ticketInfoEntity, arrayNode);
            if (StringUtils.isEmpty(secret)) {
                log.debug("目前{}无票，等待5s后重试", ticketInfoEntity.getStationTrain());
                // 将查询次数写入数据库中
                ticketInfoEntity.setTryLeftTicketTimes(ticketInfoEntity.getTryLeftTicketTimes() + 1);
                ticketInfoRepository.save(ticketInfoEntity);
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

    public void order() throws IOException, URISyntaxException, InterruptedException {
        login();
        List<String> otnLeftTicketInitResult = otnLeftTicketInit();
        String leftTicketQueryUrl = otnLeftTicketInitResult.get(0);
        String stationVersionUrl = otnLeftTicketInitResult.get(1);
        log.debug("leftTicketQueryUrl = {}", leftTicketQueryUrl);
        log.debug("stationVersionUrl = {}", stationVersionUrl);
        Map<String, String> nameCodeMap = otnResourcesJsFrameworkStationName(stationVersionUrl);
        log.debug("nameCodeMap = {}", nameCodeMap);
        otnPasscodeNewGetPassCodeNew();
        String secret = queryTicket(leftTicketQueryUrl, nameCodeMap);

        // 这里好像需要增加一个cookie
//        addJcSaveCookie(cookieStore, ticketInfoEntity, nameCodeMap);

        otnLoginCheckUser();
        otnLeftTicketSubmitOrderRequest(secret, ticketInfoEntity);
        List<String> otnConfirmPassengerInitDcResult = otnConfirmPassengerInitDc();
        String globalRepeatSubmitToken = otnConfirmPassengerInitDcResult.get(0);
        String ticketInfoForPassengerForm = otnConfirmPassengerInitDcResult.get(1);
        JsonNode ticketInfoForPassengerFormNode = objectMapper.readTree(ticketInfoForPassengerForm);
        NormalPassengerDTO normalPassengerDTO = otnConfirmPassengerGetPassengerDTOs(globalRepeatSubmitToken, ticketInfoEntity);
        otnPasscodeNewGetPassCodeNew();

        String passengerTicketStr = getPassengerTicketStr(ticketInfoForPassengerFormNode, normalPassengerDTO);
        String oldPassengerStr = getOldPassengerStr(normalPassengerDTO);
        otnConfirmPassengerCheckOrderInfo(globalRepeatSubmitToken, passengerTicketStr, oldPassengerStr);
        otnConfirmPassengerGetQueueCount(ticketInfoForPassengerFormNode, globalRepeatSubmitToken);

        TimeUnit.SECONDS.sleep(3);

        otnConfirmPassengerConfirmSingleForQueue(passengerTicketStr, oldPassengerStr, ticketInfoForPassengerFormNode, globalRepeatSubmitToken);
        String orderId = otnConfirmPassengerQueryOrderWaitTime(globalRepeatSubmitToken);
        otnConfirmPassengerResultOrderForDcQueue();

        if (!StringUtils.isEmpty(ticketInfoEntity.getEmail())) {
            sendEmail(orderId, ticketInfoEntity);
        }
    }

    private void sendEmail(String orderId, TicketInfoEntity ticketInfoEntity) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("908686171@qq.com");
        message.setTo(ticketInfoEntity.getEmail());
        message.setSubject("火车票抢票成功");
        // 恭喜您订票成功，订单号为：E898599288, 请立即打开浏览器登录12306，访问‘未完成订单’，在30分钟内完成支付!
        String text = String.format("%s，从%s到%s，列车%s，乘车人%s，已抢票成功，订单编号%s，请尽快登录12306支付购买",
                ticketInfoEntity.getTrainDate(),
                ticketInfoEntity.getFromStation(),
                ticketInfoEntity.getToStation(),
                ticketInfoEntity.getStationTrain(),
                ticketInfoEntity.getTicketPeople(),
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

    private void addJcSaveCookie(CookieStore cookieStore, TicketInfoEntity ticketInfoEntity, Map<String, String> nameCodeMap) {
        addCookie(cookieStore, "_jc_save_fromStation", string2Unicode(ticketInfoEntity.getFromStation()) + "%2C" + nameCodeMap.get(ticketInfoEntity.getFromStation()));
        addCookie(cookieStore, "_jc_save_toStation", string2Unicode(ticketInfoEntity.getToStation()) + "%2C" + nameCodeMap.get(ticketInfoEntity.getToStation()));
        addCookie(cookieStore, "_jc_save_fromDate", ticketInfoEntity.getTrainDate());
        addCookie(cookieStore, "_jc_save_toDate", ticketInfoEntity.getBackTrainDate());
        addCookie(cookieStore, "_jc_save_wfdc_flag", "dc");
    }

    private void addCookie(CookieStore cookieStore, String name, String value) {
        BasicClientCookie basicClientCookie = new BasicClientCookie(name, value);
        basicClientCookie.setDomain("kyfw.12306.cn");
        basicClientCookie.setPath("/");
        cookieStore.addCookie(basicClientCookie);
    }
}
