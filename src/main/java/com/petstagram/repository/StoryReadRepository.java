package com.petstagram.repository;

import com.petstagram.entity.StoryReadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryReadRepository extends JpaRepository<StoryReadEntity, Long> {
}
