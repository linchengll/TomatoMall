package com.example.tomatomall.service;

import com.example.tomatomall.enums.StatusEnum;
import com.example.tomatomall.vo.OrderVO;
import com.example.tomatomall.vo.PaymentVO;

import java.util.List;
import java.util.Map;

public interface OrderService {
    OrderVO submitOrder(List<String> cartItemIds, Map<Object,String> shipping_address, String payment_method);
    PaymentVO handlePayment(String orderId);
    void updateOrderStatus(String orderId, StatusEnum status);
    void reduceStock(String orderId);
}
