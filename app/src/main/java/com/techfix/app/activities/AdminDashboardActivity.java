package com.techfix.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;
import com.techfix.app.userauthentication.activities.LoginActivity;
import com.techfix.app.userauthentication.utils.SessionManager;

public class AdminDashboardActivity
        extends AppCompatActivity {

    private Button btnManageBranches;

    private Button btnManageTechnicians;

    private Button btnManageRepairs;

    private Button btnManageServices;

    private Button btnManageSpareParts;

    private Button btnManageBranchInventory;

    private Button btnApproveRepairSamples;

    private Button btnAdminLogout;


    private SessionManager sessionManager;


    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );


        // =====================================================
        // SESSION
        // =====================================================

        sessionManager =
                new SessionManager(
                        this
                );


        // =====================================================
        // ADMIN SECURITY
        // =====================================================

        if (
                !sessionManager.isLoggedIn() ||
                        !sessionManager.isAdmin()
        ) {

            Toast.makeText(
                    this,
                    "Administrator login required",
                    Toast.LENGTH_SHORT
            ).show();


            openLogin();

            return;
        }


        // =====================================================
        // LAYOUT
        // =====================================================

        setContentView(
                R.layout.activity_admin_dashboard
        );


        bindViews();

        setupNavigation();
    }


    // =========================================================
    // BIND
    // =========================================================

    private void bindViews() {

        btnManageBranches =
                findViewById(
                        R.id.btnManageBranches
                );


        btnManageTechnicians =
                findViewById(
                        R.id.btnManageTechnicians
                );


        btnManageRepairs =
                findViewById(
                        R.id.btnManageRepairs
                );


        btnManageServices =
                findViewById(
                        R.id.btnManageServices
                );


        btnManageSpareParts =
                findViewById(
                        R.id.btnManageSpareParts
                );


        btnManageBranchInventory =
                findViewById(
                        R.id.btnManageBranchInventory
                );


        btnApproveRepairSamples =
                findViewById(
                        R.id.btnApproveRepairSamples
                );


        btnAdminLogout =
                findViewById(
                        R.id.btnAdminLogout
                );
    }


    // =========================================================
    // NAVIGATION
    // =========================================================

    private void setupNavigation() {

        btnManageBranches.setOnClickListener(
                view ->
                        openActivity(
                                BranchActivity.class
                        )
        );


        btnManageTechnicians.setOnClickListener(
                view ->
                        openActivity(
                                TechnicianActivity.class
                        )
        );


        btnManageRepairs.setOnClickListener(
                view ->
                        openActivity(
                                RepairTrackingActivity.class
                        )
        );


        btnManageServices.setOnClickListener(
                view ->
                        openActivity(
                                ServiceManagementActivity.class
                        )
        );


        btnManageSpareParts.setOnClickListener(
                view ->
                        openActivity(
                                SparePartManagementActivity.class
                        )
        );


        btnManageBranchInventory.setOnClickListener(
                view ->
                        openActivity(
                                BranchInventoryActivity.class
                        )
        );


        btnApproveRepairSamples.setOnClickListener(
                view ->
                        openActivity(
                                AdminSampleApprovalActivity.class
                        )
        );


        btnAdminLogout.setOnClickListener(
                view ->
                        logoutAdmin()
        );
    }


    // =========================================================
    // OPEN ACTIVITY
    // =========================================================

    private void openActivity(
            Class<?> activityClass
    ) {

        Intent intent =
                new Intent(
                        AdminDashboardActivity.this,
                        activityClass
                );


        startActivity(
                intent
        );
    }


    // =========================================================
    // LOGOUT
    // =========================================================

    private void logoutAdmin() {

        sessionManager.logout();

        openLogin();
    }


    // =========================================================
    // LOGIN
    // =========================================================

    private void openLogin() {

        Intent intent =
                new Intent(
                        AdminDashboardActivity.this,
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