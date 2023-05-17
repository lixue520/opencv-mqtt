package com.example.opencvdemo.utils;

import com.example.opencvdemo.entity.bottleData;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class WaterLevelDetection {
    static {
        System.loadLibrary(
                Core.NATIVE_LIBRARY_NAME
        );
    }
    // 加载输入图像
    String pat = "C:\\Users\\Administrator\\Desktop\\java_pj\\b7.jpg";

    public static void main(String[] args) {
        // 加载输入图像
        String pat = "C:\\Users\\qin\\Desktop\\code\\opencv-demo\\opencv-demo\\src\\main\\resources\\static\\bb7.jpg";
        //Mat src = Imgcodecs.imread(path_server+"b7.jpeg");
        Mat src = Imgcodecs.imread(pat);
        Size newSize = new Size(src.width() * 1, src.height() * 1);
        Mat dst = new Mat();//目标图像
        Imgproc.resize(src, dst, newSize);//缩放一半
        Rect roi = new Rect(35, 35, 200, 200);//定义矩形区域
        Mat cropped = new Mat(src, roi);//剪切
        HighGui.imshow("cropped", cropped);
        HighGui.waitKey(0);
        Imgcodecs.imwrite("src.png", cropped);

        // 将图像转换为灰度图像
        Mat grayImage = new Mat();
        Imgproc.cvtColor(cropped, grayImage, Imgproc.COLOR_BGR2GRAY);

        // 直方图均衡化
        Mat equalizedImage = new Mat();
        Imgproc.equalizeHist(grayImage, equalizedImage);
        Imgcodecs.imwrite("14.1.bmp", equalizedImage);
        Imgcodecs.imwrite("zhifang.png", equalizedImage);

        // 自编写直方图均衡化
        int a = grayImage.rows();
        int b = grayImage.cols();
        double[] H = new double[256];
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < b; j++) {
                int k = (int) grayImage.get(i, j)[0];
                H[k]++;
            }
        }
        for (int i = 0; i < 256; i++) {
            H[i] /= (a * b);
        }
        double[] HH = new double[256];
        HH[0] = H[0];
        for (int i = 1; i < 256; i++) {
            HH[i] = HH[i - 1] + H[i];
        }
        for (int i = 0; i < 256; i++) {
            HH[i] *= 255;
            HH[i] = Math.round(HH[i]);
        }
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < b; j++) {
                int k = (int) grayImage.get(i, j)[0];
                grayImage.put(i, j, HH[k]);
            }
        }
        Imgcodecs.imwrite("14.2.bmp", grayImage);
        Mat gs = new Mat();
        Imgproc.GaussianBlur(grayImage, gs, new Size(3, 3), 0.2);//高斯过滤波
        HighGui.imshow("gs", gs);
        HighGui.waitKey(0);
        Imgcodecs.imwrite("gray.png",  gs);

        Mat edges = new Mat();

        //Imgproc.morphologyEx(gs,dst,Imgproc.MORPH_GRADIENT,new Mat(),new Point(-1,-1),3);
        Imgproc.Canny(gs, edges, 100, 200);//Canny边缘检测

        HighGui.imshow("edges", edges);
        HighGui.waitKey(0);
