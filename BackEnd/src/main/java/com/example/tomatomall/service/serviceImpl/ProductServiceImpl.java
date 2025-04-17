package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.enums.RoleEnum;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.*;
import com.example.tomatomall.repository.*;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.vo.ProductSpecificationVO;
import com.example.tomatomall.vo.ProductStockpileVO;
import com.example.tomatomall.vo.ProductsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductStockpileRepository productStockpileRepository;
    @Autowired
    ProductSpecificationRepository productSpecificationRepository;
    @Autowired
    AdvertisementRepository advertisementRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    private SecurityUtil securityUtil;

    @Override
    public List<ProductsVO> getProductList() {
        List<Product> raw=productRepository.findAll();
        List<ProductsVO> VO=new ArrayList<>();
        if(raw.isEmpty())
            return VO;
        for(Product po:raw){
            ProductsVO productsVO=new ProductsVO();
            productsVO.setId(po.getId());
            productsVO.setTitle(po.getTitle());
            productsVO.setPrice(po.getPrice());
            productsVO.setRate(po.getRate());
            productsVO.setDescription(po.getDescription());
            productsVO.setCover(po.getCover());
            productsVO.setDetail(po.getDetail());
            Set<ProductSpecification> productSpecifications =productSpecificationRepository.findByProductId(po.getId());
            Set<ProductSpecificationVO> productSpecificationsVO=new HashSet<>();
            for(ProductSpecification ps:productSpecifications){
                productSpecificationsVO.add(ps.toVO());
            }
            productsVO.setSpecifications(productSpecificationsVO);
            VO.add(productsVO);
        }
        return VO;
    }

    @Override
    public ProductsVO getProductById(String id) {
        Product product;
        ProductsVO productsVO=new ProductsVO();
        if(productRepository.findById(new Integer(id)).isPresent()){
            product=productRepository.findById(new Integer(id)).get();
            productsVO.setId(product.getId());
            productsVO.setTitle(product.getTitle());
            productsVO.setPrice(product.getPrice());
            productsVO.setRate(product.getRate());
            productsVO.setDescription(product.getDescription());
            productsVO.setCover(product.getCover());
            productsVO.setDetail(product.getDetail());
            Set<ProductSpecification> productSpecifications =productSpecificationRepository.findByProductId(product.getId());
            Set<ProductSpecificationVO> productSpecificationsVO=new HashSet<>();
            for(ProductSpecification ps:productSpecifications){
                productSpecificationsVO.add(ps.toVO());
            }
            productsVO.setSpecifications(productSpecificationsVO);
        }
        else
            throw TomatoMallException.productNotExists();
        return productsVO;
    }

    @Override
    public String updateProduct(ProductsVO productsVO) {
        if(!securityUtil.getCurrentUser().getRole().equals(RoleEnum.admin))
            throw TomatoMallException.unauthorized();
        Product product;
        Set<ProductSpecification> productSpecification;
        if(productRepository.findById(productsVO.getId()).isPresent()) {
            product = productRepository.findById(productsVO.getId()).get();
            productSpecification = productSpecificationRepository.findByProductId(product.getId());
        }else
            throw TomatoMallException.productNotExists();
        if(productsVO.getTitle()!=null){
            product.setTitle(productsVO.getTitle());
        }
        if(productsVO.getPrice()!=null)
            product.setPrice(productsVO.getPrice());
        if(productsVO.getRate()!=null)
            product.setRate(productsVO.getRate());
        if(productsVO.getDescription()!=null)
            product.setDescription(productsVO.getDescription());
        if(productsVO.getCover()!=null)
            product.setCover(productsVO.getCover());
        if(productsVO.getDetail()!=null)
            product.setDetail(productsVO.getDetail());
        if(productsVO.getSpecifications()!=null&&!productsVO.getSpecifications().isEmpty()){
            if(productSpecification!=null&&!productSpecification.isEmpty()){
            productSpecificationRepository.deleteAll(productSpecification);
            }
            productSpecification =productsVO.toSpecificationPO();
            productSpecificationRepository.saveAll(productSpecification);
        }
        return "更新成功";
    }

    @Override
    public ProductsVO createProduct(ProductsVO productsVO) {
        if(!securityUtil.getCurrentUser().getRole().equals(RoleEnum.admin))
            throw TomatoMallException.unauthorized();
        if(productRepository.findByTitle(productsVO.getTitle())!=null)
            throw TomatoMallException.productAlreadyExist();
        Product newProduct=new Product();
        newProduct.setCover(productsVO.getCover());
        newProduct.setDescription(productsVO.getDescription());
        newProduct.setDetail(productsVO.getDetail());
        newProduct.setPrice(productsVO.getPrice());
        newProduct.setRate(productsVO.getRate());
        newProduct.setTitle(productsVO.getTitle());
        productRepository.save(newProduct);
        productsVO.setId(productRepository.findByTitle(productsVO.getTitle()).getId());
        Set<ProductSpecificationVO> productSpecificationVO =productsVO.getSpecifications();
        if(productSpecificationVO!=null&&!productSpecificationVO.isEmpty())
            for(ProductSpecificationVO ps:productSpecificationVO){
                ps.setProductId(productsVO.getId());
                ProductSpecification po=ps.toPO();
                productSpecificationRepository.save(po);
            }
        ProductStockpile productStockpile = new ProductStockpile();
        productStockpile.setProductId(newProduct.getId());
        productStockpile.setAmount(0);
        productStockpile.setFrozen(0);
        productStockpileRepository.save(productStockpile);
        return productsVO;
    }

    @Override
    public String deleteProduct(String id) {
        if(!securityUtil.getCurrentUser().getRole().equals(RoleEnum.admin))
            throw TomatoMallException.unauthorized();
        Product product;
        if(productRepository.findById(new Integer(id)).isPresent())
            product=productRepository.findById(new Integer(id)).get();
        else
            throw TomatoMallException.productNotExists();
        productRepository.delete(product);
        Set<ProductSpecification> productSpecifications= productSpecificationRepository.findByProductId(new Integer(id));
        productSpecificationRepository.deleteAll(productSpecifications);
        ProductStockpile productStockpile =productStockpileRepository.findByProductId(new Integer(id));
        productStockpileRepository.delete(productStockpile);
        List<Cart> carts=cartRepository.findByProductId(new Integer(id));
        cartRepository.deleteAll(carts);
        List<Advertisement> advertisements=advertisementRepository.findByProductId(new Integer(id));
        advertisementRepository.deleteAll(advertisements);
        return "删除成功";
    }

    @Override
    public String updateStockpile(String id, Integer amount) {
        if(!securityUtil.getCurrentUser().getRole().equals(RoleEnum.admin))
            throw TomatoMallException.unauthorized();
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
