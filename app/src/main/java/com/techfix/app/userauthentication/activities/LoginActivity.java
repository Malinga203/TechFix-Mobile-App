package com.techfix.app.userauthentication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;
import com.techfix.app.userauthentication.database.AuthDatabaseHelper;
import com.techfix.app.userauthentication.models.User;
import com.techfix.app.userauthentication.utils.SessionManager;
import com.techfix.app.userauthentication.utils.ValidationUtils;

public class LoginActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etPassword;

    private Button btnLogin;
    private TextView tvRegister;

    private AuthDatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        initializeViews();

        databaseHelper = new AuthDatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Login button
        btnLogin.setOnClickListener(v -> loginUser());

        // Register link
        tvRegister.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });
    }

    private void initializeViews() {

        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
    }

    private void loginUser() {

        String name = etName.getText()
                .toString()
                .trim();

        String password = etPassword.getText()
                .toString();

        // Validate name
        if (ValidationUtils.isEmpty(name)) {

            etName.setError("Enter your name");
            etName.requestFocus();
            return;
        }

        // Validate password
        if (ValidationUtils.isEmpty(password)) {

            etPassword.setError("Enter your password");
            etPassword.requestFocus();
            return;
        }

        // Authenticate using name and password
        User user = databaseHelper.authenticateUser(
                name,
                password
        );

        if (user != null) {

            // Save login session
            sessionManager.createLoginSession(user);

            Toast.makeText(
                    this,
                    "Login successful. Welcome "
                            + user.getName(),
                    Toast.LENGTH_SHORT
            ).show();

            // Open MainActivity
            Intent intent = new Intent(
                    LoginActivity.this,
                    com.techfix.app.MainActivity.class
            );

            startActivity(intent);

            // Prevent returning to login with Back
            finish();

        } else {

            Toast.makeText(
                    this,
                    "Invalid name or password",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    protected void onDestroy() {

        if (databaseHelper != null) {
            databaseHelper.close();
        }

        super.onDestroy();
    }
}