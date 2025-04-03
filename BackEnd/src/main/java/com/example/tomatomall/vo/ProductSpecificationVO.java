package com.example.tomatomall.vo;

import com.example.tomatomall.po.ProductSpecification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductSpecificationVO {
    private Integer id;
    private Integer productId;
    private String item;
    private String value;

    public ProductSpecification toPO(){
        ProductSpecification po = new ProductSpecification();
        po.setId(this.id);
        po.setItem(this.item);
        po.setValue(this.value);
        po.setProductId(this.productId);
        return po;
    }
}
