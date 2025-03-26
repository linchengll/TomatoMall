package com.example.tomatomall.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Product {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "productId")
    private Integer productId;//文档里id用了两种名称，这里统一

    @Basic
    @Column(name = "title")
    private String title;//书籍名 NOT NULL 既然还有商品规格说明，这一项应该仅指书名

    @Basic
    @Column(name = "price")
    private Double price;//书籍价格 NOT NULL 非负 与商品规格有矛盾，无意义

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

//    @Basic
//    @Column(name = "specifications")
//    private Set<Spec> specifications;
//
//    private class Spec{
//        private String item;//名称
//        private String value;//规格内容 这参数名什么鬼
//        private Integer productId;//毫无意义，除非products不持有它的引用
//    }
}
