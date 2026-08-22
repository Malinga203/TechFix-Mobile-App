package com.techfix.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;

public class TechnicianDashboardActivity
        extends AppCompatActivity {

    public static final String EXTRA_TECHNICIAN_ID =
            "extra_technician_id";

    private Button btnVerifyAppointment;
    private Button btnMyRepairs;
    private Button btnTechnicianProfile;
    private Button btnTechnicianLogout;

    private int technicianId;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_technician_dashboard
        );

        technicianId =
                getIntent().getIntExtra(
                        EXTRA_TECHNICIAN_ID,
                        1
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
                view -> finish()
        );
    }
}