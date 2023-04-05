package com.example.opencvdemo.controller;


import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.io.IOException;


//1.功能1,由于每次上传视频流都保存成video-stream.jpeg。这样就可以动态调用opencv用来做图像处理。
//2.功能2,接收来自ESP32-CAM的视频流并合成mp4在后台动态保存，但是要加定时任务，这是为了避免云服务器资源被占用
@RestController
public class VideoController {


    @PostMapping(value = "/video-stream",produces = "image/jpeg")
    public byte[] saveVideoStream(HttpServletRequest request, @RequestBody byte[] data) throws IOException {
        // Save the uploaded file to the file system
        // 加载动态库
        {
            System.loadLibrary(
                    Core.NATIVE_LIBRARY_NAME
            );
        }
        String filename = "video-stream.jpeg";
        FileOutputStream outputStream = new FileOutputStream("src/main/resources/static" + filename);
        outputStream.write(data);
        outputStream.close();


        // Read the JPEG image from the request body
        ServletInputStream inputStream = request.getInputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            // Process the incoming data in the buffer
        }
        inputStream.close();

        Mat img = Imgcodecs.imread("src/main/resources/static"+ filename);

        System.out.println("ok");
        return new byte[0];

    }


}