package com.example.tomatomall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.tomatomall.po.Liker;

import java.util.List;

public interface LikerRepository extends JpaRepository<Liker,Integer> {
    List<Liker> findByCommentId(Integer commentId);

}
