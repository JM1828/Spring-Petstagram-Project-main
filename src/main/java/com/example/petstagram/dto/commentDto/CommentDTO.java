package com.example.petstagram.dto.commentDto;

import lombok.Getter;

@Getter
public class CommentDTO {
    private Long id; // 댓글의 고유 식별자.
    private String commentContent; // 댓글 내용.
    private Integer commentLikesCount; // 댓글 좋아요 수.


}
