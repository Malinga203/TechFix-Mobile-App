package com.techfix.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.userauthentication.activities.CustomerProfileActivity;
import com.techfix.app.userauthentication.activities.LoginActivity;
import com.techfix.app.userauthentication.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    private Button btnSeeProfile;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {

            openLogin();
            return;
        }

        setContentView(R.layout.activity_main);

        // Initialize buttons
        btnSeeProfile = findViewById(R.id.btnSeeProfile);
        btnLogout = findViewById(R.id.btnLogout);

        // Open customer profile
        btnSeeProfile.setOnClickListener(v -> openProfile());

        // Logout
        btnLogout.setOnClickListener(v -> logoutUser());
    }

    // =========================================================
    // OPEN CUSTOMER PROFILE
    // =========================================================

    private void openProfile() {

        Intent intent = new Intent(
                MainActivity.this,
                CustomerProfileActivity.class
        );

        startActivity(intent);
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    private void logoutUser() {

        sessionManager.logout();

        openLogin();
    }

    // =========================================================
    // OPEN LOGIN
    // =========================================================

    private void openLogin() {

        Intent intent = new Intent(
                MainActivity.this,
                LoginActivity.class
        );

        startActivity(intent);

        // Prevent going back to MainActivity
        finish();
    }
}