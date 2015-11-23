package com.uwi.msbm;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by shane on 11/22/15.
 */
public class SessionManager {
    SharedPreferences mPref;
    SharedPreferences.Editor editor;
    Context mContext;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "MSBMPref";
    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_REGISTRATION_NO = "registration_no";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_TOKEN = "token";

    public SessionManager(Context context) {
        mContext = context;
        mPref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = mPref.edit();
    }

    public void createLoginSession(String registrationNo, String userId, String token) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_REGISTRATION_NO, registrationNo);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_TOKEN,token);
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();

        user.put(KEY_REGISTRATION_NO, mPref.getString(KEY_REGISTRATION_NO, null));
        user.put(KEY_USER_ID, mPref.getString(KEY_USER_ID, null));
        user.put(KEY_TOKEN, mPref.getString(KEY_TOKEN, null));

        return user;
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }

    public boolean isLoggedIn() {
        return mPref.getBoolean(IS_LOGIN, false);
    }
}
