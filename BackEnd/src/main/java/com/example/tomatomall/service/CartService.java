package com.example.tomatomall.service;

import com.example.tomatomall.vo.CartItemsVO;
import com.example.tomatomall.vo.CartVO;

public interface CartService {
    CartVO addCart(String productId,Integer quantity);
    String deleteCartItem(String cartItemId);
    String updateCartItem(String cartItemId,Integer quantity);
    CartItemsVO getCartList();

}
