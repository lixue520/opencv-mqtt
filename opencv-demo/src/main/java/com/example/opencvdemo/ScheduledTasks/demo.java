package com.example.opencvdemo.ScheduledTasks;

import java.io.File;
import java.util.regex.Pattern;

/**
 * @version 1.0
 * @Author qin
 * @Date 2023/3/20 23:49
 */
public class demo {
    public static void main(String[] args) {
        String path = "C:\\Users\\qin\\Desktop\\code\\smart-server\\";
        File folder = new File(path);
        File[] files = folder.listFiles();
        Pattern pattern = Pattern.compile("^video-stream-16.*$");

        for (File file : files) {
            if (file.isFile() && pattern.matcher(file.getName()).matches()) {
                file.delete();
            }
        }
    }
}

