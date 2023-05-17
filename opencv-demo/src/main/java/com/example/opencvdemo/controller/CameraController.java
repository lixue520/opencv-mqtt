package com.example.opencvdemo.controller;

import com.example.opencvdemo.entity.bottleData;
import com.example.opencvdemo.mqtt.IMqttSender;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 简单的用于wifi传图接口
 * {
 *     "createtime":"时间戳",
 *     "deviceid":"0号机",
 *     "Level":"0.0%",
 *     "status":0,(0,1)
 * }
 */
@Controller
@RequestMapping("/cam")
public class CameraController {

    @Autowired
    IMqttSender mqttSender;

    @Autowired
    Gson gson;

    private static final Logger logger = LoggerFactory.getLogger(CameraController.class);

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestBody byte[] imageBytes) throws IOException {

        logger.info("--------kaishi--------");
        String filePath = "C://b2.png";  //上传服务器的时候记得改一下吧，发布的时候用注解来传。
        File file = new File(filePath);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(imageBytes);
        fos.close();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String s = now.format(formatter);

        bottleData payload = new bottleData();//设置载荷

        payload.setCreatetime(s);
        payload.setStatus(0);
        payload.setDeviceid("0号机");
        payload.setLevel((float) 0.01);

        mqttSender.sendToMqtt("smartwater/sub",gson.toJson(payload));//
        logger.info("Image uploaded successfully");
        return new ResponseEntity<>("Image uploaded successfully", HttpStatus.OK);
    }

}

