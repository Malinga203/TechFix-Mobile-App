package com.techfix.app.activities;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.card.MaterialCardView;
import com.techfix.app.R;
import com.techfix.app.database.RepairDAO;
import com.techfix.app.database.RepairMediaDAO;
import com.techfix.app.models.Repair;
import com.techfix.app.models.RepairMedia;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TechnicianRepairDetailsActivity
        extends AppCompatActivity {

    public static final String EXTRA_REPAIR_ID =
            "extra_repair_id";

    private static final long MAX_IMAGE_SIZE =
            5L * 1024L * 1024L;

    private TextView txtRepairId;
    private TextView txtRepairDevice;
    private TextView txtRepairService;
    private TextView txtRepairProblem;
    private TextView txtCurrentStatus;
    private TextView txtEstimatedCost;

    private Spinner spinnerRepairStatus;

    private EditText edtFinalCost;

    private Button btnUpdateRepair;
    private Button btnCamera;
    private Button btnGallery;
    private Button btnRemovePhoto;

    private ImageView imgProgressPreview;
    private MaterialCardView cardProgressPreview;

    private RepairDAO repairDAO;
    private RepairMediaDAO repairMediaDAO;

    private Repair repair;

    private long repairId;

    private File pendingCameraFile;
    private Uri pendingCameraUri;

    private File selectedProgressFile;
    private String selectedProgressImageUri;


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


    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {

                        if (uri != null) {
                            handleGalleryResult(uri);
                        }
                    }
            );


    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_technician_repair_details
        );

        repairDAO =
                new RepairDAO(this);

        repairMediaDAO =
                new RepairMediaDAO(this);

        repairId =
                getIntent().getLongExtra(
                        EXTRA_REPAIR_ID,
                        -1
                );

        if (repairId <= 0) {

            Toast.makeText(
                    this,
                    "Invalid repair",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        bindViews();

        setupStatusSpinner();

        loadRepair();

        btnCamera.setOnClickListener(
                view -> openCamera()
        );

        btnGallery.setOnClickListener(
                view -> galleryLauncher.launch(
                        "image/*"
                )
        );

        btnRemovePhoto.setOnClickListener(
                view -> clearSelectedProgressPhoto(
                        true
                )
        );

        btnUpdateRepair.setOnClickListener(
                view -> updateRepair()
        );
    }


    private void bindViews() {

        txtRepairId =
                findViewById(
                        R.id.txtTechnicianRepairId
                );

        txtRepairDevice =
                findViewById(
                        R.id.txtTechnicianRepairDevice
                );

        txtRepairService =
                findViewById(
                        R.id.txtTechnicianRepairService
                );

        txtRepairProblem =
                findViewById(
                        R.id.txtTechnicianRepairProblem
                );

        txtCurrentStatus =
                findViewById(
                        R.id.txtTechnicianRepairCurrentStatus
                );

        txtEstimatedCost =
                findViewById(
                        R.id.txtTechnicianRepairEstimatedCost
                );

        spinnerRepairStatus =
                findViewById(
                        R.id.spinnerTechnicianRepairStatus
                );

        edtFinalCost =
                findViewById(
                        R.id.edtTechnicianFinalCost
                );

        btnUpdateRepair =
                findViewById(
                        R.id.btnUpdateTechnicianRepair
                );

        btnCamera =
                findViewById(
                        R.id.btnTechnicianProgressCamera
                );

        btnGallery =
                findViewById(
                        R.id.btnTechnicianProgressGallery
                );

        btnRemovePhoto =
                findViewById(
                        R.id.btnRemoveTechnicianProgressPhoto
                );

        imgProgressPreview =
                findViewById(
                        R.id.imgTechnicianProgressPreview
                );

        cardProgressPreview =
                findViewById(
                        R.id.cardTechnicianProgressPreview
                );
    }


    private void setupStatusSpinner() {

        List<String> statuses =
                Arrays.asList(
                        Repair.STATUS_PENDING,
                        Repair.STATUS_DIAGNOSING,
                        Repair.STATUS_REPAIRING,
                        Repair.STATUS_READY_FOR_COLLECTION
                );

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        statuses
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerRepairStatus.setAdapter(
                adapter
        );
    }


    private void loadRepair() {

        repair =
                repairDAO.getRepairById(
                        repairId
                );

        if (repair == null) {

            Toast.makeText(
                    this,
                    "Repair not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        txtRepairId.setText(
                "Repair #" +
                        repair.getRepairId()
        );

        txtRepairDevice.setText(
                "Device: " +
                        repair.getDeviceName()
        );

        txtRepairService.setText(
                "Service: " +
                        repair.getServiceName()
        );

        txtRepairProblem.setText(
                "Issue: " +
                        repair.getProblemDescription()
        );

        txtCurrentStatus.setText(
                "Current Status: " +
                        repair.getReadableStatus()
        );

        txtEstimatedCost.setText(
                String.format(
                        Locale.getDefault(),
                        "Estimated Cost: LKR %,.2f",
                        repair.getEstimatedCost()
                )
        );

        if (repair.getFinalCost() > 0) {

            edtFinalCost.setText(
                    String.valueOf(
                            repair.getFinalCost()
                    )
            );
        }

        selectCurrentStatus();

        boolean completed =
                Repair.STATUS_COMPLETED.equals(
                        repair.getStatus()
                );

        spinnerRepairStatus.setEnabled(
                !completed
        );

        edtFinalCost.setEnabled(
                !completed
        );

        btnCamera.setEnabled(
                !completed
        );

        btnGallery.setEnabled(
                !completed
        );

        btnUpdateRepair.setEnabled(
                !completed
        );

        if (completed) {

            btnUpdateRepair.setText(
                    "Repair Completed"
            );

        } else {

            btnUpdateRepair.setText(
                    "Update Repair"
            );
        }
    }


    private void selectCurrentStatus() {

        String status =
                repair.getStatus();

        int position;

        switch (status) {

            case Repair.STATUS_DIAGNOSING:
                position = 1;
                break;

            case Repair.STATUS_REPAIRING:
                position = 2;
                break;

            case Repair.STATUS_READY_FOR_COLLECTION:
                position = 3;
                break;

            case Repair.STATUS_PENDING:
            default:
                position = 0;
                break;
        }

        spinnerRepairStatus.setSelection(
                position
        );
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

        deletePreviouslySelectedFile(
                pendingCameraFile
        );

        selectedProgressFile =
                pendingCameraFile;

        selectedProgressImageUri =
                pendingCameraUri.toString();

        pendingCameraFile = null;
        pendingCameraUri = null;

        showSelectedProgressPhoto();
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

            File copiedFile =
                    copyGalleryImage(
                            sourceUri
                    );

            Uri savedUri =
                    FileProvider.getUriForFile(
                            this,
                            getPackageName()
                                    + ".fileprovider",
                            copiedFile
                    );

            deletePreviouslySelectedFile(
                    copiedFile
            );

            selectedProgressFile =
                    copiedFile;

            selectedProgressImageUri =
                    savedUri.toString();

            showSelectedProgressPhoto();

        } catch (IOException exception) {

            Toast.makeText(
                    this,
                    "Unable to save selected image",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    private void showSelectedProgressPhoto() {

        if (TextUtils.isEmpty(
                selectedProgressImageUri
        )) {
            return;
        }

        imgProgressPreview.setImageURI(
                null
        );

        imgProgressPreview.setImageURI(
                Uri.parse(
                        selectedProgressImageUri
                )
        );

        cardProgressPreview.setVisibility(
                View.VISIBLE
        );

        btnRemovePhoto.setVisibility(
                View.VISIBLE
        );
    }


    private void clearSelectedProgressPhoto(
            boolean deleteFile
    ) {

        if (deleteFile
                && selectedProgressFile != null
                && selectedProgressFile.exists()) {

            selectedProgressFile.delete();
        }

        selectedProgressFile = null;
        selectedProgressImageUri = null;

        imgProgressPreview.setImageURI(
                null
        );

        cardProgressPreview.setVisibility(
                View.GONE
        );

        btnRemovePhoto.setVisibility(
                View.GONE
        );
    }


    private void updateRepair() {

        if (repair == null) {
            return;
        }

        String selectedStatus =
                spinnerRepairStatus
                        .getSelectedItem()
                        .toString();

        String finalCostText =
                edtFinalCost
                        .getText()
                        .toString()
                        .trim();

        double finalCost =
                repair.getFinalCost();

        if (!finalCostText.isEmpty()) {

            try {

                finalCost =
                        Double.parseDouble(
                                finalCostText
                        );

            } catch (NumberFormatException exception) {

                edtFinalCost.setError(
                        "Enter a valid amount"
                );

                return;
            }

            if (finalCost < 0) {

                edtFinalCost.setError(
                        "Final cost cannot be negative"
                );

                return;
            }
        }

        if (Repair.STATUS_READY_FOR_COLLECTION.equals(
                selectedStatus
        )) {

            if (finalCost <= 0) {

                edtFinalCost.setError(
                        "Enter the final cost before marking the repair ready for collection"
                );

                edtFinalCost.requestFocus();

                return;
            }
        }

        if (!Repair.canTransition(
                repair.getStatus(),
                selectedStatus
        )) {

            Toast.makeText(
                    this,
                    "You cannot move the repair back to a previous status",
                    Toast.LENGTH_LONG
            ).show();

            selectCurrentStatus();

            return;
        }

        boolean costUpdated =
                repairDAO.updateRepairCosts(
                        repairId,
                        repair.getEstimatedCost(),
                        finalCost
                );

        if (!costUpdated) {

            Toast.makeText(
                    this,
                    "Unable to update repair cost",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        boolean statusUpdated =
                repairDAO.updateRepairStatus(
                        repairId,
                        selectedStatus
                );

        if (!statusUpdated) {

            Toast.makeText(
                    this,
                    "Unable to update repair status",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        boolean progressPhotoSaved =
                true;

        if (!TextUtils.isEmpty(
                selectedProgressImageUri
        )) {

            RepairMedia media =
                    new RepairMedia();

            media.setRepairId(
                    repairId
            );

            media.setTechnicianId(
                    repair.getTechnicianId()
            );

            media.setImageUri(
                    selectedProgressImageUri
            );

            media.setCaption(
                    "Repair progress update"
            );

            media.setMediaType(
                    RepairMedia.TYPE_PROGRESS
            );

            media.setRepairStage(
                    selectedStatus
            );

            long mediaId =
                    repairMediaDAO.insertMedia(
                            media
                    );

            progressPhotoSaved =
                    mediaId > 0;

            if (progressPhotoSaved) {

                // The file is now part of the saved repair update.
                selectedProgressFile = null;
                selectedProgressImageUri = null;

                imgProgressPreview.setImageURI(
                        null
                );

                cardProgressPreview.setVisibility(
                        View.GONE
                );

                btnRemovePhoto.setVisibility(
                        View.GONE
                );
            }
        }

        if (!progressPhotoSaved) {

            Toast.makeText(
                    this,
                    "Repair status updated, but the progress photo could not be saved",
                    Toast.LENGTH_LONG
            ).show();

            loadRepair();
            return;
        }

        if (Repair.STATUS_READY_FOR_COLLECTION.equals(
                selectedStatus
        )) {

            Toast.makeText(
                    this,
                    "Repair is ready for collection. Customer can now make payment.",
                    Toast.LENGTH_LONG
            ).show();

        } else if (!TextUtils.isEmpty(
                selectedProgressImageUri
        )) {

            Toast.makeText(
                    this,
                    "Repair and progress photo updated",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            Toast.makeText(
                    this,
                    "Repair updated successfully",
                    Toast.LENGTH_SHORT
            ).show();
        }

        loadRepair();
    }


    private boolean isValidGalleryImage(
            Uri uri
    ) {

        String mimeType =
                getContentResolver()
                        .getType(uri);

        if (mimeType == null
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

        if (cursor == null) {
            return -1;
        }

        try {

            if (cursor.moveToFirst()) {

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

            cursor.close();
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
                        "progress_"
                                + repairId
                                + "_"
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

            long total = 0;
            int read;

            while ((read = input.read(
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
                        .getType(uri);

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
                "progress_"
                        + repairId
                        + "_",
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


    private void deletePreviouslySelectedFile(
            File newFile
    ) {

        if (selectedProgressFile == null
                || selectedProgressFile.equals(
                newFile
        )
                || !selectedProgressFile.exists()) {

            return;
        }

        selectedProgressFile.delete();
    }


    private void deletePendingCameraFile() {

        if (pendingCameraFile != null
                && pendingCameraFile.exists()) {

            pendingCameraFile.delete();
        }

        pendingCameraFile = null;
        pendingCameraUri = null;
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();

        deletePendingCameraFile();

        if (repairDAO != null) {
            repairDAO.close();
        }

        if (repairMediaDAO != null) {
            repairMediaDAO.close();
        }
    }
}