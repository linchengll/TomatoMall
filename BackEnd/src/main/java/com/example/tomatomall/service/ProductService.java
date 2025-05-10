package com.example.tomatomall.service;

import com.example.tomatomall.po.Product;
import com.example.tomatomall.vo.ProductStockpileVO;
import com.example.tomatomall.vo.ProductVO;

import java.util.List;

public interface ProductService {
    ProductVO buildVO(Product po);
    List<ProductVO> getProductList();
    ProductVO getProductById(String id);
    String updateProduct(ProductVO productVO);
    ProductVO createProduct(ProductVO productVO);
    String deleteProduct(String id);
    String updateStockpile(String id, Integer amount);
    ProductStockpileVO getStockpile(String id);
}
