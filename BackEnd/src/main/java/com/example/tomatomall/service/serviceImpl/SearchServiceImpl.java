package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.po.Product;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.service.SearchService;
import com.example.tomatomall.vo.FilterVO;
import com.example.tomatomall.vo.ProductBasicVO;
import com.example.tomatomall.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    ProductRepository productRepository;

    @Override
    public List<ProductBasicVO> getTop() {
        List<Product> raw=productRepository.getTop();
        List<ProductBasicVO> res=new ArrayList<>();
        for(Product PO:raw)
            res.add(PO.toVO());
        return res;
    }

    @Override
    public List<ProductVO> search(FilterVO filterVO) {
        return Collections.emptyList();
    }
}
