package com.petstagram.entity;

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

    private String roomName;

    // 채팅룸과 메시지 는 일대다 관계
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageEntity> messages = new ArrayList<>();

    // 채팅룸과 사용자는 다대다 관계
    @ManyToMany
    @JoinTable(
            name = "chatRoom_users",
            joinColumns = @JoinColumn(name = "chatRoom_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> user = new HashSet<>();

    // DTO -> Entity
    public static ChatRoomEntity toEntity(ChatRoomDTO chatRoomDTO, Set<UserEntity> userEntities) {
        return ChatRoomEntity.builder()
                .roomName(chatRoomDTO.getRoomName())
                .messages(new ArrayList<>())
                .user(userEntities)
                .build();
    }
}
