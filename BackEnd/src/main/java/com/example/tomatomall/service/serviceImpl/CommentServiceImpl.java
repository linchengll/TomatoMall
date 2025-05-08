package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.enums.StatusEnum;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.*;
import com.example.tomatomall.repository.*;
import com.example.tomatomall.service.CommentService;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.vo.CommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    LikerRepository likerRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    OrderArchiveRepository orderArchiveRepository;
    @Autowired
    OrderRepository orderRepository;

    @Override
    public String createComment(CommentVO commentVO) {
        boolean isBuyer=false;
        Integer currentUserId=securityUtil.getCurrentUser().getId();
        Integer productId=commentVO.getProductId();
        List<OrderArchive> orderArchives=orderArchiveRepository.findByUserIdAndProductId(currentUserId,productId);
        if(!orderArchives.isEmpty()){
            for(OrderArchive orderArchive:orderArchives){
                Orders order=orderRepository.findById(orderArchive.getOrderId()).get();
                if(order.getStatus().equals(StatusEnum.SUCCESS)){
                    isBuyer=true;
                    break;
                }
            }
        }
        if(isBuyer){
            Comment comment=new Comment();
            comment.setProductId(productId);
            comment.setOwnerUserId(currentUserId);
            comment.setContent(commentVO.getContent());
            comment.setLikeCount(0);
            comment.setCreateTime(new Time(System.currentTimeMillis()));
            Comment savedComment=commentRepository.save(comment);
            for(String imageUrl:commentVO.getImageUrls()){
                Image image=new Image();
                image.setCommentId(savedComment.getId());
                image.setImageUrl(imageUrl);
                imageRepository.save(image);
            }
            return "评论成功";
        }else
            throw TomatoMallException.notBuyer();
    }

    @Override
    public List<CommentVO> getComments() {
        List<Comment> comments=commentRepository.findAll();
        List<CommentVO> commentVOS=new ArrayList<>();
        for(Comment comment:comments){
            CommentVO commentVO=comment.toVO();
            List<Image> images=imageRepository.findByCommentId(comment.getId());
            List<String> imageUrls=new ArrayList<>();
            for(Image image:images){
                imageUrls.add(image.getImageUrl());
            }
            commentVO.setImageUrls(imageUrls);
            commentVOS.add(commentVO);
        }
        return commentVOS;
    }

    @Override
    public CommentVO getCommentById(String id) {
        CommentVO commentVO;
        if(commentRepository.findById(new Integer(id)).isPresent()){
            commentVO=commentRepository.findById(new Integer(id)).get().toVO();
            List<Image> images=imageRepository.findByCommentId(new Integer(id));
            List<String> imageUrls=new ArrayList<>();
            for(Image image:images){
                imageUrls.add(image.getImageUrl());
            }
            commentVO.setImageUrls(imageUrls);
            return commentVO;
        }else
            throw TomatoMallException.commentNotExists();
    }

    @Override
    public String deleteComment(String id) {
        Comment comment;
        List<Liker> likers;
        List<Image> Images;
        if(commentRepository.findById(new Integer(id)).isPresent()){
            comment=commentRepository.findById(new Integer(id)).get();
            commentRepository.delete(comment);
            likers=likerRepository.findByCommentId(new Integer(id));
            likerRepository.deleteAll(likers);
            Images=imageRepository.findByCommentId(new Integer(id));
            imageRepository.deleteAll(Images);
            return "删除成功";
        }else
            throw TomatoMallException.commentNotExists();
    }

    @Override
    public String updateLikes(String id) {
        Comment comment;
        if(commentRepository.findById(new Integer(id)).isPresent()){
            comment=commentRepository.findById(new Integer(id)).get();
            Integer currentUserId=securityUtil.getCurrentUser().getId();
            List<Liker> likers=likerRepository.findByCommentId(new Integer(id));
            for(Liker liker:likers){
                if(liker.getUserId().equals(currentUserId)){
                   comment.setLikeCount(comment.getLikeCount()-1);
                   return "点赞已取消";
                }
            }
            comment.setLikeCount(comment.getLikeCount()+1);
            Liker liker=new Liker();
            liker.setCommentId(new Integer(id));
            liker.setUserId(currentUserId);
            likerRepository.save(liker);
            return "点赞成功";
        }
        else
            throw TomatoMallException.commentNotExists();
    }
}
