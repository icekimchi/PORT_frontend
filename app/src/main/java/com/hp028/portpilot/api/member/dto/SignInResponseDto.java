package com.hp028.portpilot.api.member.dto;

import lombok.Getter;

@Getter
public class SignInResponseDto {
    private int status;
    private String message;
    private String email;
}

