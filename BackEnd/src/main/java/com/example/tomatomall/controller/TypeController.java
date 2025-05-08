package com.example.tomatomall.controller;

import com.example.tomatomall.service.TypeService;
import com.example.tomatomall.vo.Response;
import com.example.tomatomall.vo.TypeVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/type")
public class TypeController {//这个类用来处理管理员创建或删除类型，不是类型过滤
    @Resource
    TypeService typeService;

    @PostMapping //创建类型
    public Response<TypeVO> createType(@RequestBody TypeVO typeVO){
        return Response.buildSuccess(typeService.createType(typeVO));
    }

    @DeleteMapping("/{typeId}") //删除类型
    public Response<String> deleteType(@PathVariable String typeId){
        return Response.buildSuccess(typeService.deleteType(typeId));
    }
}
