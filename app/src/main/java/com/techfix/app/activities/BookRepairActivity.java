package com.techfix.app.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.techfix.app.R;
import com.techfix.app.database.AppointmentDAO;
import com.techfix.app.database.BranchDAO;
import com.techfix.app.database.ServiceDAO;
import com.techfix.app.models.Appointment;
import com.techfix.app.models.Branch;
import com.techfix.app.models.RepairService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BookRepairActivity extends AppCompatActivity {

    public static final String EXTRA_SERVICE_ID =
            "extra_service_id";

    private static final String[] TIME_SLOTS = {
            "09:00 AM", "10:00 AM", "11:00 AM",
            "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM"
    };

    private TextInputLayout tilService;
    private TextInputLayout tilDeviceModel;
    private TextInputLayout tilIssueDescription;
    private TextInputLayout tilDate;
    private TextInputLayout tilTime;
    private TextInputLayout tilBranch;
    private AutoCompleteTextView actvService;
    private AutoCompleteTextView actvTime;
    private AutoCompleteTextView actvBranch;
    private TextInputEditText etDeviceModel;
    private TextInputEditText etIssueDescription;
    private TextInputEditText etDate;
    private TextView tvSelectedPart;
    private TextView tvSummaryPrice;
    private Button btnBrowseParts;
    private Button btnConfirmBooking;

    private ServiceDAO serviceDAO;
    private BranchDAO branchDAO;
    private AppointmentDAO appointmentDAO;

    private List<RepairService> services;
    private List<Branch> branches;

    private RepairService selectedService;
    private Branch selectedBranch;
    private String selectedTime;

    private int selectedPartId = -1;
    private double selectedPartPrice = 0.0;
    private String selectedPartName;

    private final Calendar calendar =
            Calendar.getInstance();

    private final ActivityResultLauncher<Intent> sparePartLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK &&
                                result.getData() != null) {

                            Intent data = result.getData();

                            selectedPartId =
                                    data.getIntExtra(
                                            SparePartActivity.EXTRA_SELECTED_PART, -1
                                    );

                            selectedPartName =
                                    data.getStringExtra(
                                            SparePartActivity.EXTRA_SELECTED_PART + "_name"
                                    );

                            selectedPartPrice =
                                    data.getDoubleExtra(
                                            SparePartActivity.EXTRA_SELECTED_PART + "_price", 0.0
                                    );

                            updateSelectedPartView();

                            updateSummary();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_repair);

        MaterialToolbar toolbar =
                findViewById(R.id.toolbar_book_repair);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        serviceDAO =
                new ServiceDAO(this);

        branchDAO =
                new BranchDAO(this);

        appointmentDAO =
                new AppointmentDAO(this);

        bindViews();

        loadServices();

        loadBranches();

        setupTimeSlots();

        setupDatePicker();

        preselectServiceFromIntent();

        btnBrowseParts.setOnClickListener(v ->
                sparePartLauncher.launch(
                        new Intent(this, SparePartActivity.class)
                )
        );

        btnConfirmBooking.setOnClickListener(v ->
                confirmBooking()
        );
    }

    private void bindViews() {

        tilService =
                findViewById(R.id.til_service);

        tilDeviceModel =
                findViewById(R.id.til_device_model);

        tilIssueDescription =
                findViewById(R.id.til_issue_description);

        tilDate =
                findViewById(R.id.til_date);

        tilTime =
                findViewById(R.id.til_time);

        tilBranch =
                findViewById(R.id.til_branch);

        actvService =
                findViewById(R.id.actv_service);

        actvTime =
                findViewById(R.id.actv_time);

        actvBranch =
                findViewById(R.id.actv_branch);

        etDeviceModel =
                findViewById(R.id.et_device_model);

        etIssueDescription =
                findViewById(R.id.et_issue_description);

        etDate =
                findViewById(R.id.et_date);

        tvSelectedPart =
                findViewById(R.id.tv_selected_part);

        tvSummaryPrice =
                findViewById(R.id.tv_summary_price);

        btnBrowseParts =
                findViewById(R.id.btn_browse_parts);

        btnConfirmBooking =
                findViewById(R.id.btn_confirm_booking);
    }

    private void loadServices() {

        services =
                serviceDAO.getAllServices();

        List<String> serviceNames =
                new ArrayList<>();

        for (RepairService service : services) {
            serviceNames.add(service.getServiceName());
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        serviceNames
                );

        actvService.setAdapter(adapter);

        actvService.setOnItemClickListener((parent, view, position, id) -> {

            selectedService =
                    services.get(position);

            updateSummary();
        });
    }

    private void loadBranches() {

        branches =
                branchDAO.getAllBranches();

        List<String> branchNames =
                new ArrayList<>();

        for (Branch branch : branches) {
            branchNames.add(branch.getBranchName());
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        branchNames
                );

        actvBranch.setAdapter(adapter);

        actvBranch.setOnItemClickListener((parent, view, position, id) ->
                selectedBranch = branches.get(position)
        );
    }

    private void setupTimeSlots() {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        TIME_SLOTS
                );

        actvTime.setAdapter(adapter);

        actvTime.setOnItemClickListener((parent, view, position, id) ->
                selectedTime = TIME_SLOTS[position]
        );
    }

    private void setupDatePicker() {

        etDate.setOnClickListener(v -> {

            int year =
                    calendar.get(Calendar.YEAR);

            int month =
                    calendar.get(Calendar.MONTH);

            int day =
                    calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog =
                    new DatePickerDialog(
                            this,
                            (view, selectedYear, selectedMonth, selectedDay) -> {

                                Calendar picked =
                                        Calendar.getInstance();

                                picked.set(
                                        selectedYear,
                                        selectedMonth,
                                        selectedDay
                                );

                                SimpleDateFormat format =
                                        new SimpleDateFormat("yyyy-MM-dd", Locale.US);

                                etDate.setText(format.format(picked.getTime()));

                                tilDate.setError(null);

                            },
                            year,
                            month,
                            day
                    );

            dialog.getDatePicker().setMinDate(System.currentTimeMillis());

            dialog.show();
        });
    }

    private void preselectServiceFromIntent() {

        int serviceId =
                getIntent().getIntExtra(EXTRA_SERVICE_ID, -1);

        if (serviceId == -1) {
            return;
        }

        for (int i = 0; i < services.size(); i++) {

            if (services.get(i).getServiceId() == serviceId) {

                selectedService =
                        services.get(i);

                actvService.setText(selectedService.getServiceName(), false);

                updateSummary();

                break;
            }
        }
    }

    private void updateSelectedPartView() {

        if (selectedPartId == -1) {

            tvSelectedPart.setVisibility(View.GONE);

            tvSelectedPart.setOnClickListener(null);

            return;
        }

        tvSelectedPart.setVisibility(View.VISIBLE);

        tvSelectedPart.setText(
                getString(R.string.label_selected_part) + " " +
                        selectedPartName +
                        String.format(Locale.US, " (+$%.2f)", selectedPartPrice) + " " +
                        getString(R.string.label_tap_to_remove)
        );

        tvSelectedPart.setOnClickListener(v -> {

            selectedPartId = -1;

            selectedPartName = null;

            selectedPartPrice = 0.0;

            updateSelectedPartView();

            updateSummary();
        });
    }

    private void updateSummary() {

        if (selectedService == null) {

            tvSummaryPrice.setText(
                    getString(R.string.label_estimated_price) + ": -"
            );

            return;
        }

        double total =
                selectedService.getPrice() +
                        (selectedPartId != -1 ? selectedPartPrice : 0.0);

        tvSummaryPrice.setText(
                getString(R.string.label_estimated_price) + ": " +
                        String.format(Locale.US, "$%.2f", total)
        );
    }

    private boolean validateInputs() {

        boolean valid =
                true;

        if (selectedService == null) {

            tilService.setError(getString(R.string.msg_select_all_fields));

            valid = false;
        }

        if (etDeviceModel.getText() == null ||
                etDeviceModel.getText().toString().trim().isEmpty()) {

            tilDeviceModel.setError(getString(R.string.msg_select_all_fields));

            valid = false;
        }

        if (etIssueDescription.getText() == null ||
                etIssueDescription.getText().toString().trim().isEmpty()) {

            tilIssueDescription.setError(getString(R.string.msg_select_all_fields));

            valid = false;
        }

        if (etDate.getText() == null ||
                etDate.getText().toString().trim().isEmpty()) {

            tilDate.setError(getString(R.string.msg_select_all_fields));

            valid = false;
        }

        if (selectedTime == null) {

            tilTime.setError(getString(R.string.msg_select_all_fields));

            valid = false;
        }

        if (selectedBranch == null) {

            tilBranch.setError(getString(R.string.msg_select_all_fields));

            valid = false;
        }

        return valid;
    }

    private static final int MAX_BOOKINGS_PER_SLOT =
            2;

    private void confirmBooking() {

        if (!validateInputs()) {
            return;
        }

        int slotBookings =
                appointmentDAO.getAppointmentCountForSlot(
                        etDate.getText().toString().trim(),
                        selectedTime
                );

        if (slotBookings >= MAX_BOOKINGS_PER_SLOT) {

            tilTime.setError(getString(R.string.msg_slot_full));

            Toast.makeText(
                    this,
                    R.string.msg_slot_full,
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        Appointment appointment =
                new Appointment(
                        1,
                        selectedService.getServiceId(),
                        selectedPartId != -1 ? selectedPartId : null,
                        selectedBranch.getBranchId(),
                        etDeviceModel.getText().toString().trim(),
                        etIssueDescription.getText().toString().trim(),
                        etDate.getText().toString().trim(),
                        selectedTime
                );

        long insertedId =
                appointmentDAO.insertAppointment(appointment);

        if (insertedId > 0) {

            Toast.makeText(
                    this,
                    R.string.msg_booking_success,
                    Toast.LENGTH_LONG
            ).show();

            setResult(RESULT_OK);

            finish();

        } else {

            Toast.makeText(
                    this,
                    R.string.msg_booking_failed,
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
