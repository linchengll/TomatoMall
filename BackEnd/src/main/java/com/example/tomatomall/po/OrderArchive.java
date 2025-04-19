package com.example.tomatomall.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class OrderArchive{//用于将订单相应的购物车信息记录到数据库
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name= "cart_item_id")
    private Integer cartItemId;

    @Column(name= "user_id")
    private Integer userId;

    @Column(name= "product_id")
    private Integer productId;

    @Column(name="quantity")
    private Integer quantity;

    @Column(name="order_id")
    private Integer orderId;
}
