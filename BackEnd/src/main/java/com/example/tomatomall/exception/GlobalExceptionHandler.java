package com.example.tomatomall.exception;

import com.example.tomatomall.vo.Response;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = TomatoMallException.class)
    public Response<String> handleAIExternalException(TomatoMallException e) {
        e.printStackTrace();
        if(e.getMessage().equals("未登录!")){
        return Response.buildFailure(e.getMessage(), "401");
        }else
            return Response.buildFailure(e.getMessage(), "400");
    }
}
