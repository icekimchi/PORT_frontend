package com.hp028.portpilot.api.member.dto;

import lombok.Getter;

@Getter
public class SignupResponseDto {
    private int status;
    private String message;
    private SignUpResponseBody body;

    public static class SignUpResponseBody {
        private String email;
        private String name;
    }
}


