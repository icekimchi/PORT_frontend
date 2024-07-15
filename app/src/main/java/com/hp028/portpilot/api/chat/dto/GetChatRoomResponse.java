package com.hp028.portpilot.api.chat.dto;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
public class GetChatRoomResponse {

    private int status;
    private String message;
    private List<GetChatRoomResponseBody> body;

    @Getter
    @Setter
    public static class GetChatRoomResponseBody {
        private Long id;

        private String roomName;
    }
}