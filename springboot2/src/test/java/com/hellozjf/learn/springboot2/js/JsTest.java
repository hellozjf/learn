package com.hellozjf.learn.springboot2.js;

import com.hellozjf.learn.springboot2.util.CharacterPinYinConvert;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

/**
 * @author Jingfeng Zhou
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class JsTest {

    @Test
    public void eval() throws ScriptException, URISyntaxException, IOException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        String url = "https://cdn.bootcss.com/Mock.js/1.0.1-beta3/mock-min.js";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        URI uri = new URI(url);
        HttpGet httpGet = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity httpEntity = response.getEntity();
        String scriptString = null;
        if (httpEntity != null) {
            scriptString = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
        }
        engine.eval(scriptString);
        Object eval = engine.eval("Mock.Random.cname()");
        System.out.println(eval);

        CharacterPinYinConvert characterPinYinConvert = new CharacterPinYinConvert();
        String pinyin = characterPinYinConvert.toPinYin(eval.toString());
        System.out.println(pinyin);
    }
}
