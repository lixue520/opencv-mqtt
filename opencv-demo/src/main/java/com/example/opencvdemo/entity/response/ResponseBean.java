package com.example.opencvdemo.entity.response;

/**
 * @version 1.0
 * @Author qin
 * @Date 2023/4/10 18:08
 * 远程调用响应
 */
public class ResponseBean {

    private String code;

    private String msg;

    public String getCode(){
        return code;
    }

    public void setCode(String code){
        this.code = code;
    }

    public String getMsg(){
        return msg;
    }

    public void setMsg(String msg){
        this.msg = msg;
    }

    @Override
    public String toString(){
        return "ResponseBean{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}