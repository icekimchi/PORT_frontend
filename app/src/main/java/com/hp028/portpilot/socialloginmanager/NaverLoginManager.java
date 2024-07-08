package com.hp028.portpilot.socialloginmanager;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;

import com.hp028.portpilot.BuildConfig;
import com.navercorp.nid.NaverIdLoginSDK;
import com.navercorp.nid.oauth.NidOAuthErrorCode;

public class NaverLoginManager {
    private static final String TAG = "NaverLogin";

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
}