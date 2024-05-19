package com.example.petstagram.controller;

import com.example.petstagram.dto.UserDTO;
import com.example.petstagram.dto.UserProfileDTO;
import com.example.petstagram.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDTO userDTO){
        try {
            UserDTO registeredUser = userService.signup(userDTO);
            return ResponseEntity.ok(registeredUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody UserDTO userDTO){
        return ResponseEntity.ok(userService.login(userDTO));
    }

    // 새로고침 토큰
    @PostMapping("/refresh")
    public ResponseEntity<UserDTO> refreshToken(@RequestBody UserDTO userDTO){
        return ResponseEntity.ok(userService.refreshToken(userDTO));
    }

    // 회원수정
    @PutMapping("/update/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO){
        return ResponseEntity.ok(userService.updateUser(userId, userDTO));
    }

    // 회원탈퇴
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId){
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("회원을 탈퇴하셨습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 탈퇴에 실패했습니다.");
        }
    }

    // 회원 마이페이지
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getMyProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserDTO response = userService.getMyInfo(email);
        return  ResponseEntity.ok(response);
    }

    // 모든 회원 프로필 조회
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserProfileDTO>> getAllUserProfiles(){
        return ResponseEntity.ok(userService.getAllUserProfiles());
    }

    // 회원 한명 조회
    @GetMapping("/get/{userId}")
    public ResponseEntity<UserDTO> getUSerByID(@PathVariable Long userId){
        return ResponseEntity.ok(userService.getUsersById(userId));

    }
}
