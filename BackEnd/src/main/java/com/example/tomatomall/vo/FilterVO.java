package com.example.tomatomall.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FilterVO {
    private String searchString;//空串:仅分类
    private Integer type;//>0:指定类型id <=0:所有
    private Integer page=0;//0=第1页,留空默认为0
    private Integer size=20;//每页商品数量上限，留空默认为20
}
