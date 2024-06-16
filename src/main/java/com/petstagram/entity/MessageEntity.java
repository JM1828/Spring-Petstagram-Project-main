
package com.petstagram.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.petstagram.dto.MessageDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// 메시지
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "messages")
public class MessageEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id; // 메시지의 고유 식별자.

    private String messageContent; // 메시지 내용.

    private boolean isRead = false;

    // 메시지와 사용자는 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY) // FetchType.LAZY 는 지연 로딩을 의미
    @JoinColumn(name = "senderUser_id")
    @JsonIgnore
    private UserEntity sender; // 메시지를 보낸 사용자의 식별자.

    @ManyToOne(fetch = FetchType.LAZY) // FetchType.LAZY 는 지연 로딩을 의미
    @JoinColumn(name = "receiverUser_id")
    @JsonIgnore
    private UserEntity receiver; // 메시지를 받은 사용자의 식별자.

    // 메시지와 채팅룸은 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatRoom_id")
    @JsonIgnore
    private ChatRoomEntity chatRoom;

    // 메시지와 이미지는 일대다 관계
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageEntity> imageList = new ArrayList<>();

    // 메시지와 비디오는 일대다 관계
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VideoEntity> videoList = new ArrayList<>();
}
