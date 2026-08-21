package com.techfix.app.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;
import com.techfix.app.database.BranchDao;
import com.techfix.app.database.TechnicianDao;
import com.techfix.app.models.Branch;
import com.techfix.app.models.Technician;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddEditTechnicianActivity extends AppCompatActivity {

    private TextView txtTechnicianFormTitle;

    private EditText edtTechnicianName;
    private EditText edtTechnicianPhone;

    private Spinner spinnerSpecialization;
    private Spinner spinnerBranch;

    private Switch switchAvailable;

    private Button btnSaveTechnician;

    private TechnicianDao technicianDao;
    private BranchDao branchDao;

    private List<Branch> branchList;

    private int technicianId = -1;

    private int existingBranchId = -1;
    private String existingSpecialization = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_add_edit_technician
        );

        txtTechnicianFormTitle =
                findViewById(
                        R.id.txtTechnicianFormTitle
                );

        edtTechnicianName =
                findViewById(
                        R.id.edtTechnicianName
                );

        edtTechnicianPhone =
                findViewById(
                        R.id.edtTechnicianPhone
                );

        spinnerSpecialization =
                findViewById(
                        R.id.spinnerSpecialization
                );

        spinnerBranch =
                findViewById(
                        R.id.spinnerBranch
                );

        switchAvailable =
                findViewById(
                        R.id.switchAvailable
                );

        btnSaveTechnician =
                findViewById(
                        R.id.btnSaveTechnician
                );

        technicianDao =
                new TechnicianDao(this);

        branchDao =
                new BranchDao(this);

        loadIntentData();

        setupSpecializationSpinner();

        setupBranchSpinner();

        btnSaveTechnician.setOnClickListener(
                view -> validateAndSave()
        );
    }

    private void loadIntentData() {

        if (getIntent().hasExtra("technician_id")) {

            technicianId =
                    getIntent().getIntExtra(
                            "technician_id",
                            -1
                    );

            String name =
                    getIntent().getStringExtra(
                            "technician_name"
                    );

            String phone =
                    getIntent().getStringExtra(
                            "technician_phone"
                    );

            existingSpecialization =
                    getIntent().getStringExtra(
                            "technician_specialization"
                    );

            boolean available =
                    getIntent().getBooleanExtra(
                            "technician_available",
                            true
                    );

            existingBranchId =
                    getIntent().getIntExtra(
                            "technician_branch_id",
                            -1
                    );

            txtTechnicianFormTitle.setText(
                    "Edit Technician"
            );

            btnSaveTechnician.setText(
                    "Update Technician"
            );

            edtTechnicianName.setText(
                    name
            );

            edtTechnicianPhone.setText(
                    phone
            );

            switchAvailable.setChecked(
                    available
            );
        }
    }

    private void setupSpecializationSpinner() {

        List<String> specializations =
                Arrays.asList(
                        "Mobile Repair",
                        "Laptop Repair",
                        "Desktop Repair",
                        "Tablet Repair",
                        "Hardware Repair",
                        "Software Repair"
                );

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        specializations
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerSpecialization.setAdapter(
                adapter
        );

        if (existingSpecialization != null) {

            int position =
                    specializations.indexOf(
                            existingSpecialization
                    );

            if (position >= 0) {

                spinnerSpecialization.setSelection(
                        position
                );
            }
        }
    }

    private void setupBranchSpinner() {

        branchList =
                branchDao.getAllBranches();

        List<String> branchNames =
                new ArrayList<>();

        for (Branch branch : branchList) {

            branchNames.add(
                    branch.getBranchName()
            );
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        branchNames
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerBranch.setAdapter(
                adapter
        );

        if (existingBranchId != -1) {

            for (
                    int i = 0;
                    i < branchList.size();
                    i++
            ) {

                if (
                        branchList
                                .get(i)
                                .getBranchId()
                                ==
                                existingBranchId
                ) {

                    spinnerBranch.setSelection(
                            i
                    );

                    break;
                }
            }
        }
    }

    private void validateAndSave() {

        String name =
                edtTechnicianName
                        .getText()
                        .toString()
                        .trim();

        String phone =
                edtTechnicianPhone
                        .getText()
                        .toString()
                        .trim();

        if (name.isEmpty()) {

            edtTechnicianName.setError(
                    "Technician name is required"
            );

            edtTechnicianName.requestFocus();

            return;
        }

        if (phone.isEmpty()) {

            edtTechnicianPhone.setError(
                    "Phone number is required"
            );

            edtTechnicianPhone.requestFocus();

            return;
        }

        if (branchList.isEmpty()) {

            Toast.makeText(
                    this,
                    "No branches available",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String specialization =
                spinnerSpecialization
                        .getSelectedItem()
                        .toString();

        int selectedBranchPosition =
                spinnerBranch
                        .getSelectedItemPosition();

        Branch selectedBranch =
                branchList.get(
                        selectedBranchPosition
                );

        boolean available =
                switchAvailable.isChecked();

        Technician technician =
                new Technician(
                        technicianId,
                        name,
                        phone,
                        specialization,
                        available,
                        selectedBranch.getBranchId()
                );

        if (technicianId == -1) {

            insertTechnician(
                    technician
            );

        } else {

            confirmUpdate(
                    technician
            );
        }
    }

    private void insertTechnician(
            Technician technician
    ) {

        long result =
                technicianDao.insertTechnician(
                        technician
                );

        if (result != -1) {

            Toast.makeText(
                    this,
                    "Technician added successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Failed to add technician",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void confirmUpdate(
            Technician technician
    ) {

        new AlertDialog.Builder(this)
                .setTitle(
                        "Confirm Update"
                )
                .setMessage(
                        "Are you sure you want to save these changes?"
                )
                .setPositiveButton(
                        "Update",
                        (dialog, which) ->
                                updateTechnician(
                                        technician
                                )
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }

    private void updateTechnician(
            Technician technician
    ) {

        int result =
                technicianDao.updateTechnician(
                        technician
                );

        if (result > 0) {

            Toast.makeText(
                    this,
                    "Technician updated successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Failed to update technician",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}