package com.techfix.app.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;
import com.techfix.app.database.BranchDao;
import com.techfix.app.models.Branch;

public class AddEditBranchActivity
        extends AppCompatActivity {

    private TextView txtBranchFormTitle;

    private EditText edtBranchName;
    private EditText edtBranchAddress;
    private EditText edtLatitude;
    private EditText edtLongitude;

    private Button btnSaveBranch;

    private BranchDao branchDao;

    private int branchId = -1;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );

        setContentView(
                R.layout.activity_add_edit_branch
        );

        txtBranchFormTitle =
                findViewById(
                        R.id.txtBranchFormTitle
                );

        edtBranchName =
                findViewById(
                        R.id.edtBranchName
                );

        edtBranchAddress =
                findViewById(
                        R.id.edtBranchAddress
                );

        edtLatitude =
                findViewById(
                        R.id.edtLatitude
                );

        edtLongitude =
                findViewById(
                        R.id.edtLongitude
                );

        btnSaveBranch =
                findViewById(
                        R.id.btnSaveBranch
                );

        branchDao =
                new BranchDao(this);

        loadExistingBranch();

        btnSaveBranch.setOnClickListener(
                view -> validateAndSave()
        );
    }

    private void loadExistingBranch() {

        if (
                getIntent()
                        .hasExtra(
                                "branch_id"
                        )
        ) {

            branchId =
                    getIntent()
                            .getIntExtra(
                                    "branch_id",
                                    -1
                            );

            String branchName =
                    getIntent()
                            .getStringExtra(
                                    "branch_name"
                            );

            String branchAddress =
                    getIntent()
                            .getStringExtra(
                                    "branch_address"
                            );

            double latitude =
                    getIntent()
                            .getDoubleExtra(
                                    "branch_latitude",
                                    0
                            );

            double longitude =
                    getIntent()
                            .getDoubleExtra(
                                    "branch_longitude",
                                    0
                            );

            txtBranchFormTitle.setText(
                    "Edit Branch"
            );

            btnSaveBranch.setText(
                    "Update Branch"
            );

            edtBranchName.setText(
                    branchName
            );

            edtBranchAddress.setText(
                    branchAddress
            );

            edtLatitude.setText(
                    String.valueOf(
                            latitude
                    )
            );

            edtLongitude.setText(
                    String.valueOf(
                            longitude
                    )
            );
        }
    }

    private void validateAndSave() {

        String branchName =
                edtBranchName
                        .getText()
                        .toString()
                        .trim();

        String address =
                edtBranchAddress
                        .getText()
                        .toString()
                        .trim();

        String latitudeText =
                edtLatitude
                        .getText()
                        .toString()
                        .trim();

        String longitudeText =
                edtLongitude
                        .getText()
                        .toString()
                        .trim();

        if (branchName.isEmpty()) {

            edtBranchName.setError(
                    "Branch name is required"
            );

            edtBranchName.requestFocus();

            return;
        }

        if (address.isEmpty()) {

            edtBranchAddress.setError(
                    "Address is required"
            );

            edtBranchAddress.requestFocus();

            return;
        }

        if (latitudeText.isEmpty()) {

            edtLatitude.setError(
                    "Latitude is required"
            );

            edtLatitude.requestFocus();

            return;
        }

        if (longitudeText.isEmpty()) {

            edtLongitude.setError(
                    "Longitude is required"
            );

            edtLongitude.requestFocus();

            return;
        }

        double latitude;
        double longitude;

        try {

            latitude =
                    Double.parseDouble(
                            latitudeText
                    );

            longitude =
                    Double.parseDouble(
                            longitudeText
                    );

        } catch (
                NumberFormatException e
        ) {

            Toast.makeText(
                    this,
                    "Enter valid latitude and longitude",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (
                latitude < -90 ||
                        latitude > 90
        ) {

            edtLatitude.setError(
                    "Latitude must be between -90 and 90"
            );

            return;
        }

        if (
                longitude < -180 ||
                        longitude > 180
        ) {

            edtLongitude.setError(
                    "Longitude must be between -180 and 180"
            );

            return;
        }

        Branch branch =
                new Branch(
                        branchId,
                        branchName,
                        address,
                        latitude,
                        longitude
                );

        if (branchId == -1) {

            insertBranch(branch);

        } else {

            confirmUpdate(branch);
        }
    }

    private void insertBranch(
            Branch branch
    ) {

        long result =
                branchDao.insertBranch(
                        branch
                );

        if (result != -1) {

            Toast.makeText(
                    this,
                    "Branch added successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Failed to add branch",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void confirmUpdate(
            Branch branch
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
                                updateBranch(
                                        branch
                                )
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }

    private void updateBranch(
            Branch branch
    ) {

        int result =
                branchDao.updateBranch(
                        branch
                );

        if (result > 0) {

            Toast.makeText(
                    this,
                    "Branch updated successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Failed to update branch",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}