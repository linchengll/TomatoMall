package com.example.tomatomall.service;

import com.example.tomatomall.vo.PaymentVO;

public interface OrderService {
    PaymentVO handlePayment(String orderId);
    void handleSuccess(String orderId);
    void handleTimeout(String orderId);
    void handleFailure(String orderId);
}
