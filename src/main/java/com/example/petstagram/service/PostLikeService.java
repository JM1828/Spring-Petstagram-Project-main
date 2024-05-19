package com.example.petstagram.service;

import com.example.petstagram.entity.PostLikeEntity;
import com.example.petstagram.entity.PostEntity;
import com.example.petstagram.entity.UserEntity;
import com.example.petstagram.repository.PostLikeRepository;
import com.example.petstagram.repository.PostRepository;
import com.example.petstagram.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 좋아요 추가
    public void addPostLike(Long postId, Long userId) {

        // 게시물 찾기
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        // 사용자 찾기
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        PostLikeEntity postLikeEntity = new PostLikeEntity();
        postLikeEntity.setPost(post);
        postLikeEntity.setUser(user);
        postLikeRepository.save(postLikeEntity);
    }

    // 좋아요 삭제
    @Transactional
    public void removePostLike(Long postId, Long userId) {

        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 특정 게시물에 대한 특정 사용자의 좋아요를 찾아서 삭제
        postLikeRepository.findByPostAndUser(post, user)
                .ifPresent(postLikeRepository::delete);
    }
}
