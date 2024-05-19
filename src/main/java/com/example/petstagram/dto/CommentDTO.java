package com.example.petstagram.dto;

import com.example.petstagram.entity.CommentEntity;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private Long id; // 댓글의 고유 식별자.

    private String commentContent; // 댓글 내용.

    private long commentLikesCount; // 댓글의 좋아요 수.

    private String commentNickName; // 댓글을 작성한 사용자 닉네임

    // Entity -> DTO
    public static CommentDTO toDTO(CommentEntity commentEntity) {
        return CommentDTO.builder()
                .id(commentEntity.getId())
                .commentContent(commentEntity.getCommentContent())
                .commentNickName(commentEntity.getUser().getEmail())
                .build();
    }
}
