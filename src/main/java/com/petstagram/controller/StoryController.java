package com.petstagram.controller;

import com.petstagram.entity.StoryEntity;
import com.petstagram.service.StoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/story")
@RequiredArgsConstructor
public class StoryController {

    private final StoryService storyService;

    @GetMapping
    public List<StoryEntity> getAllStories() {
        return storyService.getAllStories();
    }

    @PostMapping
    public StoryEntity createStory(@RequestBody StoryEntity story) {
        return storyService.createStory(story);
    }

    @PostMapping("/{storyId}/read")
    public void markStoryAsRead(@PathVariable Long storyId, @RequestParam Long userId) {
        storyService.markStoryAsRead(storyId, userId);
    }
}
