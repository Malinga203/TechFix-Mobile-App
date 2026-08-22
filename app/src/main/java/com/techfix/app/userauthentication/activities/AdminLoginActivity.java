package com.techfix.app.userauthentication.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText etAdminUsername;
    private EditText etAdminPassword;

    private Button btnAdminLogin;
    private TextView tvBackToCustomerLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_login);

        etAdminUsername = findViewById(R.id.etAdminUsername);
        etAdminPassword = findViewById(R.id.etAdminPassword);

        btnAdminLogin = findViewById(R.id.btnAdminLogin);
        tvBackToCustomerLogin =
                findViewById(R.id.tvBackToCustomerLogin);

        btnAdminLogin.setOnClickListener(
                v -> adminLogin()
        );

        tvBackToCustomerLogin.setOnClickListener(
                v -> finish()
        );
    }

    private void adminLogin() {

        String username =
                etAdminUsername.getText()
                        .toString()
                        .trim();

        String password =
                etAdminPassword.getText()
                        .toString();

        if (username.isEmpty()) {

            etAdminUsername.setError(
                    "Enter admin username"
            );

            return;
        }

        if (password.isEmpty()) {

            etAdminPassword.setError(
                    "Enter admin password"
            );

            return;
        }

        // Temporary admin credentials for testing.
        // We will connect this to SQLite later.
        if (username.equals("admin")
                && password.equals("admin123")) {

            Toast.makeText(
                    this,
                    "Admin login successful",
                    Toast.LENGTH_SHORT
            ).show();

            // Admin Dashboard will be connected here later.

        } else {

            Toast.makeText(
                    this,
                    "Invalid admin username or password",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}