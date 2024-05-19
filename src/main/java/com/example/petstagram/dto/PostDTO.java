package com.example.petstagram.dto;

import com.example.petstagram.entity.CommentEntity;
import com.example.petstagram.entity.ImageEntity;
import com.example.petstagram.entity.PostEntity;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private Long id; // 게시물 고유 식별자
    private String postContent; // 게시물 내용(텍스트, 이미지, 비디오 링크 등).

    private long postCommentsCount; // 게시물에 달린 댓글 수.

    private long postLikesCount; // 게시물의 좋아요 수.

    private String postNickName; // 게시물을 작성한 사용자 닉네임

    private List<ImageDTO> imageList;
    private List<CommentDTO> commentList;

    // Entity -> DTO
    public static PostDTO toDTO(PostEntity postEntity) {
        return PostDTO.builder()
                .id(postEntity.getId())
                .postContent(postEntity.getPostContent())
                .postNickName(postEntity.getUser().getEmail())
                .imageList(postEntity.getImageList().stream()
                        .map(ImageDTO::toDTO)
                        .collect(Collectors.toList()))
                .commentList(postEntity.getCommentList().stream()
                        .map(CommentDTO::toDTO)
                        .collect(Collectors.toList()))
                .build();
    }
}
