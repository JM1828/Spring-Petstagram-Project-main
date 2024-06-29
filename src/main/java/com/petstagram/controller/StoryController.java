package com.petstagram.controller;

import com.petstagram.dto.StoryDTO;
import com.petstagram.entity.StoryEntity;
import com.petstagram.service.StoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/story")
@RequiredArgsConstructor
public class StoryController {

    private final StoryService storyService;

    // 모든 스토리 조회
    @GetMapping
    public ResponseEntity<List<StoryEntity>> getAllStories() {
        List<StoryEntity> stories = storyService.getAllStories();
        return ResponseEntity.ok(stories);
    }

    // 스토리 업로드
    @PostMapping("/write")
    public ResponseEntity<String> createStory(@RequestPart("story") StoryDTO storyDTO,
                                              @RequestPart("file") List<MultipartFile> files) {
        try {
            storyService.createStory(storyDTO, files);
            return ResponseEntity.ok("스토리가 업로드 되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("스토리 업로드에 실패했습니다.");
        }
    }

    // 스토리 읽음 표시
    @PostMapping("/{storyId}/read")
    public ResponseEntity<Void> markStoryAsRead(@PathVariable Long storyId, @RequestParam Long userId) {
        storyService.markStoryAsRead(storyId, userId);
        return ResponseEntity.ok().build();
    }
}
