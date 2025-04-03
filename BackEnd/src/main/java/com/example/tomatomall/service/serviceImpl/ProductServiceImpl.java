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

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductStockpileRepository productStockpileRepository;

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
    public String updateProduct(ProductVO productVO) {
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
        return "更新成功";
    }

    @Override
    public ProductVO createProduct(ProductVO productVO) {
        if(productRepository.findByTitle(productVO.getTitle())!=null)
            throw TomatoMallException.productAlreadyExist();
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
        Product product;
        if(productRepository.findById(new Integer(id)).isPresent())
            product=productRepository.findById(new Integer(id)).get();
        else
            throw TomatoMallException.productNotExists();
        productRepository.delete(product);
        ProductStockpile productStockpile =productStockpileRepository.findByProductId(new Integer(id));
        productStockpileRepository.delete(productStockpile);
        return "删除成功";
    }

    @Override
    public String updateStockpile(String id, Integer amount) {
        ProductStockpile productStockpile =productStockpileRepository.findByProductId(new Integer(id));
        if(productStockpile==null){
            throw TomatoMallException.productNotExists();
        }
        productStockpile.setAmount(amount);
        productStockpileRepository.save(productStockpile);
        return "调整库存成功";
    }

    @Override
    public ProductStockpileVO getStockpile(String id) {
        ProductStockpile productStockpile =productStockpileRepository.findByProductId(new Integer(id));
        if(productStockpile==null){
            throw TomatoMallException.productNotExists();
        }
        return productStockpile.toVO();
    }
}
