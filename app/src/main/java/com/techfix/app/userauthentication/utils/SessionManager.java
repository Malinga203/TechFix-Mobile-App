package com.techfix.app.userauthentication.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.techfix.app.userauthentication.models.User;

public class SessionManager {

    private static final String PREF_NAME = "TechFixUserSession";

    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_PHONE = "userPhone";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {

        sharedPreferences = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );

        editor = sharedPreferences.edit();
    }

    // Save user session after successful login
    public void createLoginSession(User user) {

        editor.putBoolean(KEY_IS_LOGGED_IN, true);

        editor.putInt(
                KEY_USER_ID,
                user.getId()
        );

        editor.putString(
                KEY_USER_NAME,
                user.getName()
        );

        editor.putString(
                KEY_USER_EMAIL,
                user.getEmail()
        );

        editor.putString(
                KEY_USER_PHONE,
                user.getPhone()
        );

        editor.apply();
    }

    // Check whether a user is currently logged in
    public boolean isLoggedIn() {

        return sharedPreferences.getBoolean(
                KEY_IS_LOGGED_IN,
                false
        );
    }

    public int getUserId() {

        return sharedPreferences.getInt(
                KEY_USER_ID,
                -1
        );
    }

    public String getUserName() {

        return sharedPreferences.getString(
                KEY_USER_NAME,
                ""
        );
    }

    public String getUserEmail() {

        return sharedPreferences.getString(
                KEY_USER_EMAIL,
                ""
        );
    }

    public String getUserPhone() {

        return sharedPreferences.getString(
                KEY_USER_PHONE,
                ""
        );
    }

    // Remove the complete login session
    public void logout() {

        editor.clear();
        editor.apply();
    }
}