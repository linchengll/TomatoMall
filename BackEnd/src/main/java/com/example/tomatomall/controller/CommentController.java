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
    /*
     * 创建评论：
     * 前端只需要传结构体的部分数据 CommentVO{
     *     String content(文本内容);
     *     Integer productId(商品Id);
     *     List<String> imageUrls(上传的图片URL列表);
     * }
     * 其他部分由后端创建或填写，如评论Id、点赞数、创建时间等
     * 且后端会判断该用户是否购买了该商品，只有购买了该商品的用户才可以评论，成功返回“评论成功”
     * 否则返回错误public static TomatoMallException notBuyer() {return new TomatoMallException("不是买家,不能评论!");}
     *
     */
    @PostMapping
    public Response<String> createComment(@RequestBody CommentVO commentVO){
        return Response.buildSuccess(commentService.createComment(commentVO));
    }

    /*
     * 获取评论：
     * * 后端返回的完整结构体 CommentVO{
     *       Integer Id(后端填写的评论Id);
     *       String  content(文本内容);
     *       Integer likeCount(点赞数，初始值为0);
     *       Integer ownerUserId(当前用户Id，通过currentUser.getId()获取);
     *       Integer productId(对应商品Id);
     *       Time createTime(创建时间);
     *       List<String> imageUrls(上传的图片URL列表);
     * }
     */

    @GetMapping
    public Response<List<CommentVO>> getComments(){
        return Response.buildSuccess(commentService.getComments());
    }

    @GetMapping("/{id}")
    public Response<CommentVO> getCommentById(@PathVariable String id){
        return Response.buildSuccess(commentService.getCommentById(id));
    }
    /*
     *  删除评论：
     *  前端只需要传评论Id
     *  且前段需要判断当前用户是否是评论的拥有者，只有拥有者才显示删除评论这个按钮
     *  成功返回“删除成功”
     *  否则返回错误public static TomatoMallException notOwner() {return new TomatoMallException("不是评论者,不能删除!");}
     */

    @DeleteMapping("/{id}")
    public Response<String> deleteComment(@PathVariable String id){
        return Response.buildSuccess(commentService.deleteComment(id));
    }

    /*
     *  更新点赞数：
     *  前端只需要传评论Id
     *  该函数执行时后端会判断该用户是否点赞过该评论，如果点过赞则取消点赞，返回“点赞已取消”，否则点赞数加1，返回“点赞成功”
     *  前端需要根据返回值做相应的提示
     */
    @PutMapping("/{id}")
    public Response<String> updateLikes(@PathVariable String id){
        return Response.buildSuccess(commentService.updateLikes(id));
    }

}
