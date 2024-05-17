package com.example.petstagram.service.userService;

import com.example.petstagram.dto.userDto.UserDTO;
import com.example.petstagram.entity.userEntity.UserEntity;
import com.example.petstagram.repository.userRepository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 회원가입
    public UserDTO signup(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (userRepository.existsByNickName(userDTO.getNickName())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // UserDTO 를 UserEntity 로 변환
        UserEntity userEntity = UserEntity.toEntity(userDTO, bCryptPasswordEncoder);

        // 사용자 등록
        UserEntity user = userRepository.save(userEntity);

        // 저장된 사용자 정보를 다시 DTO 로 변환하여 반환
        return UserDTO.toDTO(user);
    }

    // 로그인
    public UserDTO login(UserDTO userDTO) {

        // 전달 받은 이메일과 비밀번호를 인증 처리
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(userDTO.getEmail(),
                        userDTO.getPassword()));

        // 이메일로 사용자 조회
        UserEntity user = userRepository.findByEmail(userDTO.getEmail()).orElseThrow();

        // 조회된 사용자 정보를 바탕으로 JWT 토큰 생성
        String jwt = jwtUtils.generateToken(user);

        // 비어있는 맵과 사용자 정보를 바탕으로 새로고침 토큰 생성
        String refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);

        // 응답 객체에 토큰, 사용자 역할, 새로고침 토큰 설정
        UserDTO response = UserDTO.toDTO(user);
        response.setToken(jwt);
        response.setRole(user.getRole());
        response.setRefreshToken(refreshToken);

        // 설정된 응답 객체 반환
        return response;
    }

    // 새로고침 토큰
    public UserDTO refreshToken(UserDTO userDTO) {
        UserDTO response = new UserDTO();

        // 토큰에서 사용자 이메일 추출
        String ourEmail = jwtUtils.extractUsername(userDTO.getToken());

        // 이메일로 사용자 정보 조회, 없으면 예외 발생
        UserEntity users = userRepository.findByEmail(ourEmail).orElseThrow();

        // 토큰 유효성 검사 후 유효하다면 새로운 토큰 생성
        if (jwtUtils.isTokenValid(userDTO.getToken(), users)) {
            String jwt = jwtUtils.generateToken(users);
            response.setToken(jwt); // 새로운 토큰을 응답 객체에 설정
            response.setRefreshToken(userDTO.getToken());   // 기존 새로고침 토큰을 응답 객체에 설정
        }

        // 설정된 응답 객체 반환
        return response;
    }

    // 회원수정
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다. ID: " + userId));

        // 현재 사용자의 이메일과 수정하려는 이메일이 다르면서, 수정하려는 이메일이 이미 사용 중인지 확인
        if (!userEntity.getEmail().equals(userDTO.getEmail()) && userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        // 현재 사용자의 닉네임과 수정하려는 닉네임이 다르면서, 수정하려는 닉네임이 이미 사용 중인지 확인
        if (!userEntity.getNickName().equals(userDTO.getNickName()) && userRepository.existsByNickName(userDTO.getNickName())) { // 닉네임 중복 검사 추가
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        userEntity.setEmail(userDTO.getEmail());
        userEntity.setName(userDTO.getName());
        userEntity.setNickName(userDTO.getNickName());

        // userDTO 의 비밀번호가 null 이 아니고, 비어있지 않은 경우에만 업데이트
        // 새 비밀번호는 bCryptPasswordEncoder 를 사용하여 암호화
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            userEntity.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        }

        // 변경된 사용자 정보를 userRepository 를 통해 저장
        userRepository.save(userEntity);

        // 업데이트된 사용자 엔티티를 UserDTO 로 변환하여 반환
        return UserDTO.toDTO(userEntity);
    }

    // 회원탈퇴
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // 회원 마이페이지
    public UserDTO getMyInfo(String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        return UserDTO.toDTO(userEntity);
    }

    public UserDTO getAllUsers() {
        UserDTO userDTO = new UserDTO();
            List<UserEntity> result = userRepository.findAll();
                userDTO.setUserEntityList(result);
            return userDTO;
        }

    public UserDTO getUsersById(Long userId) {
        UserDTO userDTO = new UserDTO();
        UserEntity usersById = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User Not found"));
        userDTO.setUserEntity(usersById);
        return userDTO;
    }
}
