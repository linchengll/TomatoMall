package com.example.tomatomall.repository;

import com.example.tomatomall.po.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders, Integer> {
}
