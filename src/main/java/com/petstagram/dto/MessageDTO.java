package com.petstagram.dto;

import com.petstagram.entity.MessageEntity;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {

    private Long id; // 메시지의 고유 식별자
    private String messageContent; // 메시지 내용
    private boolean isRead; // 메시지 읽음 상태
    private boolean isDeleted; // 메시지 삭제 상태
    private String messageType; // 메시지 유형
    private String senderEmail; // 메세지를 작성한 사용자 이메일
    private String receiverEmail; // 메세지를 작성한 사용자 이메일

    // Entity -> DTO
    public static MessageDTO toDTO(MessageEntity messageEntity) {
        return MessageDTO.builder()
                .id(messageEntity.getId())
                .messageContent(messageEntity.getMessageContent())
                .senderEmail(messageEntity.getSender().getEmail())
                .receiverEmail(messageEntity.getReceiver().getEmail())
                .build();
    }
}
