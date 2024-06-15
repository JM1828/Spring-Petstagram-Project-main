
package com.petstagram.dto;

import com.petstagram.entity.CommentEntity;
import com.petstagram.entity.ImageEntity;
import com.petstagram.entity.PostEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private Long id; // 게시물 고유 식별자
    private String postContent;
    private String breed;
    private Long userId;
    private String email;
    private String regTime;
    private String location;
    private List<ImageDTO> imageList;
    private List<CommentDTO> commentList;

    private boolean postLiked; // 게시물 좋아요 상태
    private long postLikesCount; // 게시물의 좋아요 수.

    // Entity -> DTO
    public static PostDTO toDTO(PostEntity postEntity) {
        return PostDTO.builder()
                .id(postEntity.getId())
                .postContent(postEntity.getPostContent())
                .breed(postEntity.getBreed())
                .userId(postEntity.getUser().getId())
                .email(postEntity.getUser().getEmail())
                .regTime(postEntity.getRegTime().format(DateTimeFormatter.ISO_DATE_TIME))
                .location(postEntity.getLocation())
                .imageList(postEntity.getImageList().stream()
                        .map(ImageDTO::toDTO)
                        .collect(Collectors.toList()))
                .commentList(postEntity.getCommentList().stream()
                        .map(CommentDTO::toDTO)
                        .collect(Collectors.toList()))
                .build();
    }
}
