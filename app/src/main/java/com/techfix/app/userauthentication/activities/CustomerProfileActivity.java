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

        // Load logged-in customer's information
        loadUserProfile();

        // =====================================================
        // SAVE PROFILE
        // =====================================================

        btnSaveProfile.setOnClickListener(
                v -> updateProfile()
        );

        // =====================================================
        // CHANGE PASSWORD
        // =====================================================

        btnChangePassword.setOnClickListener(
                v -> openChangePassword()
        );

        // =====================================================
        // LOGOUT
        // =====================================================

        btnLogout.setOnClickListener(
                v -> logout()
        );

        // =====================================================
        // BACK
        // =====================================================

        tvBack.setOnClickListener(
                v -> finish()
        );
    }

    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

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

    // =========================================================
    // LOAD USER PROFILE
    // =========================================================

    private void loadUserProfile() {

        // Check whether customer is logged in
        if (!sessionManager.isLoggedIn()) {

            finish();

            return;
        }

        // Get logged-in user's ID
        int userId =
                sessionManager.getUserId();

        // Get user from SQLite
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

        // Display customer information
        etName.setText(
                currentUser.getName()
        );

        etEmail.setText(
                currentUser.getEmail()
        );

        etPhone.setText(
                currentUser.getPhone()
        );

        // Email cannot be changed
        etEmail.setEnabled(false);
    }

    // =========================================================
    // UPDATE PROFILE
    // =========================================================

    private void updateProfile() {

        String name =
                etName.getText()
                        .toString()
                        .trim();

        String phone =
                etPhone.getText()
                        .toString()
                        .trim();

        // -----------------------------------------------------
        // VALIDATE NAME
        // -----------------------------------------------------

        if (ValidationUtils.isEmpty(name)) {

            etName.setError(
                    "Enter your name"
            );

            etName.requestFocus();

            return;
        }

        // -----------------------------------------------------
        // VALIDATE PHONE
        // -----------------------------------------------------

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

        // -----------------------------------------------------
        // UPDATE DATABASE
        // -----------------------------------------------------

        int userId =
                sessionManager.getUserId();

        boolean updated =
                databaseHelper.updateUserProfile(
                        userId,
                        name,
                        phone
                );

        if (updated) {

            // Update local object as well
            currentUser =
                    databaseHelper.getUserById(userId);

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

    // =========================================================
    // OPEN CHANGE PASSWORD PAGE
    // =========================================================

    private void openChangePassword() {

        if (currentUser == null) {

            Toast.makeText(
                    this,
                    "Unable to identify customer",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Intent intent =
                new Intent(
                        CustomerProfileActivity.this,
                        ChangePasswordActivity.class
                );

        // Send logged-in customer's ID
        intent.putExtra(
                "USER_ID",
                currentUser.getId()
        );

        // Also send email in case it is needed later
        intent.putExtra(
                "USER_EMAIL",
                currentUser.getEmail()
        );

        startActivity(intent);
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    private void logout() {

        sessionManager.logout();

        Toast.makeText(
                this,
                "Logged out successfully",
                Toast.LENGTH_SHORT
        ).show();

        // Return to login
        Intent intent =
                new Intent(
                        CustomerProfileActivity.this,
                        LoginActivity.class
                );

        // Remove profile/login screens from back stack
        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
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