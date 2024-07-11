package com.hp028.portpilot;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kakao.sdk.common.KakaoSdk;
import com.navercorp.nid.NaverIdLoginSDK;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NaverIdLoginSDK.INSTANCE.initialize(this, BuildConfig.NAVER_CLIENT_ID, BuildConfig.NAVER_CLIENT_SECRET, BuildConfig.NAVER_CLIENT_NAME);

        EdgeToEdge.enable(this);
        TokenManager.getInstance(this);
        // LoginActivity로 이동
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}