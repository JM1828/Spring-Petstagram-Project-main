package com.example.petstagram.entity.commentEntity;

import com.example.petstagram.dto.commentDto.CommentDTO;
import com.example.petstagram.dto.postDto.PostDTO;
import com.example.petstagram.entity.baseEntity.BaseEntity;
import com.example.petstagram.entity.postEntity.PostEntity;
import com.example.petstagram.entity.userEntity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

// 댓글
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments")
public class CommentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id; // 댓글의 고유 식별자.

    private String commentContent; // 댓글 내용.

    private Integer commentLikesCount; // 댓글 좋아요 수.

    // 댓글과 사용자는 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY) // FetchType.LAZY 는 지연 로딩을 의미
    @JoinColumn(name = "user_id")
    private UserEntity commentAuthorId; // 댓글 작성자의 식별자.

    // 댓글과 게시물은 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY) // FetchType.LAZY 는 지연 로딩을 의미
    @JoinColumn(name = "post_id")
    private PostEntity post;

    // DTO -> Entity
    public static CommentEntity toEntity(CommentDTO dto) {
        return CommentEntity.builder()
                .commentContent(dto.getCommentContent())
                .build();
    }
}