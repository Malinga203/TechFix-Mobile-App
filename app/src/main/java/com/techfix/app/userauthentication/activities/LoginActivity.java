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

public class LoginActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etPassword;

    private Button btnLogin;
    private TextView tvRegister;
    private TextView tvAdminLogin;

    private AuthDatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        initializeViews();

        databaseHelper =
                new AuthDatabaseHelper(this);

        sessionManager =
                new SessionManager(this);

        // =====================================================
        // CUSTOMER LOGIN BUTTON
        // =====================================================

        btnLogin.setOnClickListener(
                v -> loginUser()
        );

        // =====================================================
        // REGISTER LINK
        // =====================================================

        tvRegister.setOnClickListener(
                v -> openRegister()
        );

        // =====================================================
        // ADMIN LOGIN LINK
        // =====================================================

        tvAdminLogin.setOnClickListener(
                v -> openAdminLogin()
        );
    }

    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        etName =
                findViewById(R.id.etName);

        etPassword =
                findViewById(R.id.etPassword);

        btnLogin =
                findViewById(R.id.btnLogin);

        tvRegister =
                findViewById(R.id.tvRegister);

        tvAdminLogin =
                findViewById(R.id.tvAdminLogin);
    }

    // =========================================================
    // CUSTOMER LOGIN
    // =========================================================

    private void loginUser() {

        String name =
                etName.getText()
                        .toString()
                        .trim();

        String password =
                etPassword.getText()
                        .toString();

        // -----------------------------------------------------
        // VALIDATE NAME
        // -----------------------------------------------------

        if (name.isEmpty()) {

            etName.setError(
                    "Enter your name"
            );

            etName.requestFocus();

            return;
        }

        // -----------------------------------------------------
        // VALIDATE PASSWORD
        // -----------------------------------------------------

        if (password.isEmpty()) {

            etPassword.setError(
                    "Enter your password"
            );

            etPassword.requestFocus();

            return;
        }

        // -----------------------------------------------------
        // AUTHENTICATE CUSTOMER
        // -----------------------------------------------------

        User user =
                databaseHelper.authenticateUserByName(
                        name,
                        password
                );

        // -----------------------------------------------------
        // LOGIN SUCCESS
        // -----------------------------------------------------

        if (user != null) {

            // Save login session
            sessionManager.createLoginSession(user);

            Toast.makeText(
                    this,
                    "Login successful. Welcome "
                            + user.getName(),
                    Toast.LENGTH_SHORT
            ).show();

            // Open main/customer page
            Intent intent =
                    new Intent(
                            LoginActivity.this,
                            com.techfix.app.MainActivity.class
                    );

            startActivity(intent);

            // Prevent returning to login
            finish();

        } else {

            // -------------------------------------------------
            // LOGIN FAILED
            // -------------------------------------------------

            Toast.makeText(
                    this,
                    "Invalid name or password",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // =========================================================
    // OPEN REGISTER PAGE
    // =========================================================

    private void openRegister() {

        Intent intent =
                new Intent(
                        LoginActivity.this,
                        RegisterActivity.class
                );

        startActivity(intent);
    }

    // =========================================================
    // OPEN ADMIN LOGIN PAGE
    // =========================================================

    private void openAdminLogin() {

        Intent intent =
                new Intent(
                        LoginActivity.this,
                        AdminLoginActivity.class
                );

        startActivity(intent);
    }

    // =========================================================
    // DESTROY
    // =========================================================

    @Override
    protected void onDestroy() {

        if (databaseHelper != null) {
            databaseHelper.close();
        }

        super.onDestroy();
    }
}