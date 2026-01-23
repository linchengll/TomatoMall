package com.example.tomatomall.controller;

import com.example.tomatomall.service.SearchService;
import com.example.tomatomall.vo.FilterVO;
import com.example.tomatomall.vo.PagedProductVO;
import com.example.tomatomall.vo.ProductBasicVO;
import com.example.tomatomall.vo.Response;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    @Resource
    SearchService searchService;

    //获取热度排名前5
    @GetMapping("/top")
    public Response<List<ProductBasicVO>> getTop(){
        return Response.buildSuccess(searchService.getTop());
    }

    //搜索与类型过滤
    @PostMapping
    public Response<PagedProductVO> search(@RequestBody FilterVO filterVO){
        return Response.buildSuccess(searchService.search(filterVO));
    }
}
