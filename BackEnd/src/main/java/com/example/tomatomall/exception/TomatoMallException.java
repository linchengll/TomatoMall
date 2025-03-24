package com.example.tomatomall.exception;

public class TomatoMallException extends RuntimeException {
    public TomatoMallException(String message) {super(message);}

    public static TomatoMallException  usernameAlreadyExists() {return new TomatoMallException("用户名已存在!");}

    public static TomatoMallException  notLogin() {return new TomatoMallException("未登录!");}

    public static TomatoMallException  phoneOrPasswordError() {return new TomatoMallException("手机号或密码错误!");}

    public static TomatoMallException  fileUploadFail() {return new TomatoMallException("文件上传失败!");}


}
