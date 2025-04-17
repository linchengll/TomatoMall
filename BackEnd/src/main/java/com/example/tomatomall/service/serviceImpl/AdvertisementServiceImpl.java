package com.example.tomatomall.service.serviceImpl;


import com.example.tomatomall.enums.RoleEnum;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Advertisement;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.repository.AdvertisementRepository;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.service.AdvertisementService;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.vo.AdvertisementVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdvertisementServiceImpl implements AdvertisementService {

    @Autowired
    AdvertisementRepository advertisementRepository;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    ProductRepository productRepository;

    @Override
    public List<AdvertisementVO> getAdvertisementList() {
        List<Advertisement> advertisementList = advertisementRepository.findAll();
        List<AdvertisementVO> advertisementVOList = new ArrayList<>();
        for (Advertisement advertisement : advertisementList) {
            advertisementVOList.add(advertisement.toVO());
        }
        return advertisementVOList;
    }

    @Override
    public String update(AdvertisementVO advertisementVO) {
        if(!securityUtil.getCurrentUser().getRole().equals(RoleEnum.admin))
            throw TomatoMallException.unauthorized();
        Advertisement advertisement;
        if(advertisementRepository.findById(advertisementVO.getId()).isPresent()){
            advertisement = advertisementRepository.findById(advertisementVO.getId()).get();
        }else
            throw TomatoMallException.advertisementNotExists();
        if(advertisementVO.getTitle()!= null){
            advertisement.setTitle(advertisementVO.getTitle());
        }
        if(advertisementVO.getContent()!= null){
            advertisement.setContent(advertisementVO.getContent());
        }
        if(advertisementVO.getImageUrl()!= null){
            advertisement.setImageUrl(advertisementVO.getImageUrl());
        }
        if(productRepository.findById(advertisementVO.getProductId()).isPresent()){
            advertisement.setProductId(advertisementVO.getProductId());
        }else
            throw TomatoMallException.productNotExists();

        return "更新成功";
    }

    @Override
    public AdvertisementVO create(AdvertisementVO advertisementVO) {
        if(!securityUtil.getCurrentUser().getRole().equals(RoleEnum.admin))
            throw TomatoMallException.unauthorized();
        if(!productRepository.findById(advertisementVO.getProductId()).isPresent())
            throw TomatoMallException.productNotExists();
        Advertisement advertisement = advertisementVO.toPo();
        Advertisement savedAdvertisement = advertisementRepository.save(advertisement);
        return savedAdvertisement.toVO();
    }

    @Override
    public String delete(String id) {
        if(!securityUtil.getCurrentUser().getRole().equals(RoleEnum.admin))
            throw TomatoMallException.unauthorized();
        Advertisement advertisement;
        if(advertisementRepository.findById(new Integer(id)).isPresent()){
            advertisement = advertisementRepository.findById(new Integer(id)).get();
            advertisementRepository.delete(advertisement);
        }else
            throw TomatoMallException.advertisementNotExists();

        return "删除成功";
    }
}
