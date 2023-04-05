package com.example.opencvdemo.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @version 1.0
 * @Author qin
 * @Date 2023/3/14 13:31
 */
@Component
@Getter
@Setter
public class bottleData implements Serializable {
    @SerializedName("deviceid")
    String deviceid;//设备id

    @SerializedName("Level")
    float Level;//液位高度

    @SerializedName("status")
    int status;//满还是没满

    @SerializedName("createtime")
    String createtime;//时间戳
}
