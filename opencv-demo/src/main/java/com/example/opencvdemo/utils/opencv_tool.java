package com.example.opencvdemo.utils;

import com.example.opencvdemo.controller.Opencv_controller;
import com.example.opencvdemo.entity.bottleData;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @Author qin
 * @Date 2023/4/5 23:29
 * 图像处理算法
 */
public class opencv_tool {
    private static final Logger logger = LoggerFactory.getLogger(opencv_tool.class);
    // 加载动态库
    static {
        System.loadLibrary(
                Core.NATIVE_LIBRARY_NAME
        );
    }
    public static void main(String[] args) {
        opencv_tool k = new opencv_tool();
        k.test_opencv();

    }



    public bottleData test_opencv(){

        bottleData SendData = new bottleData();
        /**
         * 1.第一步：拿到图片先读取缩放然后转换成灰度图
         */


        Mat src = Imgcodecs.imread("src/main/resources/static/b7.jpeg");
        Size newSize = new Size(src.width() * 1, src.height() * 1);
        Mat dst = new Mat();//目标图像
        Imgproc.resize(src, dst, newSize);//缩放一半
        Mat grayImage = new Mat();
        Imgproc.cvtColor(dst, grayImage, Imgproc.COLOR_BGR2GRAY);//转为灰度图
//
//        HighGui.imshow("gray",grayImage);
//        HighGui.waitKey(0);
        logger.info("1.Turn gray");

        Mat gs = new Mat();
        Imgproc.GaussianBlur(grayImage,gs, new Size(3, 3), 0.1);//高斯过滤波
//        HighGui.imshow("gs",gs);
//        HighGui.waitKey(0);
        logger.info("2.Get GaussianBlur");

        Mat edges = new Mat();

        //Imgproc.morphologyEx(gs,dst,Imgproc.MORPH_GRADIENT,new Mat(),new Point(-1,-1),3);
        Imgproc.Canny(gs, edges, 10, 85);//Canny边缘检测
        Mat hierarchy = new Mat();
//        HighGui.imshow("变形",dst);
//        HighGui.waitKey(0);
        logger.info("3.Get Ganny");

       // HighGui.imshow("edges",edges);
//        HighGui.waitKey(0);
//        Imgcodecs.imwrite("output13.jpg", gs);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>(); //接收结果集
        /**
         * 2.根据Canny边沿检测得到的得到图来查找轮廓
         */
        Imgproc.findContours(edges,contours,new Mat(),Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
        Mat target = new Mat(edges.height(),edges.width(), CvType.CV_8UC3,new Scalar(255,255,255));
        for(int i=0;i<contours.size();i++)  //从得到矩阵个数来算图层
            Imgproc.drawContours(target,contours,i,new Scalar(0,0,0),3);//target上面(白色)轮廓的一层
//        HighGui.imshow("Contours",target);
//        HighGui.waitKey(0);
        logger.info("3.Find Contours");

        /**
         * 3.霍夫直线检测用于检测液位高度
         */

        Mat lines = new Mat();
        Imgproc.HoughLines(edges, lines, 1, Math.PI / 180, 50);
        /**
         * 3.1.调整线段阈值可以找到合适的线段作为液位线这里为160,要根据瓶子形状修改
         */
        double x1 = 0, y1 = 0, x2 = 0, y2 = 0;
        Point op1,op2;//目标点
        double px1 = 0,px2 = 0,px3=0,pradius=0;//满液位标准线，液位线,瓶身高度,霍夫肩部半径
        for (int i = 0; i < lines.rows(); i++) {
            double[] data = lines.get(i, 0);
            double rho = data[0];
            double theta = data[1];
            double a = Math.cos(theta);
            double b = Math.sin(theta);
            double x0 = a * rho;
            double y0 = b * rho;
            Point pt1 = new Point(Math.round(x0 + 1000 * (-b)), Math.round(y0 + 1000 * a));
            Point pt2 = new Point(Math.round(x0 - 1000 * (-b)), Math.round(y0 - 1000 * a));

            Imgproc.line(dst, pt1, pt2, new Scalar(0, 0, 255), 3, Imgproc.LINE_AA, 0);   //检测到阈值内红色线段3条
            if (Math.abs(theta - Math.PI / 2) < 0.1) {//找到90度的那根，就是液位水平线(找到了液位的水平线以及坐标)
                x1 = pt1.x;
                y1 = pt1.y;
                x2 = pt2.x;
                y2 = pt2.y;
                //两点确定一条直线
                Imgproc.line(dst, pt1, pt2, new Scalar(255, 255, 255), 3, Imgproc.LINE_AA, 0);//白色
                op1=pt1;op2=pt2;//获取液位关键点
                px2=(op1.y+op2.y)/2;
            }
        }
//        HighGui.imshow("水位线",dst);
//        HighGui.waitKey(0) ;
        logger.info("4.Find Line");

        /**
         * 4.霍夫圆检测，真实摄像b头需要调整一下，这里用来检测肩部，任然需要调整阈值
         */
        Mat circles = new Mat();
        Imgproc.HoughCircles(edges, circles, Imgproc.HOUGH_GRADIENT, 1, 20,
                100.0, 20.0, 15, 50);//正解


        for(int x=0;x<circles.cols();x++){
            double[] c= circles.get(0,x);
            Point center = new Point(Math.round(c[0]),Math.round(c[1])); //圆心，X,Y第二个参数大概就是肩部
            Imgproc.line(dst, new Point(0,Math.round(c[1])), new Point(Math.round(c[0]),Math.round(c[1])), new Scalar(255, 0, 0), 3, Imgproc.LINE_AA, 0);
            px1=Math.round(c[1]);
            int radius = (int) Math.round(c[2]);
            pradius=radius;
            Imgproc.circle(dst,center,radius,new Scalar(0, 255, 255),3,8,0);
        }
        logger.info("5.Find Circle");
        /**
         * 5.绘制找到的霍夫圆和液位高度以及肩部高度
         */
        //HighGui.imshow("Circles",dst);
        Imgcodecs.imwrite("src/main/resources/static/output12.jpg", dst);
        //HighGui.waitKey(0);

        /**
         * 6.简单判断液位高度以及水满没得？
         * 获取圆心的x1坐标以及液位的x2坐标
         * 判断水满：flag=(px1-px2)=>0?1:0,水已经到达警戒线之上。请即时停水
         * 计算瓶子容量:霍夫圆的半径求圆柱体积*0.9:3.14*rads*rads*0.9*h
         */
        int flag;
        flag=(int)(px1-px2)>=0?1:0;
        System.out.println(px1);
        System.out.println(px2);
        System.out.println(px3);
        System.out.println(pradius);
        if(flag==1){
            //System.out.println("water is full!");
            logger.info("6.water is full!");
            SendData.setStatus(1);

        }else{
            //System.out.println("water is not full!");
            SendData.setStatus(0);
            logger.info("6.water is not full!");
        }
        float k = -(float)(px1-px2);
        //System.out.printf(String.valueOf(k));
        SendData.setLevel(k);
        logger.info("7.The bottle Height:"+k+"%");
        return SendData;
    }
}
