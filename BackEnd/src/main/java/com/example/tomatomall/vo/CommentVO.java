package com.example.tomatomall.vo;


import com.example.tomatomall.po.Comment;
import com.example.tomatomall.po.Image;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CommentVO {
      private Integer Id;
      private String content;
      private Integer likeCount;
      private Integer ownerUserId;
      private Integer productId;
      private Time createTime;
      private List<String> imageUrls;
}
