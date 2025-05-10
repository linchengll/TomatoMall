package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.enums.RoleEnum;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.ProductTypes;
import com.example.tomatomall.po.Type;
import com.example.tomatomall.repository.ProductTypeRepository;
import com.example.tomatomall.repository.TypeRepository;
import com.example.tomatomall.service.TypeService;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.vo.TypeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TypeServiceImpl implements TypeService {
    @Autowired
    SecurityUtil securityUtil;
    @Autowired
    TypeRepository typeRepository;
    @Autowired
    ProductTypeRepository productTypeRepository;

    @Override
    public TypeVO createType(TypeVO typeVO) {
        if(!securityUtil.getCurrentUser().getRole().equals(RoleEnum.admin))
            throw TomatoMallException.unauthorized();
        if(typeRepository.findByTypeName(typeVO.getTypeName()).isPresent())
            throw TomatoMallException.typeAlreadyExist();
        Type PO=typeRepository.save(typeVO.toPO());
        return PO.toVO();
    }

    @Override
    public String deleteType(String typeId) {
        if(!securityUtil.getCurrentUser().getRole().equals(RoleEnum.admin))
            throw TomatoMallException.unauthorized();
        Type PO;
        if(typeRepository.findById(new Integer(typeId)).isPresent())
            PO=typeRepository.findById(new Integer(typeId)).get();
        else
            throw TomatoMallException.typeNotExists();
        typeRepository.delete(PO);
        //级联删除
        List<ProductTypes> productType=productTypeRepository.findByTypeId(new Integer(typeId));
        productTypeRepository.deleteAll(productType);
        return "删除类型 "+PO.getTypeName()+" 成功";
    }

    @Override
    public List<TypeVO> getAllType() {
        List<Type> raw=typeRepository.findAll();
        List<TypeVO> res=new ArrayList<>();
        for(Type PO:raw)
            res.add(PO.toVO());
        return res;
    }
}
