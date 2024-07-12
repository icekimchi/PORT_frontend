package com.hp028.portpilot.socialloginmanager;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.hp028.portpilot.BuildConfig;
import com.hp028.portpilot.TokenManager;
import com.hp028.portpilot.api.RetrofitClient;
import com.hp028.portpilot.api.RetrofitService;
import com.hp028.portpilot.api.member.dto.OAuthLoginResponseDto;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.common.model.ClientError;
import com.kakao.sdk.common.model.ClientErrorCause;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KakaoLoginManager {

    private static final String TAG = "KakaoLoginManager";
    private static KakaoLoginManager instance;
    private final TokenManager tokenManager;
    private final RetrofitService service;

    private KakaoLoginManager(Context context) {
        this.tokenManager = TokenManager.getInstance(context);
        this.service = RetrofitClient.getApiService(context);
    }

    public static synchronized KakaoLoginManager getInstance(Context context) {
        if (instance == null) {
            instance = new KakaoLoginManager(context.getApplicationContext());
        }
        return instance;
    }

    public void initialize(Context context) {
        KakaoSdk.init(context, BuildConfig.KAKAO_APP_ID);
    }

    public interface KakaoLoginCallback {
        void onSuccess(OAuthToken token);
        void onFailure(Throwable error);
    }

    public void performLogin(Activity activity, KakaoLoginCallback callback) {
        if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(activity)) {
            loginWithKakaoTalk(activity, callback); //카카오톡 어플로 로그인
        } else {
            loginWithKakaoAccount(activity, callback); //계정 로그인
        }
    }

    public void kakaoSocialLogin(String token, SocialLoginCallback callback) {
        service.memberOAuthSignIn("KAKAO", token).enqueue(new Callback<OAuthLoginResponseDto>() {
            @Override
            public void onResponse(Call<OAuthLoginResponseDto> call, Response<OAuthLoginResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OAuthLoginResponseDto result = response.body();
                    if (result.getStatus() == 201 || result.getStatus() == 202) {
                        tokenManager.saveJwt(result.getJwt());
                        callback.onSuccess("jwt 저장");
                    } else {
                        callback.onFailure("카카오 소셜로그인 에러");
                    }
                } else {
                    callback.onFailure("회원가입 실패");
                }
            }

            @Override
            public void onFailure(Call<OAuthLoginResponseDto> call, Throwable t) {
                callback.onFailure("회원가입 에러 발생: " + t.getMessage());
            }
        });
    }

    //카카오 어플을 통해서 로그인
    private void loginWithKakaoTalk(Activity activity, KakaoLoginCallback callback) {
        UserApiClient.getInstance().loginWithKakaoTalk(activity, (oAuthToken, error) -> {
            if (error != null) {
                Log.e(TAG, "카카오톡으로 로그인 실패", error);
                if (error instanceof ClientError && ((ClientError) error).getReason() == ClientErrorCause.Cancelled) {
                    callback.onFailure(error);
                } else {
                    loginWithKakaoAccount(activity, callback);
                }
            } else if (oAuthToken != null) {
                Log.i(TAG, "카카오톡으로 로그인 성공");
                callback.onSuccess(oAuthToken);
            }
            return null;
        });
    }

    //카카오톡 링크로 로그인
    private void loginWithKakaoAccount(Activity activity, KakaoLoginCallback callback) {
        UserApiClient.getInstance().loginWithKakaoAccount(activity, (oAuthToken, error) -> {
            if (error != null) {
                Log.e(TAG, "카카오계정으로 로그인 실패", error);
                callback.onFailure(error);
            } else if (oAuthToken != null) {
                Log.i(TAG, "카카오계정으로 로그인 성공");
                callback.onSuccess(oAuthToken);
            }
            return null;
        });
    }

    public void requestUserInfo(KakaoUserInfoCallback callback) {
        UserApiClient.getInstance().me((user, error) -> {
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error);
                callback.onFailure(error);
            } else if (user != null) {
                Log.i(TAG, "사용자 정보 요청 성공");
                callback.onSuccess(user);
            }
            return null;
        });
    }

    public interface KakaoUserInfoCallback {
        void onSuccess(User user);
        void onFailure(Throwable error);
    }

    public interface SocialLoginCallback {
        void onSuccess(String message);
        void onFailure(String error);
    }
}