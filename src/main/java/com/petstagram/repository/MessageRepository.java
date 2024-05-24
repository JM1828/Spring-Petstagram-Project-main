package com.petstagram.repository;

import com.petstagram.entity.MessageEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    // 두 사용자 간의 메시지를 가져옴
    @Query("SELECT m FROM MessageEntity m WHERE (m.sender.email = :senderEmail AND m.receiver.email = :receiverEmail) OR" +
            " (m.sender.email = :receiverEmail AND m.receiver.email = :senderEmail) ")
    List<MessageEntity> findMessagesBetweenUsers(@Param("senderEmail") String senderEmail, @Param("receiverEmail") String receiverEmail);

    // 특정 채팅방의 모든 메시지를 가져옴
    List<MessageEntity> findByChatRoomId(Long roomId);

//    @Query("SELECT m FROM MessageEntity m WHERE m.chatRoom.id = :chatRoomId ORDER BY m.regTime DESC")
//    List<MessageEntity> findLatestMessageByChatRoomId(@Param("chatRoomId") Long chatRoomId, Pageable pageable);
}

