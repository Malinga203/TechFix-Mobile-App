package com.techfix.app.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;
import com.techfix.app.database.TechnicianDao;
import com.techfix.app.models.Technician;

public class TechnicianProfileActivity
        extends AppCompatActivity {

    private TextView txtTechnicianProfileName;
    private TextView txtTechnicianProfilePhone;
    private TextView txtTechnicianProfileSpecialization;
    private TextView txtTechnicianProfileBranch;
    private TextView txtTechnicianProfileAvailability;

    private TechnicianDao technicianDAO;

    private int technicianId;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_technician_profile
        );

        technicianId =
                getIntent().getIntExtra(
                        TechnicianDashboardActivity.EXTRA_TECHNICIAN_ID,
                        1
                );

        technicianDAO =
                new TechnicianDao(this);

        txtTechnicianProfileName =
                findViewById(
                        R.id.txtTechnicianProfileName
                );

        txtTechnicianProfilePhone =
                findViewById(
                        R.id.txtTechnicianProfilePhone
                );

        txtTechnicianProfileSpecialization =
                findViewById(
                        R.id.txtTechnicianProfileSpecialization
                );

        txtTechnicianProfileBranch =
                findViewById(
                        R.id.txtTechnicianProfileBranch
                );

        txtTechnicianProfileAvailability =
                findViewById(
                        R.id.txtTechnicianProfileAvailability
                );

        loadTechnician();
    }

    private void loadTechnician() {

        Technician technician =
                technicianDAO.getTechnicianById(
                        technicianId
                );

        if (technician == null) {

            Toast.makeText(
                    this,
                    "Technician not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        txtTechnicianProfileName.setText(
                "Name: " +
                        technician.getName()
        );

        txtTechnicianProfilePhone.setText(
                "Phone: " +
                        technician.getPhone()
        );

        txtTechnicianProfileSpecialization.setText(
                "Specialization: " +
                        technician.getSpecialization()
        );

        txtTechnicianProfileBranch.setText(
                "Branch ID: " +
                        technician.getBranchId()
        );

        txtTechnicianProfileAvailability.setText(
                technician.isAvailable()
                        ?
                        "Availability: Available"
                        :
                        "Availability: Unavailable"
        );
    }
}