package com.example.opencvdemo.controller;

import com.example.opencvdemo.entity.bottleData;
import com.example.opencvdemo.mqtt.IMqttSender;
import com.example.opencvdemo.utils.opencv_tool;
import com.google.gson.Gson;

import org.opencv.core.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @version 1.0
 * @Author qin
 * @Date 2023/4/5 23:26
 */
@RestController
@RequestMapping("/opencv")
public class Opencv_controller {

    private static final Logger logger = LoggerFactory.getLogger(Opencv_controller.class);

    @Autowired
    IMqttSender mqttSender;

    @Autowired
    Gson gson;
    String k = "C:\\Users\\Administrator\\Desktop\\java_pj\\";
    //String path_server="C:\\Users\\qin\\Desktop\\imge\\";
    String path_server = k;

    @PostMapping(value = "/video-stream", produces = "image/jpeg")
    ResponseEntity<byte[]> opencv_send(HttpServletRequest request, @RequestBody byte[] data) throws Exception {
        String filename = "b7.jpg";
        // FileOutputStream outputStream = new FileOutputStream("src/main/resources/static/" + filename);
        FileOutputStream outputStream = new FileOutputStream(path_server + filename);
        outputStream.write(data);
        outputStream.close();
        Path path = Paths.get(path_server + "b7.jpg");
        byte[] img = Files.readAllBytes(path);

        HttpHeaders headers = new HttpHeaders();
        bottleData payload = new bottleData();
        logger.info("ESP32-CAM send jpeg is ok!");
        /**
         * id:1900800724
         * status:1/0/2  ;1满了，0是没满，2是异常
         */
        payload.setLevel(0);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String s = now.format(formatter);
        payload.setCreatetime(s);
        payload.setDeviceid("1900800724");
        payload.setStatus(0);

        mqttSender.sendToMqtt("smartwater/opencv/send", gson.toJson(payload));
        return new ResponseEntity(img, headers, HttpStatus.OK);
    }

    @GetMapping("/src")
    ResponseEntity<byte[]> opencv_src() throws Exception {
        Path path = Paths.get(path_server + "b7.jpg");
        byte[] img = Files.readAllBytes(path);
        logger.info("<====get src====>");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        bottleData payload = new bottleData();
        mqttSender.sendToMqtt("smartwater/opencv/src", gson.toJson(payload));
        return new ResponseEntity(img, headers, HttpStatus.OK);
    }


    @GetMapping("/end")
    ResponseEntity<byte[]> opencv_make() throws Exception {
        // 加载动态库
        {
            System.loadLibrary(
                    Core.NATIVE_LIBRARY_NAME
            );
        }
        bottleData payload;
        //imshow("Original Image", image);
        opencv_tool k = new opencv_tool();
        payload = k.test_opencv();
        payload.setDeviceid("1900800724");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String s = now.format(formatter);
        payload.setCreatetime(s);
        Path path = Paths.get(path_server + "output12.jpg");
        byte[] img = Files.readAllBytes(path);
        logger.info("<====opencv finished====>");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        mqttSender.sendToMqtt("smartwater/opencv/end", gson.toJson(payload));
        return new ResponseEntity(img, headers, HttpStatus.OK);
    }

}
