package com.xshxy.seeklightbackend.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> success() {
        return new Result<>(200, "OK", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "OK", data);
    }

    public static <T> Result<T> failure(String message) {
        return new Result<>(500, message, null);
    }

    public static <T> Result<T> failure(int code, String message) {
        return new Result<>(code, message, null);
    }
}
