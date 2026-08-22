package com.techfix.app.userauthentication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.MainActivity;
import com.techfix.app.R;
import com.techfix.app.activities.AdminDashboardActivity;
import com.techfix.app.activities.TechnicianDashboardActivity;
import com.techfix.app.database.UserDao;
import com.techfix.app.userauthentication.models.User;
import com.techfix.app.userauthentication.utils.SessionManager;

public class LoginActivity
        extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;

    private Button btnLogin;

    private TextView tvRegister;

    private UserDao userDao;

    private SessionManager sessionManager;


    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_login
        );

        userDao =
                new UserDao(this);

        sessionManager =
                new SessionManager(this);

        etEmail =
                findViewById(
                        R.id.etEmail
                );

        etPassword =
                findViewById(
                        R.id.etPassword
                );

        btnLogin =
                findViewById(
                        R.id.btnLogin
                );

        tvRegister =
                findViewById(
                        R.id.tvRegister
                );

        btnLogin.setOnClickListener(
                view -> login()
        );

        tvRegister.setOnClickListener(
                view -> {

                    Intent intent =
                            new Intent(
                                    LoginActivity.this,
                                    RegisterActivity.class
                            );

                    startActivity(intent);
                }
        );
    }


    private void login() {

        String email =
                etEmail
                        .getText()
                        .toString()
                        .trim();

        String password =
                etPassword
                        .getText()
                        .toString();

        if (email.isEmpty()) {

            etEmail.setError(
                    "Email is required"
            );

            return;
        }

        if (password.isEmpty()) {

            etPassword.setError(
                    "Password is required"
            );

            return;
        }

        User user =
                userDao.authenticateUser(
                        email,
                        password
                );

        if (user == null) {

            Toast.makeText(
                    this,
                    "Invalid email or password",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        sessionManager.createLoginSession(
                user
        );

        routeUser(
                user
        );
    }


    private void routeUser(
            User user
    ) {

        Intent intent;

        switch (user.getRole()) {

            case User.ROLE_ADMIN:

                intent =
                        new Intent(
                                this,
                                AdminDashboardActivity.class
                        );

                break;


            case User.ROLE_TECHNICIAN:

                if (
                        user.getTechnicianId() == null
                ) {

                    Toast.makeText(
                            this,
                            "Technician account is not linked",
                            Toast.LENGTH_LONG
                    ).show();

                    sessionManager.logout();

                    return;
                }

                intent =
                        new Intent(
                                this,
                                TechnicianDashboardActivity.class
                        );

                intent.putExtra(
                        TechnicianDashboardActivity.EXTRA_TECHNICIAN_ID,
                        user.getTechnicianId()
                );

                break;


            case User.ROLE_CUSTOMER:

                intent =
                        new Intent(
                                this,
                                MainActivity.class
                        );

                break;


            default:

                Toast.makeText(
                        this,
                        "Unknown user role",
                        Toast.LENGTH_SHORT
                ).show();

                sessionManager.logout();

                return;
        }

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }
}