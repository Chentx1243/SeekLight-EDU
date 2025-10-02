package com.xshxy.seeklightbackend.exception;

// 定义业务异常类
public class BusinessException extends RuntimeException {

    private int code;  // 可选：错误码

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
