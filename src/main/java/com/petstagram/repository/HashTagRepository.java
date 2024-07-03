package com.petstagram.repository;

import com.petstagram.entity.HashTagEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HashTagRepository extends JpaRepository<HashTagEntity, Long> {
    Optional<HashTagEntity> findByName(String name);

    @Query("SELECT h.name, COUNT(pht) as usage_count " +
            "FROM HashTagEntity h " +
            "JOIN h.posts pht " +
            "GROUP BY h.name " +
            "ORDER BY usage_count DESC")
    List<Object[]> findHashtagUsageCounts();
}
