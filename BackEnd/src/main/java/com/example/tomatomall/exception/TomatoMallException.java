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
    public static TomatoMallException orderPaymentMethodInvalid(String paymentMethod) {return new TomatoMallException("订单支付方式 "+paymentMethod+" 不合法！");}

    public static TomatoMallException orderCartProductInvalid() {return new TomatoMallException("订单中存在无效的购物车商品！");}

    public static TomatoMallException orderNotExist() {return new TomatoMallException("订单不存在！");}

    public static TomatoMallException duplicateOrderUpdate(String orderId) {return new TomatoMallException("对订单"+orderId+"操作重复！");}

    //advertisement
    public static TomatoMallException advertisementNotExists() {return new TomatoMallException("广告不存在！");}
    public static TomatoMallException limitNumTooMuch() {return new TomatoMallException("限购数量超过库存！");}
    public static TomatoMallException discountInvalid() {return new TomatoMallException("折扣无效！");}

    //type
    public static TomatoMallException typeAlreadyExist() {return new TomatoMallException("类型已存在!");}

    public static TomatoMallException typeNotExists() {return new TomatoMallException("类型不存在!");}

    //pageable
    public static TomatoMallException pageInvalid(int page){return new TomatoMallException(String.format("页码 %d 无效",page));}

    public static TomatoMallException sizeInvalid(int size,int limit){return new TomatoMallException(String.format("每页商品数量 %d 无效，请使用不超过 %d 的正整数",size,limit));}

    //comment
    public static TomatoMallException commentNotExists() {return new TomatoMallException("评论不存在!");}

    public static TomatoMallException notBuyer() {return new TomatoMallException("不是买家,不能评论!");}

    public static TomatoMallException notOwner() {return new TomatoMallException("不是评论者,不能删除!");}
}
