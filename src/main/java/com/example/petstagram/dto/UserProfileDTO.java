package com.example.petstagram.dto;

import com.example.petstagram.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {

    private Long id;    // 사용자의 고유 식별자
    private String name;    // 사용자의 이름
    private String email;   // 사용자의 이메일

    private List<UserEntity> userEntityList;

    public UserProfileDTO(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Entity -> DTO
    public static UserProfileDTO toDTO(UserEntity userEntity) {
        return UserProfileDTO.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .name(userEntity.getName())
                .build();
    }
}