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

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    // 채팅방 생성
    @Transactional
    public ChatRoomEntity createChatRoom(ChatRoomDTO chatRoomDTO) {
        // 현재 인증된 사용자의 이메일 가져오기
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // 현재 사용자 찾기
        UserEntity currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Set<UserEntity> users = chatRoomDTO.getUserEmails().stream()
                .map(userService::findByEmail)
                .collect(Collectors.toSet());

        // 현재 사용자 추가
        users.add(currentUser);

        // 채팅방 엔티티 생성
        ChatRoomEntity chatRoom = ChatRoomEntity.toEntity(chatRoomDTO, users);

        // 채팅방 저장
        return chatRoomRepository.save(chatRoom);
    }

    // 채팅방에 사용자 초대
    @Transactional
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
        chatRoom.getUser().add(user);

        // 변경 사항이 자동으로 반영되므로 명시적인 저장은 생략 가능
        return chatRoom;
    }
}
