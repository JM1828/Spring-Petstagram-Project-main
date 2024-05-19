package com.example.petstagram.repository;

import com.example.petstagram.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

    List<PostEntity> findAllByOrderByIdDesc();

    List<PostEntity> findByUserId(Long userId);
}
