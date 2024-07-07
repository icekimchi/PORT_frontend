package com.hp028.portpilot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.hp028.portpilot.databinding.ActivityLoginBinding;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import com.navercorp.nid.NaverIdLoginSDK;
import com.navercorp.nid.oauth.NidOAuthErrorCode;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.d(TAG, "> LoginActivity");

        // 각 버튼에 대한 클릭 리스너 설정
        binding.kakaoLoginButton.setOnClickListener(v -> performKakaoLogin());
        binding.naverLoginButton.setOnClickListener(v -> performNaverLogin());
        binding.emailLoginButton.setOnClickListener(v -> performEmailLogin());
        binding.loginButton.setOnClickListener(v -> navigateToLogin());
    }

    private void performKakaoLogin() {
        KakaoSdk.init(this, BuildConfig.KAKAO_APP_ID);
        if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(this)) {
            UserApiClient.getInstance().loginWithKakaoTalk(this, new Function2<OAuthToken, Throwable, Unit>() {
                @Override
                public Unit invoke(OAuthToken oAuthToken, Throwable error) {
                    if (error != null) {
                        Log.e(TAG, "카카오톡으로 로그인 실패", error);
                        // 카카오톡 로그인 실패 시 카카오계정으로 로그인 시도
                        UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this, kakaoAccountCallback);
                    } else if (oAuthToken != null) {
                        Log.i(TAG, "카카오톡으로 로그인 성공");
                        handleKakaoLoginResult(oAuthToken, null);
                    }
                    return null;
                }
            });
        } else {
            UserApiClient.getInstance().loginWithKakaoAccount(this, kakaoAccountCallback);
        }
    }

    private final Function2<OAuthToken, Throwable, Unit> kakaoAccountCallback = new Function2<OAuthToken, Throwable, Unit>() {
        @Override
        public Unit invoke(OAuthToken token, Throwable error) {
            handleKakaoLoginResult(token, error);
            return null;
        }
    };

    private void handleKakaoLoginResult(OAuthToken token, Throwable error) {
        if (error != null) {
            Log.e(TAG, "카카오계정으로 로그인 실패", error);
        } else if (token != null) {
            Log.i(TAG, "카카오계정으로 로그인 성공 " + token.getAccessToken());
            requestKakaoUserInfo();
        }
    }

    private void requestKakaoUserInfo() {
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable meError) {
                if (meError != null) {
                    Log.e(TAG, "사용자 정보 요청 실패", meError);
                } else if (user != null) {
                    Log.i(TAG, "카카오 사용자 정보 요청 성공" +
                            "\n이름: " + (user.getKakaoAccount().getName() != null ? user.getKakaoAccount().getName() : "N/A") +
                            "\n이메일: " + (user.getKakaoAccount() != null ? user.getKakaoAccount().getEmail() : "N/A"));

                    // TODO: 사용자 정보를 활용하여 추가 작업 수행
                    // 예: 서버에 사용자 정보 전송, 다음 화면으로 이동 등
                }
                return null;
            }
        });
    }

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                    Log.i(TAG, "네이버 계정 연결 완료" + NaverIdLoginSDK.INSTANCE.getAccessToken());
                } else {
                    // 실패 or 에러
                    NidOAuthErrorCode errorCode = NaverIdLoginSDK.INSTANCE.getLastErrorCode();
                    String errorDescription = NaverIdLoginSDK.INSTANCE.getLastErrorDescription();
                    Log.e(TAG, errorCode + errorDescription);
                }
            }
    );

    private void performNaverLogin() {
        NaverIdLoginSDK.INSTANCE.initialize(
                this,
                BuildConfig.NAVER_CLIENT_ID,
                BuildConfig.NAVER_CLIENT_SECRET,
                BuildConfig.NAVER_CLIENT_NAME
        );

        NaverIdLoginSDK.INSTANCE.authenticate(this, launcher);
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