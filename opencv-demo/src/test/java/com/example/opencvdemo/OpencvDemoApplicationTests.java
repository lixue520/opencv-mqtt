package com.example.opencvdemo;

import com.example.opencvdemo.controller.MqttController;
import com.example.opencvdemo.mqtt.IMqttSender;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OpencvDemoApplicationTests {
    private static final Logger logger = LoggerFactory.getLogger(MqttController.class);
    @Autowired
    IMqttSender mqttSender;

    @Autowired
    Gson gson;

    //这个是外面配置文件里面的设置的接收主题之一
    private final static String SEND_TOPIC_PREFIX = "receive_iot_topic/";

    @Test
    void contextLoads() {

    }

}
