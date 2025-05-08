package com.example.tomatomall.service;

import com.example.tomatomall.vo.TypeVO;

public interface TypeService {
    TypeVO createType(TypeVO typeVO);
    String deleteType(String typeId);
}
