package com.techfix.app.userauthentication.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.userauthentication.utils.SessionManager;

public class SessionTestActivity extends AppCompatActivity {

    private static final String TAG = "AUTH_SESSION_TEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sessionManager =
                new SessionManager(this);

        // =====================================================
        // LOGIN STATUS
        // =====================================================

        Log.d(
                TAG,
                "Is logged in: "
                        + sessionManager.isLoggedIn()
        );


        // =====================================================
        // USER INFORMATION
        // =====================================================

        Log.d(
                TAG,
                "User ID: "
                        + sessionManager.getUserId()
        );

        Log.d(
                TAG,
                "User name: "
                        + sessionManager.getUserName()
        );

        Log.d(
                TAG,
                "User email: "
                        + sessionManager.getUserEmail()
        );


        // =====================================================
        // ROLE INFORMATION
        // =====================================================

        Log.d(
                TAG,
                "Role: "
                        + sessionManager.getRole()
        );


        // =====================================================
        // TECHNICIAN INFORMATION
        // =====================================================

        Log.d(
                TAG,
                "Technician ID: "
                        + sessionManager.getTechnicianId()
        );


        // =====================================================
        // ROLE CHECKS
        // =====================================================

        Log.d(
                TAG,
                "Is Customer: "
                        + sessionManager.isCustomer()
        );

        Log.d(
                TAG,
                "Is Technician: "
                        + sessionManager.isTechnician()
        );

        Log.d(
                TAG,
                "Is Admin: "
                        + sessionManager.isAdmin()
        );
    }
}