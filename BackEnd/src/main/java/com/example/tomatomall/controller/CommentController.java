package com.example.tomatomall.controller;


import com.example.tomatomall.service.CommentService;
import com.example.tomatomall.vo.CommentVO;
import com.example.tomatomall.vo.Response;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    @Resource
    private CommentService commentService;

    @PostMapping
    public Response<String> createComment(@RequestBody CommentVO commentVO){
        return Response.buildSuccess(commentService.createComment(commentVO));
    }

    @GetMapping
    public Response<List<CommentVO>> getComments(){
        return Response.buildSuccess(commentService.getComments());
    }

    @GetMapping("/{id}")
    public Response<CommentVO> getCommentById(@PathVariable String id){
        return Response.buildSuccess(commentService.getCommentById(id));
    }

    @DeleteMapping("/{id}")
    public Response<String> deleteComment(@PathVariable String id){
        return Response.buildSuccess(commentService.deleteComment(id));
    }
    @PutMapping("/{id}")
    public Response<String> updateLikes(@PathVariable String id){
        return Response.buildSuccess(commentService.updateLikes(id));
    }

}
