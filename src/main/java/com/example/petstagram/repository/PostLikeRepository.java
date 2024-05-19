package com.example.petstagram.repository;

import com.example.petstagram.entity.PostLikeEntity;
import com.example.petstagram.entity.PostEntity;
import com.example.petstagram.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLikeEntity, Long> {

    Optional<PostLikeEntity> findByPostAndUser(PostEntity post, UserEntity user);

    // 특정 게시물에 대한 좋아요 개수 조회
    long countByPost(PostEntity post);
}
