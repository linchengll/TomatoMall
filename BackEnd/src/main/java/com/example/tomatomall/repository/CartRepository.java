package com.example.tomatomall.repository;

import com.example.tomatomall.po.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Integer> {
}
