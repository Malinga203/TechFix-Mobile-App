package com.techfix.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.userauthentication.activities.LoginActivity;
import com.techfix.app.userauthentication.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        // User is not logged in
        if (!sessionManager.isLoggedIn()) {

            openLogin();
            return;
        }

        setContentView(R.layout.activity_main);

        btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(
                v -> logoutUser()
        );
    }

    private void logoutUser() {

        // Remove login session
        sessionManager.logout();

        // Return to login
        openLogin();
    }

    private void openLogin() {

        Intent intent = new Intent(
                MainActivity.this,
                LoginActivity.class
        );

        startActivity(intent);

        // Prevent returning to MainActivity
        finish();
    }
}