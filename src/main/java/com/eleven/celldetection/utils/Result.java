package com.eleven.celldetection.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  返回实体类
 * @param <T>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    Integer code;
    T data;
    String message;

    //    成功，返回空值
    public static <T> Result<T> success() {
        return new Result<>(200, null, "true");
    }

    //    成功，返回值
    public static <T> Result<T> success(T data) {
        return new Result<>(200, data, "true");
    }

    //成功 返回提示信息
    public static <T> Result<T> success(String message) {
        return new Result<>(200, null, message);
    }

    //    成功， 返回值和提示信息
    public static <T> Result<T> success(T data, String message) {
        return new Result<>(200, data, message);
    }

    //    失败，返回空值
    public static <T> Result<T> fail() {
        return new Result<>(500, null, "fail");
    }

    //    失败，返回状态码
    public static <T> Result<T> fail(Integer code) {
        return new Result<>(code, null, "fail");
    }

    //    失败, 返回提示信息
    public static <T> Result<T> fail(String message) {
        return new Result<>(500, null, message);
    }

    //    失败， 返回状态码和提示信息
    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, null, message);
    }
}
