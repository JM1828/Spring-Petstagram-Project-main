package com.example.petstagram.repository;

import com.example.petstagram.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    List<CommentEntity> findByPost_id(Long postId);

    long countByPostId(long postId);
}
