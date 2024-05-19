package com.example.petstagram.entity;


import com.example.petstagram.dto.UserDTO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    private String email;
    private String name;
    private String password;
    private String profilePicture;
    private String role = "USER";
    private String gender; // 성별
    private String bio; // 사용자 소개
    private boolean isRecommend = false; // 추천 여부, 기본값은 false

    // 사용자와 게시물은 일대다 관계
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostEntity> postList = new ArrayList<>();

    // == 연관관계 편의 메서드 == //
    // 게시물 관련 메서드
    public void addPost(PostEntity post) {
        postList.add(post);
        post.setUser(this);
    }

    // DTO -> Entity
    public static UserEntity toEntity(UserDTO userDTO, BCryptPasswordEncoder bCryptPasswordEncoder) {
        return UserEntity.builder()
                .id(userDTO.getId())
                .email(userDTO.getEmail())
                .name(userDTO.getName())
                .password(bCryptPasswordEncoder.encode(userDTO.getPassword()))
                .profilePicture(userDTO.getProfilePicture())
                .role(userDTO.getRole())
                .gender(userDTO.getGender())
                .bio(userDTO.getBio())
                .isRecommend(userDTO.isRecommend())
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
