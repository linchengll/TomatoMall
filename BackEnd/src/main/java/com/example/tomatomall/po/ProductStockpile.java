package com.example.tomatomall.po;

import com.example.tomatomall.vo.ProductStockpileVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "product_stockpile")
public class ProductStockpile {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @Basic
    @Column(name = "amount")
    private Integer amount;

    @Basic
    @Column(name = "frozen")
    private Integer frozen;

    @Basic
    @Column(name = "product_id")
    private Integer productId;

    public ProductStockpileVO toVO(){
        ProductStockpileVO VO=new ProductStockpileVO();
        VO.setId(this.id);
        VO.setAmount(this.amount);
        VO.setFrozen(this.frozen);
        VO.setProductId(this.productId);
        return VO;
    }
}
