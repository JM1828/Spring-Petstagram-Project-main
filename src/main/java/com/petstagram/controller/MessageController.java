package com.petstagram.controller;

import com.petstagram.dto.MessageDTO;
import com.petstagram.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user/message")
public class MessageController {

    private final MessageService messageService;

    // 메시지 보내기
    @PostMapping("/send")
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody MessageDTO messageDTO) {
        try {
            MessageDTO message= messageService.sendMessage(messageDTO);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            log.error("Error sending message:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 특정 사용자의 메시지 목록 조회
    @GetMapping("/list/{userId}")
    public ResponseEntity<List<MessageDTO>> getMessageList(@PathVariable("userId") Long userId) {
        // userId를 통해 해당 사용자의 메시지 목록을 조회하고, DTO 형태로 반환
        List<MessageDTO> messageList = messageService.getMessageList(userId);
        return ResponseEntity.ok(messageList);
    }

    // 메시지 삭제
    @DeleteMapping("/delete/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable("messageId") Long messageId) {
        // messageId를 통해 해당 메시지를 삭제하는 로직을 수행
        messageService.deleteMessage(messageId);
        return ResponseEntity.ok().build();
    }
}
