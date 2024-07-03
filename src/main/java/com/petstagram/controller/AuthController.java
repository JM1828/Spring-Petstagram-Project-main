package com.petstagram.controller;

import com.petstagram.dto.KakaoTokenDto;
import com.petstagram.dto.UserDTO;
import com.petstagram.entity.UserEntity;
import com.petstagram.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/kakao")
    public ResponseEntity<UserDTO> kakaoLogin(@RequestParam String code) {
        UserDTO userDTO = authService.kakaoLogin(code);
        return ResponseEntity.ok(userDTO);
    }
}

