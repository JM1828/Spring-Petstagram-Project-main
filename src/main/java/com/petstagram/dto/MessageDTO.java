package com.petstagram.dto;

import com.petstagram.entity.ChatRoomEntity;
import com.petstagram.entity.MessageEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {

    private Long id; // 메시지의 고유 식별자
    private Long chatRoomId;
    private String messageContent; // 메시지 내용
    private Long senderId; // 메시지를 보낸 사용자 ID
    private String senderName;  // 메시지를 보낸 사용자 이름
    private String senderEmail;  // 메시지를 보낸 사용자 이름
    private Long receiverId; // 메시지를 받은 사용자 ID
    private String receiverName; // 메시지를 받은 사용자 이름
    private String receiverEmail; // 메시지를 받은 사용자 이름
    private LocalDateTime regTime;

    // Entity -> DTO
    public static MessageDTO toDTO(MessageEntity messageEntity) {
        return MessageDTO.builder()
                .id(messageEntity.getId())
                .chatRoomId(messageEntity.getChatRoom().getId())
                .messageContent(messageEntity.getMessageContent())
                .senderId(messageEntity.getSender().getId())
                .senderName(messageEntity.getSender().getName())
                .senderEmail(messageEntity.getSender().getEmail())
                .receiverId(messageEntity.getReceiver().getId())
                .receiverName(messageEntity.getReceiver().getName())
                .receiverEmail(messageEntity.getReceiver().getEmail())
                .regTime(messageEntity.getRegTime())
                .build();
    }
}