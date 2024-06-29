package com.petstagram.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "stories")
public class StoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "story_id")
    private Long id;

    private String storyVideo;

    private String storyImage;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StoryReadEntity> reads = new HashSet<>();

    // 연관관계 편의 메서드
    public void addRead(StoryReadEntity storyRead) {
        reads.add(storyRead);
        storyRead.setStory(this);
    }
}
