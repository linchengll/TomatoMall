package com.example.tomatomall.service;
import java.util.List;
import com.example.tomatomall.vo.ProductVO;
import com.example.tomatomall.vo.ProductStockpileVO;
import com.example.tomatomall.vo.ProductsVO;

public interface ProductService {
    List<ProductsVO> getProductList();
    ProductsVO getProductById(String id);
    String updateProduct(ProductsVO productsVO);
    ProductsVO createProduct(ProductsVO productsVO);
    String deleteProduct(String id);
    String updateStockpile(String id, Integer amount);
    ProductStockpileVO getStockpile(String id);
}
