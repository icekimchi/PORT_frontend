package com.hp028.portpilot.api.chat.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetChatRoomResponse {
    private List<ChatRoomWithLastMessageResponse> chatRooms;
}
