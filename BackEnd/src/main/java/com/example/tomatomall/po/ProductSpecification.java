package com.example.tomatomall.po;

import com.example.tomatomall.po.compositKey.SpecificationId;
import com.example.tomatomall.vo.ProductSpecificationVO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class ProductSpecification {
    @EmbeddedId
    private SpecificationId specId;

    @Basic
    @Column(name = "item")
    private String item;

    @Basic
    @Column(name = "value")
    private String value;


    public ProductSpecification() {
        this.specId = new SpecificationId();
    }
    public ProductSpecificationVO toVO(){
        ProductSpecificationVO VO=new ProductSpecificationVO();
        VO.setId(this.getId());
        VO.setItem(this.getItem());
        VO.setValue(this.getValue());
        VO.setProductId(this.getProductId());
        return VO;
    }

    public Integer getId() {
        return this.getSpecId().getId();
    }
    public Integer getProductId(){
        return this.getSpecId().getProductId();
    }
    public void setId(Integer id){
        this.getSpecId().setId(id);
    }
    public void setProductId(Integer productId){
        this.getSpecId().setProductId(productId);
    }
}
