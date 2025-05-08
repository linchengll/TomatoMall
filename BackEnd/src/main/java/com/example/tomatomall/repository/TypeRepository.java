package com.example.tomatomall.repository;

import com.example.tomatomall.po.Type;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypeRepository extends JpaRepository<Type,Integer> {
    Optional<Type> findByTypeName(String typeName);
}
