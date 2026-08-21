package com.techfix.app.userauthentication.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;
import com.techfix.app.userauthentication.database.AuthDatabaseHelper;
import com.techfix.app.userauthentication.models.User;
import com.techfix.app.userauthentication.utils.ValidationUtils;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;

    private Button btnLogin;
    private TextView tvRegister;

    private AuthDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        initializeViews();

        databaseHelper = new AuthDatabaseHelper(this);

        btnLogin.setOnClickListener(v -> loginUser());

        tvRegister.setOnClickListener(v -> finish());
    }

    private void initializeViews() {

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
    }

    private void loginUser() {

        String email = etEmail.getText()
                .toString()
                .trim();

        String password = etPassword.getText()
                .toString();

        // Validate email
        if (ValidationUtils.isEmpty(email)) {

            etEmail.setError("Enter your email");
            etEmail.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {

            etEmail.setError("Enter a valid email");
            etEmail.requestFocus();
            return;
        }

        // Validate password
        if (ValidationUtils.isEmpty(password)) {

            etPassword.setError("Enter your password");
            etPassword.requestFocus();
            return;
        }

        // Authenticate against SQLite
        User user = databaseHelper.authenticateUser(
                email,
                password
        );

        if (user != null) {

            Toast.makeText(
                    this,
                    "Login successful. Welcome "
                            + user.getName(),
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            Toast.makeText(
                    this,
                    "Invalid email or password",
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