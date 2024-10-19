package com.template.application.common;

import com.template.application.enums.ResponseCodeEnum;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * ClassName: ResponseResult
 * Description:
 *
 * @Author 白给
 * @Create 2024/7/15 21:28
 * @Version 1.0
 */
@Data
public class ResponseResult<T> {
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;

    private ResponseResult() {
        timestamp = System.currentTimeMillis();
    }

    public ResponseResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }


    public static <T> ResponseResult<T> success() {
        return new ResponseResult<>(ResponseCodeEnum.SUCCESS.getCode(), "成功", null);
    }


    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(ResponseCodeEnum.SUCCESS.getCode(), "成功", data);
    }

    public static <T> ResponseResult<T> fail(int code, String message) {
        return new ResponseResult<>(code, message, null);
    }

}