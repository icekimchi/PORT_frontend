package com.hp028.portpilot.api.member.dto;

import lombok.Getter;

@Getter
public class LoginResponseDto {
    private int status;
    private String message;
    private LoginResponseBody body;

    public static class LoginResponseBody {
        private String email;
        private String name;
    }
}
