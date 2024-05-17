package com.example.petstagram.controller.postController;

import com.example.petstagram.dto.postDto.PostDTO;
import com.example.petstagram.service.postService.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    // 게시글 리스트 조회
    @GetMapping("/list")
    public ResponseEntity<List<PostDTO>> getPostList() {
        List<PostDTO> postList = postService.getPostList();
        return ResponseEntity.ok(postList);
    }

    // 게시글 작성
    @PostMapping("/write")
    public ResponseEntity<String> writePost(@RequestBody PostDTO postDTO) {
        postService.writePost(postDTO);
        return ResponseEntity.ok().build();
    }

    // 게시글 상세보기
    @GetMapping("/read/{postId}")
    public ResponseEntity<PostDTO> readPost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.readPost(postId));
    }

    // 게시글 수정
    @PutMapping("/update/{postId}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable Long postId, @RequestBody PostDTO postDTO) {
        return ResponseEntity.ok(postService.updatePost(postId, postDTO));
    }

    // 게시글 삭제
    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        try {
            postService.deletePost(postId);
            return ResponseEntity.ok("게시글이 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 삭제에 실패헀습니다.");
        }
    }
}
