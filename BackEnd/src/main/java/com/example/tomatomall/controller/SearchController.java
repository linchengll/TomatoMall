package com.example.tomatomall.controller;

import com.example.tomatomall.service.SearchService;
import com.example.tomatomall.vo.ProductBasicVO;
import com.example.tomatomall.vo.ProductVO;
import com.example.tomatomall.vo.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    @Resource
    SearchService searchService;

    @GetMapping("/top")
    public Response<List<ProductBasicVO>> getTop(){
        return Response.buildSuccess(searchService.getTop());
    }

    @PostMapping
    public Response<List<ProductVO>> search(){
        return Response.buildSuccess(null);
    }
}
