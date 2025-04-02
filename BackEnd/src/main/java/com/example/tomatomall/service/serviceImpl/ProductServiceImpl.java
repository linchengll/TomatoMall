package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.ProductSpecification;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.vo.ProductStockpileVO;
import com.example.tomatomall.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Override
    public List<ProductVO> getProductList() {
        List<Product> raw=productRepository.findAll();
        List<ProductVO> VO=new ArrayList<>();
        if(raw.isEmpty())
            return VO;
        for(Product po:raw)
            VO.add(po.toVO());
        return VO;
    }

    @Override
    public ProductVO getProductById(String id) {
        Product product;
        if(productRepository.findById(new Integer(id)).isPresent())
            product=productRepository.findById(new Integer(id)).get();
        else
            throw TomatoMallException.productNotExists();
        return product.toVO();
    }

    @Override
    public Boolean updateProduct(ProductVO productVO) {
        Product product;
        if(productRepository.findById(productVO.getId()).isPresent())
            product=productRepository.findById(productVO.getId()).get();
        else
            throw TomatoMallException.productNotExists();
        if(productVO.getTitle()!=null)
            product.setTitle(productVO.getTitle());
        if(productVO.getPrice()!=null)
            product.setPrice(productVO.getPrice());
        if(productVO.getRate()!=null)
            product.setRate(productVO.getRate());
        if(productVO.getDescription()!=null)
            product.setDescription(productVO.getDescription());
        if(productVO.getCover()!=null)
            product.setCover(productVO.getCover());
        if(productVO.getDetail()!=null)
            product.setDetail(productVO.getDetail());
        if(productVO.getSpecifications()!=null&&!productVO.getSpecifications().isEmpty())
            product.setSpecifications(product.getSpecifications());
        return true;
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
