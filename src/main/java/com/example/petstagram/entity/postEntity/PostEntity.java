package com.example.petstagram.entity.postEntity;

import com.example.petstagram.dto.postDto.PostDTO;
import com.example.petstagram.entity.baseEntity.BaseEntity;
import com.example.petstagram.entity.commentEntity.CommentEntity;
import com.example.petstagram.entity.imageEntity.ImageEntity;
import com.example.petstagram.entity.userEntity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

// 게시물
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "posts")
public class PostEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id; // 게시물 고유 식별자

    private String postContent; // 게시물 내용(텍스트, 이미지, 비디오 링크 등).

    private Integer postLikesCount; // 게시물의 좋아요 수.

    private Integer postCommentsCount; // 게시물에 달린 댓글 수.

    // 게시물과 사용자는 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY) // FetchType.LAZY 는 지연 로딩을 의미
    @JoinColumn(name = "user_id")
    private UserEntity postAuthorId; // 게시물 작성자의 식별자.

    // 게시물과 이미지는 일대다 관계
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageEntity> imageList = new ArrayList<>();

    // 게시물과 댓글은 일대다 관계
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> commentList = new ArrayList<>();

//    // 게시물과 해시태그는 다대다 관계
//    @ManyToMany
//    @JoinTable(
//            name = "post_hashtags",
//            joinColumns = @JoinColumn(name = "post_id"),
//            inverseJoinColumns = @JoinColumn(name = "hashtag_id")
//    )
//    private Set<Hashtag> hashtags = new HashSet<>();
//
//    // == 연관관계 편의 메서드 == //
//    // 해시태그를 포스트에 추가하는 메서드
//    public void addHashtag(Hashtag hashtag) {
//        this.hashtags.add(hashtag);
//        hashtag.getPosts().add(this);
//    }
//
    // 댓글을 포스트에 추가하는 메서드
    public void addComment(CommentEntity commentEntity) {
        this.commentList.add(commentEntity);
        commentEntity.setPost(this);
    }

    // DTO -> Entity
    public static PostEntity toEntity(PostDTO dto) {
        return PostEntity.builder()
                .postContent(dto.getPostContent())
                .build();
    }
}
