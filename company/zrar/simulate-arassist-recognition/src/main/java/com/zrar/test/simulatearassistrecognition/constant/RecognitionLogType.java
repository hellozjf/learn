package com.zrar.test.simulatearassistrecognition.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Jingfeng Zhou
 */
@AllArgsConstructor
@Getter
public enum RecognitionLogType {

    CALL_UP_ARASSIST(1, "座席辅助来电"),
    CALL_DOWN_ARASSIST(2, "座席辅助挂机"),
    CALL_UP_WEBSOCKET_CLIENT(3, "Websocket客户端来电"),
    CALL_DOWN_WEBSOCKET_CLIENT(4, "Websocket客户端挂机"),
    MESSAGE(5, "识别并发送消息"),
    ;

    private int code;
    private String desc;
}
