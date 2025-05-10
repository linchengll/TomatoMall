package com.example.tomatomall.po;


import com.example.tomatomall.vo.CommentVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Time;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Comment{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "content")
    private String content;

    @Column(name = "like_count")
    private Integer likeCount;

    @Column(name = "owner_user_id")
    private Integer ownerUserId;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "create_time")
    private Time createTime;

    public CommentVO toVO(){
        CommentVO commentVO = new CommentVO();
        commentVO.setContent(this.content);
        commentVO.setLikeCount(this.likeCount);
        commentVO.setOwnerUserId(this.ownerUserId);
        commentVO.setProductId(this.productId);
        commentVO.setCreateTime(this.createTime);
        return commentVO;
    }
}
