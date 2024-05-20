package com.example.petstagram.service;

import com.example.petstagram.entity.PostLikeEntity;
import com.example.petstagram.entity.PostEntity;
import com.example.petstagram.entity.UserEntity;
import com.example.petstagram.repository.PostLikeRepository;
import com.example.petstagram.repository.PostRepository;
import com.example.petstagram.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 게시물 좋아요 추가 또는 삭제
    @Transactional
    public void togglePostLike(Long postId) {

        // 게시물 찾기
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        // 현재 인증된 사용자의 이름(또는 이메일 등) 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 좋아요가 이미 있는지 확인
        Optional<PostLikeEntity> postLikeOpt  = postLikeRepository.findByPostAndUser(post, user);

        if (postLikeOpt.isPresent()) {
            // 좋아요 엔티티가 존재한다면, 데이터베이스에서 해당 엔티티 삭제
            postLikeRepository.delete(postLikeOpt.get());
        } else {
            // 좋아요가 없다면 추가
            PostLikeEntity postLikeEntity = new PostLikeEntity();
            postLikeEntity.setPost(post);
            postLikeEntity.setUser(user);
            postLikeRepository.save(postLikeEntity);
        }
    }
}
