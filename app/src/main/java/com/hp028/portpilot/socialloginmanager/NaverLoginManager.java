package com.hp028.portpilot.socialloginmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;

import com.hp028.portpilot.BuildConfig;
import com.hp028.portpilot.LoginActivity;
import com.hp028.portpilot.TokenManager;
import com.hp028.portpilot.api.RetrofitClient;
import com.hp028.portpilot.api.RetrofitService;
import com.hp028.portpilot.api.member.dto.OAuthLoginResponseDto;
import com.navercorp.nid.NaverIdLoginSDK;
import com.navercorp.nid.oauth.NidOAuthErrorCode;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NaverLoginManager {
    private static final String TAG = "NaverLogin";
    private static NaverLoginManager instance;
    private final TokenManager tokenManager;
    private final RetrofitService service;


    private NaverLoginManager(Context context) {
        this.tokenManager = TokenManager.getInstance(context);
        this.service = RetrofitClient.getApiService(context);
    }

    public static NaverLoginManager getInstance(Context context) {
        if (instance == null) {
            instance = new NaverLoginManager(context);
        }
        return instance;
    }

    public interface NaverLoginCallback {
        void onSuccess(String accessToken);
        void onFailure(String errorCode, String errorDescription);
    }

    public static void initialize(Activity activity) {
        NaverIdLoginSDK.INSTANCE.initialize(
                activity,
                BuildConfig.NAVER_CLIENT_ID,
                BuildConfig.NAVER_CLIENT_SECRET,
                BuildConfig.NAVER_CLIENT_NAME
        );
    }

    public static void performLogin(Activity activity, ActivityResultLauncher<Intent> launcher) {
        NaverIdLoginSDK.INSTANCE.authenticate(activity, launcher);
    }

    public static void handleLoginResult(int resultCode, NaverLoginCallback callback) {
        if (resultCode == Activity.RESULT_OK) {
            String accessToken = NaverIdLoginSDK.INSTANCE.getAccessToken();
            Log.i(TAG, "네이버 계정 연결 완료: " + accessToken);

            callback.onSuccess(accessToken);
        } else {
            NidOAuthErrorCode errorCode = NaverIdLoginSDK.INSTANCE.getLastErrorCode();
            String errorDescription = NaverIdLoginSDK.INSTANCE.getLastErrorDescription();
            Log.e(TAG, errorCode + ": " + errorDescription);
            callback.onFailure(errorCode.name(), errorDescription);
        }
    }

    public void naverSocialLogin(String token, Context context){
        service.memberOAuthSignIn("NAVER", token).enqueue(new Callback<OAuthLoginResponseDto>() {
            @Override
            public void onResponse(Call<OAuthLoginResponseDto> call, Response<OAuthLoginResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OAuthLoginResponseDto result = response.body();
                    if (result.getStatus() == 201 || result.getStatus() == 202) {
                        tokenManager.saveJwt(result.getJwt());

                        Toast.makeText(context, "jwt 저장", Toast.LENGTH_SHORT).show();
                        // TODO 로그인 후 채팅방 화면으로 이동

                    } else {
                        Toast.makeText(context, "네이버 소셜로그인 에러", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "회원가입 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OAuthLoginResponseDto> call, Throwable t) {
                Toast.makeText(context, "회원가입 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("회원가입 에러 발생", t.getMessage());
            }
        });
    }
}