package com.example.tomatomall.vo;

import com.example.tomatomall.enums.PaymentEnum;
import com.example.tomatomall.enums.StatusEnum;
import com.example.tomatomall.po.Orders;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;

@Getter
@Setter
@NoArgsConstructor
public class OrderVO {
    private Integer orderId;
    private Integer userId;
    private Float totalAmount;
    private PaymentEnum payMethod;
    private StatusEnum status;
    private Time createTime;

    public Orders toPO(){
        Orders PO=new Orders();
        PO.setOrderId(this.orderId);
        PO.setUserId(this.userId);
        PO.setTotalAmount(this.totalAmount);
        PO.setPayMethod(this.payMethod);
        PO.setStatus(this.status);
        PO.setCreateTime(this.createTime);
        return PO;
    }
}
