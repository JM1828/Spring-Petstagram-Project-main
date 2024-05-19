package com.example.petstagram.repository;

import com.example.petstagram.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLikeEntity, Long> {

    Optional<CommentLikeEntity> findByCommentAndUser(CommentEntity comment, UserEntity user);

    // 특정 댓글에 대한 좋아요 개수 조회
    long countByComment(CommentEntity comment);
}
