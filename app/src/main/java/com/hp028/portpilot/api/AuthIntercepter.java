package com.hp028.portpilot.api;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import com.hp028.portpilot.TokenManager;

import okhttp3.Interceptor;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class AuthIntercepter implements Interceptor {
    private TokenManager tokenManager;

    public AuthIntercepter(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder();

        if(tokenManager.containsKey("jwt")){
            String token = tokenManager.getJwtToken();
            if (token != null) {
                // 헤더에 토큰 추가
                requestBuilder.header("Authorization", token);
            }
        }

        Request request = requestBuilder// Remove custom header before sending the request
                .build();
        return chain.proceed(request);
    }
}
