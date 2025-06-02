package com.example.tomatomall.repository;

import com.example.tomatomall.po.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Integer> {
    List<Advertisement> findByProductId(Integer productId);
    Advertisement findByProductIdAndDiscount(Integer productId, Integer discount);
}
