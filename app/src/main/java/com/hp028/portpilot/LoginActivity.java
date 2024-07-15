package com.hp028.portpilot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.hp028.portpilot.api.RetrofitClient;
import com.hp028.portpilot.api.RetrofitService;
import com.hp028.portpilot.api.member.dto.OAuthLoginRequestDto;
import com.hp028.portpilot.api.member.dto.OAuthLoginResponseDto;
import com.hp028.portpilot.api.member.dto.SignInRequestDto;
import com.hp028.portpilot.api.member.dto.SignupResponseDto;
import com.hp028.portpilot.databinding.ActivityLoginBinding;
import com.hp028.portpilot.socialloginmanager.KakaoLoginManager;
import com.hp028.portpilot.socialloginmanager.NaverLoginManager;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private static final String TAG = "LoginActivity";
    private KakaoLoginManager kakaoLoginManager;
    private NaverLoginManager naverLoginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.d(TAG, "> LoginActivity");

        // 초기화
        kakaoLoginManager = KakaoLoginManager.getInstance(this);
        naverLoginManager = NaverLoginManager.getInstance(this);
        kakaoLoginManager.initialize(this);
        NaverLoginManager.initialize(this);

        // 각 버튼에 대한 클릭 리스너 설정
        binding.kakaoLoginButton.setOnClickListener(v -> performKakaoLogin());
        binding.naverLoginButton.setOnClickListener(v -> performNaverLogin());
        binding.emailLoginButton.setOnClickListener(v -> performEmailLogin());
        binding.startButton.setOnClickListener(v -> navigateToLogin());
        binding.loginButton.setOnClickListener(v -> navigateToLogin());
    }

    private void performKakaoLogin() {
        kakaoLoginManager.performLogin(this, new KakaoLoginManager.KakaoLoginCallback() {
            @Override
            public void onSuccess(OAuthToken token) {
                Log.i(TAG, "카카오 로그인 성공: " + token.getAccessToken());
                kakaoLoginManager.kakaoSocialLogin(token.getAccessToken(), new KakaoLoginManager.SocialLoginCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        // TODO: 로그인 후 채팅방 화면으로 이동
                        Intent intent = new Intent(LoginActivity.this, ChatRoomActivity.class);
                        startActivity(intent);
                        finish(); // 현재 LoginActivity를 종료 (선택사항)
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "카카오 로그인 실패", error);
                Toast.makeText(LoginActivity.this, "카카오 로그인 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private final ActivityResultLauncher<Intent> naverLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                NaverLoginManager.handleLoginResult(result.getResultCode(), new NaverLoginManager.NaverLoginCallback() {
                    @Override
                    public void onSuccess(String accessToken) {
                        naverLoginManager.naverSocialLogin(accessToken, LoginActivity.this);
                        Log.i(TAG, "네이버 로그인 성공: " + accessToken);
                        // TODO: 추가 작업 수행
                        Intent intent = new Intent(LoginActivity.this, ChatRoomActivity.class);
                        startActivity(intent);
                        finish(); // 현재 LoginActivity를 종료 (선택사항)
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
        Intent intent = new Intent(this, EmailLoginActivity.class);
        startActivity(intent);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginWithAccountActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}