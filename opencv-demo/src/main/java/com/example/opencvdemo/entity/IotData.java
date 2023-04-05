package com.example.opencvdemo.entity;

/**
 * @version 1.0
 * @Author qin
 * @Date 2023/3/12 0:53
 */


import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

@Component
@Getter
@Setter
public class IotData implements Serializable {

    @SerializedName("deviceid")
    String deviceid;//设备id

    @SerializedName("sensorid")
    String sensorid;//数据id

    @SerializedName("types")
    String types;//设备来源

    @SerializedName("loraid")
    String loraid;//loraid硬件的id

    @SerializedName("createtime")
    Date createtime;//创建时间

    @SerializedName("temp")
    float temp;//温度

    @SerializedName("humi")
    float humi;//湿度

    @SerializedName("light")
    float light;//光敏

//get/set/tostring省略...

}