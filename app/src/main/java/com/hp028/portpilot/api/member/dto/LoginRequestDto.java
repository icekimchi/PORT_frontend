package com.hp028.portpilot.api.member.dto;

import com.google.gson.annotations.SerializedName;

public class LoginRequestDto {
    @SerializedName("email")
    private String memberId;
    @SerializedName("name")
    private String memberPw;

    @SerializedName("password")
    private String password;

    public LoginRequestDto(String memberId, String memberPw, String password) {
        this.memberId = memberId;
        this.memberPw = memberPw;
        this.password = password;
    }
}
