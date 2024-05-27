package com.petstagram.service;

import com.petstagram.dto.ChatRoomDTO;
import com.petstagram.dto.MessageDTO;
import com.petstagram.dto.ProfileImageDTO;
import com.petstagram.dto.UserDTO;
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

        List<UserEntity> userEntities = chatRoomDTO.getUserIds().stream()
                .map(email -> userRepository.findById(email)
                        .orElseThrow(() -> new RuntimeException("User not found: " + email)))
                .collect(Collectors.toList());

        // 채팅방 엔티티 생성
        ChatRoomEntity chatRoom = ChatRoomEntity.toEntity(chatRoomDTO, userEntities);

        // 채팅방 저장
        ChatRoomEntity savedChatRoom = chatRoomRepository.save(chatRoom);

        return ChatRoomDTO.toDTO(savedChatRoom);
    }

    // 채팅방 존재 여부 확인
    public boolean checkChatRoomExists(Long userId1, Long userId2) {
        Optional<ChatRoomEntity> chatRoom = chatRoomRepository.findChatRoomByUserIds(userId1, userId2);
        return chatRoom.isPresent();
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

        // 사용자가 보낸 메시지가 포함된 채팅방 목록을 가져옴
        List<ChatRoomEntity> sentChatRooms = chatRoomRepository.findDistinctByMessages_Sender(currentUserOptional);

        // 사용자가 받은 메시지가 포함된 채팅방 목록을 가져옴
        List<ChatRoomEntity> receivedChatRooms = chatRoomRepository.findDistinctByMessages_Receiver(currentUserOptional);

        // 두 목록을 합침
        Set<ChatRoomEntity> allChatRooms = new HashSet<>();
        allChatRooms.addAll(sentChatRooms);
        allChatRooms.addAll(receivedChatRooms);

        // ChatRoomEntity 를 ChatRoomDTO 로 변환하여 반환
        return allChatRooms.stream()
                .map(chatRoomEntity -> {
                    // 채팅방의 최근 메시지를 가져옴
                    List<MessageEntity> recentMessages = messageRepository.findRecentMessagesByChatRoomId(chatRoomEntity.getId());

                    // ChatRoomDTO 생성
                    ChatRoomDTO.ChatRoomDTOBuilder builder = ChatRoomDTO.builder()
                            .id(chatRoomEntity.getId())
                            .messages(recentMessages.stream().map(MessageDTO::toDTO).collect(Collectors.toList()))
                            .userIds(chatRoomEntity.getUsers().stream().map(UserEntity::getId).collect(Collectors.toList()))
                            .userEmails(chatRoomEntity.getUsers().stream().map(UserEntity::getEmail).collect(Collectors.toList()))
                            .userNames(chatRoomEntity.getUsers().stream().map(UserEntity::getName).collect(Collectors.toList()))
                            .users(chatRoomEntity.getUsers().stream().map(user -> {
                                UserDTO userDTO = UserDTO.toDTO(user);
                                ProfileImageDTO profileImageDTO = null;
                                if (user.getProfileImage() != null) {
                                    profileImageDTO = ProfileImageDTO.builder()
                                            .imageUrl(user.getProfileImage().getImageUrl())
                                            .build();
                                }
                                userDTO.setProfileImage(profileImageDTO);
                                return userDTO;
                            }).collect(Collectors.toList()));

                    return builder.build();
                })
                .collect(Collectors.toList());
    }

    // 특정 채팅방의 상세 정보 조회
    public ChatRoomDTO getChatRoomById(Long chatRoomId) {
        // 채팅방 찾기
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        // ChatRoomDTO 변환
        ChatRoomDTO chatRoomDTO = ChatRoomDTO.toDTO(chatRoom);

        // 사용자 정보 설정
        List<UserEntity> users = chatRoom.getUsers();
        chatRoomDTO.setUsers(users.stream().map(UserDTO::toDTO).collect(Collectors.toList()));

        return chatRoomDTO;
    }
}

