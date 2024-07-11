package com.hp028.portpilot.api.member.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

@Getter
public class SignupRequestDto {
    @SerializedName("email")
    private String email;
    @SerializedName("name")
    private String name;
    @SerializedName("password")
    private String password;

    public SignupRequestDto(String memberId, String memberPw, String password) {
        this.email = memberId;
        this.name = memberPw;
        this.password = password;
    }
}
