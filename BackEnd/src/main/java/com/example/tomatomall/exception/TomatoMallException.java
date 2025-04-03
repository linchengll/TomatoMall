package com.example.tomatomall.exception;

public class TomatoMallException extends RuntimeException {
    public TomatoMallException(String message) {super(message)
    ;}
    //user
    public static TomatoMallException  usernameAlreadyExists() {return new TomatoMallException("用户名已存在!");}

    public static TomatoMallException  notLogin() {return new TomatoMallException("未登录!");}

    public static TomatoMallException  usernameNotExists() {return new TomatoMallException("用户不存在");}

    public static TomatoMallException  passwordError() {return new TomatoMallException("用户密码错误");}

    //product
    public static TomatoMallException  productAlreadyExist() {return new TomatoMallException("商品已存在!");}

    public static TomatoMallException  productNotExists() {return new TomatoMallException("商品不存在!");}

    //utility
    public static TomatoMallException  fileUploadFail() {return new TomatoMallException("文件上传失败!");}


}
