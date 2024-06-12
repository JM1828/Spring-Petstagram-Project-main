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

    boolean existsByChatRoomIdAndReceiverAndIsReadFalse(Long id, UserEntity currentUser);

    @Query("SELECT COUNT(m) FROM MessageEntity m WHERE m.receiver = :receiver AND m.isRead = false")
    Long countUnreadMessagesByReceiver(@Param("receiver") UserEntity receiver);
}