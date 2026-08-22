package com.techfix.app.userauthentication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;
import com.techfix.app.database.UserDao;
import com.techfix.app.userauthentication.models.User;
import com.techfix.app.userauthentication.utils.SessionManager;

public class CustomerProfileActivity
        extends AppCompatActivity {

    private EditText etName;
    private EditText etEmail;
    private EditText etPhone;

    private Button btnSaveProfile;
    private Button btnChangePassword;
    private Button btnProfileLogout;

    private TextView tvBack;

    private UserDao userDao;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_customer_profile
        );

        userDao =
                new UserDao(this);

        sessionManager =
                new SessionManager(this);

        etName =
                findViewById(
                        R.id.etProfileName
                );

        etEmail =
                findViewById(
                        R.id.etProfileEmail
                );

        etPhone =
                findViewById(
                        R.id.etProfilePhone
                );

        btnSaveProfile =
                findViewById(
                        R.id.btnSaveProfile
                );

        btnChangePassword =
                findViewById(
                        R.id.btnChangePassword
                );

        btnProfileLogout =
                findViewById(
                        R.id.btnProfileLogout
                );

        tvBack =
                findViewById(
                        R.id.tvBack
                );

        loadProfile();

        tvBack.setOnClickListener(
                view -> finish()
        );

        btnSaveProfile.setOnClickListener(
                view -> saveProfile()
        );

        btnChangePassword.setOnClickListener(
                view -> {

                    Intent intent =
                            new Intent(
                                    CustomerProfileActivity.this,
                                    ChangePasswordActivity.class
                            );

                    startActivity(intent);
                }
        );

        btnProfileLogout.setOnClickListener(
                view -> logout()
        );
    }

    private void loadProfile() {

        User user =
                userDao.getUserById(
                        sessionManager.getUserId()
                );

        if (user == null) {

            Toast.makeText(
                    this,
                    "Unable to load profile",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        etName.setText(
                user.getName()
        );

        etEmail.setText(
                user.getEmail()
        );

        etPhone.setText(
                user.getPhone()
        );

        etEmail.setEnabled(
                false
        );
    }

    private void saveProfile() {

        String name =
                etName
                        .getText()
                        .toString()
                        .trim();

        String phone =
                etPhone
                        .getText()
                        .toString()
                        .trim();

        if (name.isEmpty()) {

            etName.setError(
                    "Name is required"
            );

            return;
        }

        if (phone.isEmpty()) {

            etPhone.setError(
                    "Phone number is required"
            );

            return;
        }

        boolean updated =
                userDao.updateUserProfile(
                        sessionManager.getUserId(),
                        name,
                        phone
                );

        Toast.makeText(
                this,
                updated
                        ? "Profile updated"
                        : "Profile update failed",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void logout() {

        sessionManager.logout();

        Intent intent =
                new Intent(
                        this,
                        LoginActivity.class
                );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }
}