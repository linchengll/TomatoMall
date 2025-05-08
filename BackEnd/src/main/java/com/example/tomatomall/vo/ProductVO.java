package com.example.tomatomall.vo;

import com.example.tomatomall.po.ProductSpecification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ProductVO {
    private Integer id;
    private String title;
    private Float price;
    private Float rate;
    private String description;
    private String cover;
    private String detail;
    private Integer popularity;
    private Set<ProductSpecificationVO> specifications;
    private Set<TypeVO> types;

    public Set<ProductSpecification> toSpecificationPO(){
        Set<ProductSpecification> productSpecifications=new HashSet<>();
        for(ProductSpecificationVO specificationVO : this.specifications){
            specificationVO.setProductId(this.id);
            productSpecifications.add(specificationVO.toPO());
        }
        return productSpecifications;
    }
}
