package com.example.petstagram.repository.userRepository;


import com.example.petstagram.entity.userEntity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Boolean existsByEmail(String email);

    Boolean existsByNickName(String name);

    Optional<UserEntity> findByEmail(String email);

    UserEntity findByName(String username);
}
