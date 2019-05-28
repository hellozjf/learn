package com.hellozjf.learn.projects.order12306.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hellozjf
 */
@Getter
@AllArgsConstructor
public enum UriEnum {

    INDEX(HttpMethod.GET, "https", "www.12306.cn", -1,
            "/index/",
            index()),
    INDEX_FONTS_ICONFONT(HttpMethod.GET, "https", "www.12306.cn", -1,
            "/index/fonts/iconfont.ttf",
            indexFontsIconfontTtf()),
    INDEX_OTN_INDEX12306_GETBANNER(HttpMethod.GET, "https", "www.12306.cn", -1,
            "/index/otn/index12306/getBanner",
            indexOtnIndex12306GetBanner()),
    OTN_HTTPZF_GETJS(HttpMethod.GET, "https", "kyfw.12306.cn", -1,
            "/otn/HttpZF/GetJS",
            otnHttpzfGetjs()),
    OTN_HTTPZF_LOGDEVICE(HttpMethod.GET, "https", "kyfw.12306.cn", -1,
            "/otn/HttpZF/logdevice",
            otnHttpZFLogdevice()),
    OTN_RESOURCES_LOGIN_HTML(HttpMethod.GET, "https", "kyfw.12306.cn", -1,
            "/otn/resources/login.html",
            otnResourcesLoginHtml()),
    OTN_LOGIN_CONF(HttpMethod.POST, "https", "kyfw.12306.cn", -1,
            "/otn/login/conf",
            otnLoginConfMap()),
    OTN_INDEX12306_GETLOGINBANNER(HttpMethod.GET, "https", "kyfw.12306.cn", -1,
            "/otn/index12306/getLoginBanner",
            otnIndex12306GetloginbannerMap()),
    PASSPORT_WEB_AUTH_UAMTK_STATIC(HttpMethod.POST, "https", "kyfw.12306.cn", -1,
            "/passport/web/auth/uamtk-static",
            passportWebAuthUamtkStaticMap()),
    PASSPORT_CAPTCHA_CAPTCHA_IMAGE64(HttpMethod.GET, "https", "kyfw.12306.cn",-1,
            "/passport/captcha/captcha-image64",
            passportCaptchaCaptchaImage64Map()),
    GET_ANSWER(HttpMethod.POST, "http", "aliyun.hellozjf.com", 12306,
            "/result/base64",
            getAnswerMap()),
    PASSPORT_CAPTCHA_CAPTCHA_CHECK(HttpMethod.GET, "https", "kyfw.12306.cn", -1,
            "/passport/captcha/captcha-check",
            getPassportCaptchaCaptchaCheckMap()),
    PASSPORT_WEB_LOGIN(HttpMethod.POST, "https", "kyfw.12306.cn", -1,
            "/passport/web/login",
            getPassportWebLoginMap()),
    OTN_LOGIN_USER_LOGIN(HttpMethod.GET, "https", "kyfw.12306.cn", -1,
            "/otn/login/userLogin",
            getOtnLoginUserLoginMap()),
    OTN_PASSPORT(HttpMethod.GET, "https", "kyfw.12306.cn", -1,
            "/otn/passport",
            getOtnPassportMap()),
    PASSPORT_WEB_AUTH_UAMTK(HttpMethod.POST, "https", "kyfw.12306.cn", -1,
            "/passport/web/auth/uamtk",
            getPassportWebAuthUamtkMap()),
    OTN_UAMAUTHCLIENT(HttpMethod.POST, "https", "kyfw.12306.cn", -1,
            "/otn/uamauthclient",
            getOtnUamauthclientMap()),
    ;

    private static Map<Object, Object> indexOtnIndex12306GetBanner() {
        Map<Object, Object> params = new HashMap<>();
        return params;
    }

    private static Map<Object, Object> indexFontsIconfontTtf() {
        Map<Object, Object> params = new HashMap<>();
        params.put("t", null);
        return params;
    }

    private static Map<Object, Object> index() {
        Map<Object, Object> params = new HashMap<>();
        return params;
    }

    private static Map<Object, Object> otnResourcesLoginHtml() {
        Map<Object, Object> params = new HashMap<>();
        return params;
    }

