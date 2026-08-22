package com.techfix.app.userauthentication.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.userauthentication.utils.SessionManager;

public class LogoutTestActivity extends AppCompatActivity {

    private static final String TAG = "AUTH_LOGOUT_TEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sessionManager =
                new SessionManager(this);

        // =====================================================
        // BEFORE LOGOUT
        // =====================================================

        Log.d(
                TAG,
                "Before logout - Is logged in: "
                        + sessionManager.isLoggedIn()
        );

        Log.d(
                TAG,
                "Before logout - User ID: "
                        + sessionManager.getUserId()
        );

        Log.d(
                TAG,
                "Before logout - User name: "
                        + sessionManager.getUserName()
        );

        Log.d(
                TAG,
                "Before logout - User email: "
                        + sessionManager.getUserEmail()
        );

        Log.d(
                TAG,
                "Before logout - Role: "
                        + sessionManager.getRole()
        );

        Log.d(
                TAG,
                "Before logout - Technician ID: "
                        + sessionManager.getTechnicianId()
        );


        // =====================================================
        // LOGOUT
        // =====================================================

        sessionManager.logout();


        // =====================================================
        // AFTER LOGOUT
        // =====================================================

        Log.d(
                TAG,
                "After logout - Is logged in: "
                        + sessionManager.isLoggedIn()
        );

        Log.d(
                TAG,
                "After logout - User ID: "
                        + sessionManager.getUserId()
        );

        Log.d(
                TAG,
                "After logout - User name: "
                        + sessionManager.getUserName()
        );

        Log.d(
                TAG,
                "After logout - User email: "
                        + sessionManager.getUserEmail()
        );

        Log.d(
                TAG,
                "After logout - Role: "
                        + sessionManager.getRole()
        );

        Log.d(
                TAG,
                "After logout - Technician ID: "
                        + sessionManager.getTechnicianId()
        );

        finish();
    }
}