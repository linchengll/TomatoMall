package com.example.tomatomall.controller;

import com.example.tomatomall.service.SearchService;
import com.example.tomatomall.vo.FilterVO;
import com.example.tomatomall.vo.ProductBasicVO;
import com.example.tomatomall.vo.ProductVO;
import com.example.tomatomall.vo.Response;
import org.springframework.web.bind.annotation.*;

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
    public Response<List<ProductVO>> search(@RequestBody FilterVO filterVO){
        return Response.buildSuccess(searchService.search(filterVO));
    }
}
