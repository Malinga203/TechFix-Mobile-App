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


    public SessionManager(
            Context context
    ) {

        preferences =
                context.getApplicationContext()
                        .getSharedPreferences(
                                PREF_NAME,
                                Context.MODE_PRIVATE
                        );
    }


    // =========================================================
    // CREATE SESSION
    // CUSTOMER / TECHNICIAN / ADMIN
    // =========================================================

    public void createLoginSession(
            User user
    ) {

        if (user == null) {

            return;
        }


        SharedPreferences.Editor editor =
                preferences.edit();


        /*
         * Clear previous user session first.
         *
         * This prevents information from a previous
         * technician/customer/admin remaining in storage.
         */
        editor.clear();


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
                user.getName() == null
                        ? ""
                        : user.getName()
        );


        editor.putString(
                KEY_EMAIL,
                user.getEmail() == null
                        ? ""
                        : user.getEmail()
        );


        editor.putString(
                KEY_ROLE,
                user.getRole() == null
                        ? ""
                        : user.getRole()
        );


        /*
         * Only technicians need technician_id.
         */
        if (
                User.ROLE_TECHNICIAN.equals(
                        user.getRole()
                ) &&
                        user.getTechnicianId() != null
        ) {

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


    // =========================================================
    // LOGIN STATUS
    // =========================================================

    public boolean isLoggedIn() {

        return preferences.getBoolean(
                KEY_LOGGED_IN,
                false
        );
    }


    // =========================================================
    // USER ID
    // =========================================================

    public int getUserId() {

        return preferences.getInt(
                KEY_USER_ID,
                -1
        );
    }


    // =========================================================
    // NAME
    // =========================================================

    public String getUserName() {

        return preferences.getString(
                KEY_NAME,
                ""
        );
    }


    // =========================================================
    // EMAIL
    // =========================================================

    public String getUserEmail() {

        return preferences.getString(
                KEY_EMAIL,
                ""
        );
    }


    // =========================================================
    // ROLE
    // =========================================================

    public String getRole() {

        return preferences.getString(
                KEY_ROLE,
                ""
        );
    }


    // =========================================================
    // TECHNICIAN ID
    // =========================================================

    public int getTechnicianId() {

        return preferences.getInt(
                KEY_TECHNICIAN_ID,
                -1
        );
    }


    // =========================================================
    // CUSTOMER
    // =========================================================

    public boolean isCustomer() {

        return isLoggedIn() &&
                User.ROLE_CUSTOMER.equals(
                        getRole()
                );
    }


    // =========================================================
    // ADMIN
    // =========================================================

    public boolean isAdmin() {

        return isLoggedIn() &&
                User.ROLE_ADMIN.equals(
                        getRole()
                );
    }


    // =========================================================
    // TECHNICIAN
    // =========================================================

    public boolean isTechnician() {

        return isLoggedIn() &&
                User.ROLE_TECHNICIAN.equals(
                        getRole()
                );
    }


    // =========================================================
    // VALID TECHNICIAN SESSION
    // =========================================================

    public boolean hasTechnicianSession() {

        return isTechnician() &&
                getTechnicianId() > 0;
    }


    // =========================================================
    // LOGOUT
    // =========================================================

    public void logout() {

        preferences
                .edit()
                .clear()
                .apply();
    }
}