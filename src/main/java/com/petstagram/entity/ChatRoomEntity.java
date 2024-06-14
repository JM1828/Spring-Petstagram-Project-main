package com.petstagram.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.petstagram.dto.ChatRoomDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chatRooms")
public class ChatRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatRoom_id")
    private Long id;

    private boolean senderHasUnreadMessage; // 발신자의 읽지 않은 메시지 여부

    private long senderUnreadMessageCount; // 발신자의 메시지 개수

    private boolean receiverHasUnreadMessage; // 수신자의 읽지 않은 메시지 여부

    private long receiverUnreadMessageCount; // 수신자의 메시지 개수

    // 채팅룸과 메시지 는 일대다 관계
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageEntity> messages = new ArrayList<>();

    // 채팅룸과 사용자는 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    @JsonIgnore
    private UserEntity sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    @JsonIgnore
    private UserEntity receiver;

    // 메시지를 추가하는 메서드
//    public void addMessage(MessageEntity message) {
//        this.messages.add(message);
//        if (message.getChatRoom() != this) {
//            message.setChatRoom(this);
//        }
//        if (message.getSender().getId().equals(this.sender.getId())) {
//            // 발신자가 보낸 메시지일 경우, 수신자의 읽지 않은 메시지 개수를 증가
//            this.receiverUnreadMessageCount++;
//            this.receiverHasUnreadMessage = true;
//        } else if (message.getSender().getId().equals(this.receiver.getId())) {
//            // 수신자가 보낸 메시지일 경우, 발신자의 읽지 않은 메시지 개수를 증가
//            this.senderUnreadMessageCount++;
//            this.senderHasUnreadMessage = true;
//        }
//    }

    public void addMessage(MessageEntity message) {
        this.messages.add(message);
        message.setChatRoom(this);
    }

    // 메시지를 읽었을 때 호출하는 메서드
    public void markMessageAsRead(UserEntity user) {
        if (user.getId().equals(this.sender.getId())) {
            this.senderUnreadMessageCount = 0;
            this.senderHasUnreadMessage = false;
        } else if (user.getId().equals(this.receiver.getId())) {
            this.receiverUnreadMessageCount = 0;
            this.receiverHasUnreadMessage = false;
        }
    }
}