package com.hellozjf.learn.projects.order12306;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.hellozjf.learn.projects.order12306.constant.UriEnum;
import com.hellozjf.learn.projects.order12306.dto.*;
import com.hellozjf.learn.projects.order12306.service.CookieService;
import com.hellozjf.learn.projects.order12306.service.SendService;
import com.hellozjf.learn.projects.order12306.service.UriService;
import com.hellozjf.learn.projects.order12306.util.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class Order12306ApplicationTests {

    private HttpClient httpClient;
    private String username;
    private String password;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SendService sendService;

    @Autowired
    private CookieService cookieService;

    @Autowired
    @Qualifier("customCookieUri")
    private URI customCookieUri;

    @Autowired
    @Qualifier("site12306CookieUri")
    private URI site12306CookieUri;

    @Before
    public void before() {

//        System.setProperty("java.net.useSystemProxies", "true");      // 没用，必须写在启动命令里面
//        System.setProperty("http.proxyHost", "127.0.0.1");
//        System.setProperty("http.proxyPort", "8888");
//        System.setProperty("https.proxyHost", "127.0.0.1");
//        System.setProperty("https.proxyPort", "8888");

        CookieManager cookieManager = new CookieManager();
        this.httpClient = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .build();
        this.username = "15158037019";
        this.password = "Zjf@1234";
    }

    private String otnHttpZFGetJS() {
        Map<Object, Object> params = UriEnum.OTN_HTTPZF_GETJS.getParams();
        String result =
                sendService.send(httpClient,
                        UriEnum.OTN_HTTPZF_GETJS,
                        params);
        log.debug("result = {}", result);
        return result;
    }

    private ResultLogdeviceDTO otnHttpZFLogdevice() {
        Map<Object, Object> params = UriEnum.OTN_HTTPZF_LOGDEVICE.getParams();
        params.put("timestamp", TimeUtils.currentTimeMillisStr());
        ResultLogdeviceDTO resultLogdeviceDTO =
                sendService.send(httpClient,
                        UriEnum.OTN_HTTPZF_LOGDEVICE,
                        params,
                        ".*\\('(.*)'\\)",
                        new TypeReference<ResultLogdeviceDTO>() {
                        });
        log.debug("resultLogdeviceDTO = {}", resultLogdeviceDTO);
        HttpCookie railExpiration = new HttpCookie(CookieService.RAIL_EXPIRATION, resultLogdeviceDTO.getExp());
        railExpiration.setDomain(".12306.cn");
        railExpiration.setPath("/");
        railExpiration.setVersion(0);
        HttpCookie railDeviceid = new HttpCookie(CookieService.RAIL_DEVICEID, resultLogdeviceDTO.getDfp());
        railDeviceid.setDomain(".12306.cn");
        railDeviceid.setPath("/");
        railDeviceid.setVersion(0);
        cookieService.add(httpClient, site12306CookieUri, railExpiration);
        cookieService.add(httpClient, site12306CookieUri, railDeviceid);
        return resultLogdeviceDTO;
    }

    private String otnResourcesLoginHtml() {
        Map<Object, Object> params = UriEnum.OTN_RESOURCES_LOGIN_HTML.getParams();
        String result =
                sendService.send(httpClient,
                        UriEnum.OTN_RESOURCES_LOGIN_HTML,
                        params);
        log.debug("result = {}", result);
        return result;
    }

    private ResultNormalDTO<OtnLoginConfDataDTO> otnLoginConf() throws JsonProcessingException {
        Map<Object, Object> params = UriEnum.OTN_LOGIN_CONF.getParams();
        ResultNormalDTO<OtnLoginConfDataDTO> resultNormalDTO =
                sendService.send(httpClient,
                        UriEnum.OTN_LOGIN_CONF,
                        params,
                        new TypeReference<ResultNormalDTO<OtnLoginConfDataDTO>>() {
                        });
        log.debug("resultNormalDTO = {}",
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultNormalDTO));
        return resultNormalDTO;
    }

    private ResultNormalDTO<OtnIndex12306GetLoginBannerDataDTO> otnIndex12306GetLoginBanner() throws JsonProcessingException {
        Map<Object, Object> params = UriEnum.OTN_INDEX12306_GETLOGINBANNER.getParams();
        ResultNormalDTO<OtnIndex12306GetLoginBannerDataDTO> resultNormalDTO =
                sendService.send(httpClient,
                        UriEnum.OTN_INDEX12306_GETLOGINBANNER,
                        params,
                        new TypeReference<ResultNormalDTO<OtnIndex12306GetLoginBannerDataDTO>>() {
                        });
        log.debug("resultNormalDTO = {}",
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultNormalDTO));
        return resultNormalDTO;
    }

    private ResultUamtkStaticDTO passportWebAuthUamtkStatic() throws JsonProcessingException {
        List<HttpCookie> httpCookieList = cookieService.getAll(httpClient, site12306CookieUri);
        log.debug("httpCookieList = {}", httpCookieList);
        Map<Object, Object> params = UriEnum.PASSPORT_WEB_AUTH_UAMTK_STATIC.getParams();
        ResultUamtkStaticDTO resultUamtkStaticDTO =
                sendService.send(httpClient,
                        UriEnum.PASSPORT_WEB_AUTH_UAMTK_STATIC,
                        params,
                        new TypeReference<ResultUamtkStaticDTO>() {
                        });
        log.debug("resultUamtkStaticDTO = {}",
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultUamtkStaticDTO));
        return resultUamtkStaticDTO;
    }

    private ResultImageDTO passportCaptchaCaptchaImage64() throws JsonProcessingException {
        Map<Object, Object> params = UriEnum.PASSPORT_CAPTCHA_CAPTCHA_IMAGE64.getParams();
        params.put(TimeUtils.currentTimeMillisStr(), null);
        ResultImageDTO resultImageDTO =
                sendService.send(httpClient,
                        UriEnum.PASSPORT_CAPTCHA_CAPTCHA_IMAGE64,
                        params,
                        ".*\\((.*)\\)",
                        new TypeReference<ResultImageDTO>() {
                        });
        log.debug("resultImageDTO={}",
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultImageDTO));
        cookieService.add(httpClient, customCookieUri, CookieService.IMAGE, resultImageDTO.getImage());
        return resultImageDTO;
    }

    private AnswerDTO getAnswer() throws JsonProcessingException {
        String base64String = cookieService.get(httpClient, customCookieUri, CookieService.IMAGE);
        Map<Object, Object> params = UriEnum.GET_ANSWER.getParams();
        params.put("base64String", base64String);
        AnswerDTO answerDTO =
                sendService.send(httpClient,
                        UriEnum.GET_ANSWER,
                        params,
                        new TypeReference<AnswerDTO>() {
                        });
        log.debug("answerDTO={}",
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(answerDTO));
        cookieService.add(httpClient, customCookieUri, CookieService.ANSWER, answerDTO.getData());
        return answerDTO;
    }

    private ResultImageDTO passportCaptchaCaptchaCheck() throws JsonProcessingException {
        String answer = cookieService.get(httpClient, customCookieUri, CookieService.ANSWER);
        Map<Object, Object> params = UriEnum.PASSPORT_CAPTCHA_CAPTCHA_CHECK.getParams();
        params.put("answer", answer);
        params.put("_", TimeUtils.currentTimeMillisStr());
        ResultImageDTO resultImageDTO =
                sendService.send(httpClient,
                        UriEnum.PASSPORT_CAPTCHA_CAPTCHA_CHECK,
                        params,
                        ".*\\((.*)\\)",
                        new TypeReference<ResultImageDTO>() {
                        });
        log.debug("resultImageDTO={}",
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultImageDTO));
        return resultImageDTO;
    }

    private ResultLoginDTO passportWebLogin(int tryCount) throws IOException {
        String answer = cookieService.get(httpClient, customCookieUri, CookieService.ANSWER);
        Map<Object, Object> params = UriEnum.PASSPORT_WEB_LOGIN.getParams();
        params.put("username", username);
        params.put("password", password);
        params.put("answer", answer);
        ResultLoginDTO resultLoginDTO = null;
        for (int i = 0; i < tryCount; i++) {
            String result =
                    sendService.send(httpClient,
                            UriEnum.PASSPORT_WEB_LOGIN,
                            params);
            if (!StringUtils.isEmpty(result)) {
                resultLoginDTO = objectMapper.readValue(result, ResultLoginDTO.class);
                break;
            }

            // 空字符串，等待1秒后重试
            try {
                log.debug("{}没有任何返回，等待1秒重试", UriEnum.PASSPORT_WEB_LOGIN.getPath());
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                log.error("e = {}", e);
            }
        }
        log.debug("resultLoginDTO={}",
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultLoginDTO));
        return resultLoginDTO;
    }

    public String otnLoginUserLogin() {
        Map<Object, Object> params = UriEnum.OTN_LOGIN_USER_LOGIN.getParams();
        String result =
                sendService.send(httpClient,
                        UriEnum.OTN_LOGIN_USER_LOGIN,
                        params);
        log.debug("result={}", result);
        return result;
    }

    private String otnPassport() {
        Map<Object, Object> params = UriEnum.OTN_PASSPORT.getParams();
        String result =
                sendService.send(httpClient,
                        UriEnum.OTN_PASSPORT,
                        params);
        log.debug("result={}", result);
        return result;
    }

    private ResultLoginDTO passportWebAuthUamtk() throws JsonProcessingException {
        Map<Object, Object> params = UriEnum.PASSPORT_WEB_AUTH_UAMTK.getParams();
        ResultLoginDTO resultLoginDTO =
                sendService.send(httpClient,
                        UriEnum.PASSPORT_WEB_AUTH_UAMTK,
                        params,
                        new TypeReference<ResultLoginDTO>() {
                        });
        log.debug("resultLoginDTO={}",
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultLoginDTO));
        cookieService.add(httpClient, customCookieUri, CookieService.TK, resultLoginDTO.getNewapptk());
        return resultLoginDTO;
    }

    private ResultLoginDTO otnUamauthclient() throws JsonProcessingException {
        String tk = cookieService.get(httpClient, customCookieUri, CookieService.TK);
        Map<Object, Object> params = UriEnum.OTN_UAMAUTHCLIENT.getParams();
        params.put("tk", tk);
        ResultLoginDTO resultLoginDTO =
                sendService.send(httpClient,
                        UriEnum.OTN_UAMAUTHCLIENT,
                        params,
                        new TypeReference<ResultLoginDTO>() {
                        });
        log.debug("resultLoginDTO={}",
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultLoginDTO));
        return resultLoginDTO;
    }

    private void saveCookie(HttpClient httpClient, String username) {
        File parent = new File("cookie");
        parent.mkdirs();
        List<HttpCookie> httpCookieList = cookieService.getAll(httpClient, site12306CookieUri);
        File file = new File(parent, username);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            String str = objectMapper.writeValueAsString(httpCookieList);
            objectOutputStream.writeObject(str);
        } catch (Exception e) {
            log.error("e = {}", e);
        }
    }

    private void loadCookie(HttpClient httpClient, String username) {
        File parent = new File("cookie");
        parent.mkdirs();
        File file = new File(parent, username);
        if (!file.exists()) {
            return;
        }
        try (FileInputStream fileInputStream = new FileInputStream(file);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            String str = (String) objectInputStream.readObject();
            ArrayNode arrayNode = (ArrayNode) objectMapper.readTree(str);
            for (JsonNode jsonNode : arrayNode) {
                HttpCookie httpCookie = new HttpCookie(jsonNode.get("name").asText(), jsonNode.get("value").asText());
                httpCookie.setComment(jsonNode.get("comment").asText());
                httpCookie.setCommentURL(jsonNode.get("commentURL").asText());
                httpCookie.setDomain(jsonNode.get("domain").asText());
                httpCookie.setMaxAge(jsonNode.get("maxAge").asLong());
                httpCookie.setPath(jsonNode.get("path").asText());
                httpCookie.setPortlist(jsonNode.get("portlist").asText());
                httpCookie.setSecure(jsonNode.get("secure").booleanValue());
                httpCookie.setHttpOnly(jsonNode.get("httpOnly").booleanValue());
                httpCookie.setVersion(jsonNode.get("version").intValue());
                httpCookie.setDiscard(jsonNode.get("discard").booleanValue());
                cookieService.add(httpClient, site12306CookieUri, httpCookie);
            }
        } catch (Exception e) {
            log.error("e = {}", e);
        }
    }

    @Test
    public void testAll() throws Exception {

        otnHttpZFGetJS();
        otnHttpZFLogdevice();
        passportWebAuthUamtkStatic();
        otnLoginConf();
        otnIndex12306GetLoginBanner();
        passportWebAuthUamtkStatic();
        passportCaptchaCaptchaImage64();
        getAnswer();
        passportCaptchaCaptchaCheck();
        if (passportWebLogin(10) == null) {
            log.error("null");
            return;
        }
        otnLoginUserLogin();
        otnPassport();
        passportWebAuthUamtk();
        otnUamauthclient();
    }
}
