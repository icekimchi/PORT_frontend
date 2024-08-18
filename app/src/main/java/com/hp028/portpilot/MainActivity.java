package com.hp028.portpilot;

import android.content.Intent;
import android.os.Bundle;
import android.window.SplashScreen;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hp028.portpilot.databinding.ActivityMainBinding;
import com.kakao.sdk.common.KakaoSdk;
import com.navercorp.nid.NaverIdLoginSDK;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Base_Theme_PortPilot);
        super.onCreate(savedInstanceState);
        //네이버 소셜로그인 설정
        NaverIdLoginSDK.INSTANCE.initialize(this, BuildConfig.NAVER_CLIENT_ID, BuildConfig.NAVER_CLIENT_SECRET, BuildConfig.NAVER_CLIENT_NAME);
        EdgeToEdge.enable(this);
        //토큰 초기화
        TokenManager tokenManager = TokenManager.getInstance(this);
        tokenManager.clearTokens();


        // LoginActivity로 이동
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}