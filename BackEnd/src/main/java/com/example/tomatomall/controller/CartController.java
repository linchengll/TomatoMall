package com.example.tomatomall.controller;


import com.example.tomatomall.service.CartService;
import com.example.tomatomall.vo.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Resource
    private CartService cartService;

    @PostMapping
    public Response<CartVO> addCart(@RequestBody Map<Object,Float> Body) {
        return Response.buildSuccess(cartService.addCart(Math.round(Body.get("productId")),Math.round(Body.get("quantity")),Body.get("discount")));
    }

    @DeleteMapping("/{cartItemId}")
    public Response<String> deleteCartItem(@PathVariable String cartItemId) {
        return Response.buildSuccess(cartService.deleteCartItem(cartItemId));
    }

    @PatchMapping("/{cartItemId}")
    public Response<String> updateCartItem(@PathVariable String cartItemId, @RequestBody Map<Object,Integer> Body) {
        return Response.buildSuccess(cartService.updateCartItem(cartItemId,Body.get("quantity")));
    }

    @GetMapping
    public Response<CartItemsVO> getCart() {
        return Response.buildSuccess(cartService.getCartList());
    }

    @PostMapping("/checkout")
    public Response<OrderVO> submitOrder(@RequestBody OrderBodyVO Body){
        return Response.buildSuccess(cartService.submitOrder(Body.getCartItemIds(), Body.getShippingAddress(), Body.getPaymentMethod()));
    }
}
