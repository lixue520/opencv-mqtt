package com.example.opencvdemo.ScheduledTasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.regex.Pattern;

/**
 * @version 1.0
 * @Author qin
 * @Date 2023/3/20 23:31
 * 定时清理图片任务
 */

@Component
public class DelImgTask {
    private static final Logger logger = LoggerFactory.getLogger(DelImgTask.class);
    @Scheduled(cron ="*/60 * * * * ?")//60秒执行一次
    public void DeletePicture() {
        logger.info("Start the delete picture task now");
        String path = "C:\\Users\\qin\\Desktop\\code\\smart-server\\";
        File folder = new File(path);
        File[] files = folder.listFiles();
        Pattern pattern = Pattern.compile("^video-stream-00.*$");

        for (File file : files) {
            if (file.isFile() && pattern.matcher(file.getName()).matches()) {
                file.delete();
            }
        }
    }

}
