package com.hp028.portpilot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.hp028.portpilot.databinding.ActivityLoginBinding;
import com.hp028.portpilot.socialloginmanager.KakaoLoginManager;
import com.hp028.portpilot.socialloginmanager.NaverLoginManager;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.model.User;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private static final String TAG = "LoginActivity";
    private KakaoLoginManager kakaoLoginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.d(TAG, "> LoginActivity");

        // 초기화
        kakaoLoginManager = KakaoLoginManager.getInstance();
        kakaoLoginManager.initialize(this);
        NaverLoginManager.initialize(this);

        // 각 버튼에 대한 클릭 리스너 설정
        binding.kakaoLoginButton.setOnClickListener(v -> performKakaoLogin());
        binding.naverLoginButton.setOnClickListener(v -> performNaverLogin());
        binding.emailLoginButton.setOnClickListener(v -> performEmailLogin());
        binding.loginButton.setOnClickListener(v -> navigateToLogin());
    }

    private void performKakaoLogin() {
        kakaoLoginManager.performLogin(this, new KakaoLoginManager.KakaoLoginCallback() {
            @Override
            public void onSuccess(OAuthToken token) {
                Log.i(TAG, "카카오 로그인 성공: " + token.getAccessToken());
                requestKakaoUserInfo();
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "카카오 로그인 실패", error);
            }
        });
    }

    private void requestKakaoUserInfo() {
        kakaoLoginManager.requestUserInfo(new KakaoLoginManager.KakaoUserInfoCallback() {
            @Override
            public void onSuccess(User user) {
                Log.i(TAG, "카카오 사용자 정보 요청 성공" +
                        "\n이름: " + (user.getKakaoAccount().getName() != null ? user.getKakaoAccount().getName() : "N/A") +
                        "\n이메일: " + (user.getKakaoAccount() != null ? user.getKakaoAccount().getEmail() : "N/A"));
                // TODO: 사용자 정보를 활용하여 추가 작업 수행
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "카카오 사용자 정보 요청 실패", error);
            }
        });
    }

    private final ActivityResultLauncher<Intent> naverLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                NaverLoginManager.handleLoginResult(result.getResultCode(), new NaverLoginManager.NaverLoginCallback() {
                    @Override
                    public void onSuccess(String accessToken) {
                        Log.i(TAG, "네이버 로그인 성공: " + accessToken);
                        // TODO: 추가 작업 수행
                    }

                    @Override
                    public void onFailure(String errorCode, String errorDescription) {
                        Log.e(TAG, "네이버 로그인 실패: " + errorCode + " - " + errorDescription);
                    }
                });
            }
    );

    private void performNaverLogin() {
        NaverLoginManager.performLogin(this, naverLauncher);
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