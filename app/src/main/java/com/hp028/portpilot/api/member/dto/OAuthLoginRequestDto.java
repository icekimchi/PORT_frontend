package com.hp028.portpilot.api.member.dto;

import com.google.gson.annotations.SerializedName;
import com.hp028.portpilot.api.common.ApiResponse;

public class OAuthLoginRequestDto {

    @SerializedName("provider")
    private String provider;

    @SerializedName("accessToken")
    private String accessToken;
}
