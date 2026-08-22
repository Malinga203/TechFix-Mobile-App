package com.techfix.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.activities.CustomerPaymentsActivity;
import com.techfix.app.activities.MyAppointmentsActivity;
import com.techfix.app.activities.RepairHistoryActivity;
import com.techfix.app.activities.RepairTrackingActivity;
import com.techfix.app.activities.SampleRepairsActivity;
import com.techfix.app.activities.ServiceListActivity;
import com.techfix.app.userauthentication.activities.LoginActivity;
import com.techfix.app.userauthentication.models.User;
import com.techfix.app.userauthentication.utils.SessionManager;
import com.techfix.app.userauthentication.activities.CustomerProfileActivity;


public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    private Button btnServices;
    private Button btnMyAppointments;
    private Button btnRepairTracking;
    private Button btnRepairHistory;
    private Button btnPayments;
    private Button btnSampleRepairs;
    private Button btnSeeProfile;
    private Button btnLogout;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        sessionManager =
                new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            openLogin();
            return;
        }

        if (!User.ROLE_CUSTOMER.equals(
                sessionManager.getRole()
        )) {

            sessionManager.logout();
            openLogin();
            return;
        }

        setContentView(
                R.layout.activity_main
        );

        initializeViews();
        setupNavigation();
    }

    private void initializeViews() {

        btnServices =
                findViewById(
                        R.id.btnServices
                );

        btnMyAppointments =
                findViewById(
                        R.id.btnMyAppointments
                );

        btnRepairTracking =
                findViewById(
                        R.id.btnRepairTracking
                );

        btnRepairHistory =
                findViewById(
                        R.id.btnRepairHistory
                );

        btnPayments =
                findViewById(
                        R.id.btnPayments
                );

        btnSampleRepairs =
                findViewById(
                        R.id.btnSampleRepairs
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

    private void setupNavigation() {

        btnServices.setOnClickListener(
                view -> openServices()
        );

        btnMyAppointments.setOnClickListener(
                view -> openMyAppointments()
        );

        btnRepairTracking.setOnClickListener(
                view -> openRepairTracking()
        );

        btnRepairHistory.setOnClickListener(
                view -> openRepairHistory()
        );

        btnPayments.setOnClickListener(
                view -> openPayments()
        );

        btnSampleRepairs.setOnClickListener(
                view -> openSampleRepairs()
        );

        btnSeeProfile.setOnClickListener(
                view -> openProfile()
        );

        btnLogout.setOnClickListener(
                view -> logoutUser()
        );
    }

    private void openServices() {

        Intent intent =
                new Intent(
                        MainActivity.this,
                        ServiceListActivity.class
                );

        startActivity(intent);
    }

    private void openMyAppointments() {

        Intent intent =
                new Intent(
                        MainActivity.this,
                        MyAppointmentsActivity.class
                );

        startActivity(intent);
    }

    private void openRepairTracking() {

        Intent intent =
                new Intent(
                        MainActivity.this,
                        RepairTrackingActivity.class
                );

        startActivity(intent);
    }

    private void openRepairHistory() {

        Intent intent =
                new Intent(
                        MainActivity.this,
                        RepairHistoryActivity.class
                );

        startActivity(intent);
    }

    private void openPayments() {

        Intent intent =
                new Intent(
                        MainActivity.this,
                        CustomerPaymentsActivity.class
                );

        startActivity(intent);
    }

    private void openSampleRepairs() {

        Intent intent =
                new Intent(
                        MainActivity.this,
                        SampleRepairsActivity.class
                );

        startActivity(intent);
    }

    private void openProfile() {

        Intent intent =
                new Intent(
                        MainActivity.this,
                        CustomerProfileActivity.class
                );

        startActivity(intent);
    }

    private void logoutUser() {

        sessionManager.logout();

        Intent intent =
                new Intent(
                        MainActivity.this,
                        LoginActivity.class
                );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }

    private void openLogin() {

        Intent intent =
                new Intent(
                        MainActivity.this,
                        LoginActivity.class
                );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}