package com.example.tomatomall.vo;

import com.example.tomatomall.po.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductBasicVO {
    private Integer id;
    private String title;
    private Float price;
    private Float rate;
    private String description;
    private String cover;
    private String detail;
    private Integer popularity;
    public Product toPO() {
        Product product = new Product();
        product.setId(this.id);
        product.setTitle(this.title);
        product.setPrice(this.price);
        product.setRate(this.rate);
        product.setDescription(this.description);
        product.setCover(this.cover);
        product.setDetail(this.detail);
        return product;
    }
}
