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
    private Long senderId; // 발신자 ID
    private String senderEmail; // 발신자 이메일
    private String senderName; // 발신자 이름
    private Long receiverId; // 수신자 ID
    private String receiverEmail; // 수신자 이메일
    private String receiverName; // 수신자 이름

    // Entity -> DTO 변환 메서드
    public static ChatRoomDTO toDTO(ChatRoomEntity chatRoom) {
        UserEntity sender = chatRoom.getSender();
        UserEntity receiver = chatRoom.getReceiver();

        return ChatRoomDTO.builder()
                .id(chatRoom.getId())
                .messages(chatRoom.getMessages().stream()
                        .map(MessageDTO::toDTO)
                        .collect(Collectors.toList()))
                .senderId(sender.getId())
                .senderEmail(sender.getEmail())
                .senderName(sender.getName())
                .receiverId(receiver.getId())
                .receiverEmail(receiver.getEmail())
                .receiverName(receiver.getName())
                .build();
    }
}

