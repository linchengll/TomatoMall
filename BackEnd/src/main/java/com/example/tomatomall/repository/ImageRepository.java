package com.example.tomatomall.repository;

import com.example.tomatomall.po.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image,Integer> {
    List<Image> findByCommentId(Integer commentId);

}
