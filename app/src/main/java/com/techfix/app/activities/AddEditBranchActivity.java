package com.techfix.app.activities;

import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.techfix.app.R;
import com.techfix.app.database.BranchDao;
import com.techfix.app.models.Branch;

public class AddEditBranchActivity extends AppCompatActivity {

    private TextView txtBranchFormTitle;

    private EditText edtBranchName;
    private EditText edtBranchAddress;
    private EditText edtLatitude;
    private EditText edtLongitude;

    private Button btnUseCurrentLocation;
    private Button btnSelectOnMap;
    private Button btnSaveBranch;

    private BranchDao branchDao;

    private FusedLocationProviderClient fusedLocationClient;

    private int branchId = -1;

    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {

                        if (isGranted) {

                            getCurrentLocation();

                        } else {

                            Toast.makeText(
                                    this,
                                    "Location permission is required",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

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

        btnUseCurrentLocation =
                findViewById(
                        R.id.btnUseCurrentLocation
                );

        btnSelectOnMap =
                findViewById(
                        R.id.btnSelectOnMap
                );

        btnSaveBranch =
                findViewById(
                        R.id.btnSaveBranch
                );

        branchDao =
                new BranchDao(this);

        fusedLocationClient =
                LocationServices
                        .getFusedLocationProviderClient(
                                this
                        );

        loadExistingBranch();

        btnUseCurrentLocation
                .setOnClickListener(
                        view ->
                                checkLocationPermission()
                );

        btnSelectOnMap.setOnClickListener(
                view -> {

                    Intent intent =
                            new Intent(
                                    AddEditBranchActivity.this,
                                    MapPickerActivity.class
                            );

                    mapPickerLauncher.launch(
                            intent
                    );
                }
        );

        btnSaveBranch
                .setOnClickListener(
                        view ->
                                validateAndSave()
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

    private void checkLocationPermission() {

        if (
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                        ==
                        PackageManager.PERMISSION_GRANTED
        ) {

            getCurrentLocation();

        } else {

            locationPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
            );
        }
    }

    private void getCurrentLocation() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        fusedLocationClient.getCurrentLocation(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                null
        ).addOnSuccessListener(location -> {

            if (location != null) {

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                edtLatitude.setText(
                        String.valueOf(latitude)
                );

                edtLongitude.setText(
                        String.valueOf(longitude)
                );

                Toast.makeText(
                        this,
                        "Current location selected",
                        Toast.LENGTH_SHORT
                ).show();

            } else {

                Toast.makeText(
                        this,
                        "Could not detect your location",
                        Toast.LENGTH_LONG
                ).show();
            }

        }).addOnFailureListener(e -> {

            Toast.makeText(
                    this,
                    "Location error: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        });
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

            Toast.makeText(
                    this,
                    "Please select a branch location",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (longitudeText.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please select a branch location",
                    Toast.LENGTH_SHORT
            ).show();

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

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Invalid location coordinates",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (
                latitude < -90 ||
                        latitude > 90
        ) {

            Toast.makeText(
                    this,
                    "Invalid latitude",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (
                longitude < -180 ||
                        longitude > 180
        ) {

            Toast.makeText(
                    this,
                    "Invalid longitude",
                    Toast.LENGTH_SHORT
            ).show();

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

    private final ActivityResultLauncher<Intent> mapPickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (
                                result.getResultCode() == RESULT_OK &&
                                        result.getData() != null
                        ) {

                            Intent data =
                                    result.getData();

                            double latitude =
                                    data.getDoubleExtra(
                                            "selected_latitude",
                                            0
                                    );

                            double longitude =
                                    data.getDoubleExtra(
                                            "selected_longitude",
                                            0
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

                            Toast.makeText(
                                    this,
                                    "Map location selected",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );
}