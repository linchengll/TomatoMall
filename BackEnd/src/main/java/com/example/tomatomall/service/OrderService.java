package com.example.tomatomall.service;

import com.example.tomatomall.vo.OrderVO;
import com.example.tomatomall.vo.PaymentVO;
import com.example.tomatomall.vo.ProductOfOrderVO;

import java.util.List;

public interface OrderService {
    PaymentVO handlePayment(String orderId);
    void handleSuccess(String orderId);
    void handleTimeout(String orderId);
    void handleFailure(String orderId);
    List<OrderVO> getOrderByUserId(String userId);
    OrderVO getOrderById(String orderId);
    List<ProductOfOrderVO> getProductsByOrderId(String orderId);
}
