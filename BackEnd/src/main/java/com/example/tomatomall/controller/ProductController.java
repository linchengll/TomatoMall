package com.example.tomatomall.controller;

import com.example.tomatomall.enums.RoleEnum;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.vo.ProductVO;
import com.example.tomatomall.vo.ProductStockpileVO;
import com.example.tomatomall.vo.ProductsVO;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")

public class ProductController {
    @Resource
    private ProductService productService;
    @Autowired
    private SecurityUtil securityUtil;

    @GetMapping //获取所有商品 权限：无
    public Response<List<ProductsVO>> getProducts(){
        return Response.buildSuccess(productService.getProductList());
    }

    @GetMapping("/{id}") //获取指定商品 目标：id 权限：无
    public Response<ProductsVO> getProductById(@PathVariable String id){
        return Response.buildSuccess(productService.getProductById(id));
    }

    @PutMapping //更新商品信息 目标：productVO.id 权限：admin
    public Response<String> updateProduct(@RequestBody ProductsVO productsVO){
        if(securityUtil.getCurrentUser().getRole().equals(RoleEnum.admin))
            return Response.buildSuccess(productService.updateProduct(productsVO));
        return Response.buildFailure("unauthorized","401");
    }

    @PostMapping //创建商品 权限：admin
    public Response<ProductsVO> createProduct(@RequestBody ProductsVO productsVO){
        if(securityUtil.getCurrentUser().getRole().equals(RoleEnum.admin))
            return Response.buildSuccess(productService.createProduct(productsVO));
        return Response.buildFailure("unauthorized","401");
    }

    @DeleteMapping("/{id}") //删除商品 目标：id 权限：admin
    public Response<String> deleteProduct(@PathVariable String id){
        if(securityUtil.getCurrentUser().getRole().equals(RoleEnum.admin))
            return Response.buildSuccess(productService.deleteProduct(id));
        return Response.buildFailure("unauthorized","401");
    }

    @PatchMapping("/stockpile/{productId}") //更新库存 目标：id 权限：admin
    public Response<String> updateProductStockpile(@PathVariable("productId") String productId, @RequestBody Map<Object,Integer> Body){
        if(securityUtil.getCurrentUser().getRole().equals(RoleEnum.admin))
            return Response.buildSuccess(productService.updateStockpile(productId, Body.get("amount")));
        return Response.buildFailure("unauthorized","401");
    }

    @GetMapping("/stockpile/{productId}") //获取库存 目标：id 权限：无
    public Response<ProductStockpileVO> getProductStockpile(@PathVariable("productId") String productId){
        return Response.buildSuccess(productService.getStockpile(productId));
    }

}
