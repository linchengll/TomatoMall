package com.example.tomatomall.service;
import java.util.List;
import com.example.tomatomall.vo.ProductVO;
import com.example.tomatomall.vo.ProductStockpileVO;
public interface ProductService {
    List<ProductVO> getProductList();
    ProductVO getProductById(String id);
    String updateProduct(ProductVO productVO);
    ProductVO createProduct(ProductVO productVO);
    String deleteProduct(String id);
    String updateStockpile(String id, Integer amount);
    ProductStockpileVO getStockpile(String id);
}
