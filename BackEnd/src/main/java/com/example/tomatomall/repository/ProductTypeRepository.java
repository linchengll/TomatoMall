package com.example.tomatomall.repository;

import com.example.tomatomall.po.ProductTypes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ProductTypeRepository extends JpaRepository<ProductTypes,Integer> {
    Set<ProductTypes> findByProductId(Integer productId);
    List<ProductTypes> findByTypeId(Integer typeId);
}
