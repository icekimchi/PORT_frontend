package com.hp028.portpilot.api.member.dto;

import lombok.Getter;

@Getter
public class OAuthLoginResponseDto {
    private int status;
    private String jwt;
}
