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

    // 게시물 좋아요 추가
    @PostMapping("/post/add/{postId}")
    public ResponseEntity<String> addPostLike(@PathVariable("postId") Long postId, @AuthenticationPrincipal UserEntity user) {
        postLikeService.addPostLike(postId, user.getId());
        return ResponseEntity.ok("좋아요가 추가되었습니다.");
    }

    // 게시물 좋아요 삭제
    @DeleteMapping("/post/remove/{postId}")
    public ResponseEntity<String> removePostLike(@PathVariable("postId") Long postId, @AuthenticationPrincipal UserEntity user) {
        postLikeService.removePostLike(postId, user.getId());
        return ResponseEntity.ok("좋아요가 취소되었습니다.");
    }
}
