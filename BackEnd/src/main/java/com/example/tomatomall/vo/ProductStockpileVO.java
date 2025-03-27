package com.example.tomatomall.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.tomatomall.po.ProductStockpile;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Setter
@NoArgsConstructor
public class ProductStockpileVO {
    private Integer id;
    private Integer amount;
    private Integer frozen;
    private Integer productId;

    public ProductStockpile toPO(){
        ProductStockpile po = new ProductStockpile();
        po.setId(this.id);
        po.setAmount(this.amount);
        po.setFrozen(this.frozen);
        po.setProductId(this.productId);
        return po;
    }
}
