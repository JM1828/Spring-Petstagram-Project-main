package com.example.petstagram.dto.postDto;

import com.example.petstagram.entity.commentEntity.CommentEntity;
import com.example.petstagram.entity.imageEntity.ImageEntity;
import com.example.petstagram.entity.postEntity.PostEntity;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private Long id; // 게시물 고유 식별자
    private String postContent; // 게시물 내용(텍스트, 이미지, 비디오 링크 등).
    private Integer postLikesCount; // 게시물의 좋아요 수.
    private Integer postCommentsCount; // 게시물에 달린 댓글 수.

    private String nickName; // 게시물을 작석한 사용자 닉네임

    private List<ImageEntity> imageList;
    private List<CommentEntity> commentList;

    // Entity -> DTO
    public static PostDTO toDTO(PostEntity postEntity) {
        return PostDTO.builder()
                .id(postEntity.getId())
                .postContent(postEntity.getPostContent())
                .nickName(postEntity.getPostAuthorId().getNickName())
                .imageList(postEntity.getImageList())
                .commentList(postEntity.getCommentList())
                .build();
    }
}
