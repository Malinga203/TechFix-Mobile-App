package com.techfix.app.userauthentication.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;
import com.techfix.app.database.UserDao;
import com.techfix.app.userauthentication.models.User;

public class RegisterActivity
        extends AppCompatActivity {

    private EditText etName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etPassword;
    private EditText etConfirmPassword;

    private Button btnRegister;

    private UserDao userDao;


    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_register
        );

        userDao =
                new UserDao(this);

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

        etPassword =
                findViewById(
                        R.id.etPassword
                );

        etConfirmPassword =
                findViewById(
                        R.id.etConfirmPassword
                );

        btnRegister =
                findViewById(
                        R.id.btnRegister
                );

        btnRegister.setOnClickListener(
                view -> registerCustomer()
        );
    }


    private void registerCustomer() {

        String name =
                etName
                        .getText()
                        .toString()
                        .trim();

        String email =
                etEmail
                        .getText()
                        .toString()
                        .trim();

        String phone =
                etPhone
                        .getText()
                        .toString()
                        .trim();

        String password =
                etPassword
                        .getText()
                        .toString();

        String confirmPassword =
                etConfirmPassword
                        .getText()
                        .toString();

        if (name.isEmpty()) {

            etName.setError(
                    "Name is required"
            );

            return;
        }

        if (email.isEmpty()) {

            etEmail.setError(
                    "Email is required"
            );

            return;
        }

        if (phone.isEmpty()) {

            etPhone.setError(
                    "Phone is required"
            );

            return;
        }

        if (password.isEmpty()) {

            etPassword.setError(
                    "Password is required"
            );

            return;
        }

        if (!password.equals(
                confirmPassword
        )) {

            etConfirmPassword.setError(
                    "Passwords do not match"
            );

            return;
        }

        if (
                userDao.isEmailRegistered(
                        email
                )
        ) {

            etEmail.setError(
                    "Email already registered"
            );

            return;
        }

        User user =
                new User();

        user.setName(
                name
        );

        user.setEmail(
                email
        );

        user.setPhone(
                phone
        );

        user.setPassword(
                password
        );

        user.setRole(
                User.ROLE_CUSTOMER
        );

        user.setTechnicianId(
                null
        );

        long result =
                userDao.insertUser(
                        user
                );

        if (result > 0) {

            Toast.makeText(
                    this,
                    "Registration successful",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Registration failed",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}