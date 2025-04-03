package com.example.tomatomall.repository;

import com.example.tomatomall.po.ProductSpecification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ProductSpecificationRepository extends JpaRepository<ProductSpecification,Integer> {
    Set<ProductSpecification> findByProductId(Integer productId);
}
