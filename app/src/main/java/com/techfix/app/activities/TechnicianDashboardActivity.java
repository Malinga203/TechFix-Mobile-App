package com.techfix.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;
import com.techfix.app.database.TechnicianDao;
import com.techfix.app.models.Technician;
import com.techfix.app.userauthentication.activities.LoginActivity;
import com.techfix.app.userauthentication.utils.SessionManager;

public class TechnicianDashboardActivity
        extends AppCompatActivity {

    public static final String EXTRA_TECHNICIAN_ID =
            "extra_technician_id";

    private TextView txtTechnicianWelcome;
    private TextView txtTechnicianInfo;

    private Button btnVerifyAppointment;
    private Button btnMyRepairs;
    private Button btnTechnicianProfile;
    private Button btnTechnicianLogout;

    private SessionManager sessionManager;
    private TechnicianDao technicianDao;

    private int technicianId;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        sessionManager =
                new SessionManager(this);

        if (
                !sessionManager.isLoggedIn() ||
                        !sessionManager.isTechnician()
        ) {

            openLogin();
            return;
        }

        setContentView(
                R.layout.activity_technician_dashboard
        );

        technicianDao =
                new TechnicianDao(this);

        technicianId =
                sessionManager.getTechnicianId();

        if (technicianId <= 0) {

            technicianId =
                    getIntent().getIntExtra(
                            EXTRA_TECHNICIAN_ID,
                            -1
                    );
        }

        if (technicianId <= 0) {

            Toast.makeText(
                    this,
                    "Technician account is not linked correctly",
                    Toast.LENGTH_LONG
            ).show();

            sessionManager.logout();

            openLogin();

            return;
        }

        bindViews();

        loadTechnician();

        setupNavigation();
    }

    private void bindViews() {

        txtTechnicianWelcome =
                findViewById(
                        R.id.txtTechnicianWelcome
                );

        txtTechnicianInfo =
                findViewById(
                        R.id.txtTechnicianInfo
                );

        btnVerifyAppointment =
                findViewById(
                        R.id.btnVerifyAppointment
                );

        btnMyRepairs =
                findViewById(
                        R.id.btnMyRepairs
                );

        btnTechnicianProfile =
                findViewById(
                        R.id.btnTechnicianProfile
                );

        btnTechnicianLogout =
                findViewById(
                        R.id.btnTechnicianLogout
                );
    }

    private void loadTechnician() {

        Technician technician =
                technicianDao.getTechnicianById(
                        technicianId
                );

        if (technician == null) {

            Toast.makeText(
                    this,
                    "Technician profile not found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        txtTechnicianWelcome.setText(
                "Welcome, " +
                        technician.getName()
        );

        txtTechnicianInfo.setText(
                technician.getSpecialization() +
                        " Technician"
        );
    }

    private void setupNavigation() {

        btnVerifyAppointment.setOnClickListener(
                view -> {

                    Intent intent =
                            new Intent(
                                    this,
                                    TechnicianVerifyAppointmentActivity.class
                            );

                    intent.putExtra(
                            EXTRA_TECHNICIAN_ID,
                            technicianId
                    );

                    startActivity(intent);
                }
        );

        btnMyRepairs.setOnClickListener(
                view -> {

                    Intent intent =
                            new Intent(
                                    this,
                                    TechnicianRepairsActivity.class
                            );

                    intent.putExtra(
                            EXTRA_TECHNICIAN_ID,
                            technicianId
                    );

                    startActivity(intent);
                }
        );

        btnTechnicianProfile.setOnClickListener(
                view -> {

                    Intent intent =
                            new Intent(
                                    this,
                                    TechnicianProfileActivity.class
                            );

                    intent.putExtra(
                            EXTRA_TECHNICIAN_ID,
                            technicianId
                    );

                    startActivity(intent);
                }
        );

        btnTechnicianLogout.setOnClickListener(
                view -> logout()
        );
    }

    private void logout() {

        sessionManager.logout();

        openLogin();
    }

    private void openLogin() {

        Intent intent =
                new Intent(
                        this,
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