package com.techfix.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.activities.RepairHistoryActivity;
import com.techfix.app.activities.RepairTrackingActivity;
import com.techfix.app.activities.ServiceListActivity;
import com.techfix.app.userauthentication.activities.CustomerProfileActivity;
import com.techfix.app.userauthentication.activities.LoginActivity;
import com.techfix.app.userauthentication.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    private Button btnServices;
    private Button btnRepairTracking;
    private Button btnRepairHistory;
    private Button btnSeeProfile;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        sessionManager =
                new SessionManager(this);

        // Keep existing authentication flow
        if (!sessionManager.isLoggedIn()) {

            openLogin();

            return;
        }

        setContentView(
                R.layout.activity_main
        );

        initializeViews();

        setupNavigation();
    }


    // =========================================================
    // INITIALIZE
    // =========================================================

    private void initializeViews() {

        btnServices =
                findViewById(
                        R.id.btnServices
                );

        btnRepairTracking =
                findViewById(
                        R.id.btnRepairTracking
                );

        btnRepairHistory =
                findViewById(
                        R.id.btnRepairHistory
                );

        btnSeeProfile =
                findViewById(
                        R.id.btnSeeProfile
                );

        btnLogout =
                findViewById(
                        R.id.btnLogout
                );
    }


    // =========================================================
    // PAGE NAVIGATION
    // =========================================================

    private void setupNavigation() {

        btnServices.setOnClickListener(
                view -> openServices()
        );

        btnRepairTracking.setOnClickListener(
                view -> openRepairTracking()
        );

        btnRepairHistory.setOnClickListener(
                view -> openRepairHistory()
        );

        btnSeeProfile.setOnClickListener(
                view -> openProfile()
        );

        btnLogout.setOnClickListener(
                view -> logoutUser()
        );
    }


    // =========================================================
    // SERVICES
    // =========================================================

    private void openServices() {

        Intent intent =
                new Intent(
                        MainActivity.this,
                        ServiceListActivity.class
                );

        startActivity(intent);
    }


    // =========================================================
    // REPAIR TRACKING
    // =========================================================

    private void openRepairTracking() {

        Intent intent =
                new Intent(
                        MainActivity.this,
                        RepairTrackingActivity.class
                );

        startActivity(intent);
    }


    // =========================================================
    // REPAIR HISTORY
    // =========================================================

    private void openRepairHistory() {

        Intent intent =
                new Intent(
                        MainActivity.this,
                        RepairHistoryActivity.class
                );

        startActivity(intent);
    }


    // =========================================================
    // PROFILE
    // =========================================================

    private void openProfile() {

        Intent intent =
                new Intent(
                        MainActivity.this,
                        CustomerProfileActivity.class
                );

        startActivity(intent);
    }


    // =========================================================
    // LOGOUT
    // =========================================================

    private void logoutUser() {

        sessionManager.logout();

        openLogin();
    }


    // =========================================================
    // LOGIN
    // =========================================================

    private void openLogin() {

        Intent intent =
                new Intent(
                        MainActivity.this,
                        LoginActivity.class
                );

        startActivity(intent);

        finish();
    }
}