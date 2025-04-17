package com.example.tomatomall.controller;


import com.example.tomatomall.service.AdvertisementService;
import com.example.tomatomall.vo.AdvertisementVO;
import com.example.tomatomall.vo.Response;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/advertisements")
public class AdvertisementController {
    @Resource
    private AdvertisementService advertisementService;

    /**
     * 获取所有广告信息
     */
    @GetMapping
    public Response<List<AdvertisementVO>> getAdvertisements() {
        return Response.buildSuccess(advertisementService.getAdvertisementList());
    }

    /**
     * 更新广告信息
     */
    @PutMapping
    public Response<String> updateAdvertisement(@RequestBody AdvertisementVO advertisementVO) {
        return Response.buildSuccess(advertisementService.update(advertisementVO));
    }

    /**
     * 创建广告
     */
    @PostMapping
    public Response<AdvertisementVO> createAdvertisement(@RequestBody AdvertisementVO advertisementVO) {
        return Response.buildSuccess(advertisementService.create(advertisementVO));
    }

    /**
     * 删除广告
     */
    @DeleteMapping("/{id}")
    public Response<String> deleteAdvertisement(@PathVariable("id") String id) {
        return Response.buildSuccess(advertisementService.delete(id));
    }

}
