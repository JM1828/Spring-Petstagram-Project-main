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

    List<ChatRoomEntity> findBySender(UserEntity currentUser);

    List<ChatRoomEntity> findByReceiver(UserEntity currentUser);

    Optional<ChatRoomEntity> findBySenderAndReceiver(UserEntity sender, UserEntity receiver);
}