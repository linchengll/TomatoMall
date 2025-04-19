package com.example.tomatomall.service;

import com.example.tomatomall.enums.StatusEnum;
import com.example.tomatomall.vo.OrderVO;
import com.example.tomatomall.vo.PaymentVO;
import com.example.tomatomall.vo.ShippingAddress;

import java.util.List;

public interface OrderService {
    OrderVO submitOrder(List<String> cartItemIds, ShippingAddress shipping_address, String payment_method);
    PaymentVO handlePayment(String orderId);
    void updateOrderStatus(String orderId, StatusEnum status);
    void reduceStock(String orderId);
}
