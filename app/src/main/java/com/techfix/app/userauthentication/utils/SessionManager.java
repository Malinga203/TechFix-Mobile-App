package com.techfix.app.userauthentication.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.techfix.app.userauthentication.models.User;

public class SessionManager {

    private static final String PREF_NAME =
            "techfix_session";

    private static final String KEY_LOGGED_IN =
            "logged_in";

    private static final String KEY_USER_ID =
            "user_id";

    private static final String KEY_NAME =
            "name";

    private static final String KEY_EMAIL =
            "email";

    private static final String KEY_ROLE =
            "role";

    private static final String KEY_TECHNICIAN_ID =
            "technician_id";

    private final SharedPreferences preferences;

    private final SharedPreferences.Editor editor;

    public SessionManager(
            Context context
    ) {

        preferences =
                context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                );

        editor =
                preferences.edit();
    }


    public void createLoginSession(
            User user
    ) {

        editor.putBoolean(
                KEY_LOGGED_IN,
                true
        );

        editor.putInt(
                KEY_USER_ID,
                user.getId()
        );

        editor.putString(
                KEY_NAME,
                user.getName()
        );

        editor.putString(
                KEY_EMAIL,
                user.getEmail()
        );

        editor.putString(
                KEY_ROLE,
                user.getRole()
        );

        if (user.getTechnicianId() != null) {

            editor.putInt(
                    KEY_TECHNICIAN_ID,
                    user.getTechnicianId()
            );

        } else {

            editor.remove(
                    KEY_TECHNICIAN_ID
            );
        }

        editor.apply();
    }


    public boolean isLoggedIn() {

        return preferences.getBoolean(
                KEY_LOGGED_IN,
                false
        );
    }


    public int getUserId() {

        return preferences.getInt(
                KEY_USER_ID,
                -1
        );
    }


    public String getUserName() {

        return preferences.getString(
                KEY_NAME,
                ""
        );
    }


    public String getUserEmail() {

        return preferences.getString(
                KEY_EMAIL,
                ""
        );
    }


    public String getRole() {

        return preferences.getString(
                KEY_ROLE,
                ""
        );
    }


    public int getTechnicianId() {

        return preferences.getInt(
                KEY_TECHNICIAN_ID,
                -1
        );
    }


    public boolean isCustomer() {

        return User.ROLE_CUSTOMER.equals(
                getRole()
        );
    }


    public boolean isAdmin() {

        return User.ROLE_ADMIN.equals(
                getRole()
        );
    }


    public boolean isTechnician() {

        return User.ROLE_TECHNICIAN.equals(
                getRole()
        );
    }


    public void logout() {

        editor.clear();

        editor.apply();
    }
}