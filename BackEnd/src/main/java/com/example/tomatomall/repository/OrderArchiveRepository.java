package com.example.tomatomall.repository;

import com.example.tomatomall.po.OrderArchive;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderArchiveRepository extends JpaRepository<OrderArchive, Integer> {
    List<OrderArchive> findByOrderId(Integer orderId);
}
