package com.example.tomatomall.repository;

import com.example.tomatomall.po.ProductTypes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductTypeRepository extends JpaRepository<ProductTypes,Integer> {
    List<ProductTypes> findByProductId(Integer productId);
    List<ProductTypes> findByTypeId(Integer typeId);
}
