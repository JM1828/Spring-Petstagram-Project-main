package com.petstagram.controller;

import com.petstagram.dto.MessageDTO;
import com.petstagram.entity.MessageEntity;
import com.petstagram.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user")
public class MessageController {

    private final MessageService messageService;

    // 메시지 보내기
    @PostMapping("/message/send")
    public ResponseEntity<String> sendMessage(@RequestBody MessageDTO messageDTO) {
        try {
            messageService.sendMessage(messageDTO);
         return ResponseEntity.ok("메시지가 작성되었습니다.");
        } catch (Exception e) {
            log.error("파일 업로드 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 작성에 실패했습니다.");
        }
    }

    // 두 사용자 간의 메시지 목록 조회
    @GetMapping("/message/between/{receiverEmail}")
    public ResponseEntity<List<MessageDTO>> getMessageBetweenUsers(@PathVariable String receiverEmail) {
        List<MessageDTO> messages = messageService.getMessageBetweenUsers(receiverEmail);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }
}
