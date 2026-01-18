package com.xshxy.seeklightbackend.exception;

import com.xshxy.seeklightbackend.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException ex) {
        int code = ex.getCode() > 0 ? ex.getCode() : 500;
        return Result.failure(code, ex.getMessage());
    }
}
