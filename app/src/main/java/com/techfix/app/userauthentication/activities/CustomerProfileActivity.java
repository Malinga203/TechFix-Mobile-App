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
import com.techfix.app.userauthentication.utils.SessionManager;
import com.techfix.app.userauthentication.utils.ValidationUtils;

public class CustomerProfileActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etEmail;
    private EditText etPhone;

    private Button btnSaveProfile;
    private Button btnChangePassword;
    private Button btnLogout;

    private TextView tvBack;

    private AuthDatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customer_profile);

        initializeViews();

        databaseHelper =
                new AuthDatabaseHelper(this);

        sessionManager =
                new SessionManager(this);

        loadUserProfile();

        btnSaveProfile.setOnClickListener(
                v -> updateProfile()
        );

        btnChangePassword.setOnClickListener(
                v -> showChangePassword()
        );

        btnLogout.setOnClickListener(
                v -> logout()
        );

        tvBack.setOnClickListener(
                v -> finish()
        );
    }

    private void initializeViews() {

        etName =
                findViewById(R.id.etProfileName);

        etEmail =
                findViewById(R.id.etProfileEmail);

        etPhone =
                findViewById(R.id.etProfilePhone);

        btnSaveProfile =
                findViewById(R.id.btnSaveProfile);

        btnChangePassword =
                findViewById(R.id.btnChangePassword);

        btnLogout =
                findViewById(R.id.btnProfileLogout);

        tvBack =
                findViewById(R.id.tvBack);
    }

    private void loadUserProfile() {

        if (!sessionManager.isLoggedIn()) {

            finish();
            return;
        }

        int userId =
                sessionManager.getUserId();

        currentUser =
                databaseHelper.getUserById(userId);

        if (currentUser == null) {

            Toast.makeText(
                    this,
                    "Unable to load profile",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        etName.setText(
                currentUser.getName()
        );

        etEmail.setText(
                currentUser.getEmail()
        );

        etPhone.setText(
                currentUser.getPhone()
        );

        // Email should not be edited
        etEmail.setEnabled(false);
    }

    private void updateProfile() {

        String name =
                etName.getText()
                        .toString()
                        .trim();

        String phone =
                etPhone.getText()
                        .toString()
                        .trim();

        if (ValidationUtils.isEmpty(name)) {

            etName.setError(
                    "Enter your name"
            );

            etName.requestFocus();
            return;
        }

        if (ValidationUtils.isEmpty(phone)) {

            etPhone.setError(
                    "Enter your phone number"
            );

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

        int userId =
                sessionManager.getUserId();

        boolean updated =
                databaseHelper.updateUserProfile(
                        userId,
                        name,
                        phone
                );

        if (updated) {

            Toast.makeText(
                    this,
                    "Profile updated successfully",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            Toast.makeText(
                    this,
                    "Profile update failed",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void showChangePassword() {

        Toast.makeText(
                this,
                "Change password screen will be added next",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void logout() {

        sessionManager.logout();

        Toast.makeText(
                this,
                "Logged out successfully",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }

    @Override
    protected void onDestroy() {

        if (databaseHelper != null) {
            databaseHelper.close();
        }

        super.onDestroy();
    }
}