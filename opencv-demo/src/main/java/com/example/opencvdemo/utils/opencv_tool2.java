package com.example.opencvdemo.utils;

import com.example.opencvdemo.entity.bottleData;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @Author qin
 * @Date 2023/4/20 0:31
 */
public class opencv_tool2 {
    private static final Logger logger = LoggerFactory.getLogger(opencv_tool.class);
    //String path_server="C:\\Users\\qin\\Desktop\\imge\\";
    String k = "C:\\Users\\Administrator\\Desktop\\java_pj\\";
    String path_server = k;

    // 加载动态库
    static {
        System.loadLibrary(
                Core.NATIVE_LIBRARY_NAME
        );
    }

    public static void main(String[] args) {
        opencv_tool2 k = new opencv_tool2();
        k.test_tool();

    }


    public void test_tool(){
        //1.获取图片
        String pat="src/main/resources/static/bb7.jpg";
        //Mat src = Imgcodecs.imread(path_server+"b7.jpeg");
        Mat src = Imgcodecs.imread(pat);
        HighGui.imshow("src", src);
        HighGui.waitKey(0);
        //2.图片剪切
        Rect roi = new Rect(40,20,200,200);//定义矩形区域
        Mat cropped = new Mat(src,roi);//剪切
        HighGui.imshow("cropped", cropped);
        HighGui.waitKey(0);
        //3.灰度
        Mat gray = new Mat();
        Imgproc.cvtColor(cropped, gray, Imgproc.COLOR_BGR2GRAY);//转为灰度图
        HighGui.imshow("gray",gray);
        HighGui.waitKey(0);

        //4.液位
        // 读取图像
        Mat image = src;

        // 高斯滤波
        Imgproc.GaussianBlur(image, image, new Size(3, 3), 0);

        // Sobel算子
        Mat gray1 = new Mat();
        Imgproc.cvtColor(image, gray1, Imgproc.COLOR_BGR2GRAY);
        Mat gradX = new Mat();
        Imgproc.Sobel(gray1, gradX, CvType.CV_32F, 1, 0);
        Core.convertScaleAbs(gradX, gradX);
        Mat gradY = new Mat();
        Imgproc.Sobel(gray1, gradY, CvType.CV_32F, 0, 1);
        Core.convertScaleAbs(gradY, gradY);
        Mat edges = new Mat();
        Core.addWeighted(gradX, 0.5, gradY, 0.5, 0, edges);

        // 双阈值处理和边缘连接
        Imgproc.Canny(edges, edges, 50, 150);
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.dilate(edges, edges, kernel);
        Imgproc.erode(edges, edges, kernel);

        // 创建掩膜
        Mat mask = new Mat(image.size(), CvType.CV_8UC1, new Scalar(0));
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(edges, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.drawContours(mask, contours, -1, new Scalar(255), -1);

        // 应用掩膜
        Mat result = new Mat();
        image.copyTo(result, mask);

        // 计算液位百分比
        gray = new Mat();
        Imgproc.cvtColor(result, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(gray, gray, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
        contours.clear();
        Imgproc.findContours(gray, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        double maxArea = 0;
        Rect maxRect = new Rect();
        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            double area = Imgproc.contourArea(contour);
            if (area > maxArea && rect.width > rect.height) {
                maxArea = area;
                maxRect = rect;
            }
        }
        double liquidLevel = (maxRect.y + maxRect.height) * 100.0 / result.rows();

        // 显示结果
        Imgcodecs.imwrite("result.jpg", result);
        System.out.println("液位百分比：" + liquidLevel + "%");



    }
    private static double angle(Point pt1, Point pt2, Point pt0) {
        double dx1 = pt1.x - pt0.x;
        double dy1 = pt1.y - pt0.y;
        double dx2 = pt2.x - pt0.x;
        double dy2 = pt2.y - pt0.y;
        return (dx1 * dx2 + dy1 * dy2) / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
    }

    private Mat doBackgroundRemoval(Mat frame)
    {
        // init
        Mat hsvImg = new Mat();
        List<Mat> hsvPlanes = new ArrayList<>();
        Mat thresholdImg = new Mat();
        int thresh_type = Imgproc.THRESH_BINARY_INV;
// threshold the image with the average hue value
        hsvImg.create(frame.size(), CvType.CV_8U);
        Imgproc.cvtColor(frame, hsvImg,
                Imgproc.COLOR_BGR2HSV);
        Core.split(hsvImg, hsvPlanes);

        // get the average hue value of the image
        Scalar average=Core.mean(hsvPlanes.get(0));
        double threshValue =average.val[0];
        Imgproc.threshold(hsvPlanes.get(0), thresholdImg, threshValue, 179.0, thresh_type);

        Imgproc.blur(thresholdImg, thresholdImg, new Size(5, 5));
        // dilate to fill gaps, erode to smooth edges
        Imgproc.dilate(thresholdImg, thresholdImg, new Mat(), new Point(-1, -1), 1);

        Imgproc.erode(thresholdImg, thresholdImg, new Mat(), new Point(-1, -1), 3);
        Imgproc.threshold(thresholdImg, thresholdImg, threshValue, 179.0, Imgproc.THRESH_BINARY);

        // create the new image
        Mat foreground = new Mat(frame.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
        thresholdImg.convertTo(thresholdImg,CvType.CV_8U);
        frame.copyTo(foreground, thresholdImg);//掩膜图像复制
        return foreground;
    }

    public void tool() {
        bottleData SendData = new bottleData();
        /**
         * 1.第一步：拿到图片先读取缩放然后转换成灰度图
         */

        String pat = "src/main/resources/static/k.jpg";
        //Mat src = Imgcodecs.imread(path_server+"b7.jpeg");
        Mat src = Imgcodecs.imread(pat);

        Mat grayImage = new Mat();
        Imgproc.cvtColor(src, grayImage, Imgproc.COLOR_BGR2GRAY);//转为灰度图
        HighGui.imshow("gray", grayImage);
        HighGui.waitKey(0);
        logger.info("1.Turn gray");

        // 读取图像
        Mat image = Imgcodecs.imread(pat);

        // 读取模板图像和待匹配图像
        Mat templateImage = Imgcodecs.imread("src/main/resources/static/temp1.jpg");
        Mat targetImage = Imgcodecs.imread("src/main/resources/static/ppt.jpg");

        // 转换为灰度图像
        Mat gray1 = new Mat();
        Imgproc.cvtColor(templateImage, gray1, Imgproc.COLOR_BGR2GRAY);
        Mat gray2 = new Mat();
        Imgproc.cvtColor(targetImage, gray2, Imgproc.COLOR_BGR2GRAY);

        // 进行模板匹配
        Mat result = new Mat();
        Imgproc.matchTemplate(gray2, gray1, result, Imgproc.TM_CCOEFF_NORMED);

        // 找到最大匹配位置
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        Point maxLoc = mmr.maxLoc;

        // 绘制匹配结果
        Mat drawing = new Mat();
        targetImage.copyTo(drawing);
        Imgproc.rectangle(drawing, maxLoc, new Point(maxLoc.x + templateImage.cols(), maxLoc.y + templateImage.rows()), new Scalar(0, 255, 0), 2);

        // 抠出瓶子
        Rect roi = new Rect(maxLoc, new Size(templateImage.cols(), templateImage.rows()));
        Mat bottle = new Mat(targetImage, roi);

        // 显示结果
        Imgcodecs.imwrite("result.jpg", drawing);
        Imgcodecs.imwrite("bottle.jpg", bottle);
    }

}


