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

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    // 채팅방 생성
    @Transactional
    public ChatRoomDTO createChatRoom(ChatRoomDTO chatRoomDTO) {

        // sender 와 receiver 를 각각 조회
        UserEntity sender = userRepository.findById(chatRoomDTO.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found: " + chatRoomDTO.getSenderId()));
        UserEntity receiver = userRepository.findById(chatRoomDTO.getId())
                .orElseThrow(() -> new RuntimeException("Receiver not found: " + chatRoomDTO.getId()));

        // 채팅방 엔티티 생성
        ChatRoomEntity chatRoom = ChatRoomEntity.toEntity(chatRoomDTO, sender, receiver);

        // 연관관계 설정
        sender.addSentChatRoom(chatRoom);
        receiver.addReceivedChatRoom(chatRoom);

        // 채팅방 저장
        ChatRoomEntity savedChatRoom = chatRoomRepository.save(chatRoom);

        return ChatRoomDTO.toDTO(savedChatRoom);
    }

    // 채팅방 ID에 해당하는 채팅방과 메시지들을 가져오는 메서드
    public ChatRoomDTO getChatRoomWithMessagesById(Long chatRoomId) {
        // 현재 인증된 사용자의 이름(또는 이메일 등) 가져오기
        String senderEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // 보내는 사람 찾기
        userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 채팅방 찾기
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        // 채팅방에 속한 모든 메시지 목록 조회
        List<MessageEntity> messages = messageRepository.findAllByChatRoomId(chatRoomId);

        // ChatRoomDTO 변환
        ChatRoomDTO chatRoomDTO = ChatRoomDTO.toDTO(chatRoom);

        // 메시지 정보 설정
        List<MessageDTO> messageDTOs = messages.stream()
                .map(MessageDTO::toDTO)
                .collect(Collectors.toList());
        chatRoomDTO.setMessages(messageDTOs);

        return chatRoomDTO;
    }

    // 채팅방 리스트 조회
    public List<ChatRoomDTO> getChatRoomList(String currentUserEmail) {
        // 현재 사용자를 가져옴
        Optional<UserEntity> currentUserOptional = userRepository.findByEmail(currentUserEmail);

        if (!currentUserOptional.isPresent()) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        UserEntity currentUser = currentUserOptional.get();

        // 사용자가 송신자인 채팅방 목록과 수신자인 채팅방 목록을 각각 가져옴
        List<ChatRoomEntity> sentChatRooms = chatRoomRepository.findBySender(currentUser);
        List<ChatRoomEntity> receivedChatRooms = chatRoomRepository.findByReceiver(currentUser);

        // 각각의 채팅방 목록을 ChatRoomDTO로 변환
        List<ChatRoomDTO> sentChatRoomDTOs = sentChatRooms.stream()
                .map(chatRoomEntity -> {
                    // 채팅방의 최근 메시지를 가져옴
                    List<MessageEntity> recentMessages = chatRoomRepository.findRecentMessagesByChatRoomId(chatRoomEntity.getId());

                    return ChatRoomDTO.builder()
                            .chatRoomId(chatRoomEntity.getId())
                            .messages(recentMessages.stream().map(MessageDTO::toDTO).collect(Collectors.toList()))
                            .senderId(chatRoomEntity.getSender().getId())
                            .senderEmail(chatRoomEntity.getSender().getEmail())
                            .senderName(chatRoomEntity.getSender().getName())
                            .id(chatRoomEntity.getReceiver().getId())
                            .email(chatRoomEntity.getReceiver().getEmail())
                            .name(chatRoomEntity.getReceiver().getName())
                            .build();
                })
                .collect(Collectors.toList());

        List<ChatRoomDTO> receivedChatRoomDTOs = receivedChatRooms.stream()
                .map(chatRoomEntity -> {
                    List<MessageEntity> recentMessages = chatRoomRepository.findRecentMessagesByChatRoomId(chatRoomEntity.getId());

                    return ChatRoomDTO.builder()
                            .chatRoomId(chatRoomEntity.getId())
                            .messages(recentMessages.stream().map(MessageDTO::toDTO).collect(Collectors.toList()))
                            .senderId(chatRoomEntity.getSender().getId())
                            .senderEmail(chatRoomEntity.getSender().getEmail())
                            .senderName(chatRoomEntity.getSender().getName())
                            .id(chatRoomEntity.getReceiver().getId())
                            .email(chatRoomEntity.getReceiver().getEmail())
                            .name(chatRoomEntity.getReceiver().getName())
                            .build();
                })
                .collect(Collectors.toList());

        // 현재 사용자가 송신자인 경우와 수신자인 경우에 따라 적절한 목록 반환
        // 이 부분은 비즈니스 로직에 따라 조정될 수 있습니다.
        // 예: 모든 채팅방을 보여주거나, 특정 조건에 따라 필터링하여 반환할 수 있습니다.
        List<ChatRoomDTO> result = new ArrayList<>();
        result.addAll(sentChatRoomDTOs);
        result.addAll(receivedChatRoomDTOs);

        return result;
    }

    // 특정 채팅방의 상세 정보 조회
    public ChatRoomDTO getChatRoomById(Long chatRoomId) {
        // 현재 인증된 사용자의 이메일 가져오기
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // 채팅방 찾기
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        // 사용자 역할에 따른 정보 결정
        boolean isCurrentUserSender = chatRoom.getSender().getEmail().equals(currentUserEmail);

        // ChatRoomResponseDTO 변환
        ChatRoomDTO chatRoomResponseDTO = new ChatRoomDTO();

        // 현재 사용자가 송신자인 경우
        if (isCurrentUserSender) {
            chatRoomResponseDTO.setId(chatRoom.getReceiver().getId());
            chatRoomResponseDTO.setName(chatRoom.getReceiver().getName());
            chatRoomResponseDTO.setEmail(chatRoom.getReceiver().getEmail());
        } else { // 현재 사용자가 수신자인 경우
            chatRoomResponseDTO.setId(chatRoom.getSender().getId());
            chatRoomResponseDTO.setName(chatRoom.getSender().getName());
            chatRoomResponseDTO.setEmail(chatRoom.getSender().getEmail());
        }

        // 나머지 필요한 정보 설정
        chatRoomResponseDTO.setChatRoomId(chatRoom.getId());
        chatRoomResponseDTO.setMessages(chatRoom.getMessages().stream().map(MessageDTO::toDTO).collect(Collectors.toList()));

        return chatRoomResponseDTO;
    }
}