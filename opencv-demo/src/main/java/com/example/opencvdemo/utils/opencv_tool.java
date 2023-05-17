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
    //String path_server="C:\\Users\\qin\\Desktop\\imge\\";
    String k = "C:\\Users\\Administrator\\Desktop\\java_pj\\";
    String path_server=k;
    String test="C:\\Users\\qin\\Desktop\\code\\smart-server\\bb7.jpg";
    // 加载动态库
    static {
        System.loadLibrary(
                Core.NATIVE_LIBRARY_NAME
        );
    }
    public static void main(String[] args) {
        opencv_tool k = new opencv_tool();
      //  k.mm();
         k.k();
        //k.test_opencv();


    }
    public  void k(){
        Mat img = Imgcodecs.imread("cropped.png"); //读取图片
        HighGui.imshow("原图", img); //显示原图
        HighGui.waitKey(0);
        Mat grayimg = new Mat();
        Imgproc.cvtColor(img, grayimg, Imgproc.COLOR_BGR2GRAY); //将图片转化为灰度图
        HighGui.imshow("灰度图", grayimg); //显示灰度图
        HighGui.waitKey(0);

        Imgproc.blur(grayimg, grayimg, new Size(5, 5)); //对图片进行平滑处理
        HighGui.imshow("平滑处理后的灰度图", grayimg); //显示平滑处理后的灰度图
        HighGui.waitKey(0);
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(20, 20));
        Mat openimg1 = new Mat();
        Imgproc.morphologyEx(grayimg, openimg1, Imgproc.MORPH_OPEN, element); // 对图片进行开运算
        HighGui.imshow("开运算图", openimg1); //显示开运算后的图
        HighGui.waitKey(0);
        Mat dstimg = new Mat(openimg1.size(), openimg1.type()); //创建一个和开运算图片一样大小，类型的矩阵

        //下面是对图片进行二值化处理（二值化就是把有灰度的图直接转换为黑白图）
        byte[] p = new byte[(int) (openimg1.total() * openimg1.channels())]; //定义一个指针，指向图片矩阵第一个元素
        openimg1.get(0, 0, p);
        int h = openimg1.rows(); //赋值图片的高给h
        int w = openimg1.cols(); //赋值图片的宽给w
        for (int i = 0; i < h * w; i++) //遍历图片所有元素
        {
            if ((p[i] & 0xff) > 170) {
                p[i] = (byte) 255; //对于元素值大于170的赋值给元素255，即为白色
            } else {
                p[i] = 0; //其他情况，设为黑色
            }
        }
        openimg1.put(0, 0, p);
        HighGui.imshow("二值图", openimg1);
        HighGui.waitKey(0);
        //设置一个相框
        byte[] q = new byte[(int) (dstimg.total() * dstimg.channels())];
        dstimg.get(0, 0, q);
        for (int i = 0; i < h; i++) {
            if (i < 5 || i > h - 5) {
                for (int j = 0; j < w; j++) {
                    q[i * w + j] = 0;
                }
            } else {
                for (int n = 0; n < w; n++) {
                    if (n < 5 || n > w - 5) {
                        q[i * w + n] = 0;
                    } else {
                        q[i * w + n] = 1;
                    }
                }
            }
        }
        dstimg.put(0, 0, q);

        //用相框与处理的开运算图进行叠加
        byte[] pa = new byte[(int) (openimg1.total() * openimg1.channels())];
        openimg1.get(0, 0, pa);
        for (int i = 0; i < w * h; i++) {
            pa[i] = (byte) ((pa[i] & 0xff) * (q[i] & 0xff));
        }
        openimg1.put(0, 0, pa);
        HighGui.imshow("加相框图", openimg1);
        HighGui.waitKey(0);
        Mat cannyimg = new Mat();
        Imgproc.Canny(openimg1, cannyimg, 3, 9, 3); //对图片进行边缘检测
        HighGui.imshow("边缘检测", cannyimg); //显示边缘检测的图结果
        HighGui.waitKey(0);
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(cannyimg, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0)); //提取图片轮廓
        Mat drawing = Mat.zeros(cannyimg.size(), CvType.CV_8UC3); //定义一个三通道的和原图尺寸一样大小的矩阵，方便上色
        System.out.printf("图片尺寸为高h=%d    宽w=%d    \n", h, w);
        for (int i = 0; i < contours.size(); i++) //遍历连通区域的个数
        {
            if (Imgproc.contourArea(contours.get(i)) > 10000) // 判断连通区域的面积是否大于10000
            {
                Scalar color = new Scalar(255, 255, 0); //定义一个颜色
                Imgproc.drawContours(drawing, contours, i, color, Imgproc.FILLED, 8, hierarchy, 0, new Point()); //对于满足要求的区域进行填充颜色
            } else {
                Scalar color = new Scalar(0, 0, 0); //定义黑色
                Imgproc.drawContours(drawing, contours, i, color, 2, 8, hierarchy, 0, new Point()); //对不满足要求的区域删除（元素值全部为零，黑色）
            }
        } //最后会得到一个只有不满足要求区域的图
        HighGui.imshow("提取后的图", drawing); //显示上面操作所得到的图
        HighGui.waitKey(0);
        Core.subtract(img, drawing, drawing); //用原图减去处理所得的图即可标记出相应的区域
        HighGui.imshow("最终结果", drawing); //显示最终结果

        HighGui.waitKey(0);

    }
    public void hsv(){

        String pat="grayImage.png";
        // Load the input image
        Mat inputImage = Imgcodecs.imread(pat);

        // Convert the input image to HSV color space
        Mat hsvImage = inputImage;
//        Imgproc.cvtColor(inputImage, hsvImage, Imgproc.COLOR_BGR2HSV);

        // Define the HSV color range to keep
        Scalar lower = new Scalar(186, 0, 0);
        Scalar upper = new Scalar(200, 0, 0);

        // Threshold the HSV image to keep only the desired color range
        Mat mask = new Mat();
        Core.inRange(hsvImage, lower, upper, mask);
        HighGui.imshow("mask",mask);
        HighGui.waitKey(0);
        // Apply morphological operations to remove noise and fill gaps
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_OPEN, kernel);
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel);

        // Find contours in the mask image
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Find the contour with the largest area
        double maxArea = 0;
        int maxAreaIdx = -1;
        for (int i = 0; i < contours.size(); i++) {
            double area = Imgproc.contourArea(contours.get(i));
            if (area > maxArea) {
                maxArea = area;
                maxAreaIdx = i;
            }
        }

        // Draw the largest contour on the input image
        Mat outputImage = inputImage.clone();
        if (maxAreaIdx != -1) {
            Imgproc.drawContours(outputImage, contours, maxAreaIdx, new Scalar(0, 0, 255), 2);
        }

        // Find lines in the largest contour using the Hough transform
        Mat lines = new Mat();
        Imgproc.HoughLinesP(mask, lines, 1, Math.PI / 180, 50, 50, 10);

        // Draw the lines on the output image
        for (int i = 0; i < lines.rows(); i++) {
            double[] line = lines.get(i, 0);
            Imgproc.line(outputImage, new Point(line[0], line[1]), new Point(line[2], line[3]), new Scalar(0, 255, 0), 2);
        }

        // Save the output image
        Imgcodecs.imwrite("hsv.jpg", outputImage);
    }

    public void mm(){
        // 加载输入图像
        String pat="C:\\Users\\qin\\Desktop\\code\\smart-server\\bb7.jpg";
        Mat src = Imgcodecs.imread(pat);
        Size newSize = new Size(src.width() * 1, src.height() * 1);
        Mat dst = new Mat();//目标图像
        Imgproc.resize(src, dst, newSize);//缩放一半
        Rect roi = new Rect(45,25,200,200);//定义矩形区域
        Mat cropped = new Mat(src,roi);//剪切
        HighGui.imshow("cropped", cropped);
        HighGui.waitKey(0);
        Mat inputImage=cropped;
        Imgcodecs.imwrite("cropped.png", cropped);
        // 将图像转换为灰度图像
        Mat grayImage = new Mat();
        Imgproc.cvtColor(inputImage, grayImage, Imgproc.COLOR_BGR2GRAY);

        Imgcodecs.imwrite("grayImage.png", grayImage);
        // 应用自适应阈值分割来分割图像
        Mat binaryImage = new Mat();
        Imgproc.adaptiveThreshold(grayImage, binaryImage, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 21, 6);
        HighGui.imshow("binaryImage", binaryImage);
        HighGui.waitKey(0);
        // 在二进制图像中查找轮廓
        Mat contoursImage = inputImage.clone();
        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(binaryImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // 循环遍历轮廓并过滤掉不是透明水瓶的轮廓
        for (int i = 0; i < contours.size(); i++) {
            Mat contour = contours.get(i);
            double area = Imgproc.contourArea(contour);
            if (area < 500) {
                // 忽略小轮廓

                continue;
            }
            Mat mask = new Mat(inputImage.size(), inputImage.type(), new Scalar(0, 0, 0));
            Imgproc.drawContours(mask, contours, i, new Scalar(255, 255, 255), -1);
            Mat maskedImage = new Mat();
            Core.bitwise_and(inputImage, mask, maskedImage);
            Mat hsvImage = new Mat();
            Imgproc.cvtColor(maskedImage, hsvImage, Imgproc.COLOR_BGR2HSV);
            Scalar lower = new Scalar(0, 0, 0);
            Scalar upper = new Scalar(180, 255, 50);
            Mat thresholdImage = new Mat();
            Core.inRange(hsvImage, lower, upper, thresholdImage);
            double nonZeroPixels = Core.countNonZero(thresholdImage);
            double totalPixels = thresholdImage.size().area();
            double transparency = nonZeroPixels / totalPixels;
            if (transparency < 0.5) {
                // 不是透明水瓶，将非透明像素设置为黑色
                Core.bitwise_not(mask, mask);
                Core.bitwise_and(inputImage, mask, inputImage);
            }
        }

        // 保存输出图像
        Imgcodecs.imwrite("output_image.png", inputImage);
    }



    public bottleData test_opencv(){

        bottleData SendData = new bottleData();
        /**
         * 1.第一步：拿到图片先读取缩放然后转换成灰度图
         */

        String pat="C:\\Users\\qin\\Desktop\\code\\smart-server\\bb7.jpg";
        //Mat src = Imgcodecs.imread(path_server+"b7.jpeg");
        Mat src = Imgcodecs.imread(pat);
        Size newSize = new Size(src.width() * 1, src.height() * 1);
        Mat dst = new Mat();//目标图像
        Imgproc.resize(src, dst, newSize);//缩放一半
        Rect roi = new Rect(45,25,200,200);//定义矩形区域
        Mat cropped = new Mat(src,roi);//剪切
        HighGui.imshow("cropped", cropped);
        HighGui.waitKey(0);

        Mat grayImage = new Mat();
        Imgproc.cvtColor(cropped, grayImage, Imgproc.COLOR_BGR2GRAY);//转为灰度图
//
        HighGui.imshow("gray",grayImage);
        HighGui.waitKey(0);
        logger.info("1.Turn gray");



        Mat gs = new Mat();
        Imgproc.GaussianBlur(grayImage,gs, new Size(3, 3), 0.2);//高斯过滤波
        HighGui.imshow("gs",gs);
        HighGui.waitKey(0);
        logger.info("2.Get GaussianBlur");

        Mat edges = new Mat();

        //Imgproc.morphologyEx(gs,dst,Imgproc.MORPH_GRADIENT,new Mat(),new Point(-1,-1),3);
        Imgproc.Canny(gs, edges, 20, 180);//Canny边缘检测
        logger.info("3.Get Ganny");
        HighGui.imshow("edges",edges);
        HighGui.waitKey(0);
//        Imgcodecs.imwrite("output13.jpg", gs);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>(); //接收结果集
        /**
         * 2.根据Canny边沿检测得到的得到图来查找轮廓
         */
        Imgproc.findContours(edges,contours,new Mat(),Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
        Mat target = new Mat(edges.height(),edges.width(), CvType.CV_8UC3,new Scalar(255,255,255));
        for(int i=0;i<contours.size();i++)  //从得到矩阵个数来算图层
            Imgproc.drawContours(target,contours,i,new Scalar(0,0,0),3);//target上面(白色)轮廓的一层
        HighGui.imshow("Contours",target);
        HighGui.waitKey(0);
        logger.info("3.Find Contours");

        //查找矩形
        Mat result = new Mat(edges.size(), CvType.CV_8UC3, new Scalar(0, 0, 0)); // 创建一张新的图像，用于绘制保留的轮廓

        for (int i = 0; i < contours.size(); i++) {
            if (Imgproc.isContourConvex(contours.get(i))) { // 判断轮廓是否为凸包
                Imgproc.drawContours(result, contours, i, new Scalar(255, 255, 255), -1); // 绘制保留的轮廓
            }
        }
        HighGui.imshow("Contours",result);
        HighGui.waitKey(0);
        logger.info("3.Find Contours");

        /**
         * 3.霍夫直线检测用于检测液位高度
         */

        Mat lines = new Mat();
        Imgproc.HoughLines(edges, lines, 1, Math.PI / 180, 70,80,90);
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

            //Imgproc.line(dst, pt1, pt2, new Scalar(0, 0, 255), 3, Imgproc.LINE_AA, 0);   //检测到阈值内红色线段3条
            if ((Math.abs(theta - Math.PI / 2) < 0.1)&&pt2.x>20000) {//找到90度的那根，就是液位水平线(找到了液位的水平线以及坐标)
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
        HighGui.imshow("水位线",dst);
        HighGui.waitKey(0) ;
        logger.info("4.Find Line");

        /**
         * 4.霍夫圆检测，真实摄像b头需要调整一下，这里用来检测肩部，任然需要调整阈值
         */
        Mat circles = new Mat();
        Imgproc.HoughCircles(edges, circles, Imgproc.HOUGH_GRADIENT, 1, 15,
                60.0, 20.0, 20, 40);//正解


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
        HighGui.imshow("Circles",dst);
        Imgcodecs.imwrite( path_server+"output12.jpg", dst);
        HighGui.waitKey(0);

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
