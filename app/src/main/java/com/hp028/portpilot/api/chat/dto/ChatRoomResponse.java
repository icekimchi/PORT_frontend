package com.hp028.portpilot.api.chat.dto;

import com.hp028.portpilot.api.member.dto.SignupResponseDto;

import lombok.Getter;

@Getter
public class ChatRoomResponse {

    private int status;
    private String message;
    private ChatRoomResponseBody body;

    public static class ChatRoomResponseBody {

        private Long id;

        private String roomName;
    }
}
