package com.hp028.portpilot;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREF_NAME = "AuthPrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_JWT = "jwt";

    private static TokenManager instance;
    private SharedPreferences preferences;

    private TokenManager(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized TokenManager getInstance(Context context) {
        if (instance == null) {
            instance = new TokenManager(context);
        }
        return instance;
    }

    public void saveAccessToken(String token) {
        preferences.edit().putString(KEY_ACCESS_TOKEN, token).apply();
    }

    public String getAccessToken() {
        return preferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public void saveJwt(String token) {
        preferences.edit().putString(KEY_JWT, token).apply();
    }

    public String getJwtToken() {
        return preferences.getString(KEY_JWT, null);
    }

    public void clearTokens() {
        preferences.edit().remove(KEY_ACCESS_TOKEN).remove(KEY_JWT).apply();
    }
}