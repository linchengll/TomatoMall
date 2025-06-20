package com.example.tomatomall.vo;

import com.example.tomatomall.po.Advertisement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdvertisementVO {
    private Integer id;

    private String title;

    private String content;

    private String imageUrl;

    private Integer productId;

    private Float discount;

    private Integer limitNum;

    public Advertisement toPo(){
        Advertisement advertisement = new Advertisement();
        advertisement.setId(this.id);
        advertisement.setTitle(this.title);
        advertisement.setContent(this.content);
        advertisement.setImageUrl(this.imageUrl);
        advertisement.setProductId(this.productId);
        advertisement.setDiscount(Math.round(this.discount*100));
        advertisement.setLimitNum(this.limitNum);
        return advertisement;
    }
}
