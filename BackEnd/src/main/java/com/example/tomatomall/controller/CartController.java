package com.example.tomatomall.controller;


import com.example.tomatomall.service.CartService;
import com.example.tomatomall.vo.CartItemsVO;
import com.example.tomatomall.vo.CartVO;
import com.example.tomatomall.vo.Response;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Resource
    private CartService cartService;

    @PostMapping
    public Response<CartVO>addCart(@RequestBody Map<Object,Integer> Body) {
        return Response.buildSuccess(cartService.addCart(String.valueOf(Body.get("productId")),Body.get("quantity")));
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


}
