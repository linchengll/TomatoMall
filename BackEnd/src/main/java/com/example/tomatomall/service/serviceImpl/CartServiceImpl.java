package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.repository.CartRepository;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.repository.ProductSpecificationRepository;
import com.example.tomatomall.repository.ProductStockpileRepository;
import com.example.tomatomall.service.CartService;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.vo.CartItemsVO;
import com.example.tomatomall.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductStockpileRepository productStockpileRepository;
    @Autowired
    ProductSpecificationRepository productSpecificationRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    SecurityUtil securityUtil;

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
