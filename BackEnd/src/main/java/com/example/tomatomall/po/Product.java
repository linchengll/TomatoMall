package com.example.tomatomall.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.tomatomall.vo.ProductVO;
import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "product")
public class Product {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer Id;

    @Basic
    @Column(name = "title")
    private String title;//书籍名 NOT NULL 既然还有商品规格说明，这一项应该仅指书名

    @Basic
    @Column(name = "price")
    private Float price;//书籍价格 NOT NULL 非负 与商品规格有矛盾，无意义

    @Basic
    @Column(name = "rate")
    private Float rate;//书籍评分 0~10 在创建更新时必填，极其不合理

    @Basic
    @Column(name = "description")
    private String description;//

    @Basic
    @Column(name = "cover")
    private String cover;//应该也是url

    @Basic
    @Column(name = "detail")
    private String detail;//描述分两块是想干什么

    @OneToMany(mappedBy = "product", cascade = {CascadeType.ALL},orphanRemoval = true)
    private List<ProductSpecification> product_specification;

    @OneToMany(mappedBy = "products", cascade = {CascadeType.ALL},orphanRemoval = true)
    private List<ProductStockpile> product_stockpile;
    public ProductVO toVO() {
        ProductVO productVO = new ProductVO();
        productVO.setId(Id);
        productVO.setTitle(title);
        productVO.setPrice(price);
        productVO.setRate(rate);
        productVO.setDescription(description);
        productVO.setCover(cover);
        productVO.setDetail(detail);
        return productVO;
    }
}
