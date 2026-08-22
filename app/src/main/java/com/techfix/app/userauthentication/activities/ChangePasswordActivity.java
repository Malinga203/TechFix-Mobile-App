package com.techfix.app.userauthentication.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;
import com.techfix.app.userauthentication.database.AuthDatabaseHelper;
import com.techfix.app.userauthentication.utils.SessionManager;
import com.techfix.app.userauthentication.utils.ValidationUtils;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etCurrentPassword;
    private EditText etNewPassword;
    private EditText etConfirmPassword;

    private Button btnChangePassword;
    private TextView tvBack;

    private AuthDatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_password);

        initializeViews();

        databaseHelper =
                new AuthDatabaseHelper(this);

        sessionManager =
                new SessionManager(this);

        // =====================================================
        // GET LOGGED-IN USER ID
        // =====================================================

        userId =
                getIntent().getIntExtra(
                        "USER_ID",
                        -1
                );

        // If USER_ID was not supplied, get it from session
        if (userId == -1) {

            if (sessionManager.isLoggedIn()) {

                userId =
                        sessionManager.getUserId();

            } else {

                Toast.makeText(
                        this,
                        "User session not found",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

                return;
            }
        }

        // =====================================================
        // CHANGE PASSWORD BUTTON
        // =====================================================

        btnChangePassword.setOnClickListener(
                v -> changePassword()
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

        etCurrentPassword =
                findViewById(
                        R.id.etCurrentPassword
                );

        etNewPassword =
                findViewById(
                        R.id.etNewPassword
                );

        etConfirmPassword =
                findViewById(
                        R.id.etConfirmPassword
                );

        btnChangePassword =
                findViewById(
                        R.id.btnChangePassword
                );

        tvBack =
                findViewById(
                        R.id.tvBack
                );
    }

    // =========================================================
    // CHANGE PASSWORD
    // =========================================================

    private void changePassword() {

        String currentPassword =
                etCurrentPassword
                        .getText()
                        .toString();

        String newPassword =
                etNewPassword
                        .getText()
                        .toString();

        String confirmPassword =
                etConfirmPassword
                        .getText()
                        .toString();

        // =====================================================
        // CURRENT PASSWORD VALIDATION
        // =====================================================

        if (ValidationUtils.isEmpty(currentPassword)) {

            etCurrentPassword.setError(
                    "Enter your current password"
            );

            etCurrentPassword.requestFocus();

            return;
        }

        // =====================================================
        // NEW PASSWORD VALIDATION
        // =====================================================

        if (ValidationUtils.isEmpty(newPassword)) {

            etNewPassword.setError(
                    "Enter your new password"
            );

            etNewPassword.requestFocus();

            return;
        }

        if (!ValidationUtils.isValidPassword(newPassword)) {

            etNewPassword.setError(
                    "Password must contain at least 6 characters"
            );

            etNewPassword.requestFocus();

            return;
        }

        // =====================================================
        // CONFIRM PASSWORD
        // =====================================================

        if (ValidationUtils.isEmpty(confirmPassword)) {

            etConfirmPassword.setError(
                    "Confirm your new password"
            );

            etConfirmPassword.requestFocus();

            return;
        }

        if (!ValidationUtils.passwordsMatch(
                newPassword,
                confirmPassword
        )) {

            etConfirmPassword.setError(
                    "Passwords do not match"
            );

            etConfirmPassword.requestFocus();

            return;
        }

        // =====================================================
        // CHECK CURRENT PASSWORD
        // =====================================================

        boolean currentPasswordCorrect =
                databaseHelper.verifyCurrentPassword(
                        userId,
                        currentPassword
                );

        if (!currentPasswordCorrect) {

            etCurrentPassword.setError(
                    "Current password is incorrect"
            );

            etCurrentPassword.requestFocus();

            return;
        }

        // =====================================================
        // UPDATE PASSWORD
        // =====================================================

        boolean changed =
                databaseHelper.changePassword(
                        userId,
                        currentPassword,
                        newPassword
                );

        // =====================================================
        // SUCCESS
        // =====================================================

        if (changed) {

            Toast.makeText(
                    this,
                    "Password changed successfully",
                    Toast.LENGTH_SHORT
            ).show();

            // Clear fields
            etCurrentPassword.setText("");
            etNewPassword.setText("");
            etConfirmPassword.setText("");

            // Return to Customer Profile
            finish();

        } else {

            Toast.makeText(
                    this,
                    "Password change failed",
                    Toast.LENGTH_SHORT
            ).show();
        }
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