package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.enums.RoleEnum;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.*;
import com.example.tomatomall.repository.*;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.vo.ProductSpecificationVO;
import com.example.tomatomall.vo.ProductStockpileVO;
import com.example.tomatomall.vo.ProductVO;
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
    public List<ProductVO> getProductList() {
        List<Product> raw=productRepository.findAll();
        List<ProductVO> VO=new ArrayList<>();
        if(raw.isEmpty())
            return VO;
        for(Product po:raw){
            ProductVO productVO =new ProductVO();
            productVO.setId(po.getId());
            productVO.setTitle(po.getTitle());
            productVO.setPrice(po.getPrice());
            productVO.setRate(po.getRate());
            productVO.setDescription(po.getDescription());
            productVO.setCover(po.getCover());
            productVO.setDetail(po.getDetail());
            Set<ProductSpecification> productSpecifications =productSpecificationRepository.findByProductId(po.getId());
            Set<ProductSpecificationVO> productSpecificationsVO=new HashSet<>();
            for(ProductSpecification ps:productSpecifications){
                productSpecificationsVO.add(ps.toVO());
            }
            productVO.setSpecifications(productSpecificationsVO);
            VO.add(productVO);
        }
        return VO;
    }

    @Override
    public ProductVO getProductById(String id) {
        Product product;
        ProductVO productVO =new ProductVO();
        if(productRepository.findById(new Integer(id)).isPresent()){
            product=productRepository.findById(new Integer(id)).get();
            productVO.setId(product.getId());
            productVO.setTitle(product.getTitle());
            productVO.setPrice(product.getPrice());
            productVO.setRate(product.getRate());
            productVO.setDescription(product.getDescription());
            productVO.setCover(product.getCover());
            productVO.setDetail(product.getDetail());
            Set<ProductSpecification> productSpecifications =productSpecificationRepository.findByProductId(product.getId());
            Set<ProductSpecificationVO> productSpecificationsVO=new HashSet<>();
            for(ProductSpecification ps:productSpecifications){
                productSpecificationsVO.add(ps.toVO());
            }
            productVO.setSpecifications(productSpecificationsVO);
        }
        else
            throw TomatoMallException.productNotExists();
        return productVO;
    }

    @Override
    public String updateProduct(ProductVO productVO) {
        if(!securityUtil.getCurrentUser().getRole().equals(RoleEnum.admin))
            throw TomatoMallException.unauthorized();
        Product product;
        Set<ProductSpecification> productSpecification;
        if(productRepository.findById(productVO.getId()).isPresent()) {
            product = productRepository.findById(productVO.getId()).get();
            productSpecification = productSpecificationRepository.findByProductId(product.getId());
        }else
            throw TomatoMallException.productNotExists();
        if(productVO.getTitle()!=null){
            product.setTitle(productVO.getTitle());
        }
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
        if(productVO.getSpecifications()!=null&&!productVO.getSpecifications().isEmpty()){
            if(productSpecification!=null&&!productSpecification.isEmpty()){
            productSpecificationRepository.deleteAll(productSpecification);
            }
            productSpecification = productVO.toSpecificationPO();
            productSpecificationRepository.saveAll(productSpecification);
        }
        return "更新成功";
    }

    @Override
    public ProductVO createProduct(ProductVO productVO) {
        if(!securityUtil.getCurrentUser().getRole().equals(RoleEnum.admin))
            throw TomatoMallException.unauthorized();
        if(productRepository.findByTitle(productVO.getTitle())!=null)
            throw TomatoMallException.productAlreadyExist();
        Product newProduct=new Product();
        newProduct.setCover(productVO.getCover());
        newProduct.setDescription(productVO.getDescription());
        newProduct.setDetail(productVO.getDetail());
        newProduct.setPrice(productVO.getPrice());
        newProduct.setRate(productVO.getRate());
        newProduct.setTitle(productVO.getTitle());
        productRepository.save(newProduct);
        productVO.setId(productRepository.findByTitle(productVO.getTitle()).getId());
        Set<ProductSpecificationVO> productSpecificationVO = productVO.getSpecifications();
        if(productSpecificationVO!=null&&!productSpecificationVO.isEmpty())
            for(ProductSpecificationVO ps:productSpecificationVO){
                ps.setProductId(productVO.getId());
                ProductSpecification po=ps.toPO();
                productSpecificationRepository.save(po);
            }
        ProductStockpile productStockpile = new ProductStockpile();
        productStockpile.setProductId(newProduct.getId());
        productStockpile.setAmount(0);
        productStockpile.setFrozen(0);
        productStockpileRepository.save(productStockpile);
        return productVO;
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
        //级联删除
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
