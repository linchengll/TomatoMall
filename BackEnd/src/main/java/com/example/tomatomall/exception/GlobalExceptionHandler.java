package com.example.tomatomall.exception;

import com.example.tomatomall.vo.Response;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = TomatoMallException.class)
    public Response<String> handleAIExternalException(TomatoMallException e) {
        //e.printStackTrace();
        System.err.println("#####"+e.getMessage());
        switch (e.getMessage()) {
            case "未登录!": case "没有权限进行此操作!": case "当前用户与请求不符!": return Response.buildFailure(e.getMessage(), "401");
            default: return Response.buildFailure(e.getMessage(), "400");
        }
    }
}
