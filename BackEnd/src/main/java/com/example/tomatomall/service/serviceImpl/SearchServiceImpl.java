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
        if(filterVO.getType()>0){//有类型过滤
            if(!typeRepository.existsById(filterVO.getType()))
                throw TomatoMallException.typeNotExists();
            List<ProductTypes> typeFiltered=productTypeRepository.findByTypeId(filterVO.getType());
            if (typeFiltered.isEmpty())
                return res;
            List<Integer> productIds=new ArrayList<>();
            for(ProductTypes item:typeFiltered)
                productIds.add(item.getProductId());
            if (filterVO.getSearchString().isEmpty()){//仅类型过滤
                List<Product> raw=productRepository.findByIdIn(productIds);
                for(Product po:raw)
                    res.add(productService.buildVO(po));
                res.sort((p1, p2) -> p2.getPopularity().compareTo(p1.getPopularity()));
            }else {//类型+搜索
                List<Product> raw=productRepository.searchWithType(productIds, filterVO.getSearchString());
                if(raw.isEmpty()) {
                    System.err.println("empty");
                    return res;
                }
                for(Product po:raw)
                    res.add(productService.buildVO(po));
            }
        }else if (!filterVO.getSearchString().isEmpty()){//仅搜索
            List<Product> raw=productRepository.searchWithoutType(filterVO.getSearchString());
            for(Product item:raw)
                res.add(productService.buildVO(item));
        }else{//全局
            List<Product> raw=productRepository.findAll();
            if(raw.isEmpty())
                return res;
            for(Product po:raw)
                res.add(productService.buildVO(po));
            res.sort((p1, p2) -> p2.getPopularity().compareTo(p1.getPopularity()));
        }
        return res;
    }
}
