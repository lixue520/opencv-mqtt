package com.example.opencvdemo.controller;

import com.example.opencvdemo.entity.IotData;
import com.example.opencvdemo.mqtt.ApiResult;
import com.example.opencvdemo.mqtt.IMqttSender;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version 1.0
 * @Author qin
 * @Date 2023/3/12 0:57
 */

@RestController
@RequestMapping("/mqtt")
public class MqttController {


    private static final Logger logger = LoggerFactory.getLogger(MqttController.class);
    @Autowired
    IMqttSender mqttSender;

    @Autowired
    Gson gson;

    //这个是外面配置文件里面的设置的接收主题之一
    private final static String SEND_TOPIC_PREFIX = "receive_iot_topic/";


    @PostMapping("/sendToTopic")
    public ApiResult sendToTopic(String topic, String payload) {
        /**
         * 想接收方方法消息-主题：receive_iot_topic/#,receive_chat_topic/#
         */
        mqttSender.sendToMqtt(topic,payload);
        logger.info("send success=>" + "topic:" + topic + "payload:" + payload);
        return ApiResult.success(null, "mqtt send ok");
    }


    /**
     * 127.0.0.1:8082/mqtt/control_command
     * post、json
     * {
     *   "createtime": "2023-04-01T07:02:23.707Z",
     *   "deviceid": "001ABC",
     *   "humi": 39,
     *   "light": 11,
     *   "loraid": "rd001",
     *   "sensorid": "123456789",
     *   "temp": 100,
     *   "types": "esp32"
     * }
     */

    @PostMapping("/control_command")
    public ApiResult controlCommand(@RequestBody IotData iotData) {
        String deviceId = iotData.getDeviceid();
        // 前缀 + 设备号
        String topic = SEND_TOPIC_PREFIX + deviceId;
        String payload=gson.toJson(iotData);
        mqttSender.sendToMqtt( topic,payload);
        logger.info("send success=>" + "topic:" + topic + "payload:" + payload);
        return ApiResult.success(null, "发送成功");
    }


}

