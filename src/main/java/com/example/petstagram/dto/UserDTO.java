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
public class UserDTO {

    private Long id;    // 사용자의 고유 식별자
    private String name;    // 사용자의 이름
    private String email;   // 사용자의 이메일
    private String password;    // 사용자의 비밀번호
    private String nickName;    // 사용자의 닉네임
    private String profilePicture;  // 사용자의 프로필 사진의 경로나 URL
    private String role = "USER";   // 사용자의 역할
    private String token;   // 사용자의 세션 또는 인증을 확인하기 위해 사용되는 JWT
    private String refreshToken;    // token 이 만료되었을 때, 새로운 token 을 발급받기 위해 사용되는 토큰

    private UserEntity userEntity;
    private List<UserEntity> userEntityList;

    // Entity -> DTO
    public static UserDTO toDTO(UserEntity userEntity) {
        return UserDTO.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .name(userEntity.getName())
                .password(userEntity.getPassword())
                .nickName(userEntity.getNickName())
                .profilePicture(userEntity.getProfilePicture())
                .build();
    }
}
