package com.example.tomatomall.service;

import com.example.tomatomall.vo.FilterVO;
import com.example.tomatomall.vo.ProductBasicVO;
import com.example.tomatomall.vo.ProductVO;

import java.util.List;

public interface SearchService {
    List<ProductBasicVO> getTop();
    List<ProductVO> search(FilterVO filterVO);
}
