package com.example.tomatomall.controller;

import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.ProductSpecification;
import com.example.tomatomall.po.ProductStockpile;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.vo.ProductVO;
import com.example.tomatomall.vo.ProductStockpileVO;
import com.example.tomatomall.vo.Response;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/products")

public class ProductController {
    @Resource
    private ProductService productService;

    @GetMapping //获取所有用户
    public Response<List<ProductVO>> getProducts(){
        return Response.buildSuccess(productService.getProductList());
    }

    @GetMapping("/{id}") //获取指定用户 目标：id
    public Response<ProductVO> getProductById(@PathVariable String id){
        return Response.buildSuccess(productService.getProductById(id));
    }

    @PutMapping //更新商品信息 目标：productVO.id
    public Response<Boolean> updateProduct(@RequestBody ProductVO productVO){
        return Response.buildSuccess(productService.updateProduct(productVO));
    }

    @PostMapping
    public Response<ProductVO> createProduct(@RequestBody ProductVO productVO){
        return Response.buildSuccess(productService.createProduct(productVO));
    }

    @DeleteMapping("/{id}")
    public Response<String> deleteProduct(@PathVariable String id){
        return Response.buildSuccess(productService.deleteProduct(id));
    }

    @PatchMapping("/stockpile/{productId}")
    public Response<String> updateProductStockpile(@PathVariable("productId") String productId, @RequestParam("amount") Integer amount){
        return Response.buildSuccess(productService.updateStockpile(productId, amount));
    }

    @GetMapping("/stockpile/{productId}")
    public Response<ProductStockpileVO> getProductStockpile(@PathVariable("productId") String productId){
        return Response.buildSuccess(productService.getStockpile(productId));
    }

}
