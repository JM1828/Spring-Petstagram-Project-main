package com.example.petstagram.entity.userEntity;


import com.example.petstagram.dto.userDto.UserDTO;
import com.example.petstagram.entity.postEntity.PostEntity;
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
    private String nickName;
    private String profilePicture;
    private String role = "USER";

    // 사용자와 게시물은 일대다 관계
    @OneToMany(mappedBy = "postAuthorId", cascade = CascadeType.ALL)
    private List<PostEntity> postList = new ArrayList<>();

    // == 연관관계 편의 메서드 == //
    // 게시물 관련 메서드
    public void addPost(PostEntity post) {
        postList.add(post);
        post.setPostAuthorId(this);
    }

    // DTO -> Entity
    public static UserEntity toEntity(UserDTO userDTO, BCryptPasswordEncoder bCryptPasswordEncoder) {
        return UserEntity.builder()
                .id(userDTO.getId())
                .email(userDTO.getEmail())
                .name(userDTO.getName())
                .password(bCryptPasswordEncoder.encode(userDTO.getPassword()))
                .nickName(userDTO.getNickName())
                .profilePicture(userDTO.getProfilePicture())
                .role(userDTO.getRole())
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
