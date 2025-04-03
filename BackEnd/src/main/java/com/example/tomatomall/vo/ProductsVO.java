package com.example.tomatomall.vo;


import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.ProductSpecification;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
public class ProductsVO {
    private Integer id;
    private String title;
    private Float price;
    private Float rate;
    private String description;
    private String cover;
    private String detail;
    private Set<ProductSpecificationVO> specifications;

    public Set<ProductSpecification> toPO(){
        Set<ProductSpecification> productSpecifications=new HashSet<>();
        for(ProductSpecificationVO specificationVO : this.specifications){
            productSpecifications.add(specificationVO.toPO());
        }
        return productSpecifications;
    }
}
