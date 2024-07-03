package com.petstagram.repository;

import com.petstagram.entity.StoryEntity;
import com.petstagram.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoryRepository extends JpaRepository<StoryEntity, Long> {

    List<StoryEntity> findByUser(UserEntity user);
}
