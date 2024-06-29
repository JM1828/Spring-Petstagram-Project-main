package com.petstagram.dto;

import com.petstagram.entity.PostEntity;
import com.petstagram.entity.StoryEntity;
import lombok.Builder;
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class StoryDTO {
    private Long id;
    private List<ImageDTO> imageList;
    private List<VideoDTO> videoList;
    private String storyType;

    // Entity -> DTO
    public static StoryDTO toDTO(StoryEntity storyEntity) {
        return StoryDTO.builder()
                .id(storyEntity.getId())
                .storyType(storyEntity.getStoryType())
                .imageList(storyEntity.getImageList().stream()
                        .map(ImageDTO::toDTO)
                        .collect(Collectors.toList()))
                .videoList(storyEntity.getVideoList().stream()
                        .map(VideoDTO::toDTO)
                        .collect(Collectors.toList()))
                .build();
    }
}
