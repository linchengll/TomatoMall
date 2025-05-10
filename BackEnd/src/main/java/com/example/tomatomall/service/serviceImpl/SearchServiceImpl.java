package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.ProductTypes;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.repository.ProductTypeRepository;
import com.example.tomatomall.repository.TypeRepository;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.service.SearchService;
import com.example.tomatomall.vo.FilterVO;
import com.example.tomatomall.vo.ProductBasicVO;
import com.example.tomatomall.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    TypeRepository typeRepository;
    @Autowired
    ProductTypeRepository productTypeRepository;
    @Autowired
    ProductService productService;

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
        List<ProductVO> res=new ArrayList<>();
        if(filterVO.getType()>0){
            if(!typeRepository.existsById(filterVO.getType()))
                throw TomatoMallException.typeNotExists();
            List<ProductTypes> typeFiltered=productTypeRepository.findByTypeId(filterVO.getType());
            if (typeFiltered.isEmpty())
                return res;
            if (filterVO.getSearchString().isEmpty()){
                for(ProductTypes item:typeFiltered)
                    res.add(productService.buildVO(productRepository.findById(item.getProductId()).get()));
                res.sort((p1, p2) -> p2.getPopularity().compareTo(p1.getPopularity()));
            }else {
                for(ProductTypes item:typeFiltered) {
                    if(productRepository.searchTitle(item.getProductId(), filterVO.getSearchString()).isPresent())
                        res.add(productService.buildVO(productRepository.searchTitle(item.getProductId(), filterVO.getSearchString()).get()));
                }
                //
            }
        }else {
            List<Product> raw=productRepository.findByTitleLike("%"+filterVO.getSearchString()+"%");
            for(Product item:raw)
                res.add(productService.buildVO(item));
            //
        }
        return res;
    }
}
