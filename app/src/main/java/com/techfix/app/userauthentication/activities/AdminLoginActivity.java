//package com.techfix.app.userauthentication.activities;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.techfix.app.R;
//import com.techfix.app.activities.AdminDashboardActivity;
//
//public class AdminLoginActivity extends AppCompatActivity {
//
//    private EditText etAdminUsername;
//    private EditText etAdminPassword;
//
//    private Button btnAdminLogin;
//
//    private TextView tvBackToCustomerLogin;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//
//        setContentView(
//                R.layout.activity_admin_login
//        );
//
//        initializeViews();
//
//        setupListeners();
//    }
//
//    private void initializeViews() {
//
//        etAdminUsername =
//                findViewById(
//                        R.id.etAdminUsername
//                );
//
//        etAdminPassword =
//                findViewById(
//                        R.id.etAdminPassword
//                );
//
//        btnAdminLogin =
//                findViewById(
//                        R.id.btnAdminLogin
//                );
//
//        tvBackToCustomerLogin =
//                findViewById(
//                        R.id.tvBackToCustomerLogin
//                );
//    }
//
//    private void setupListeners() {
//
//        btnAdminLogin.setOnClickListener(
//                view -> loginAdmin()
//        );
//
//        tvBackToCustomerLogin.setOnClickListener(
//                view -> openCustomerLogin()
//        );
//    }
//
//    private void loginAdmin() {
//
//        String username =
//                etAdminUsername
//                        .getText()
//                        .toString()
//                        .trim();
//
//        String password =
//                etAdminPassword
//                        .getText()
//                        .toString()
//                        .trim();
//
//        if (username.isEmpty()) {
//
//            etAdminUsername.setError(
//                    "Admin username is required"
//            );
//
//            etAdminUsername.requestFocus();
//
//            return;
//        }
//
//        if (password.isEmpty()) {
//
//            etAdminPassword.setError(
//                    "Admin password is required"
//            );
//
//            etAdminPassword.requestFocus();
//
//            return;
//        }
//
//        if (
//                username.equals("admin")
//                        &&
//                        password.equals("admin123")
//        ) {
//
//            Toast.makeText(
//                    this,
//                    "Admin login successful",
//                    Toast.LENGTH_SHORT
//            ).show();
//
//            Intent intent =
//                    new Intent(
//                            AdminLoginActivity.this,
//                            AdminDashboardActivity.class
//                    );
//
//            /*
//             * Remove login screens from the back stack.
//             */
//            intent.setFlags(
//                    Intent.FLAG_ACTIVITY_NEW_TASK |
//                            Intent.FLAG_ACTIVITY_CLEAR_TASK
//            );
//
//            startActivity(intent);
//
//            finish();
//
//        } else {
//
//            Toast.makeText(
//                    this,
//                    "Invalid admin username or password",
//                    Toast.LENGTH_SHORT
//            ).show();
//        }
//    }
//
//    private void openCustomerLogin() {
//
//        Intent intent =
//                new Intent(
//                        AdminLoginActivity.this,
//                        LoginActivity.class
//                );
//
//        startActivity(intent);
//
//        finish();
//    }
//}