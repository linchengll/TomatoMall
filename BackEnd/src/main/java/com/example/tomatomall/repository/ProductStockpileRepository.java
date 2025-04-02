package com.example.tomatomall.repository;

import com.example.tomatomall.po.ProductStockpile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductStockpileRepository extends JpaRepository<ProductStockpile,Integer> {
    ProductStockpile findByProductId(String productId);
}
