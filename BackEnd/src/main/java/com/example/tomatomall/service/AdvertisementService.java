package com.example.tomatomall.service;

import com.example.tomatomall.vo.AdvertisementVO;

import java.util.List;

public interface AdvertisementService {
    List<AdvertisementVO> getAdvertisementList();
    String update(AdvertisementVO advertisementVO);
    AdvertisementVO create(AdvertisementVO advertisementVO);
    String delete(String id);
}
