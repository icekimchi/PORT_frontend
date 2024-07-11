package com.hp028.portpilot.socialloginmanager;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.hp028.portpilot.BuildConfig;
import com.hp028.portpilot.EmailLoginActivity;
import com.hp028.portpilot.api.RetrofitClient;
import com.hp028.portpilot.api.RetrofitService;
import com.hp028.portpilot.api.member.dto.OAuthLoginRequestDto;
import com.hp028.portpilot.api.member.dto.OAuthLoginResponseDto;
import com.hp028.portpilot.api.member.dto.SignInRequestDto;
import com.hp028.portpilot.api.member.dto.SignupResponseDto;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.common.model.ClientError;
import com.kakao.sdk.common.model.ClientErrorCause;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KakaoLoginManager {
    private static final String TAG = "KakaoLoginManager";
    private static KakaoLoginManager instance;

    private KakaoLoginManager() {}

    public static synchronized KakaoLoginManager getInstance() {
        if (instance == null) {
            instance = new KakaoLoginManager();
        }
        return instance;
    }

    public void initialize(Context context) {
        KakaoSdk.init(context, BuildConfig.KAKAO_APP_ID);
    }

    public interface KakaoLoginCallback {
        void onSuccess(OAuthToken token);
        void onFailure(Throwable error);
    }

    public void performLogin(Activity activity, KakaoLoginCallback callback) {
        if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(activity)) {
            loginWithKakaoTalk(activity, callback);
        } else {
            loginWithKakaoAccount(activity, callback);
        }
    }

    private void loginWithKakaoTalk(Activity activity, KakaoLoginCallback callback) {
        UserApiClient.getInstance().loginWithKakaoTalk(activity, (oAuthToken, error) -> {
            if (error != null) {
                Log.e(TAG, "카카오톡으로 로그인 실패", error);
                if (error instanceof ClientError && ((ClientError) error).getReason() == ClientErrorCause.Cancelled) {
                    callback.onFailure(error);
                } else {
                    loginWithKakaoAccount(activity, callback);
                }
            } else if (oAuthToken != null) {
                Log.i(TAG, "카카오톡으로 로그인 성공");
                callback.onSuccess(oAuthToken);
            }
            return null;
        });
    }

    private void loginWithKakaoAccount(Activity activity, KakaoLoginCallback callback) {
        UserApiClient.getInstance().loginWithKakaoAccount(activity, (oAuthToken, error) -> {
            if (error != null) {
                Log.e(TAG, "카카오계정으로 로그인 실패", error);
                callback.onFailure(error);
            } else if (oAuthToken != null) {
                Log.i(TAG, "카카오계정으로 로그인 성공");
                callback.onSuccess(oAuthToken);
            }
            return null;
        });
    }

    public void requestUserInfo(KakaoUserInfoCallback callback) {
        UserApiClient.getInstance().me((user, error) -> {
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error);
                callback.onFailure(error);
            } else if (user != null) {
                Log.i(TAG, "사용자 정보 요청 성공");
                callback.onSuccess(user);
            }
            return null;
        });
    }

    public interface KakaoUserInfoCallback {
        void onSuccess(User user);
        void onFailure(Throwable error);
    }
}