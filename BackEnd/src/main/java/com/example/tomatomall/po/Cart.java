package com.example.tomatomall.po;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Cart {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name= "cart_item_id")
    private Integer cartItemId;

    @Column(name= "user_id")
    private Integer userId;

    @Column(name= "product_id")
    private Integer productId;

    @Column(name="quantity")
    private Integer quantity = 1;

    @Column(name="ordered")
    private boolean ordered = false;

    @Column(name="discount")
    private Integer discount;
}
