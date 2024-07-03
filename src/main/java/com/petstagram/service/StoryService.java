package com.petstagram.service;

import com.petstagram.dto.StoryDTO;
import com.petstagram.entity.*;
import com.petstagram.repository.StoryReadRepository;
import com.petstagram.repository.StoryRepository;
import com.petstagram.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
@Setter
public class StoryService {

    private final StoryRepository storyRepository;
    private final StoryReadRepository storyReadRepository;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;

    // 현재 사용자의 스토리 조회
    public List<StoryEntity> getUserStories() {

        // 현재 인증된 사용자의 이름(또는 이메일 등의 식별 정보) 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 현재 로그인한 사용자의 이름을 DB 에서 가져옴
        UserEntity userEntity = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. email = " + username));

        return storyRepository.findByUser(userEntity);
    }

    // 스토리 저장
    @Transactional
    public void createStory(StoryDTO storyDTO, List<MultipartFile> files) {

        // 현재 인증된 사용자의 이름(또는 이메일 등의 식별 정보) 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 현재 로그인한 사용자의 이름을 DB 에서 가져옴
        UserEntity userEntity = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. email = " + username));

        // DTO -> Entity
        StoryEntity storyEntity = StoryEntity.toEntity(storyDTO);
        userEntity.addStory(storyEntity);
        storyEntity.setUser(userEntity);

        // 파일 업로드 처리
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    String fileName = fileUploadService.storeFile(file);
                    String contentType = file.getContentType();

                    if (contentType != null) {
                        if (contentType.startsWith("image/")) {
                            ImageEntity imageEntity = new ImageEntity();
                            imageEntity.setImageUrl(fileName);
                            imageEntity.setStory(storyEntity);
                            storyEntity.getImageList().add(imageEntity);
                        } else if (contentType.startsWith("video/")) {
                            VideoEntity videoEntity = new VideoEntity();
                            videoEntity.setVideoUrl(fileName);
                            videoEntity.setStory(storyEntity);
                            storyEntity.getVideoList().add(videoEntity);
                        }
                    }
                }
            }
        }

        // 스토리 저장
        storyRepository.save(storyEntity);
    }

    // 스토리 읽기
    @Transactional
    public void markStoryAsRead(Long storyId, Long userId) {
        StoryEntity story = storyRepository.findById(storyId).orElseThrow(() -> new RuntimeException("Story not found"));
        StoryReadEntity storyRead = new StoryReadEntity();
        storyRead.setUserId(userId);
        story.addRead(storyRead); // 연관관계 편의 메서드 사용

        storyReadRepository.save(storyRead);
    }
}
