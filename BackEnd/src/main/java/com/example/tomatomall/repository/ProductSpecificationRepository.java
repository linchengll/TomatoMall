package com.example.tomatomall.repository;

import com.example.tomatomall.po.ProductSpecification;

import com.example.tomatomall.po.compositKey.SpecificationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface ProductSpecificationRepository extends JpaRepository<ProductSpecification, SpecificationId> {
    @Query("select ps from ProductSpecification ps where ps.specId.productId = :productId")
    Set<ProductSpecification> findByProductId(@Param("productId") Integer productId);
}
