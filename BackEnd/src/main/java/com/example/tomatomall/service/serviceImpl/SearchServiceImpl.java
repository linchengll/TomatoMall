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
import com.example.tomatomall.vo.PagedProductVO;
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

    private static final int pageSizeLimit=200;

    @Override
    public List<ProductBasicVO> getTop() {
        List<Product> raw=productRepository.getTop();
        List<ProductBasicVO> res=new ArrayList<>();
        for(Product PO:raw)
            res.add(PO.toVO());
        return res;
    }

    @Override
    public PagedProductVO search(FilterVO filterVO) {
        filterVO.setSearchString(filterVO.getSearchString().replaceAll("[,;%$&^|'\"]"," ").replaceAll("\\s+", "|").replaceAll("^\\||\\|$", ""));
        if(filterVO.getSize()<=0||filterVO.getSize()>pageSizeLimit)
            throw TomatoMallException.sizeInvalid(filterVO.getSize(), pageSizeLimit);
        if(filterVO.getPage()<0)
            throw TomatoMallException.pageInvalid(filterVO.getPage());
        PagedProductVO pagedProductVO=new PagedProductVO();
        List<ProductVO> productVO =new ArrayList<>();
        pagedProductVO.setPage(filterVO.getPage());
        pagedProductVO.setSize(filterVO.getSize());
        pagedProductVO.setProductList(productVO);
        if(filterVO.getType()>0){//有类型过滤
            if(!typeRepository.existsById(filterVO.getType()))
                throw TomatoMallException.typeNotExists();
            List<ProductTypes> typeFiltered=productTypeRepository.findByTypeId(filterVO.getType());
            if (typeFiltered.isEmpty()) {
                if(pagedProductVO.getPage()>0)
                    throw TomatoMallException.pageInvalid(pagedProductVO.getPage());
                return pagedProductVO;
            }
            List<Integer> productIds=new ArrayList<>();
            for(ProductTypes item:typeFiltered)
                productIds.add(item.getProductId());
            if (filterVO.getSearchString().isEmpty()){//仅类型过滤
                pagedProductVO.setItemCount(productIds.size());
                pagedProductVO.setPageCount(pagedProductVO.getItemCount()/pagedProductVO.getSize()+((pagedProductVO.getItemCount()%pagedProductVO.getSize())>0?1:0));
                if(pagedProductVO.getPage()!=0&&pagedProductVO.getPage()>=pagedProductVO.getPageCount())
                    throw TomatoMallException.pageInvalid(pagedProductVO.getPage());
                List<Product> raw=productRepository.searchType(productIds, filterVO.getPage(), filterVO.getSize());
                for(Product po:raw)
                    productVO.add(productService.buildVO(po));
                productVO.sort((p1, p2) -> p2.getPopularity().compareTo(p1.getPopularity()));
            }else {//类型+搜索
                pagedProductVO.setItemCount(productRepository.countWithType(productIds, filterVO.getSearchString()));
                pagedProductVO.setPageCount(pagedProductVO.getItemCount()/pagedProductVO.getSize()+((pagedProductVO.getItemCount()%pagedProductVO.getSize())>0?1:0));
                if(pagedProductVO.getPage()!=0&&pagedProductVO.getPage()>=pagedProductVO.getPageCount())
                    throw TomatoMallException.pageInvalid(pagedProductVO.getPage());
                List<Product> raw=productRepository.searchWithType(productIds, filterVO.getSearchString(), filterVO.getPage(), filterVO.getSize());
                if(raw.isEmpty())
                    return pagedProductVO;
                for(Product po:raw)
                    productVO.add(productService.buildVO(po));
            }
        }else if (!filterVO.getSearchString().isEmpty()){//仅搜索
            pagedProductVO.setItemCount(productRepository.countWithoutType(filterVO.getSearchString()));
            pagedProductVO.setPageCount(pagedProductVO.getItemCount()/pagedProductVO.getSize()+((pagedProductVO.getItemCount()%pagedProductVO.getSize())>0?1:0));
            if(pagedProductVO.getPage()!=0&&pagedProductVO.getPage()>=pagedProductVO.getPageCount())
                throw TomatoMallException.pageInvalid(pagedProductVO.getPage());
            List<Product> raw=productRepository.searchWithoutType(filterVO.getSearchString(),filterVO.getPage(), filterVO.getSize());
            for(Product item:raw)
                productVO.add(productService.buildVO(item));
        }else{//全局
            pagedProductVO.setItemCount((int) productRepository.count());
            pagedProductVO.setPageCount(pagedProductVO.getItemCount()/pagedProductVO.getSize()+((pagedProductVO.getItemCount()%pagedProductVO.getSize())>0?1:0));
            if(pagedProductVO.getPage()!=0&&pagedProductVO.getPage()>=pagedProductVO.getPageCount())
                throw TomatoMallException.pageInvalid(pagedProductVO.getPage());
            List<Product> raw=productRepository.searchAll(filterVO.getPage(), filterVO.getSize());
            if(raw.isEmpty())
                return pagedProductVO;
            for(Product po:raw)
                productVO.add(productService.buildVO(po));
            productVO.sort((p1, p2) -> p2.getPopularity().compareTo(p1.getPopularity()));
        }
        pagedProductVO.setProductList(productVO);

        return pagedProductVO;
    }
}
