package com.petstagram.repository;

import com.petstagram.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {


}
