package com.example.tomatomall.vo;

import com.example.tomatomall.po.Type;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TypeVO {
    private Integer typeId;

    private String typeName;

    public Type toPO(){
        Type PO=new Type();
        PO.setTypeId(this.getTypeId());
        PO.setTypeName(this.getTypeName());
        return PO;
    }
}
