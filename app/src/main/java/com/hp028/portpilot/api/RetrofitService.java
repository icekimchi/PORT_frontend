package com.hp028.portpilot.api;

import com.hp028.portpilot.api.member.dto.OAuthLoginRequestDto;
import com.hp028.portpilot.api.member.dto.OAuthLoginResponseDto;
import com.hp028.portpilot.api.member.dto.SignInRequestDto;
import com.hp028.portpilot.api.member.dto.SignInResponseDto;
import com.hp028.portpilot.api.member.dto.SignupRequestDto;
import com.hp028.portpilot.api.member.dto.SignupResponseDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface RetrofitService {

    @POST("/api/member/auth/sign-up")
    Call<SignupResponseDto> memberSignUp(@Body SignupRequestDto data);

    @POST("/api/member/auth/sign-in")
    Call<SignInResponseDto> memberSignIn(@Body SignInRequestDto data);

    @POST("/api/member/oauth/{provider}")
    Call<OAuthLoginResponseDto> memberOAuthSignIn(@Path ("provider") String provider, @Body String data);

}
