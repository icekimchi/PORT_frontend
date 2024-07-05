package com.hp028.portpilot;
import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.hp028.portpilot.databinding.ActivityLoginBinding;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.common.model.ClientError;
import com.kakao.sdk.common.model.ClientErrorCause;
import com.kakao.sdk.user.UserApiClient;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        KakaoSdk.init(this, BuildConfig.KAKAO_APP_ID);
        setContentView(binding.getRoot());
        Log.d(TAG, "로그인액티비티 시작");
        // 각 버튼에 대한 클릭 리스너 설정
        binding.kakaoLoginButton.setOnClickListener(v -> performKakaoLogin(LoginActivity.this));
        binding.facebookLoginButton.setOnClickListener(v -> performFacebookLogin());
        binding.naverLoginButton.setOnClickListener(v -> performNaverLogin());
        binding.emailLoginButton.setOnClickListener(v -> performEmailLogin());
        binding.loginButton.setOnClickListener(v -> navigateToLogin());
    }

    public void performKakaoLogin(Context context) {
        Log.d(TAG, "performKakaoLogin called");
        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken token, Throwable error) {
                if (error != null) {
                    Log.e(TAG, "카카오계정으로 로그인 실패", error);
                } else if (token != null) {
                    Log.i(TAG, "카카오계정으로 로그인 성공 " + token.getAccessToken());
                }
                return null;
            }
        };

        if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(context)) {
            UserApiClient.getInstance().loginWithKakaoTalk(context, (oAuthToken, throwable) -> {
                if (throwable != null) {
                    Log.e(TAG, "카카오톡으로 로그인 실패", throwable);

                    if (throwable instanceof ClientError && ((ClientError) throwable).getReason() == ClientErrorCause.Cancelled) {
                        return null;
                    }

                    UserApiClient.getInstance().loginWithKakaoAccount(context, callback);
                } else if (oAuthToken != null) {
                    Log.i(TAG, "카카오톡으로 로그인 성공 " + oAuthToken.getAccessToken());
                }
                return null;
            });
        } else {
            UserApiClient.getInstance().loginWithKakaoAccount(context, callback);
        }
    }

    private void performFacebookLogin() {
        // 페이스북 로그인 로직 구현
    }

    private void performNaverLogin() {
        // 네이버 로그인 로직 구현
    }

    private void performEmailLogin() {
        // 이메일 로그인 로직 구현
    }

    private void navigateToLogin() {
        // 로그인 화면으로 이동하는 로직 구현
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}