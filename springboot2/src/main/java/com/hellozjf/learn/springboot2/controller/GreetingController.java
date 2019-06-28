package com.hellozjf.learn.springboot2.controller;

import com.hellozjf.learn.springboot2.dto.GreetingDTO;
import com.hellozjf.learn.springboot2.dto.HelloMessageDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

/**
 * @author Jingfeng Zhou
 */
@Controller
public class GreetingController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public GreetingDTO greeting(HelloMessageDTO message) throws Exception {
        // simulated delay
        Thread.sleep(1000);
        return new GreetingDTO("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }
}
