package com.hp028.portpilot.api.chat.dto;

import com.google.gson.annotations.SerializedName;

public class SendChatMessageRequest {
    @SerializedName("chatMessage")
    private String chatMessage;

    @SerializedName("chatRoomId")
    private Long chatRoomId;
}
