package com.hp028.portpilot.api.chat.dto;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ChatMessageDto {

    @SerializedName("chatMessage")
    private String chatMessage;

    @SerializedName("senderType")
    private SenderType senderType;

    @SerializedName("timestamp")
    private String timestamp;


    public ChatMessageDto(String chatMessage, SenderType senderType, String timestamp) {
        this.chatMessage = chatMessage;
        this.senderType = senderType;
        this.timestamp = timestamp;
    }
}
