package com.petstagram.dto;

import com.petstagram.entity.ChatRoomEntity;
import com.petstagram.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class ChatRoomDTO {

    private Long id;
    private List<MessageDTO> messages; // 채팅방의 모든 메시지
    private List<Long> userIds; // 사용자 ID 목록
    private List<String> userEmails; // 사용자 이메일 목록
    private List<String> userNames; // 사용자 이름 목록
    private List<UserDTO> users; // 사용자 목록

    // Entity -> DTO
    public static ChatRoomDTO toDTO(ChatRoomEntity chatRoom) {
        return ChatRoomDTO.builder()
                .id(chatRoom.getId())
                .messages(chatRoom.getMessages().stream()
                        .map(MessageDTO::toDTO)
                        .collect(Collectors.toList()))
                .userIds(chatRoom.getUsers().stream()
                        .map(UserEntity::getId)
                        .collect(Collectors.toList()))
                .userEmails(chatRoom.getUsers().stream()
                        .map(UserEntity::getEmail)
                        .collect(Collectors.toList()))
                .userNames(chatRoom.getUsers().stream()
                        .map(UserEntity::getName)
                        .collect(Collectors.toList()))
                .users(chatRoom.getUsers().stream()
                        .map(user -> {
                            UserDTO userDTO = UserDTO.toDTO(user);
                            ProfileImageDTO profileImageDTO = null;
                            if (user.getProfileImage() != null) {
                                profileImageDTO = ProfileImageDTO.builder()
                                        .imageUrl(user.getProfileImage().getImageUrl())
                                        .build();
                            }
                            userDTO.setProfileImage(profileImageDTO);
                            return userDTO;
                        })
                        .collect(Collectors.toList()))
                .build();
    }
}

