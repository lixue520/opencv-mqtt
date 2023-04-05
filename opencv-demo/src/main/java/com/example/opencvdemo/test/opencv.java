package com.example.opencvdemo.test;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.waitKey;
import static org.opencv.imgproc.Imgproc.LINE_8;

/**
 * @version 1.0
 * @Author qin
 * @Date 2023/4/5 21:13
 */
public class opencv {
    public static void main(String[] args) {
        {
            System.loadLibrary(
                    Core.NATIVE_LIBRARY_NAME
            );
        }
        //1.读取图片
        String path="C:\\b7.jpeg";
        Mat img= Imgcodecs.imread(path);
        //2.指定图像写到指定路径
        String savePath = "new_b7.png";
        Imgcodecs.imwrite(savePath,img);
        //3.图像灰度化
        Mat gray = new Mat();
        Imgproc.cvtColor(
                img,gray,Imgproc.COLOR_BGR2GRAY
        );
        imshow("gray",gray);
        waitKey(0);
        //4.图像二值化
        /*高斯滤波 */
        Mat blurImg = new Mat();
        Imgproc.GaussianBlur(
                gray,
                blurImg,
                new Size(3,3),2,2
        );
        /**
         * 使用自适应移动平均阈值法
         * 继续对图像进行黑白二值化处理
         */
        Mat binaryImg = new Mat();

        Imgproc.adaptiveThreshold(
                blurImg,
                binaryImg,
                255,
                Imgproc.ADAPTIVE_THRESH_MEAN_C,
                Imgproc.THRESH_BINARY,
                45,
                11
        );
        imshow("二值化",binaryImg);
        waitKey(0);

        /**
         * Canny边沿检测
         */
        //5.Canny边沿检测

        Mat cannyImg = new Mat();
        Imgproc.Canny(
                binaryImg,
                cannyImg,
                20,
                60,
                3,false
        );
        imshow("Canny",cannyImg);
        waitKey(0);

        //6.膨胀增强边沿
        Mat dilateImg = new Mat();
        Imgproc.dilate(
                cannyImg,
                dilateImg,
                new Mat(),
                new Point(-1,-1),
                3, 1,
                new Scalar(1));
        imshow("膨胀增强边沿",dilateImg);
        waitKey(0);

        //7.轮廓查找
        List<MatOfPoint> contours = new ArrayList();
        Mat hierarchy = new Mat();

        Imgproc.findContours(
                binaryImg,
                contours,
                hierarchy,
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE
        );

/**
 * 从所有轮廓中找到最大的轮廓
 */
        int maxIdx = 0;
        double maxSize = 0;
        for (int i = 0; i < contours.size(); i++) {
            double size = Imgproc.contourArea(
                    contours.get(i)
            );
            if(maxSize < size) {
                maxIdx = i;
                maxSize = size;
            }
        }

        MatOfPoint maxContour = contours.get(maxIdx);

/**
 * 将最大的轮廓绘制在原始图片上
 */
        Mat imgCopy = img.clone();
        Imgproc.drawContours(
                imgCopy,
                contours,
                maxIdx,
                new Scalar(0, 0, 255),
                4,
                LINE_8
        );
        imshow("查找轮廓",imgCopy);
        waitKey(0);


        /**
         * 找到轮廓的外接矩形
         */
        Rect rect = Imgproc.boundingRect(maxContour);

/**
 * 在原图上绘制出外接矩形
 */
        Mat rectImg = img.clone();
        Imgproc.rectangle(
                rectImg,
                rect,
                new Scalar(0, 0, 255),
                2,
                Imgproc.LINE_8
        );

        imshow("矩形",rectImg);
        waitKey(0);





    }
}
