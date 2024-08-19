package com.hp028.portpilot.api.chat.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class GetChatMessagesResponse {
    private int status;
    private String message;
    private List<ChatMessageDto> body;
}