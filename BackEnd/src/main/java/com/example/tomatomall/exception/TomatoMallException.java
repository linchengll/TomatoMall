package com.example.tomatomall.exception;

public class TomatoMallException extends RuntimeException {
    public TomatoMallException(String message) {super(message)
    ;}
    //utility
    public static TomatoMallException fileUploadFail() {return new TomatoMallException("文件上传失败!");}

    //auth
    public static TomatoMallException unauthorized() {return new TomatoMallException("没有权限进行此操作!");}

    public static TomatoMallException userMismatch() {return new TomatoMallException("当前用户与请求不符!");}

    //user
    public static TomatoMallException usernameAlreadyExists() {return new TomatoMallException("用户名已存在!");}

    public static TomatoMallException notLogin() {return new TomatoMallException("未登录!");}

    public static TomatoMallException usernameNotExists() {return new TomatoMallException("用户不存在");}

    public static TomatoMallException passwordError() {return new TomatoMallException("用户密码错误");}

    //product
    public static TomatoMallException productAlreadyExist() {return new TomatoMallException("商品已存在!");}

    public static TomatoMallException productNotExists() {return new TomatoMallException("商品不存在!");}

    //cart
    public static TomatoMallException cartProductNotExists() {return new TomatoMallException("购物车商品不存在！");}

    public static TomatoMallException cartProductQuantityNotEnough() {return new TomatoMallException("商品数量不足！");}

    //order
    public static TomatoMallException orderPaymentMethodInvalid() {return new TomatoMallException("订单支付方式不合法！");}

    public static TomatoMallException orderCartProductInvalid() {return new TomatoMallException("订单中存在无效的购物车商品！");}

    public static TomatoMallException orderNotExist() {return new TomatoMallException("订单不存在！");}
}
