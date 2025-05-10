package com.example.tomatomall.service;

import com.example.tomatomall.vo.CommentVO;

import java.util.List;

public interface CommentService {
    String createComment(CommentVO commentVO);
    List<CommentVO> getComments(String Id);
    String deleteComment(String id);
    String updateLikes(String id);
}
