package com.petstagram.service;

import com.petstagram.dto.ChatRoomDTO;
import com.petstagram.dto.MessageDTO;
import com.petstagram.entity.ChatRoomEntity;
import com.petstagram.entity.MessageEntity;
import com.petstagram.entity.UserEntity;
import com.petstagram.repository.ChatRoomRepository;
import com.petstagram.repository.MessageRepository;
import com.petstagram.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    // 채팅방 생성
    @Transactional
    public ChatRoomDTO createChatRoom(ChatRoomDTO chatRoomDTO) {
        // 현재 인증된 사용자의 이메일 가져오기
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // 현재 사용자 찾기
        UserEntity currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 사용자의 이메일 목록에서 UserEntity를 찾아 Set으로 수집
        Set<UserEntity> userEntities = chatRoomDTO.getUserEmails().stream()
                .map(email -> userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found: " + email)))
                .collect(Collectors.toSet());

        // 현재 사용자를 Set 에 추가
        userEntities.add(currentUser);

        // 채팅방 엔티티 생성
        ChatRoomEntity chatRoom = ChatRoomEntity.toEntity(chatRoomDTO, userEntities);

        // 채팅방 저장
        ChatRoomEntity save = chatRoomRepository.save(chatRoom);

        return ChatRoomDTO.toDTO(save);
    }

    // 채팅방 및 메시지 목록 조회
    @Transactional
    public ChatRoomDTO getLatestMessagesByChatRoomId(Long chatRoomId) {

        // 현재 인증된 사용자의 이름(또는 이메일 등) 가져오기
        String senderEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // 보내는 사람 찾기
        userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 채팅방 찾기
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        // 채팅방에 속한 메시지 목록 조회
        List<MessageEntity> messages = messageRepository.findByChatRoomId(chatRoomId);

        // 채팅방 엔티티에 메시지 목록 설정
        chatRoom.addMessages(messages);

        // ChatRoomDTO 변환 (메시지 목록 포함)
        ChatRoomDTO chatRoomDTO  = ChatRoomDTO.toDTO(chatRoom);

        // 메시지 정보 설정
        List<MessageDTO> messageDTOs = messages.stream()
                .map(message -> {
                    MessageDTO messageDTO = MessageDTO.toDTO(message);
                    // 사용자 정보 설정
                    messageDTO.setSenderEmail(message.getSender().getEmail());
                    messageDTO.setReceiverEmail(message.getReceiver().getEmail());
                    // 시간 설정
                    messageDTO.setRegTime(message.getRegTime());
                    return messageDTO;
                })
                .collect(Collectors.toList());
        chatRoomDTO.setMessages(messageDTOs);

        return chatRoomDTO;
    }
}
