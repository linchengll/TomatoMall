package com.example.tomatomall.po;

import com.example.tomatomall.vo.ProductSpecificationVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ProductSpecification {
    @Id

    @Basic
    @Column(name = "id")
    private Integer id;

    @Basic
    @Column(name = "item")
    private String item;

    @Basic
    @Column(name = "value")
    private String value;

    @Basic
    @Column(name = "product_id")
    private Integer productId;

    public ProductSpecificationVO toVO(){
        ProductSpecificationVO VO=new ProductSpecificationVO();
        VO.setId(this.id);
        VO.setItem(this.item);
        VO.setValue(this.value);
        VO.setProductId(this.productId);
        return VO;
    }
}
