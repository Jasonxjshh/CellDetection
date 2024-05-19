package com.eleven.celldetection.util;


/**
 * 统一API响应结果封装
 */


public class Result {

    private int code;

    private String message = "success";

    private Object data;

    // 后面result生成器需要以下方法
    public Result setCode(Integer resultCode){
        this.code = resultCode;

        return this;
    }

    public Result setMessage(String message){
        this.message = message;
        return this;
    }

    public Result setData(Object data){
        this.data = data;
        return this;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}
