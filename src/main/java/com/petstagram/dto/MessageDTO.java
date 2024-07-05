package com.petstagram.dto;

import com.petstagram.entity.MessageEntity;
import lombok.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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
    private String senderEmail;  // 메시지를 보낸 사용자 이메일
    private Long receiverId; // 메시지를 받은 사용자 ID
    private String receiverName; // 메시지를 받은 사용자 이름
    private String receiverEmail; // 메시지를 받은 사용자 이메일
    private String regTime;
    private List<ImageDTO> imageList;
    private List<String> imageUrls;
    private String audioUrl;
    private List<VideoDTO> videoList; // 비디오 리스트
    private List<String> videoUrls;   // 비디오 URL 리스트
    private boolean isRead = false;

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
                .regTime(messageEntity.getRegTime().format(DateTimeFormatter.ISO_DATE_TIME))
                .isRead(messageEntity.isRead())
                .imageList(messageEntity.getImageList().stream()
                        .map(ImageDTO::toDTO)
                        .collect(Collectors.toList()))
                .imageUrls(messageEntity.getImageList().stream()
                        .map(image -> image.getImageUrl())
                        .collect(Collectors.toList()))
                .audioUrl(messageEntity.getAudioUrl()) // 음성 메시지 URL 설정
                .videoList(messageEntity.getVideoList().stream()
                        .map(VideoDTO::toDTO)
                        .collect(Collectors.toList())) // 비디오 리스트 설정
                .videoUrls(messageEntity.getVideoList().stream()
                        .map(video -> video.getVideoUrl())
                        .collect(Collectors.toList())) // 비디오 URL 리스트 설정
                .build();
    }
}
