package com.example.tomatomall.po;

import com.example.tomatomall.vo.TypeVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Type {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "type_id")
    private Integer typeId;

    @Basic
    @Column(name = "type_name")
    private String typeName;

    public TypeVO toVO(){
        TypeVO VO=new TypeVO();
        VO.setTypeId(this.getTypeId());
        VO.setTypeName(this.getTypeName());
        return VO;
    }
}
