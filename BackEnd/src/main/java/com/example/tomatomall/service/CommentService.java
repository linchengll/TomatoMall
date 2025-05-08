package com.example.tomatomall.service;

import com.example.tomatomall.vo.CommentVO;

import java.util.List;

public interface CommentService {
    String createComment(CommentVO commentVO);
    List<CommentVO> getComments();
    CommentVO getCommentById(String id);
    String deleteComment(String id);
    String updateLikes(String id);
}
