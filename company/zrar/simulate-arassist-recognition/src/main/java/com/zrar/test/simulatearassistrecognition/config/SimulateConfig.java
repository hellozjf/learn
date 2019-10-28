package com.zrar.test.simulatearassistrecognition.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Jingfeng Zhou
 */
@Component
@ConfigurationProperties(prefix = "simulate.arassist")
@Data
public class SimulateConfig {

    private WebsocketClient websocketClient;
    private Recognition recognition;

    @Data
    public static class WebsocketClient {
        private String callUp;
        private String callDown;
    }

    @Data
    public static class Recognition {
        private Integer callCount;
        private Integer minNewCallInterval;
        private Integer maxNewCallInterval;
        private Call call;

        @Data
        public static class Call {
            private Integer minThreadCount;
            private Integer maxThreadCount;
            private Integer minThreadInterval;
            private Integer maxThreadInterval;
            private Integer minRecognitionInterval;
            private Integer maxRecognitionInterval;
            private String systemCallNumber;
            private String minTaxPayerCallNumber;
            private String maxTaxPayerCallNumber;
            private String minCsadCallNumber;
            private String maxCsadCallNumber;
        }
    }
}
