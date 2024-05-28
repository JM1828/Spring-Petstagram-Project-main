package com.petstagram.dto;

import com.petstagram.entity.ChatRoomEntity;
import com.petstagram.entity.CommentEntity;
import com.petstagram.entity.MessageEntity;
import com.petstagram.entity.UserEntity;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {

    private Long chatRoomId;
    private List<MessageDTO> messages; // 채팅방의 모든 메시지
    private Long senderId; // 발신자 ID
    private String senderEmail; // 발신자 이메일
    private String senderName; // 발신자 이름
    private Long id; // 수신자 ID
    private String email; // 수신자 이메일
    private String name; // 수신자 이름
    private ProfileImageDTO profileImage;

    // Entity -> DTO 변환 메서드
    public static ChatRoomDTO toDTO(ChatRoomEntity chatRoom) {
        return ChatRoomDTO.builder()
                .chatRoomId(chatRoom.getId())
                .messages(chatRoom.getMessages().stream()
                        .map(MessageDTO::toDTO)
                        .collect(Collectors.toList()))
                .senderId(chatRoom.getSender().getId())
                .senderEmail(chatRoom.getSender().getEmail())
                .senderName(chatRoom.getSender().getName())
                .id(chatRoom.getReceiver().getId())
                .email(chatRoom.getReceiver().getEmail())
                .name(chatRoom.getReceiver().getName())
                .build();
    }
}