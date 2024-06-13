package com.petstagram.service;

import com.petstagram.dto.ChatRoomDTO;
import com.petstagram.dto.MessageDTO;
import com.petstagram.dto.UserDTO;
import com.petstagram.entity.*;
import com.petstagram.repository.ChatRoomRepository;
import com.petstagram.repository.MessageRepository;
import com.petstagram.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        // 기존 채팅방 존재 여부 확인
        Optional<ChatRoomEntity> existingChatRoom = chatRoomRepository.findBySenderIdAndReceiverId(
                chatRoomDTO.getSenderId(), chatRoomDTO.getReceiverId()
        );

        // 기존 채팅방이 이미 존재할 경우 해당 채팅방의 ID를 반환
        if (existingChatRoom.isPresent()) {
            return ChatRoomDTO.toDTO(existingChatRoom.get());
        }

        // 새로운 채팅방 생성
        ChatRoomEntity chatRoom = new ChatRoomEntity();
        chatRoom.setMessages(new ArrayList<>());
        chatRoom.setSender(userRepository.findById(chatRoomDTO.getSenderId()).orElseThrow());
        chatRoom.setReceiver(userRepository.findById(chatRoomDTO.getReceiverId()).orElseThrow());

        // 연관관계 메서드
        chatRoom.getSender().addSentChatRoom(chatRoom);
        chatRoom.getReceiver().addReceivedChatRoom(chatRoom);

        // 채팅방 저장
        ChatRoomEntity savedChatRoom = chatRoomRepository.save(chatRoom);

        return ChatRoomDTO.toDTO(savedChatRoom);
    }

    // 메시지 작성
    @Transactional
    public MessageDTO sendMessage(MessageDTO messageDTO, Principal principal) {

        String name = principal.getName();
        UserEntity sender = userRepository.findByEmail(name)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 받는 사람 찾기
        UserEntity receiver = userRepository.findById(messageDTO.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("수신자가 존재하지 않습니다."));

        // 채팅방 찾기
        ChatRoomEntity chatRoom = chatRoomRepository.findById(messageDTO.getChatRoomId())
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        // DTO 를 Entity 로 변환하고 사용자 정보 설정
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setMessageContent(messageDTO.getMessageContent());
        messageEntity.setImageList(new ArrayList<>());
        messageEntity.setChatRoom(chatRoom);
        messageEntity.setSender(sender);
        messageEntity.setReceiver(receiver);
        messageEntity.setRegTime(LocalDateTime.now());

        // 이미지 URL 을 저장
        if (messageDTO.getImageUrl() != null && !messageDTO.getImageUrl().isEmpty()) {
            try {
                ImageEntity imageEntity = new ImageEntity();
                imageEntity.setImageUrl(messageDTO.getImageUrl());
                imageEntity.setMessage(messageEntity);

                List<ImageEntity> imageList = messageEntity.getImageList();
                imageList.add(imageEntity);
                messageEntity.setImageList(imageList);
            } catch (Exception e) {
                throw new RuntimeException("이미지 저장에 실패했습니다.", e);
            }
        }

        // 연관관계 편의 메서드 설정
        chatRoom.addMessage(messageEntity);

        // 메시지 저장
        MessageEntity savedMessage = messageRepository.save(messageEntity);

        return MessageDTO.toDTO(savedMessage);
    }

    // 읽지 않은 메시지 개수 증가 메서드
    public UserDTO incrementUnreadMessageCount(Long receiverId) {
        UserEntity receiverUser = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("수신자를 찾을 수 없습니다."));
        receiverUser.incrementUnreadMessages();
        UserEntity savedUser = userRepository.save(receiverUser);
        return UserDTO.toDTO(savedUser);
    }

    // 채팅방 메시지 개수 업데이트
    public Long getUnreadMessageCountForUser(String receiverEmail) {
        UserEntity user = userRepository.findByEmail(receiverEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return messageRepository.countUnreadMessagesByReceiver(user);
    }

    // 채팅방 리스트 조회
    public List<ChatRoomDTO> getChatRoomList(Principal principal) {

        String name = principal.getName();
        UserEntity currentUser = userRepository.findByEmail(name)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 현재 사용자가 송신자 또는 수신자인 모든 채팅방 목록을 가져옴
        List<ChatRoomEntity> allChatRooms = Stream.concat(
                        chatRoomRepository.findBySender(currentUser).stream(),
                        chatRoomRepository.findByReceiver(currentUser).stream())
                .collect(Collectors.toList());

        // 각 채팅방을 ChatRoomDTO 로 변환하여 리스트에 추가하고 읽지 않은 메시지 여부 확인
        List<ChatRoomDTO> chatRoomDTOs = allChatRooms.stream()
                .map(chatRoomEntity -> {
                    List<MessageEntity> recentMessages = chatRoomRepository.findRecentMessagesByChatRoomId(chatRoomEntity.getId());

                    // 읽지 않은 메시지를 확인하여 • 표시 추가
//                    boolean hasUnreadMessage = messageRepository.existsByChatRoomIdAndReceiverAndIsReadFalse(chatRoomEntity.getId(), currentUser);

                    return ChatRoomDTO.builder()
                            .id(chatRoomEntity.getId())
                            .messages(recentMessages.stream().map(MessageDTO::toDTO).collect(Collectors.toList()))
                            .senderId(chatRoomEntity.getSender().getId())
                            .senderName(chatRoomEntity.getSender().getName())
                            .receiverId(chatRoomEntity.getReceiver().getId())
                            .receiverName(chatRoomEntity.getReceiver().getName())
                            .build();
                })
                .sorted(Comparator.comparing(chatRoom -> {
                    if (chatRoom.getMessages().isEmpty()) {
                        return LocalDateTime.MIN;
                    } else {
                        String regTime = chatRoom.getMessages().get(0).getRegTime();
                        return LocalDateTime.parse(regTime, DateTimeFormatter.ISO_DATE_TIME);
                    }
                }, Comparator.reverseOrder()))
                .collect(Collectors.toList());

        return chatRoomDTOs;
    }

    // 송신자와 수신자가 서로 다른 사용자와 대화한 채팅방만 목록에 포함
    public List<ChatRoomDTO> getActiveChatRoomList(Principal principal) {
        String name = principal.getName();
        UserEntity currentUser = userRepository.findByEmail(name)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 현재 사용자가 송신자 또는 수신자인 모든 채팅방과 관련된 메시지를 패치 조인을 통해 한 번에 로드
        List<ChatRoomEntity> activeChatRooms = chatRoomRepository.findActiveChatRoomsByUser(currentUser);

        // 각 채팅방의 ID를 추출하여 중복을 제거하고 필터링
        Set<Long> distinctChatRoomIds = activeChatRooms.stream()
                .map(ChatRoomEntity::getId)
                .collect(Collectors.toSet());

        // 활성 채팅방 ID 목록을 기반으로 채팅방 엔티티 조회
        List<ChatRoomEntity> distinctActiveChatRooms = chatRoomRepository.findAllById(new ArrayList<>(distinctChatRoomIds));

        // 각 채팅방을 ChatRoomDTO로 변환하여 리스트에 추가하고, 메시지의 도착 시간에 따라 정렬
        List<ChatRoomDTO> chatRoomDTOs = distinctActiveChatRooms.stream()
                .map(chatRoomEntity -> {
                    List<MessageEntity> recentMessages = chatRoomRepository.findRecentMessagesByChatRoomId(chatRoomEntity.getId());

                    return ChatRoomDTO.builder()
                            .id(chatRoomEntity.getId())
                            .messages(recentMessages.stream().map(MessageDTO::toDTO).collect(Collectors.toList()))
                            .senderId(chatRoomEntity.getSender().getId())
                            .senderName(chatRoomEntity.getSender().getName())
                            .receiverId(chatRoomEntity.getReceiver().getId())
                            .receiverName(chatRoomEntity.getReceiver().getName())
                            .build();
                })
                .sorted(Comparator.comparing(chatRoom -> {
                    if (chatRoom.getMessages().isEmpty()) {
                        return LocalDateTime.MIN;
                    } else {
                        String regTime = chatRoom.getMessages().get(0).getRegTime();
                        return LocalDateTime.parse(regTime, DateTimeFormatter.ISO_DATE_TIME);
                    }
                }, Comparator.reverseOrder()))
                .collect(Collectors.toList());

        return chatRoomDTOs;
    }

    // 채팅방 ID에 해당하는 채팅방과 메시지들을 가져오고 읽음으로 처리하는 메서드
    @Transactional
    public ChatRoomDTO getChatRoomWithMessagesByIdAndMarkAsRead(Long chatRoomId, String userEmail) {

        // 채팅방 찾기
        ChatRoomEntity chatRoom = chatRoomRepository.findChatRoomWithMessagesById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        // 채팅방에 속한 가장 최근 메시지 목록 조회 (내림차순으로 정렬)
        List<MessageEntity> messages = chatRoomRepository.findRecentMessagesByChatRoomId(chatRoomId);

        // 메시지를 읽음 상태로 업데이트 (수신자가 현재 사용자일 때만)
        for (MessageEntity message : messages) {
            if (!message.isRead() && message.getReceiver().getEmail().equals(userEmail)) {
                message.setRead(true);
            }
        }

        messageRepository.saveAll(messages);
        chatRoomRepository.save(chatRoom);

        // ChatRoomDTO 변환
        ChatRoomDTO chatRoomDTO = ChatRoomDTO.toDTO(chatRoom);

        // 메시지 정보 설정
        List<MessageDTO> messageDTOs = messages.stream()
                .map(MessageDTO::toDTO)
                .collect(Collectors.toList());

        chatRoomDTO.setMessages(messageDTOs);

        return chatRoomDTO;
    }
}