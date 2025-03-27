package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.vo.ProductStockpileVO;
import com.example.tomatomall.vo.ProductVO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Override
    public List<ProductVO> getProductList() {
        return Collections.emptyList();
    }

    @Override
    public ProductVO getProductById(String id) {
        return null;
    }

    @Override
    public Boolean updateProduct(ProductVO productVO) {
        return null;
    }

    @Override
    public ProductVO createProduct(ProductVO productVO) {
        return null;
    }

    @Override
    public Boolean deleteProduct(String id) {
        return null;
    }

    @Override
    public Boolean updateStockpile(String id, Integer amount) {
        return null;
    }

    @Override
    public ProductStockpileVO getStockpile(String id) {
        return null;
    }
}
