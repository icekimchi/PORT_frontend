package com.hp028.portpilot.socialloginmanager;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.model.ClientError;
import com.kakao.sdk.common.model.ClientErrorCause;
import com.kakao.sdk.user.UserApiClient;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class KakaoLoginManager {
    private static KakaoLoginManager instance;
    private Context context;

    private KakaoLoginManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized KakaoLoginManager getInstance(Context context) {
        if (instance == null) {
            instance = new KakaoLoginManager(context);
        }
        return instance;
    }

    public void performKakaoLogin(Activity activity, Function2<OAuthToken, Throwable, Unit> callback) {
        Log.d(TAG, "performKakaoLogin called");

        if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(activity)) {
            UserApiClient.getInstance().loginWithKakaoTalk(activity, (oAuthToken, throwable) -> {
                if (throwable != null) {
                    Log.e(TAG, "카카오톡으로 로그인 실패", throwable);

                    if (throwable instanceof ClientError && ((ClientError) throwable).getReason() == ClientErrorCause.Cancelled) {
                        callback.invoke(null, throwable);
                        return null;
                    }

                    UserApiClient.getInstance().loginWithKakaoAccount(activity, callback);
                } else if (oAuthToken != null) {
                    Log.i(TAG, "카카오톡으로 로그인 성공 " + oAuthToken.getAccessToken());
                    callback.invoke(oAuthToken, null);
                }
                return null;
            });
        } else {
            UserApiClient.getInstance().loginWithKakaoAccount(activity, callback);
        }
    }
}