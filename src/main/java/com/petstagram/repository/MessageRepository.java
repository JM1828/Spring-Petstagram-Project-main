package com.petstagram.repository;

import com.petstagram.entity.ChatRoomEntity;
import com.petstagram.entity.MessageEntity;
import com.petstagram.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Arrays;
import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    // 두 사용자 간의 메시지를 가져옴
    @Query("SELECT m FROM MessageEntity m WHERE (m.sender.email = :senderEmail AND m.receiver.email = :receiverEmail) OR" +
            " (m.sender.email = :receiverEmail AND m.receiver.email = :senderEmail) ")
    List<MessageEntity> findMessagesBetweenUsers(@Param("senderEmail") String senderEmail, @Param("receiverEmail") String receiverEmail);

    List<MessageEntity> findBySenderIdOrReceiverId(Long SenderId, Long ReceiverId);

//    List<MessageEntity> findRecentMessagesByChatRoomId(Long id);

    List<MessageEntity> findAllByChatRoomId(Long chatRoomId);

    List<MessageEntity> findByChatRoomOrderByRegTimeAsc(ChatRoomEntity chatRoom);

    // 송신자 ID를 기반으로 채팅방 ID 조회
    @Query("SELECT DISTINCT m.chatRoom.id FROM MessageEntity m WHERE m.sender.id = :senderId")
    List<Long> findChatRoomIdsBySenderId(Long senderId);

    // 수신자 ID를 기반으로 채팅방 ID 조회
    @Query("SELECT DISTINCT m.chatRoom.id FROM MessageEntity m WHERE m.receiver.id = :receiverId")
    List<Long> findChatRoomIdsByReceiverId(Long receiverId);

    Long countByReceiver(UserEntity user);

    Long countByReceiverAndIsReadFalse(UserEntity receiver);

    List<MessageEntity> findByChatRoomIdAndReceiverAndIsReadFalse(Long chatRoomId, UserEntity receiver);
}