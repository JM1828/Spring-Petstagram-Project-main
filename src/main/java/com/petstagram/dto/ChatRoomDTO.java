package com.petstagram.dto;

import com.petstagram.entity.MessageEntity;
import com.petstagram.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ChatRoomDTO {

    private String roomName;

    private List<MessageEntity> messages;

    private Set<String> userEmails; // 사용자 이메일 목록
}

