package com.example.tomatomall.service;

import com.example.tomatomall.vo.CartItemsVO;
import com.example.tomatomall.vo.CartVO;
import com.example.tomatomall.vo.OrderVO;
import com.example.tomatomall.vo.ShippingAddress;

import java.util.List;

public interface CartService {
    CartVO addCart(String productId,Integer quantity,Integer discount);
    String deleteCartItem(String cartItemId);
    String updateCartItem(String cartItemId,Integer quantity);
    CartItemsVO getCartList();
    OrderVO submitOrder(List<String> cartItemIds, ShippingAddress shipping_address, String payment_method);
    void updateOrderFlag(String cartItemId,boolean ordered);
}
