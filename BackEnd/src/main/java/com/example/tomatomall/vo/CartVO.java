package com.example.tomatomall.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CartVO {
    private Integer cartItemId;
    private Integer productId;
    private String title;
    private Float price;
    private String description;
    private String cover;
    private String detail;
    private Integer quantity;
    private Integer discount;
    private boolean ordered;
}
