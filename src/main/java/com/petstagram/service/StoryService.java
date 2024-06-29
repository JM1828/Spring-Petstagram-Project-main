package com.petstagram.service;

import com.petstagram.entity.BaseEntity;
import com.petstagram.entity.StoryEntity;
import com.petstagram.entity.StoryReadEntity;
import com.petstagram.repository.StoryReadRepository;
import com.petstagram.repository.StoryRepository;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
@Setter
public class StoryService extends BaseEntity {

    private final StoryRepository storyRepository;
    private final StoryReadRepository storyReadRepository;

    // 모든 스토리 조회
    public List<StoryEntity> getAllStories() {
        return storyRepository.findAll();
    }

    // 스토리 저장
    public StoryEntity createStory(StoryEntity story) {
        return storyRepository.save(story);
    }

    // 스토리 읽기
    public void markStoryAsRead(Long storyId, Long userId) {
        StoryEntity story = storyRepository.findById(storyId).orElseThrow(() -> new RuntimeException("Story not found"));
        StoryReadEntity storyRead = new StoryReadEntity();
        storyRead.setUserId(userId);
        story.addRead(storyRead); // 연관관계 편의 메서드 사용

        storyReadRepository.save(storyRead);
    }
}
