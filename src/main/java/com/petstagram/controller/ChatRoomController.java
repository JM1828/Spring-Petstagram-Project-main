package com.petstagram.controller;

import com.petstagram.dto.ChatRoomDTO;
import com.petstagram.dto.MessageDTO;
import com.petstagram.dto.UserDTO;
import com.petstagram.entity.ChatRoomEntity;
import com.petstagram.entity.UserEntity;
import com.petstagram.repository.ChatRoomRepository;
import com.petstagram.repository.MessageRepository;
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
import java.time.format.DateTimeFormatter;
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
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;

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

        // Principal 에서 사용자 ID를 가져옴
        String userEmail = principal.getName();

        // 상대방 사용자 ID 가져오기
        Long receiverId = sentMessage.getReceiverId();
        UserEntity receiverUser = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("수신자를 찾을 수 없습니다."));
        String receiverEmail = receiverUser.getEmail();

        // 메시지 도착 시간에 따른 정렬 로직을 Comparator 변수에 저장
        Comparator<ChatRoomDTO> sortByRegTimeDesc = Comparator.comparing(chatRoom -> {
            if (chatRoom.getMessages().isEmpty()) {
                return LocalDateTime.MIN;
            } else {
                String regTime = chatRoom.getMessages().get(0).getRegTime();
                return LocalDateTime.parse(regTime, DateTimeFormatter.ISO_DATE_TIME);
            }
        }, Comparator.reverseOrder());

        // 채팅방 리스트 업데이트 알림 (메시지 도착 시간에 따라 정렬)
        List<ChatRoomDTO> updatedChatRoomListSender = chatRoomService.getActiveChatRoomList(principal)
                .stream()
                .sorted(sortByRegTimeDesc)
                .collect(Collectors.toList());
        messagingTemplate.convertAndSend("/sub/chatRoomList/" + userEmail, updatedChatRoomListSender);

        List<ChatRoomDTO> updatedChatRoomListReceiver = chatRoomService.getActiveChatRoomList(() -> receiverEmail)
                .stream()
                .sorted(sortByRegTimeDesc)
                .collect(Collectors.toList());
        messagingTemplate.convertAndSend("/sub/chatRoomList/" + receiverEmail, updatedChatRoomListReceiver);

        // 메시지 개수 업데이트 (수신자에게만 보냄)
        UserDTO receiverMessageCount = chatRoomService.incrementUnreadMessageCount(receiverId);
        messagingTemplate.convertAndSend("/sub/messageCount/" + receiverId, receiverMessageCount);
    }

    // 수신자 채팅방의 메시지 개수
    @GetMapping("/chatRooms/totalMessageCount/{receiverEmail}")
    public ResponseEntity<Long> getTotalMessageCount(@PathVariable String receiverEmail) {
        Long receiverMessageCount = chatRoomService.getUnreadMessageCountForUser(receiverEmail);
        System.out.println("수신자 ID :" + receiverEmail + "메시지 개수 " + receiverMessageCount);
        return ResponseEntity.ok(receiverMessageCount);
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

    // 채팅방 및 메시지 목록 조회 및 메시지 개수 초기화
    @GetMapping("/chatRooms/{chatRoomId}")
    public ResponseEntity<ChatRoomDTO> getChatRoomWithMessages(@PathVariable Long chatRoomId, Principal principal) {
        String userEmail = principal.getName();
        ChatRoomDTO chatRoomDTO = chatRoomService.getChatRoomWithMessagesByIdAndMarkAsRead(chatRoomId, userEmail);
        return ResponseEntity.ok(chatRoomDTO);
    }

//    // 모든 채팅방의 메시지 개수 합산
//    @GetMapping("/chatRooms/totalMessageCount/{receiverEmail}")
//    public ResponseEntity<Long> getTotalMessageCount(@PathVariable String receiverEmail) {
//        Long totalMessageCount = chatRoomRepository.getUnreadMessageCountForUser(receiverEmail);
//        return ResponseEntity.ok(totalMessageCount);
//    }
}
