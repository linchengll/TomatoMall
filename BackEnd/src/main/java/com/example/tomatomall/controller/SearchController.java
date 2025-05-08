package com.example.tomatomall.controller;

import com.example.tomatomall.service.SearchService;
import com.example.tomatomall.vo.FilterVO;
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

    @GetMapping("/history")
    public Response<FilterVO> getHistory(){
        return Response.buildSuccess(null);
    }

    @PostMapping
    public Response<List<ProductVO>> search(){
        return Response.buildSuccess(null);
    }
}
