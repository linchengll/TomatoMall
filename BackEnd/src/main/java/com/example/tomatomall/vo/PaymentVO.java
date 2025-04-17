package com.example.tomatomall.vo;

import com.example.tomatomall.enums.PaymentEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentVO {
    private String paymentForm; //支付宝支付表单HTML
    private Integer orderId; //订单ID
    private Float totalAmount; //订单总金额
    private PaymentEnum paymentMethod; //Alipay+???
}
