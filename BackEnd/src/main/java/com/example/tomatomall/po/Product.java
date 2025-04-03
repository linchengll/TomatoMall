package com.example.tomatomall.po;

import com.example.tomatomall.vo.ProductVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Product {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @Basic
    @Column(name = "title")
    private String title;//书籍名 NOT NULL

    @Basic
    @Column(name = "price")
    private Float price;//书籍价格 NOT NULL

    @Basic
    @Column(name = "rate")
    private Float rate;//书籍评分 0~10

    @Basic
    @Column(name = "description")
    private String description;//

    @Basic
    @Column(name = "cover")
    private String cover;//url

    @Basic
    @Column(name = "detail")
    private String detail;

    public ProductVO toVO(){
        ProductVO VO=new ProductVO();
        VO.setId(this.id);
        VO.setTitle(this.title);
        VO.setPrice(this.price);
        VO.setRate(this.rate);
        VO.setDescription(this.description);
        VO.setCover(this.cover);
        VO.setDetail(this.detail);
        return VO;
    }
}
