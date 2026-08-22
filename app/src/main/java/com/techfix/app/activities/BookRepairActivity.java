package com.techfix.app.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.techfix.app.R;
import com.techfix.app.database.AppointmentDAO;
import com.techfix.app.database.ServiceDAO;
import com.techfix.app.models.Appointment;
import com.techfix.app.models.Branch;
import com.techfix.app.models.RepairService;
import com.techfix.app.services.BranchAssignmentService;

import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BookRepairActivity
        extends AppCompatActivity {

    public static final String EXTRA_SERVICE_ID =
            "extra_service_id";

    private static final String[] TIME_SLOTS = {
            "09:00 AM",
            "10:00 AM",
            "11:00 AM",
            "01:00 PM",
            "02:00 PM",
            "03:00 PM",
            "04:00 PM"
    };

    private static final int MAX_BOOKINGS_PER_SLOT =
            2;

    private TextInputLayout tilService;
    private TextInputLayout tilDeviceModel;
    private TextInputLayout tilIssueDescription;
    private TextInputLayout tilDate;
    private TextInputLayout tilTime;

    private AutoCompleteTextView actvService;
    private AutoCompleteTextView actvTime;

    private TextInputEditText etDeviceModel;
    private TextInputEditText etIssueDescription;
    private TextInputEditText etDate;

    private TextView tvSelectedPart;
    private TextView tvSummaryPrice;

    private Button btnBrowseParts;
    private Button btnConfirmBooking;

    private ServiceDAO serviceDAO;
    private AppointmentDAO appointmentDAO;

    private BranchAssignmentService branchAssignmentService;

    private FusedLocationProviderClient fusedLocationClient;

    private List<RepairService> services;

    private RepairService selectedService;

    private Branch selectedBranch;

    private String selectedTime;

    private int selectedPartId = -1;

    private double selectedPartPrice = 0.0;

    private String selectedPartName;

    private final Calendar calendar =
            Calendar.getInstance();


    // =========================================================
    // SPARE PART RESULT
    // =========================================================

    private final ActivityResultLauncher<Intent> sparePartLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (
                                result.getResultCode() == RESULT_OK &&
                                        result.getData() != null
                        ) {

                            Intent data =
                                    result.getData();

                            selectedPartId =
                                    data.getIntExtra(
                                            SparePartActivity.EXTRA_SELECTED_PART,
                                            -1
                                    );

                            selectedPartName =
                                    data.getStringExtra(
                                            SparePartActivity.EXTRA_SELECTED_PART
                                                    + "_name"
                                    );

                            selectedPartPrice =
                                    data.getDoubleExtra(
                                            SparePartActivity.EXTRA_SELECTED_PART
                                                    + "_price",
                                            0.0
                                    );

                            updateSelectedPartView();

                            updateSummary();
                        }
                    }
            );


    // =========================================================
    // LOCATION PERMISSION
    // =========================================================

    private final ActivityResultLauncher<String>
            locationPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {

                        if (isGranted) {

                            getLocationAndAssignBranch();

                        } else {

                            Toast.makeText(
                                    this,
                                    "Location permission is required to assign the nearest branch",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
            );


    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );

        setContentView(
                R.layout.activity_book_repair
        );

        MaterialToolbar toolbar =
                findViewById(
                        R.id.toolbar_book_repair
                );

        setSupportActionBar(
                toolbar
        );

        if (getSupportActionBar() != null) {

            getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(
                            true
                    );
        }

        toolbar.setNavigationOnClickListener(
                view -> finish()
        );

        serviceDAO =
                new ServiceDAO(this);

        appointmentDAO =
                new AppointmentDAO(this);

        branchAssignmentService =
                new BranchAssignmentService(this);

        fusedLocationClient =
                LocationServices
                        .getFusedLocationProviderClient(
                                this
                        );

        bindViews();

        loadServices();

        setupTimeSlots();

        setupDatePicker();

        preselectServiceFromIntent();

        btnBrowseParts.setOnClickListener(
                view ->
                        sparePartLauncher.launch(
                                new Intent(
                                        this,
                                        SparePartActivity.class
                                )
                        )
        );

        btnConfirmBooking.setOnClickListener(
                view ->
                        startBranchAssignment()
        );
    }


    // =========================================================
    // VIEW BINDING
    // =========================================================

    private void bindViews() {

        tilService =
                findViewById(
                        R.id.til_service
                );

        tilDeviceModel =
                findViewById(
                        R.id.til_device_model
                );

        tilIssueDescription =
                findViewById(
                        R.id.til_issue_description
                );

        tilDate =
                findViewById(
                        R.id.til_date
                );

        tilTime =
                findViewById(
                        R.id.til_time
                );

        actvService =
                findViewById(
                        R.id.actv_service
                );

        actvTime =
                findViewById(
                        R.id.actv_time
                );

        etDeviceModel =
                findViewById(
                        R.id.et_device_model
                );

        etIssueDescription =
                findViewById(
                        R.id.et_issue_description
                );

        etDate =
                findViewById(
                        R.id.et_date
                );

        tvSelectedPart =
                findViewById(
                        R.id.tv_selected_part
                );

        tvSummaryPrice =
                findViewById(
                        R.id.tv_summary_price
                );

        btnBrowseParts =
                findViewById(
                        R.id.btn_browse_parts
                );

        btnConfirmBooking =
                findViewById(
                        R.id.btn_confirm_booking
                );
    }


    // =========================================================
    // SERVICES
    // =========================================================

    private void loadServices() {

        services =
                serviceDAO.getAllServices();

        List<String> serviceNames =
                new ArrayList<>();

        for (RepairService service : services) {

            serviceNames.add(
                    service.getServiceName()
            );
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        serviceNames
                );

        actvService.setAdapter(
                adapter
        );

        actvService.setOnItemClickListener(
                (parent, view, position, id) -> {

                    selectedService =
                            services.get(
                                    position
                            );

                    tilService.setError(
                            null
                    );

                    updateSummary();
                }
        );
    }


    // =========================================================
    // TIME SLOTS
    // =========================================================

    private void setupTimeSlots() {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        TIME_SLOTS
                );

        actvTime.setAdapter(
                adapter
        );

        actvTime.setOnItemClickListener(
                (parent, view, position, id) -> {

                    selectedTime =
                            TIME_SLOTS[position];

                    tilTime.setError(
                            null
                    );
                }
        );
    }


    // =========================================================
    // DATE
    // =========================================================

    private void setupDatePicker() {

        etDate.setOnClickListener(
                view -> {

                    int year =
                            calendar.get(
                                    Calendar.YEAR
                            );

                    int month =
                            calendar.get(
                                    Calendar.MONTH
                            );

                    int day =
                            calendar.get(
                                    Calendar.DAY_OF_MONTH
                            );

                    DatePickerDialog dialog =
                            new DatePickerDialog(
                                    this,
                                    (
                                            datePicker,
                                            selectedYear,
                                            selectedMonth,
                                            selectedDay
                                    ) -> {

                                        Calendar picked =
                                                Calendar
                                                        .getInstance();

                                        picked.set(
                                                selectedYear,
                                                selectedMonth,
                                                selectedDay
                                        );

                                        SimpleDateFormat format =
                                                new SimpleDateFormat(
                                                        "yyyy-MM-dd",
                                                        Locale.US
                                                );

                                        etDate.setText(
                                                format.format(
                                                        picked.getTime()
                                                )
                                        );

                                        tilDate.setError(
                                                null
                                        );
                                    },
                                    year,
                                    month,
                                    day
                            );

                    dialog.getDatePicker()
                            .setMinDate(
                                    System.currentTimeMillis()
                            );

                    dialog.show();
                }
        );
    }


    // =========================================================
    // PRESELECT SERVICE
    // =========================================================

    private void preselectServiceFromIntent() {

        int serviceId =
                getIntent()
                        .getIntExtra(
                                EXTRA_SERVICE_ID,
                                -1
                        );

        if (serviceId == -1) {
            return;
        }

        for (int i = 0; i < services.size(); i++) {

            if (
                    services.get(i)
                            .getServiceId()
                            ==
                            serviceId
            ) {

                selectedService =
                        services.get(i);

                actvService.setText(
                        selectedService
                                .getServiceName(),
                        false
                );

                updateSummary();

                break;
            }
        }
    }


    // =========================================================
    // SELECTED PART
    // =========================================================

    private void updateSelectedPartView() {

        if (selectedPartId == -1) {

            tvSelectedPart.setVisibility(
                    View.GONE
            );

            tvSelectedPart.setOnClickListener(
                    null
            );

            return;
        }

        tvSelectedPart.setVisibility(
                View.VISIBLE
        );

        tvSelectedPart.setText(
                "Selected Part: " +
                        selectedPartName +
                        String.format(
                                Locale.US,
                                " (+$%.2f)",
                                selectedPartPrice
                        ) +
                        "\nTap to remove"
        );

        tvSelectedPart.setOnClickListener(
                view -> {

                    selectedPartId =
                            -1;

                    selectedPartName =
                            null;

                    selectedPartPrice =
                            0.0;

                    updateSelectedPartView();

                    updateSummary();
                }
        );
    }


    // =========================================================
    // PRICE SUMMARY
    // =========================================================

    private void updateSummary() {

        if (selectedService == null) {

            tvSummaryPrice.setText(
                    "Estimated Price: -"
            );

            return;
        }

        double total =
                selectedService.getPrice()
                        +
                        (
                                selectedPartId != -1
                                        ?
                                        selectedPartPrice
                                        :
                                        0.0
                        );

        tvSummaryPrice.setText(
                "Estimated Price: " +
                        String.format(
                                Locale.US,
                                "$%.2f",
                                total
                        )
        );
    }


    // =========================================================
    // INPUT VALIDATION
    // =========================================================

    private boolean validateInputs() {

        boolean valid =
                true;

        if (selectedService == null) {

            tilService.setError(
                    "Select a repair service"
            );

            valid =
                    false;

        } else {

            tilService.setError(
                    null
            );
        }


        if (
                etDeviceModel.getText() == null ||
                        etDeviceModel
                                .getText()
                                .toString()
                                .trim()
                                .isEmpty()
        ) {

            tilDeviceModel.setError(
                    "Enter the device model"
            );

            valid =
                    false;

        } else {

            tilDeviceModel.setError(
                    null
            );
        }


        if (
                etIssueDescription.getText() == null ||
                        etIssueDescription
                                .getText()
                                .toString()
                                .trim()
                                .isEmpty()
        ) {

            tilIssueDescription.setError(
                    "Describe the issue"
            );

            valid =
                    false;

        } else {

            tilIssueDescription.setError(
                    null
            );
        }


        if (
                etDate.getText() == null ||
                        etDate
                                .getText()
                                .toString()
                                .trim()
                                .isEmpty()
        ) {

            tilDate.setError(
                    "Select appointment date"
            );

            valid =
                    false;

        } else {

            tilDate.setError(
                    null
            );
        }


        if (selectedTime == null) {

            tilTime.setError(
                    "Select appointment time"
            );

            valid =
                    false;

        } else {

            tilTime.setError(
                    null
            );
        }


        return valid;
    }


    // =========================================================
    // START BRANCH ASSIGNMENT
    // =========================================================

    private void startBranchAssignment() {

        if (!validateInputs()) {
            return;
        }

        if (
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                        ==
                        PackageManager.PERMISSION_GRANTED
        ) {

            getLocationAndAssignBranch();

        } else {

            locationPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
            );
        }
    }


    // =========================================================
    // GET GPS + FIND BRANCH
    // =========================================================

    private void getLocationAndAssignBranch() {

        if (
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                        !=
                        PackageManager.PERMISSION_GRANTED
        ) {

            return;
        }

        fusedLocationClient
                .getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        null
                )
                .addOnSuccessListener(
                        location -> {

                            if (location == null) {

                                Toast.makeText(
                                        this,
                                        "Unable to detect your current location",
                                        Toast.LENGTH_LONG
                                ).show();

                                return;
                            }

                            String requiredSpecialization =
                                    selectedService
                                            .getCategory();

                            Integer requiredPartId =
                                    selectedPartId != -1
                                            ?
                                            selectedPartId
                                            :
                                            null;

                            selectedBranch =
                                    branchAssignmentService
                                            .findNearestSuitableBranch(
                                                    location.getLatitude(),
                                                    location.getLongitude(),
                                                    requiredSpecialization,
                                                    requiredPartId
                                            );

                            if (selectedBranch == null) {

                                Toast.makeText(
                                        this,
                                        "No branch currently has the required technician and spare part",
                                        Toast.LENGTH_LONG
                                ).show();

                                return;
                            }

                            float distance =
                                    branchAssignmentService
                                            .getDistanceToBranch(
                                                    location.getLatitude(),
                                                    location.getLongitude(),
                                                    selectedBranch
                                            );

                            showAssignedBranchDialog(
                                    distance
                            );
                        }
                )
                .addOnFailureListener(
                        error -> {

                            Toast.makeText(
                                    this,
                                    "Failed to retrieve your location",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }


    // =========================================================
    // BRANCH CONFIRMATION
    // =========================================================

    private void showAssignedBranchDialog(
            float distance
    ) {

        String message =
                "Nearest suitable branch:\n\n" +
                        selectedBranch
                                .getBranchName() +
                        "\n" +
                        selectedBranch
                                .getAddress() +
                        "\n\nDistance: " +
                        String.format(
                                Locale.getDefault(),
                                "%.2f km",
                                distance
                        ) +
                        "\n\nThis branch has the required technician" +
                        (
                                selectedPartId != -1
                                        ?
                                        " and selected spare part."
                                        :
                                        "."
                        );

        new AlertDialog.Builder(this)
                .setTitle(
                        "Branch Assigned"
                )
                .setMessage(
                        message
                )
                .setPositiveButton(
                        "Continue",
                        (
                                dialog,
                                which
                        ) ->
                                confirmBooking()
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }


    // =========================================================
    // SAVE APPOINTMENT
    // =========================================================

    private void confirmBooking() {

        int slotBookings =
                appointmentDAO
                        .getAppointmentCountForSlot(
                                etDate
                                        .getText()
                                        .toString()
                                        .trim(),

                                selectedTime
                        );

        if (
                slotBookings
                        >=
                        MAX_BOOKINGS_PER_SLOT
        ) {

            tilTime.setError(
                    "This time slot is full"
            );

            Toast.makeText(
                    this,
                    "This time slot is full. Please choose another time.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        Appointment appointment =
                new Appointment(
                        1,

                        selectedService
                                .getServiceId(),

                        selectedPartId != -1
                                ?
                                selectedPartId
                                :
                                null,

                        selectedBranch
                                .getBranchId(),

                        etDeviceModel
                                .getText()
                                .toString()
                                .trim(),

                        etIssueDescription
                                .getText()
                                .toString()
                                .trim(),

                        etDate
                                .getText()
                                .toString()
                                .trim(),

                        selectedTime
                );

        long insertedId =
                appointmentDAO
                        .insertAppointment(
                                appointment
                        );

        if (insertedId > 0) {

            Toast.makeText(
                    this,
                    "Repair appointment booked successfully",
                    Toast.LENGTH_LONG
            ).show();

            setResult(
                    RESULT_OK
            );

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Failed to book appointment",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}