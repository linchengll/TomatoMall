package com.example.tomatomall.vo;

import lombok.Data;

import java.util.List;

@Data
public class OrderBodyVO {
        private List<String> cartItemIds;

        private ShippingAddress shippingAddress;

        private String paymentMethod;
    }
