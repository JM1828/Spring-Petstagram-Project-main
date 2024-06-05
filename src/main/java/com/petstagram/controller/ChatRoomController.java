package com.petstagram.controller;

import com.petstagram.dto.ChatRoomDTO;
import com.petstagram.dto.MessageDTO;
import com.petstagram.entity.UserEntity;
import com.petstagram.repository.UserRepository;
import com.petstagram.service.ChatRoomService;
import com.petstagram.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;

    // 채팅방 생성
    @PostMapping("/chatRooms")
    public ResponseEntity<ChatRoomDTO> createChatRoom(@RequestBody ChatRoomDTO chatRoomDTO) {
        ChatRoomDTO newChatRoom = chatRoomService.createChatRoom(chatRoomDTO);
        return ResponseEntity.ok(newChatRoom);
    }

    // 채팅방 리스트 조회
    @GetMapping("/chatRooms/list")
    public ResponseEntity<List<ChatRoomDTO>> getChatRoomList(Principal principal) {
        List<ChatRoomDTO> chatRoomList = chatRoomService.getChatRoomList(principal);
        return ResponseEntity.ok(chatRoomList);
    }

    // 메시지 전송
    @MessageMapping("/sendMessage/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, @RequestBody MessageDTO messageDTO, Principal principal) {

        MessageDTO sentMessage = chatRoomService.sendMessage(messageDTO, principal);

        // 메시지 전송 후, 해당 채팅방을 구독하는 클라이언트에게 메시지 정보 업데이트 알림
        messagingTemplate.convertAndSend("/sub/chat/room/" + roomId, sentMessage);

        // Principal 에서 사용자 ID를 가져옴 (보통 사용자 이름을 반환)
        String userEmail = principal.getName();

        // 상대방 사용자 ID 가져오기
        Long receiverId = sentMessage.getReceiverId();
        UserEntity receiverUser = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("수신자를 찾을 수 없습니다."));
        String receiverEmail = receiverUser.getEmail();

        // 채팅방 리스트 업데이트 알림 (메시지 도착 시간에 따라 정렬)
        List<ChatRoomDTO> updatedChatRoomListSender = chatRoomService.getActiveChatRoomList(principal)
                .stream()
                .sorted(Comparator.comparing(chatRoom -> chatRoom.getMessages().isEmpty() ? LocalDateTime.MIN : chatRoom.getMessages().get(0).getRegTime(), Comparator.reverseOrder()))
                .collect(Collectors.toList());
        messagingTemplate.convertAndSend("/sub/chatRoomList/" + userEmail, updatedChatRoomListSender);

        List<ChatRoomDTO> updatedChatRoomListReceiver = chatRoomService.getActiveChatRoomList(() -> receiverEmail)
                .stream()
                .sorted(Comparator.comparing(chatRoom -> chatRoom.getMessages().isEmpty() ? LocalDateTime.MIN : chatRoom.getMessages().get(0).getRegTime(), Comparator.reverseOrder()))
                .collect(Collectors.toList());
        messagingTemplate.convertAndSend("/sub/chatRoomList/" + receiverEmail, updatedChatRoomListReceiver);
    }

    // 이미지 파일 업로드 후 URL 반환
    @PostMapping("/uploadImage")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 파일 업로드 처리 로직
            String imageUrl = fileUploadService.storeFile(file);

            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 업로드 실패 시 에러 메시지 반환
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // 채팅방 및 메시지 목록 조회
    @GetMapping("/chatRooms/{chatRoomId}")
    public ResponseEntity<ChatRoomDTO> getChatRoomWithMessagesById(@PathVariable Long chatRoomId, Principal principal) {
        ChatRoomDTO chatRoomDTO = chatRoomService.getChatRoomWithMessagesById(chatRoomId, principal);
        return ResponseEntity.ok(chatRoomDTO);
    }
}