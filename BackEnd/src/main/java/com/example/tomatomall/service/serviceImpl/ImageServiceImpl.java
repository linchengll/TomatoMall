package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.tomatomall.util.OssUtil;
@Service
public class ImageServiceImpl implements ImageService {

    @Override
    public String upload(MultipartFile file) {
        System.out.println("上传图片");
        try {
            return OssUtil.upload(file.getOriginalFilename(),file.getInputStream());
        }catch (Exception e){
            e.printStackTrace();
            throw TomatoMallException.fileUploadFail();
        }
    }
}