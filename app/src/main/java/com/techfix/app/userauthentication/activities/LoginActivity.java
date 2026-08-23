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

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;

    private Button btnLogin;

    private TextView tvRegister;

    private UserDao userDao;

    private SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        // =====================================================
        // IMPORTANT
        //
        // Attach Login UI immediately.
        //
        // Splash -> Login transition eke blank / black waiting
        // time eka adu karanna setContentView() first.
        // =====================================================

        setContentView(
                R.layout.activity_login
        );


        // =====================================================
        // SESSION MANAGER
        // =====================================================

        sessionManager =
                new SessionManager(
                        this
                );


        /*
         * If there is already a valid session,
         * route to the correct dashboard.
         */
        if (sessionManager.isLoggedIn()) {

            routeExistingSession();

            return;
        }


        // =====================================================
        // DATABASE
        // =====================================================

        userDao =
                new UserDao(
                        this
                );


        // =====================================================
        // VIEWS
        // =====================================================

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


        // =====================================================
        // LOGIN BUTTON
        // =====================================================

        btnLogin.setOnClickListener(
                view ->
                        login()
        );


        // =====================================================
        // REGISTER
        // =====================================================

        tvRegister.setOnClickListener(
                view -> {

                    Intent intent =
                            new Intent(
                                    LoginActivity.this,
                                    RegisterActivity.class
                            );

                    startActivity(
                            intent
                    );
                }
        );
    }


    // =========================================================
    // LOGIN
    // =========================================================

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


        // =====================================================
        // VALIDATION
        // =====================================================

        if (email.isEmpty()) {

            etEmail.setError(
                    "Email is required"
            );

            etEmail.requestFocus();

            return;
        }


        if (password.isEmpty()) {

            etPassword.setError(
                    "Password is required"
            );

            etPassword.requestFocus();

            return;
        }


        // =====================================================
        // AUTHENTICATE FROM SQLITE
        // =====================================================

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


        // =====================================================
        // VALIDATE ROLE
        // =====================================================

        String role =
                user.getRole();


        if (
                role == null ||
                        role.trim().isEmpty()
        ) {

            Toast.makeText(
                    this,
                    "User account has no role assigned",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        // =====================================================
        // VALIDATE TECHNICIAN LINK
        // =====================================================

        if (
                User.ROLE_TECHNICIAN.equals(
                        role
                )
        ) {

            if (
                    user.getTechnicianId() == null ||
                            user.getTechnicianId() <= 0
            ) {

                Toast.makeText(
                        this,
                        "Technician account is not linked to a technician profile",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }
        }


        // =====================================================
        // SAVE SESSION FOR EVERY ROLE
        // =====================================================

        sessionManager.createLoginSession(
                user
        );


        // =====================================================
        // ROUTE
        // =====================================================

        routeUser(
                user
        );
    }


    // =========================================================
    // ROUTE NEW LOGIN
    // =========================================================

    private void routeUser(
            User user
    ) {

        Intent intent;


        switch (user.getRole()) {

            // =================================================
            // ADMIN
            // =================================================

            case User.ROLE_ADMIN:

                intent =
                        new Intent(
                                LoginActivity.this,
                                AdminDashboardActivity.class
                        );

                break;


            // =================================================
            // TECHNICIAN
            // =================================================

            case User.ROLE_TECHNICIAN:

                intent =
                        new Intent(
                                LoginActivity.this,
                                TechnicianDashboardActivity.class
                        );


                if (
                        user.getTechnicianId() != null
                ) {

                    intent.putExtra(
                            TechnicianDashboardActivity.EXTRA_TECHNICIAN_ID,
                            user.getTechnicianId()
                    );
                }

                break;


            // =================================================
            // CUSTOMER
            // =================================================

            case User.ROLE_CUSTOMER:

                intent =
                        new Intent(
                                LoginActivity.this,
                                MainActivity.class
                        );

                break;


            // =================================================
            // UNKNOWN
            // =================================================

            default:

                Toast.makeText(
                        this,
                        "Unknown user role: " +
                                user.getRole(),
                        Toast.LENGTH_LONG
                ).show();


                sessionManager.logout();

                return;
        }


        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );


        startActivity(
                intent
        );


        finish();
    }


    // =========================================================
    // ROUTE EXISTING SESSION
    // =========================================================

    private void routeExistingSession() {

        String role =
                sessionManager.getRole();


        Intent intent;


        // =====================================================
        // CUSTOMER
        // =====================================================

        if (
                User.ROLE_CUSTOMER.equals(
                        role
                )
        ) {

            intent =
                    new Intent(
                            LoginActivity.this,
                            MainActivity.class
                    );


            // =====================================================
            // ADMIN
            // =====================================================

        } else if (
                User.ROLE_ADMIN.equals(
                        role
                )
        ) {

            intent =
                    new Intent(
                            LoginActivity.this,
                            AdminDashboardActivity.class
                    );


            // =====================================================
            // TECHNICIAN
            // =====================================================

        } else if (
                User.ROLE_TECHNICIAN.equals(
                        role
                )
        ) {

            int technicianId =
                    sessionManager.getTechnicianId();


            if (technicianId <= 0) {

                sessionManager.logout();

                showLoginAgain();

                return;
            }


            intent =
                    new Intent(
                            LoginActivity.this,
                            TechnicianDashboardActivity.class
                    );


            intent.putExtra(
                    TechnicianDashboardActivity.EXTRA_TECHNICIAN_ID,
                    technicianId
            );


        } else {

            /*
             * Invalid / corrupted session.
             */

            sessionManager.logout();

            showLoginAgain();

            return;
        }


        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );


        startActivity(
                intent
        );


        finish();
    }


    // =========================================================
    // SHOW LOGIN AFTER INVALID SESSION
    // =========================================================

    private void showLoginAgain() {

        Intent intent =
                new Intent(
                        LoginActivity.this,
                        LoginActivity.class
                );


        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );


        startActivity(
                intent
        );


        finish();
    }
}