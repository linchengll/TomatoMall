package com.example.tomatomall.vo;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductOfOrderVO {
    private Integer productId;
    private String productName;
    private String productImage;
    private Integer productQuantity;
    private Float productPrice;
}
