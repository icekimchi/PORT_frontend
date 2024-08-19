package com.hp028.portpilot.api.chat.dto;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class SendChatMessageResponse {
    @SerializedName("userMessage")
    private String userMessage;

    @SerializedName("serverMessage")
    private String serverMessage;

    @SerializedName("userMessageTime")
    private String userMessageTime;

    @SerializedName("serverMessageTime")
    private String serverMessageTime;
}
