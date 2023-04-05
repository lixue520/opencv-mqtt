package com.example.opencvdemo.controller;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.opencv.core.Core;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2GRAY;
import static org.opencv.imgproc.Imgproc.cvtColor;

/**
 * @version 1.0
 * @Author qin
 * @Date 2023/4/5 16:19
 */
@RestController
public class hello {

    @GetMapping("test")
    ResponseEntity<byte[]> hello() throws Exception {
        // 加载动态库
        {
            System.loadLibrary(
                    Core.NATIVE_LIBRARY_NAME
            );
        }

        // 读取图像
        Mat image = imread("C:\\cat.png");
        if (image.empty()) {
            throw new Exception("image is empty");
        }
        //imshow("Original Image", image);

        // 创建输出单通道图像
        Mat grayImage = new Mat(image.rows(), image.cols(), CvType.CV_8SC1);
        // 进行图像色彩空间转换
        cvtColor(image, grayImage, COLOR_RGB2GRAY);
        Mat edges = new Mat();

        //Imgproc.morphologyEx(gs,dst,Imgproc.MORPH_GRADIENT,new Mat(),new Point(-1,-1),3);
        Imgproc.Canny(grayImage, edges, 10, 85);//Canny边缘检测

        //imshow("Processed Image", grayImage);
        imwrite("hello.jpg", edges);
        Path path = Paths.get("hello.jpg");
        byte[] img = Files.readAllBytes(path);
        System.out.println("shit");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity(img,headers, HttpStatus.OK);
    }

    @GetMapping("src")
    ResponseEntity<byte[]> opencv_yuantu() throws Exception {
        // 加载动态库
        {
            System.loadLibrary(
                    Core.NATIVE_LIBRARY_NAME
            );
        }

        // 读取图像
        Mat image = imread("C:\\cat.png");
        if (image.empty()) {
            throw new Exception("image is empty");
        }
        //imshow("Original Image", image);

        Path path = Paths.get("hello.jpg");
        byte[] img = Files.readAllBytes(path);
        System.out.println("shit");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity(img,headers, HttpStatus.OK);
    }




}
