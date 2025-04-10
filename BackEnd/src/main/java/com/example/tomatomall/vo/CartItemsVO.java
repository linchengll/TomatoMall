package com.example.tomatomall.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CartItemsVO {
    private List<CartVO> items;
    private Integer total;
    private Float totalAmount;
}
