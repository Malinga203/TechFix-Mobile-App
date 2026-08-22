package com.techfix.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;
import com.techfix.app.userauthentication.activities.LoginActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private Button btnManageBranches;
    private Button btnManageTechnicians;
    private Button btnManageRepairs;
    private Button btnManageServices;
    private Button btnManageSpareParts;

    // Repair Sample Approval
    private Button btnApproveRepairSamples;

    private Button btnAdminLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_admin_dashboard
        );

        bindViews();

        setupNavigation();
    }

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

        //Admin repair sample approval button
        btnApproveRepairSamples =
                findViewById(
                        R.id.btnApproveRepairSamples
                );

        btnAdminLogout =
                findViewById(
                        R.id.btnAdminLogout
                );
    }

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

        // Open pending repair sample approval page
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

    private void openActivity(
            Class<?> activityClass
    ) {

        Intent intent =
                new Intent(
                        AdminDashboardActivity.this,
                        activityClass
                );

        startActivity(intent);
    }

    private void logoutAdmin() {

        Intent intent =
                new Intent(
                        AdminDashboardActivity.this,
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