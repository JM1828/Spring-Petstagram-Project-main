package com.example.petstagram.controller;

import com.example.petstagram.entity.UserEntity;
import com.example.petstagram.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
public class PostLikeController {

    private final PostLikeService postLikeService;

    // 게시물 좋아요 추가 및 삭제
    @PostMapping("/post/toggle/{postId}")
    public ResponseEntity<String> togglePostLike(@PathVariable("postId") Long postId, @AuthenticationPrincipal UserEntity user) {
        postLikeService.togglePostLike(postId, user.getId());
        return ResponseEntity.ok("게시물에 좋아요가 추가되었습니다.");
    }
}
