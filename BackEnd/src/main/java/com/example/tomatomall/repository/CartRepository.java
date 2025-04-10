package com.example.tomatomall.repository;

import com.example.tomatomall.po.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Cart findByCartItemId(Integer cartItemId);
    List<Cart> findByProductId(Integer productId);
    List<Cart> findByUserId(Integer userId);
}
