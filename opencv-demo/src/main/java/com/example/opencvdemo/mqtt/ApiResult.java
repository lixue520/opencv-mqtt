package com.example.opencvdemo.mqtt;

/**
 * @author suqinyi
 * @Date 2022/4/15
 * 通用返回对象
 */
public class ApiResult<T> {

    private long code;
    private String message;
    private T data;


    private final static long SUCCESS_CODE=1000;
    private final static long FAIL_CODE=2000;


    protected ApiResult() {
    }

    protected ApiResult(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }


    /**
     * 成功返回结果
     */
    public static <T> ApiResult<T> success(T data, String message) {
        return new ApiResult<T>(SUCCESS_CODE, message, data);
    }


    /**
     * 失败返回结果
     */
    public static <T> ApiResult<T> failed(String message) {
        return new ApiResult<T>(FAIL_CODE, message, null);
    }

    //get/set/tostring省略...

}

