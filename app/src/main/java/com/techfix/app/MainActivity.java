package com.techfix.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.userauthentication.activities.LoginActivity;
import com.techfix.app.userauthentication.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        // Check whether a user is already logged in
        if (!sessionManager.isLoggedIn()) {

            // No active session → open Login screen
            Intent intent = new Intent(
                    this,
                    LoginActivity.class
            );

            startActivity(intent);

            finish();

            return;
        }

        // User is already logged in
        setContentView(R.layout.activity_main);
    }
}