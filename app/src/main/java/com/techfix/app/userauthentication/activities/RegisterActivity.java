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

public class RegisterActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etPassword;
    private EditText etConfirmPassword;

    private Button btnRegister;
    private TextView tvLogin;

    private AuthDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        initializeViews();

        databaseHelper = new AuthDatabaseHelper(this);

        btnRegister.setOnClickListener(v -> registerUser());

        tvLogin.setOnClickListener(v -> finish());
    }

    private void initializeViews() {

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
    }

    private void registerUser() {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword =
                etConfirmPassword.getText().toString();

        // Validate name
        if (ValidationUtils.isEmpty(name)) {

            etName.setError("Enter your name");
            etName.requestFocus();
            return;
        }

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

        // Validate phone
        if (ValidationUtils.isEmpty(phone)) {

            etPhone.setError("Enter your phone number");
            etPhone.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidPhone(phone)) {

            etPhone.setError(
                    "Phone number must contain 10 digits"
            );

            etPhone.requestFocus();
            return;
        }

        // Validate password
        if (ValidationUtils.isEmpty(password)) {

            etPassword.setError("Enter a password");
            etPassword.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {

            etPassword.setError(
                    "Password must contain at least 6 characters"
            );

            etPassword.requestFocus();
            return;
        }

        // Validate confirm password
        if (!ValidationUtils.passwordsMatch(
                password,
                confirmPassword)) {

            etConfirmPassword.setError(
                    "Passwords do not match"
            );

            etConfirmPassword.requestFocus();
            return;
        }

        // Check whether email already exists
        if (databaseHelper.isEmailRegistered(email)) {

            etEmail.setError(
                    "This email is already registered"
            );

            etEmail.requestFocus();

            Toast.makeText(
                    this,
                    "Email is already registered",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Create User object
        User user = new User(
                name,
                email,
                phone,
                password
        );

        // Insert user into SQLite
        long userId = databaseHelper.insertUser(user);

        if (userId != -1) {

            Toast.makeText(
                    this,
                    "Registration successful",
                    Toast.LENGTH_SHORT
            ).show();

            clearFields();

            // For now, return to previous screen.
            // Later this will navigate to LoginActivity.
            finish();

        } else {

            Toast.makeText(
                    this,
                    "Registration failed",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void clearFields() {

        etName.setText("");
        etEmail.setText("");
        etPhone.setText("");
        etPassword.setText("");
        etConfirmPassword.setText("");
    }

    @Override
    protected void onDestroy() {

        if (databaseHelper != null) {
            databaseHelper.close();
        }

        super.onDestroy();
    }
}