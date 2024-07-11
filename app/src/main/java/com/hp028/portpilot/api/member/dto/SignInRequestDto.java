package com.hp028.portpilot.api.member.dto;

import com.google.gson.annotations.SerializedName;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SignInRequestDto {
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;

    public SignInRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
