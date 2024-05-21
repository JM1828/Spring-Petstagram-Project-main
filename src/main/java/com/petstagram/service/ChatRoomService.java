package com.petstagram.service;

import com.petstagram.dto.ChatRoomDTO;
import com.petstagram.entity.ChatRoomEntity;
import com.petstagram.entity.UserEntity;
import com.petstagram.repository.ChatRoomRepository;
import com.petstagram.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    // 채팅방 생성
    @Transactional
    public ChatRoomEntity createChatRoom(ChatRoomDTO chatRoomDTO) {
        ChatRoomEntity chatRoom = ChatRoomEntity.toEntity(chatRoomDTO);
        return chatRoomRepository.save(chatRoom);
    }

    // 채팅방에 사용자 초대
    public ChatRoomEntity addUserToChatRoom(Long roomId) {

        // 현재 인증된 사용자의 이름(또는 이메일 등) 가져오기
        String senderEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // 보내는 사람 찾기
        UserEntity user = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 채팅방 찾기
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        // 사용자 추가
        chatRoom.getUsers().add(user);
        return chatRoomRepository.save(chatRoom);
    }
}
