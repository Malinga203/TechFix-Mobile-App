package com.techfix.app.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

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
import com.techfix.app.models.PartSelection;
import com.techfix.app.models.RepairService;
import com.techfix.app.services.BranchAssignmentService;
import com.techfix.app.userauthentication.utils.SessionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BookRepairActivity extends AppCompatActivity {

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

    private static final int MAX_BOOKINGS_PER_SLOT = 2;

    private static final long MAX_IMAGE_SIZE =
            5L * 1024L * 1024L;

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
    private Button btnBookingCamera;
    private Button btnBookingGallery;
    private Button btnRemoveBookingImage;

    private ImageView imgBookingDevice;

    private File pendingCameraFile;
    private Uri pendingCameraUri;

    private File selectedDeviceImageFile;
    private String selectedDeviceImageUri;

    private ServiceDAO serviceDAO;
    private AppointmentDAO appointmentDAO;

    private BranchAssignmentService branchAssignmentService;

    private FusedLocationProviderClient fusedLocationClient;

    private SessionManager sessionManager;

    private List<RepairService> services;

    private RepairService selectedService;

    private Branch selectedBranch;

    private String selectedTime;

    private final ArrayList<PartSelection> selectedParts =
            new ArrayList<>();

    private final Calendar calendar =
            Calendar.getInstance();


    // Spare part selection result
    private final ActivityResultLauncher<Intent> sparePartLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (
                                result.getResultCode() == RESULT_OK
                                        &&
                                        result.getData() != null
                        ) {

                            Intent data =
                                    result.getData();

                            Serializable serializable =
                                    data.getSerializableExtra(
                                            SparePartActivity.EXTRA_SELECTED_PARTS
                                    );

                            selectedParts.clear();

                            if (
                                    serializable
                                            instanceof
                                            ArrayList<?>
                            ) {

                                ArrayList<?> returnedList =
                                        (ArrayList<?>) serializable;

                                for (Object item : returnedList) {

                                    if (
                                            item
                                                    instanceof
                                                    PartSelection
                                    ) {

                                        selectedParts.add(
                                                (PartSelection) item
                                        );
                                    }
                                }
                            }

                            /*
                             * If parts change, a branch selected from an
                             * older inventory check must no longer be used.
                             */
                            selectedBranch =
                                    null;

                            updateSelectedPartView();

                            updateSummary();
                        }
                    }
            );


    // Device photo from camera
    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.TakePicture(),
                    success -> {

                        if (success) {

                            handleCameraResult();

                        } else {

                            deletePendingCameraFile();
                        }
                    }
            );


    // Device photo from gallery
    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {

                        if (uri != null) {
                            handleGalleryResult(uri);
                        }
                    }
            );


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

        super.onCreate(savedInstanceState);

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

        sessionManager =
                new SessionManager(this);

        if (!sessionManager.isLoggedIn()
                || sessionManager.getUserId() <= 0) {

            Toast.makeText(
                    this,
                    "Please login before booking an appointment",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

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

        btnBookingCamera.setOnClickListener(
                view -> openCamera()
        );

        btnBookingGallery.setOnClickListener(
                view ->
                        galleryLauncher.launch(
                                "image/*"
                        )
        );

        btnRemoveBookingImage.setOnClickListener(
                view -> removeSelectedImage()
        );
    }


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

        imgBookingDevice =
                findViewById(
                        R.id.img_booking_device
                );

        btnBookingCamera =
                findViewById(
                        R.id.btn_booking_camera
                );

        btnBookingGallery =
                findViewById(
                        R.id.btn_booking_gallery
                );

        btnRemoveBookingImage =
                findViewById(
                        R.id.btn_remove_booking_image
                );
    }


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

        for (int i = 0;
             i < services.size();
             i++) {

            if (services.get(i)
                    .getServiceId()
                    == serviceId) {

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


    private void updateSelectedPartView() {

        if (selectedParts.isEmpty()) {

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

        StringBuilder text =
                new StringBuilder(
                        "Selected Spare Parts:\n"
                );

        double partsTotal =
                0.0;

        int totalUnits =
                0;

        for (
                PartSelection selection
                :
                selectedParts
        ) {

            double lineTotal =
                    selection.getTotalPrice();

            partsTotal +=
                    lineTotal;

            totalUnits +=
                    selection.getQuantity();

            text.append(
                    "• "
            );

            text.append(
                    selection.getPartName()
            );

            text.append(
                    " × "
            );

            text.append(
                    selection.getQuantity()
            );

            text.append(
                    String.format(
                            Locale.getDefault(),
                            " = LKR %,.2f",
                            lineTotal
                    )
            );

            text.append(
                    "\n"
            );
        }

        text.append(
                String.format(
                        Locale.getDefault(),
                        "\n%d item type(s), %d unit(s) • Parts total: LKR %,.2f",
                        selectedParts.size(),
                        totalUnits,
                        partsTotal
                )
        );

        text.append(
                "\nTap to edit selection"
        );

        tvSelectedPart.setText(
                text.toString()
        );

        tvSelectedPart.setOnClickListener(
                view -> {

                    Intent intent =
                            new Intent(
                                    this,
                                    SparePartActivity.class
                            );

                    intent.putExtra(
                            SparePartActivity.EXTRA_SELECTED_PARTS,
                            new ArrayList<>(
                                    selectedParts
                            )
                    );

                    sparePartLauncher.launch(
                            intent
                    );
                }
        );
    }


    private double getSelectedPartsTotal() {

        double total =
                0.0;

        for (
                PartSelection selection
                :
                selectedParts
        ) {

            total +=
                    selection.getTotalPrice();
        }

        return total;
    }


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
                        getSelectedPartsTotal();

        tvSummaryPrice.setText(
                "Estimated Price: "
                        +
                        String.format(
                                Locale.getDefault(),
                                "LKR %,.2f",
                                total
                        )
        );
    }


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

        if (etDeviceModel.getText() == null
                || etDeviceModel
                .getText()
                .toString()
                .trim()
                .isEmpty()) {

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

        if (etIssueDescription.getText() == null
                || etIssueDescription
                .getText()
                .toString()
                .trim()
                .isEmpty()) {

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

        if (etDate.getText() == null
                || etDate
                .getText()
                .toString()
                .trim()
                .isEmpty()) {

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


    private void startBranchAssignment() {

        if (!validateInputs()) {
            return;
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) {

            getLocationAndAssignBranch();

        } else {

            locationPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
            );
        }
    }


    private void getLocationAndAssignBranch() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

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
                            selectedBranch =
                                    branchAssignmentService
                                            .findNearestSuitableBranch(
                                                    location.getLatitude(),
                                                    location.getLongitude(),
                                                    requiredSpecialization,
                                                    selectedParts
                                            );

                            if (selectedBranch == null) {

                                Toast.makeText(
                                        this,
                                        "No branch currently has the required technician and enough stock for all selected spare parts",
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


    private void showAssignedBranchDialog(
            float distance
    ) {

        String message =
                "Nearest suitable branch:\n\n"
                        + selectedBranch
                        .getBranchName()
                        + "\n"
                        + selectedBranch
                        .getAddress()
                        + "\n\nDistance: "
                        + String.format(
                        Locale.getDefault(),
                        "%.2f km",
                        distance
                )
                        + "\n\nThis branch has the required technician"
                        +
                        (
                                !selectedParts.isEmpty()
                                        ? " and enough stock for all selected spare parts."
                                        : "."
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


    private void openCamera() {

        try {

            deletePendingCameraFile();

            pendingCameraFile =
                    createImageFile();

            pendingCameraUri =
                    FileProvider.getUriForFile(
                            this,
                            getPackageName()
                                    + ".fileprovider",
                            pendingCameraFile
                    );

            cameraLauncher.launch(
                    pendingCameraUri
            );

        } catch (IOException exception) {

            Toast.makeText(
                    this,
                    "Unable to create image file",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    private void handleCameraResult() {

        if (!isValidImageFile(
                pendingCameraFile
        )) {

            deletePendingCameraFile();

            Toast.makeText(
                    this,
                    "Invalid camera image",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (pendingCameraUri == null) {

            deletePendingCameraFile();

            Toast.makeText(
                    this,
                    "Unable to access captured image",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        deletePreviouslySelectedFile(
                pendingCameraFile
        );

        selectedDeviceImageFile =
                pendingCameraFile;

        selectedDeviceImageUri =
                pendingCameraUri.toString();

        showSelectedImage(
                pendingCameraUri
        );

        Toast.makeText(
                this,
                "Device photo added",
                Toast.LENGTH_SHORT
        ).show();
    }


    private void handleGalleryResult(
            Uri sourceUri
    ) {

        if (!isValidGalleryImage(
                sourceUri
        )) {

            Toast.makeText(
                    this,
                    "Please select a valid image under 5 MB",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        try {

            File copiedImage =
                    copyGalleryImage(
                            sourceUri
                    );

            Uri savedUri =
                    FileProvider.getUriForFile(
                            this,
                            getPackageName()
                                    + ".fileprovider",
                            copiedImage
                    );

            deletePreviouslySelectedFile(
                    copiedImage
            );

            selectedDeviceImageFile =
                    copiedImage;

            selectedDeviceImageUri =
                    savedUri.toString();

            showSelectedImage(
                    savedUri
            );

            Toast.makeText(
                    this,
                    "Device photo added",
                    Toast.LENGTH_SHORT
            ).show();

        } catch (IOException exception) {

            Toast.makeText(
                    this,
                    "Unable to save selected image",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    private void showSelectedImage(
            Uri uri
    ) {

        imgBookingDevice.setImageURI(
                null
        );

        imgBookingDevice.setPadding(
                0,
                0,
                0,
                0
        );

        imgBookingDevice.setScaleType(
                ImageView.ScaleType.CENTER_CROP
        );

        imgBookingDevice.setImageURI(
                uri
        );

        btnRemoveBookingImage.setVisibility(
                View.VISIBLE
        );
    }


    private void removeSelectedImage() {

        if (selectedDeviceImageFile != null
                && selectedDeviceImageFile.exists()) {

            selectedDeviceImageFile.delete();
        }

        if (pendingCameraFile != null
                && pendingCameraFile.exists()
                && pendingCameraFile != selectedDeviceImageFile) {

            pendingCameraFile.delete();
        }

        selectedDeviceImageFile =
                null;

        selectedDeviceImageUri =
                null;

        pendingCameraFile =
                null;

        pendingCameraUri =
                null;

        imgBookingDevice.setImageURI(
                null
        );

        imgBookingDevice.setScaleType(
                ImageView.ScaleType.CENTER_INSIDE
        );

        int padding =
                dpToPx(18);

        imgBookingDevice.setPadding(
                padding,
                padding,
                padding,
                padding
        );

        imgBookingDevice.setImageResource(
                android.R.drawable.ic_menu_camera
        );

        btnRemoveBookingImage.setVisibility(
                View.GONE
        );

        Toast.makeText(
                this,
                "Device photo removed",
                Toast.LENGTH_SHORT
        ).show();
    }


    private void deletePreviouslySelectedFile(
            File newFile
    ) {

        if (selectedDeviceImageFile == null) {
            return;
        }

        if (selectedDeviceImageFile.equals(
                newFile
        )) {

            return;
        }

        if (selectedDeviceImageFile.exists()) {

            selectedDeviceImageFile.delete();
        }
    }


    private void deletePendingCameraFile() {

        if (pendingCameraFile != null
                && pendingCameraFile.exists()
                && pendingCameraFile != selectedDeviceImageFile) {

            pendingCameraFile.delete();
        }

        pendingCameraFile =
                null;

        pendingCameraUri =
                null;
    }


    private boolean isValidGalleryImage(
            Uri uri
    ) {

        if (uri == null) {
            return false;
        }

        String mimeType =
                getContentResolver()
                        .getType(
                                uri
                        );

        if (TextUtils.isEmpty(
                mimeType
        )
                || !mimeType.startsWith(
                "image/"
        )) {

            return false;
        }

        long size =
                getContentSize(
                        uri
                );

        return size <= 0
                || size <= MAX_IMAGE_SIZE;
    }


    private long getContentSize(
            Uri uri
    ) {

        Cursor cursor =
                null;

        try {

            cursor =
                    getContentResolver()
                            .query(
                                    uri,
                                    new String[]{
                                            OpenableColumns.SIZE
                                    },
                                    null,
                                    null,
                                    null
                            );

            if (cursor != null
                    && cursor.moveToFirst()) {

                int index =
                        cursor.getColumnIndex(
                                OpenableColumns.SIZE
                        );

                if (index >= 0
                        && !cursor.isNull(
                        index
                )) {

                    return cursor.getLong(
                            index
                    );
                }
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        return -1;
    }


    private File copyGalleryImage(
            Uri sourceUri
    ) throws IOException {

        File directory =
                getExternalFilesDir(
                        Environment.DIRECTORY_PICTURES
                );

        if (directory == null) {

            throw new IOException(
                    "Image directory unavailable"
            );
        }

        String extension =
                getFileExtension(
                        sourceUri
                );

        File destination =
                new File(
                        directory,
                        "booking_device_"
                                + System.currentTimeMillis()
                                + "."
                                + extension
                );

        try (
                InputStream input =
                        getContentResolver()
                                .openInputStream(
                                        sourceUri
                                );

                FileOutputStream output =
                        new FileOutputStream(
                                destination
                        )
        ) {

            if (input == null) {

                throw new IOException(
                        "Unable to read image"
                );
            }

            byte[] buffer =
                    new byte[8192];

            long total =
                    0;

            int read;

            while ((read =
                    input.read(
                            buffer
                    )) != -1) {

                total += read;

                if (total > MAX_IMAGE_SIZE) {

                    destination.delete();

                    throw new IOException(
                            "Image too large"
                    );
                }

                output.write(
                        buffer,
                        0,
                        read
                );
            }
        }

        if (!isValidImageFile(
                destination
        )) {

            destination.delete();

            throw new IOException(
                    "Invalid image"
            );
        }

        return destination;
    }


    private String getFileExtension(
            Uri uri
    ) {

        String mimeType =
                getContentResolver()
                        .getType(
                                uri
                        );

        String extension =
                MimeTypeMap
                        .getSingleton()
                        .getExtensionFromMimeType(
                                mimeType
                        );

        return TextUtils.isEmpty(
                extension
        )
                ? "jpg"
                : extension;
    }


    private File createImageFile()
            throws IOException {

        File directory =
                getExternalFilesDir(
                        Environment.DIRECTORY_PICTURES
                );

        if (directory == null) {

            throw new IOException(
                    "Image directory unavailable"
            );
        }

        return File.createTempFile(
                "booking_device_",
                ".jpg",
                directory
        );
    }


    private boolean isValidImageFile(
            File file
    ) {

        if (file == null
                || !file.exists()
                || file.length() <= 0
                || file.length() > MAX_IMAGE_SIZE) {

            return false;
        }

        BitmapFactory.Options options =
                new BitmapFactory.Options();

        options.inJustDecodeBounds =
                true;

        BitmapFactory.decodeFile(
                file.getAbsolutePath(),
                options
        );

        return options.outWidth > 0
                && options.outHeight > 0;
    }


    private int dpToPx(
            int dp
    ) {

        return Math.round(
                dp
                        * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }


    private void confirmBooking() {

        if (!validateInputs()) {
            return;
        }

        if (selectedBranch == null) {

            Toast.makeText(
                    this,
                    "No branch has been assigned",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        int customerId =
                sessionManager.getUserId();

        if (customerId <= 0) {

            Toast.makeText(
                    this,
                    "Customer session not found. Please login again.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        int slotBookings =
                appointmentDAO
                        .getAppointmentCountForSlot(
                                etDate
                                        .getText()
                                        .toString()
                                        .trim(),
                                selectedTime
                        );

        if (slotBookings
                >= MAX_BOOKINGS_PER_SLOT) {

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
                        customerId,

                        selectedService
                                .getServiceId(),
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

        if (!TextUtils.isEmpty(
                selectedDeviceImageUri
        )) {

            appointment.setImageUri(
                    selectedDeviceImageUri
            );
        }

        long insertedId =
                appointmentDAO
                        .insertAppointmentWithParts(
                                appointment,
                                selectedParts
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