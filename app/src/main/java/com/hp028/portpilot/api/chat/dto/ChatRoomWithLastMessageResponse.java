package com.hp028.portpilot.api.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ChatRoomWithLastMessageResponse {

    private ChatRoomResponse chatRoom;
    private ChatMessageDto lastMessage;

    @Getter
    @Setter
    public static class ChatRoomResponse {
        private Long id;

        private String roomName;
    }
}