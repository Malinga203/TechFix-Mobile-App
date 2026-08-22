package com.techfix.app.userauthentication.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;
import com.techfix.app.database.UserDao;
import com.techfix.app.userauthentication.utils.SessionManager;

public class ChangePasswordActivity
        extends AppCompatActivity {

    private EditText etCurrentPassword;
    private EditText etNewPassword;
    private EditText etConfirmPassword;

    private Button btnChangePassword;

    private UserDao userDao;

    private SessionManager sessionManager;


    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_change_password
        );

        userDao =
                new UserDao(this);

        sessionManager =
                new SessionManager(this);

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

        btnChangePassword.setOnClickListener(
                view -> changePassword()
        );
    }


    private void changePassword() {

        String current =
                etCurrentPassword
                        .getText()
                        .toString();

        String newPassword =
                etNewPassword
                        .getText()
                        .toString();

        String confirm =
                etConfirmPassword
                        .getText()
                        .toString();

        if (!newPassword.equals(confirm)) {

            etConfirmPassword.setError(
                    "Passwords do not match"
            );

            return;
        }

        boolean changed =
                userDao.changePassword(
                        sessionManager.getUserId(),
                        current,
                        newPassword
                );

        if (changed) {

            Toast.makeText(
                    this,
                    "Password changed successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Current password is incorrect",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}