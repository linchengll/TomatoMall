package com.example.tomatomall.po;


import com.example.tomatomall.vo.AdvertisementVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Advertisement {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "product_id")
    private Integer productId;

    public AdvertisementVO toVO() {
        AdvertisementVO vo = new AdvertisementVO();
        vo.setId(id);
        vo.setTitle(title);
        vo.setContent(content);
        vo.setImageUrl(imageUrl);
        vo.setProductId(productId);
        return vo;
    }


}
