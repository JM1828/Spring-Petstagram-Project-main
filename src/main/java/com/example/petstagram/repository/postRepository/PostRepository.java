package com.example.petstagram.repository.postRepository;

import com.example.petstagram.entity.postEntity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

    List<PostEntity> findAllByOrderByIdDesc();
}
