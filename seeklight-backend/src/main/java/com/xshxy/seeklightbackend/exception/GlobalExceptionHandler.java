package com.xshxy.seeklightbackend.exception;

import com.xshxy.seeklightbackend.common.Result;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException ex) {
        int code = ex.getCode() > 0 ? ex.getCode() : 500;
        return Result.failure(code, ex.getMessage());
    }
//
//    @ExceptionHandler(MaxUploadSizeExceededException.class)
//    public Result<Void> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
//        return Result.failure(413, ex.getMessage());
//    }
//
//    @ExceptionHandler(Exception.class)
//    public Result<Void> handleException(Exception ex) {
//        return Result.failure("Internal server error");
//    }
}