    private static Map<Object, Object> otnHttpzfGetjs() {
        Map<Object, Object> params = new HashMap<>();
        return params;
    }

    private static Map<Object, Object> otnHttpZFLogdevice() {
        Map<Object, Object> params = new HashMap<>();
        params.put("algID", "koEzGW9FGs");
        params.put("hashCode", "O4LMWxq7Du6CWAlC1synsWdkPewMb4kDFZuwCHivU7w");
        params.put("FMQw", "1");
        params.put("q4f3", "zh-CN");
        params.put("VPIf", "1");
        params.put("custID", "133");
        params.put("VEek", "unknown");
        params.put("dzuS", "0");
        params.put("yD16", "0");
        params.put("EOQP", "4902a61a235fbb59700072139347967d");
        params.put("lEnu", "3232243468");
        params.put("jp76", "52d67b2a5aa5e031084733d5006cc664");
        params.put("hAqN", "Win32");
        params.put("platform", "WEB");
        params.put("ks0Q", "d22ca0b81584fbea62237b14bd04c866");
        params.put("TeRS", "824x1536");
        params.put("tOHY", "24xx864x1536");
        params.put("Fvje", "i1l1o1s1");
        params.put("q5aJ", "-8");
        params.put("wNLf", "99115dfb07133750ba677d055874de87");
        params.put("0aew", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
        params.put("E3gR", "f261c349945f631297a54071aab69cc8");
        params.put("timestamp", null);
        return params;
    }

    private static Map<Object, Object> otnLoginConfMap() {
        Map<Object, Object> params = new HashMap<>();
        return params;
    }

    private static Map<Object, Object> otnIndex12306GetloginbannerMap() {
        Map<Object, Object> params = new HashMap<>();
        return params;
    }

    private static Map<Object, Object> passportWebAuthUamtkStaticMap() {
        Map<Object, Object> params = new HashMap<>();
        params.put("appid", "otn");
        return params;
    }

    private static Map<Object, Object> passportCaptchaCaptchaImage64Map() {
        Map<Object, Object> params = new HashMap<>();
        params.put("login_site", "E");
        params.put("module", "login");
        params.put("rand", "sjrand");
        params.put("callback", "jQuery191027188156577876876_1558182495381");
        params.put("_", "1558182495382");
        // 记得再put一个key为毫秒，value为null的东西
        return params;
    }

    private static Map<Object, Object> getAnswerMap() {
        Map<Object, Object> params = new HashMap<>();
        params.put("base64String", "");
        return params;
    }

    private static Map<Object, Object> getPassportCaptchaCaptchaCheckMap() {
        Map<Object, Object> params = new HashMap<>();
        params.put("callback", "jQuery191027188156577876876_1558182495381");
        params.put("answer", "");
        params.put("rand", "sjrand");
        params.put("login_site", "E");
        params.put("_", "1558182495383");
        return params;
    }

    private static Map<Object, Object> getPassportWebLoginMap() {
        Map<Object, Object> params = new HashMap<>();
        params.put("username", "");
        params.put("password", "");
        params.put("appid", "otn");
        params.put("answer", "");
        return params;
    }

    private static Map<Object, Object> getOtnLoginUserLoginMap() {
        Map<Object, Object> params = new HashMap<>();
        return params;
    }

    private static Map<Object, Object> getOtnPassportMap() {
        Map<Object, Object> params = new HashMap<>();
        params.put("redirect", "/otn/login/userLogin");
        return params;
    }

    private static Map<Object, Object> getPassportWebAuthUamtkMap() {
        Map<Object, Object> params = new HashMap<>();
        params.put("appid", "otn");
        return params;
    }

    private static Map<Object, Object> getOtnUamauthclientMap() {
        Map<Object, Object> params = new HashMap<>();
        params.put("tk", "c9g0-ECRWXRzrVzAM9mty3IEo020ratnKtn-76pLPh4ij1110");
        return params;
    }

    HttpMethod httpMethod;
    String scheme;
    String host;
    int port;
    String path;
    Map<Object, Object> params;

}
