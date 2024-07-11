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
    private final RetrofitService service = RetrofitClient.getClient().create(RetrofitService.class);
    private KakaoLoginManager kakaoLoginManager;
    private final TokenManager tokenManager = TokenManager.getInstance(this);
    private NaverLoginManager naverLoginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.d(TAG, "> LoginActivity");

        // 초기화
        kakaoLoginManager = KakaoLoginManager.getInstance();
        naverLoginManager = NaverLoginManager.getInstance(this);
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

                kakaoSocialLogin(token.getAccessToken());
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "카카오 로그인 실패", error);
            }
        });
    }

    private void kakaoSocialLogin(String token){
        service.memberOAuthSignIn("KAKAO", token).enqueue(new Callback<OAuthLoginResponseDto>() {
            @Override
            public void onResponse(Call<OAuthLoginResponseDto> call, Response<OAuthLoginResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OAuthLoginResponseDto result = response.body();
                    if (result.getStatus() == 201 || result.getStatus() == 202) {
                        tokenManager.saveJwt(result.getJwt());

                        Toast.makeText(LoginActivity.this, "jwt 저장", Toast.LENGTH_SHORT).show();
                        // TODO 로그인 후 채팅방 화면으로 이동

                    } else {
                        Toast.makeText(LoginActivity.this, "카카오 소셜로그인 에러", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OAuthLoginResponseDto> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "회원가입 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("회원가입 에러 발생", t.getMessage());
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
        // 로그인 화면으로 이동하는 로직 구현
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}