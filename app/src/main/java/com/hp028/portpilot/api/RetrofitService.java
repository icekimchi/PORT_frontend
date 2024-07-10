package com.hp028.portpilot.api;

import com.hp028.portpilot.api.member.dto.LoginRequestDto;
import com.hp028.portpilot.api.member.dto.LoginResponseDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitService {

    @POST("/api/member/auth/sign-up")
    Call<LoginResponseDto> memberLogin(@Body LoginRequestDto data);

}
