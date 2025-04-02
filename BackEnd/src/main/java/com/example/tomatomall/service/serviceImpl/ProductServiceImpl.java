package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.ProductStockpile;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.repository.ProductStockpileRepository;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.vo.ProductStockpileVO;
import com.example.tomatomall.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductStockpileRepository productStockpileRepository;

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
        Product product = productRepository.findByTitle(productVO.getTitle());
        if(product!=null){
            throw TomatoMallException.productAlreadyExist();
        }
        Product newProduct =productVO.toPO();
        productRepository.save(newProduct);
        ProductStockpile productStockpile = new ProductStockpile();
        productStockpile.setProductId(newProduct.getId());
        productStockpile.setAmount(0);
        productStockpile.setFrozen(0);
        productStockpileRepository.save(productStockpile);
        return newProduct.toVO();
    }

    @Override
    public String deleteProduct(String id) {
        Product product = productRepository.findById(id);
        if(product==null){
            throw TomatoMallException.productNotExists();
        }
        productRepository.delete(product);
        ProductStockpile productStockpile =productStockpileRepository.findByProductId(id);
        productStockpileRepository.delete(productStockpile);
        return "删除成功";
    }

    @Override
    public String updateStockpile(String id, Integer amount) {
        ProductStockpile productStockpile =productStockpileRepository.findByProductId(id);
        if(productStockpile==null){
            throw TomatoMallException.productNotExists();
        }
        productStockpile.setAmount(amount);
        productStockpileRepository.save(productStockpile);
        return "调整库存成功";
    }

    @Override
    public ProductStockpileVO getStockpile(String id) {
        ProductStockpile productStockpile =productStockpileRepository.findByProductId(id);
        if(productStockpile==null){
            throw TomatoMallException.productNotExists();
        }
        return productStockpile.toVO();
    }
}
