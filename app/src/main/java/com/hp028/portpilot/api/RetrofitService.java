package com.hp028.portpilot.api;

import com.hp028.portpilot.api.chat.dto.CreateChatRoomResponse;
import com.hp028.portpilot.api.chat.dto.GetChatMessagesResponse;
import com.hp028.portpilot.api.chat.dto.ChatRoomWithLastMessageResponse;
import com.hp028.portpilot.api.chat.dto.GetChatRoomResponse;
import com.hp028.portpilot.api.chat.dto.SendChatMessageRequest;
import com.hp028.portpilot.api.chat.dto.SendChatMessageResponse;
import com.hp028.portpilot.api.common.ApiResponse;
import com.hp028.portpilot.api.member.dto.OAuthLoginResponseDto;
import com.hp028.portpilot.api.member.dto.SignInRequestDto;
import com.hp028.portpilot.api.member.dto.SignInResponseDto;
import com.hp028.portpilot.api.member.dto.SignupRequestDto;
import com.hp028.portpilot.api.member.dto.SignupResponseDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface RetrofitService {

    @POST("/api/member/auth/sign-up")
    Call<SignupResponseDto> memberSignUp(@Body SignupRequestDto data);

    @POST("/api/member/auth/sign-in")
    Call<SignInResponseDto> memberSignIn(@Body SignInRequestDto data);

    @POST("/api/member/oauth/{provider}")
    Call<OAuthLoginResponseDto> memberOAuthSignIn(@Path ("provider") String provider, @Body String data);

    @POST("/api/chatrooms")
    Call<CreateChatRoomResponse> createChatRoom(@Body String data);

    @GET("/api/chatrooms")
    Call<ApiResponse<GetChatRoomResponse>> getChatRoom();

    @POST("/api/chat")
    Call<ApiResponse<SendChatMessageResponse>> sendChatMessage(@Body SendChatMessageRequest data);

    @GET("/api/chat/{chatRoomId}")
    Call<GetChatMessagesResponse> getChatMessages(@Path ("chatRoomId") Long chatRoomId);

}
