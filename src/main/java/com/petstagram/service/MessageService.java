
package com.petstagram.service;

import com.petstagram.dto.MessageDTO;
import com.petstagram.entity.ChatRoomEntity;
import com.petstagram.entity.ImageEntity;
import com.petstagram.entity.MessageEntity;
import com.petstagram.entity.UserEntity;
import com.petstagram.repository.ChatRoomRepository;
import com.petstagram.repository.MessageRepository;
import com.petstagram.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    private final ChatRoomRepository chatRoomRepository;

    // 메시지 작성
    public MessageDTO sendMessage(MessageDTO messageDTO) {

        // 현재 인증된 사용자의 이름(또는 이메일 등) 가져오기
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // 보내는 사람 찾기
        UserEntity sender = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 받는 사람 찾기
        UserEntity receiver;
        receiver = userRepository.findById(messageDTO.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("수신자가 존재하지 않습니다."));

        // 채팅방 찾기
        ChatRoomEntity chatRoom = chatRoomRepository.findById(messageDTO.getChatRoomId())
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        // DTO 를 Entity 로 변환하고 사용자 정보 설정
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setMessageContent(messageDTO.getMessageContent());
        messageEntity.setRegTime(LocalDateTime.now());
        messageEntity.setSender(sender);
        messageEntity.setReceiver(receiver);
        messageEntity.setChatRoom(chatRoom);

        // 연관관계 편의 메서드 설정
        chatRoom.addMessage(messageEntity);

        // 메시지 저장
        MessageEntity savedMessage = messageRepository.save(messageEntity);

        return MessageDTO.toDTO(savedMessage);
    }

    // 특정 사용자의 메시지 목록 조회
    public List<MessageDTO> getMessageList(Long userId) {
        List<MessageEntity> messages = messageRepository.findBySenderIdOrReceiverId(userId, userId);
        return messages.stream().map(MessageDTO::toDTO).collect(Collectors.toList());
    }

    // 메시지 삭제
    public void deleteMessage(Long messageId) {
        messageRepository.deleteById(messageId);
    }

    // 이미지 업로드 처리 메서드
    private void handleFileUpload(MultipartFile file, MessageEntity messageEntity) {
        if (file != null && !file.isEmpty()) {
            String fileName = fileUploadService.storeFile(file);
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setImageUrl(fileName);
            imageEntity.setMessage(messageEntity);
            messageEntity.getImageList().add(imageEntity);
        }
    }
}
