package com.example.tomatomall.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PagedProductVO {
    private List<ProductVO> productList;
    private Integer page;
    private Integer size;
    private Integer itemCount=0;
    private Integer pageCount=0;
}
