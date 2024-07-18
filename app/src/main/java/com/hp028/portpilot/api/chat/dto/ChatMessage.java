package com.hp028.portpilot.api.chat.dto;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ChatMessage {
    private String chatMessage;

    private SenderType senderType;

    private LocalDateTime timestamp;
}
