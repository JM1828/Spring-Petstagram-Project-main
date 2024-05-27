package com.petstagram.repository;

import com.petstagram.entity.ChatRoomEntity;
import com.petstagram.entity.MessageEntity;
import com.petstagram.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

    // 특정 채팅 방의 가장 최근 메시지를 가져오는 쿼리
    @Query("SELECT message FROM MessageEntity message WHERE message.chatRoom.id = :chatRoomId ORDER BY message.regTime DESC")
    List<MessageEntity> findRecentMessagesByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    @Query("SELECT cr FROM ChatRoomEntity cr JOIN cr.users u1 JOIN cr.users u2 " +
            "WHERE u1.id = :userId1 AND u2.id = :userId2")
    Optional<ChatRoomEntity> findChatRoomByUserIds(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    List<ChatRoomEntity> findAllByUsersContains(Optional<UserEntity> currentUser);

    List<ChatRoomEntity> findDistinctByMessages_Sender(Optional<UserEntity> sender);

    List<ChatRoomEntity> findDistinctByMessages_Receiver(Optional<UserEntity> receiver);

}
