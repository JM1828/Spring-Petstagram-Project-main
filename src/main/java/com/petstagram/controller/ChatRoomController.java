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

    @PostMapping("/chatRooms")
    public ResponseEntity<ChatRoomDTO> createChatRoom(@RequestBody ChatRoomDTO chatRoomDTO) {
        ChatRoomDTO newChatRoom = chatRoomService.createChatRoom(chatRoomDTO);
        return ResponseEntity.ok(newChatRoom);
    }

//    // 채팅방 및 메시지 목록 조회
//    @PostMapping("/chatRooms/{chatRoomId}")
//    public ResponseEntity<ChatRoomDTO > getMessagesByChatRoomId(@PathVariable Long chatRoomId) {
//        ChatRoomDTO updatedChatRoom = chatRoomService.getMessagesByChatRoomId(chatRoomId);
//        return ResponseEntity.ok(updatedChatRoom);
//    }

    @GetMapping("/chatRooms/{chatRoomId}")
    public ResponseEntity<ChatRoomDTO> getLatestMessages(@PathVariable Long chatRoomId) {
        try {
            ChatRoomDTO chatRoomDTO = chatRoomService.getLatestMessagesByChatRoomId(chatRoomId);
            return ResponseEntity.ok(chatRoomDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
