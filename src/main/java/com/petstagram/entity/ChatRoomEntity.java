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

    private boolean hasUnreadMessage = false; // 읽지 않은 메시지 여부

    private long messageCount; // 메시지 개수

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
    public void addMessage(MessageEntity message) {
        this.messages.add(message);
        if (message.getChatRoom() != this) {
            message.setChatRoom(this);
        }
        this.messageCount++;
        this.hasUnreadMessage = true; // 새로운 메시지가 추가될 때 읽지 않은 메시지가 있다고 표시
    }

    // 메시지를 읽었을 때 메시지 개수 초기화
    public void resetMessageCount() {
        this.messageCount = 0;
        this.hasUnreadMessage = false;
    }
}