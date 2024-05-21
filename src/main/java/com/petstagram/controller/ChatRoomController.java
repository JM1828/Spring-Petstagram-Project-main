package com.petstagram.controller;

import com.petstagram.dto.ChatRoomDTO;
import com.petstagram.entity.ChatRoomEntity;
import com.petstagram.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatRooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping
    public ResponseEntity<ChatRoomEntity> createChatRoom(@RequestBody ChatRoomDTO chatRoomDTO) {
        ChatRoomEntity newChatRoom = chatRoomService.createChatRoom(chatRoomDTO);
        return ResponseEntity.ok(newChatRoom);
    }

    @PostMapping("/join/{roomId}")
    public ResponseEntity<ChatRoomEntity> addUserToChatRoom(@PathVariable Long roomId) {
        ChatRoomEntity updatedChatRoom = chatRoomService.addUserToChatRoom(roomId);
        return ResponseEntity.ok(updatedChatRoom);
    }
}
