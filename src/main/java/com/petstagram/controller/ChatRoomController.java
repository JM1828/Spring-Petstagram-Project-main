package com.petstagram.controller;

import com.petstagram.dto.ChatRoomDTO;
import com.petstagram.entity.ChatRoomEntity;
import com.petstagram.entity.MessageEntity;
import com.petstagram.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 채팅방 생성
    @PostMapping("/chatRooms")
    public ResponseEntity<ChatRoomDTO> createChatRoom(@RequestBody ChatRoomDTO chatRoomDTO) {
        ChatRoomDTO newChatRoom = chatRoomService.createChatRoom(chatRoomDTO);
        return ResponseEntity.ok(newChatRoom);
    }

    // 채팅방이 이미 존재하는지 확인
    @GetMapping("/chatRooms/exists")
    public ResponseEntity<Boolean> checkChatRoomExists(@RequestParam Long userId1, @RequestParam Long userId2) {
        boolean exists = chatRoomService.checkChatRoomExists(userId1, userId2);
        return ResponseEntity.ok(exists);
    }

    // 채팅방 및 메시지 목록 조회
    @GetMapping("/chatRooms/{chatRoomId}")
    public ResponseEntity<ChatRoomDTO> getChatRoomWithMessagesById(@PathVariable Long chatRoomId) {
            ChatRoomDTO chatRoomDTO = chatRoomService.getChatRoomWithMessagesById(chatRoomId);
            return ResponseEntity.ok(chatRoomDTO);
    }

    // 모든 채팅방 리스트 조회
    @GetMapping("/chatRooms/list")
    public ResponseEntity<List<ChatRoomDTO>> getChatRoomList() {
        List<ChatRoomDTO> chatRoomList = chatRoomService.getChatRoomList();
        return ResponseEntity.ok(chatRoomList);
    }

    // 특정 채팅방의 상세 정보 조회
    @GetMapping("/chatRooms/list/{chatRoomId}")
    public ResponseEntity<ChatRoomDTO> getChatRoomById(@PathVariable Long chatRoomId) {
        ChatRoomDTO chatRoomDTO = chatRoomService.getChatRoomById(chatRoomId);
        return ResponseEntity.ok(chatRoomDTO);
    }
}