//        Imgcodecs.imwrite("output13.jpg", gs);
        Imgcodecs.imwrite("edges.png",  edges);
        Rect roit = new Rect(35, 35, 120, 160);//定义矩形区域,水瓶满时大概是130
        Mat croppedt = new Mat(edges, roit);//剪切
        HighGui.imshow("cropped", croppedt);
        Imgcodecs.imwrite("croppedt.png",  croppedt);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>(); //接收结果集
        /**
         * 2.根据Canny边沿检测得到的得到图来查找轮廓
         */
        Imgproc.findContours(croppedt, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Mat target = new Mat(croppedt.height(), croppedt.width(), CvType.CV_8UC3, new Scalar(255, 255, 255));
        for (int i = 0; i < contours.size(); i++)  //从得到矩阵个数来算图层
            Imgproc.drawContours(target, contours, i, new Scalar(0, 0, 0), 3);//target上面(白色)轮廓的一层
        HighGui.imshow("Contours", target);
        HighGui.waitKey(0);
        Imgcodecs.imwrite("target.png",  target);

        Mat lines = new Mat();
        Imgproc.HoughLines(croppedt, lines, 1, Math.PI / 180, 10, 10, 10);
        /**
         * 3.1.调整线段阈值可以找到合适的线段作为液位线这里为160,要根据瓶子形状修改
         */
        Mat z = croppedt.clone();
        Mat lower = croppedt.clone();
        int f1 = 0, f2 = 0, f3 = 0;
        double s = 0, g = 0, d = 0;//竖直、高位、低位
        double x1 = 0, y1 = 0, x2 = 0, y2 = 0;
        Point op1, op2;//目标点
        double px1 = 0, px2 = 0, px3 = 0, pradius = 0;//满液位标准线，液位线,瓶身高度,霍夫肩部半径
        double s_x1 = 0, s_x2 = 0, s_y1 = 0, s_y2 = 0;
        double g_x1 = 0, g_x2 = 0, g_y1 = 0, g_y2 = 0;
        double d_x1 = 0, d_x2 = 0, d_y1 = 0, d_y2 = 0;
        for (int i = 0; i < lines.rows(); i++) {
            double[] data = lines.get(i, 0);
            double rho = data[0];
            double theta = data[1];
            double aa = Math.cos(theta);
            double bb = Math.sin(theta);
            double x0 = aa * rho;
            double y0 = bb * rho;
            Point pt1 = new Point(Math.round(x0 + 1000 * (-bb)), Math.round(y0 + 1000 * aa));
            Point pt2 = new Point(Math.round(x0 - 1000 * (-bb)), Math.round(y0 - 1000 * aa));
            if (Math.abs(theta - Math.PI / 180) < 0.1) {//找到90度的那根，就是液位水平线(找到了液位的水平线以及坐标)
                x1 = pt1.x;
                y1 = pt1.y;
                x2 = pt2.x;
                y2 = pt2.y;
                if (f1 == 0) {
                    Imgproc.line(croppedt, pt1, pt2, new Scalar(255, 255, 255), 3, Imgproc.LINE_AA, 0);
                    f1 = 1;
                    //处理确定坐标的逻辑
                    s_x1 = x1;
                    s_x2 = x2;
                    s_y1 = y1;
                    s_y2 = y2;
                }
            }

            //Imgproc.line(dst, pt1, pt2, new Scalar(0, 0, 255), 3, Imgproc.LINE_AA, 0);   //检测到阈值内红色线段3条
            if (Math.abs(theta - Math.PI / 2) < 0.1 && pt2.y < 15) {//找到90度的那根，就是液位水平线(找到了液位的水平线以及坐标)
                x1 = pt1.x;
                y1 = pt1.y;
                x2 = pt2.x;
                y2 = pt2.y;
                //两点确定一条直线,这个就是警戒线
                if (f2 == 0) {
                    Imgproc.line(croppedt, pt1, pt2, new Scalar(255, 255, 255), 3, Imgproc.LINE_AA, 0);//白色
                    f2 = 1;
                    //处理确定坐标的逻辑
                    g_x1 = x1;
                    g_x2 = x2;
                    g_y1 = y1;
                    g_y2 = y2;
                }

            }
            if (Math.abs(theta - Math.PI / 2) < 0.1 && pt2.y > 100) {//找到90度的那根，就是液位水平线(找到了液位的水平线以及坐标)
                x1 = pt1.x;
                y1 = pt1.y;
                x2 = pt2.x;
                y2 = pt2.y;
                //两点确定一条直线,这个就是最低线
                if (f3 == 0) {
                    Imgproc.line(croppedt, pt1, pt2, new Scalar(255, 255, 255), 3, Imgproc.LINE_AA, 0);//白色
                    f3 = 1;
                    //处理确定坐标的逻辑
                    d_x1 = x1;
                    d_x2 = x2;
                    d_y1 = y1;
                    d_y2 = y2;
                }
            }
        }
        /**
         * 4.1.处理坐标交点逻辑
         */
        // 计算第一条直线的斜率和截距
        double k1 = (s_y2 - s_y1) / (s_x2 - s_x1);
        double b1 = s_y1 - k1 * s_x1;

        // 计算第二条直线的斜率和截距
        double k2 = 0;
        double b2 = g_y1;

        // 计算第三条直线的斜率和截距
        double k3 = 0;
        double b3 = d_y1;

        // 计算第二条直线和第一条直线的交点坐标
        double intersection1_x = s_x1;
        double intersection1_y = k2 * intersection1_x + b2;

        // 计算第三条直线和第一条直线的交点坐标
        double intersection2_x = (b3 - b1) / (k1 - k3);
        double intersection2_y = k1 * intersection2_x + b1;
        System.out.printf("x= %.2f  y= %.2f\n",intersection1_x,intersection1_y);
        System.out.printf("x= %.2f  y= %.2f\n",intersection2_x,intersection2_y);

        HighGui.imshow("z", z);
        HighGui.waitKey(0);
        HighGui.imshow("警戒线", croppedt);
        HighGui.waitKey(0);
        HighGui.imshow("最低点", lower);
        HighGui.waitKey(0);
        Imgcodecs.imwrite("final.png",  croppedt);

        /**
         * 5.计算警戒线与最低点位之比求出水位百分比,两者的点由与纵部分的交点
         */
        if(intersection2_y>intersection1_y){
            System.out.println(((intersection2_y-intersection1_y)/intersection2_y)*100);//20是校正
            bottleData bottleData = new bottleData();
            bottleData.setLevel((float) (((intersection2_y-intersection1_y)/intersection2_y)*100));
            System.out.println(bottleData.getLevel());

        }else{
            System.out.println(((intersection1_y-intersection2_y)/intersection1_y)*100);//20是校正
            bottleData bottleData = new bottleData();
            bottleData.setLevel((float) (((intersection1_y-intersection2_y)/intersection1_y)*100));
            System.out.println(bottleData.getLevel());
        }
    }

}