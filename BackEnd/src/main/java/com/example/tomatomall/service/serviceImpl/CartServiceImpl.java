package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.service.CartService;
import com.example.tomatomall.vo.CartItemsVO;
import com.example.tomatomall.vo.CartVO;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {

    @Override
    public CartVO addCart(String productId, Integer quantity) {
        return null;
    }

    @Override
    public String deleteCartItem(String cartItemId) {
        return "";
    }

    @Override
    public String updateCartItem(String cartItemId, Integer quantity) {
        return "";
    }

    @Override
    public CartItemsVO getCartList() {
        return null;
    }
}
