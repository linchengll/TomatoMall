package com.example.tomatomall.service;

import com.example.tomatomall.vo.OrderVO;
import com.example.tomatomall.vo.PaymentVO;

import java.util.List;

public interface OrderService {
    OrderVO submitOrder(List<String> cartItemIds, Object shipping_address, String payment_method);
    PaymentVO handlePayment(String orderId);
}
