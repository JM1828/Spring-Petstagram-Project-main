package com.petstagram.repository;

import com.petstagram.dto.ProfileImageDTO;
import com.petstagram.dto.UserProfileDTO;
import com.petstagram.entity.ProfileImageEntity;
import com.petstagram.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Boolean existsByEmail(String email);

    Optional<UserEntity> findByEmail(String email);

    @Query("SELECT new com.petstagram.dto.UserProfileDTO(u.id, u.name, u.email, " +
            "new com.petstagram.dto.ProfileImageDTO(p.id, p.imageUrl, u.id), " +
            "u.bio, u.isRecommend) " +
            "FROM UserEntity u " +
            "LEFT JOIN u.profileImage p")
    List<UserProfileDTO> findAllUserProfiles();

    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.profileImage WHERE u.email = :email")
    Optional<UserEntity> findByEmailWithProfileImage(@Param("email") String email);

    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.profileImage WHERE u.id = :id")
    Optional<UserEntity> findByIdWithProfileImage(@Param("id") Long id);
}