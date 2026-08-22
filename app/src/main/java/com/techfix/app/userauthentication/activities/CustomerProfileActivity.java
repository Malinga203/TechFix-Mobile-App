package com.techfix.app.userauthentication.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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
                        R.id.etName
                );

        etEmail =
                findViewById(
                        R.id.etEmail
                );

        etPhone =
                findViewById(
                        R.id.etPhone
                );

        btnSaveProfile =
                findViewById(
                        R.id.btnSaveProfile
                );

        loadProfile();

        btnSaveProfile.setOnClickListener(
                view -> saveProfile()
        );
    }


    private void loadProfile() {

        User user =
                userDao.getUserById(
                        sessionManager.getUserId()
                );

        if (user == null) {

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
}