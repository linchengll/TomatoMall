package com.example.tomatomall.service;

import com.example.tomatomall.vo.TypeVO;

import java.util.List;

public interface TypeService {
    TypeVO createType(TypeVO typeVO);
    String deleteType(String typeId);
    List<TypeVO> getAllType();
}
