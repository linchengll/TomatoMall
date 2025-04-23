package com.example.tomatomall.repository;

import com.example.tomatomall.enums.StatusEnum;
import com.example.tomatomall.po.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Integer> {
    List<Orders> findByStatus(StatusEnum status);
}
