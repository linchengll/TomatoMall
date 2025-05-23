package com.example.tomatomall.service;

import com.example.tomatomall.vo.FilterVO;
import com.example.tomatomall.vo.PagedProductVO;
import com.example.tomatomall.vo.ProductBasicVO;

import java.util.List;

public interface SearchService {
    List<ProductBasicVO> getTop();
    PagedProductVO search(FilterVO filterVO);
}
