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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.techfix.app.R;
import com.techfix.app.database.RepairDAO;
import com.techfix.app.models.Repair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RepairDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_REPAIR_ID = "repair_id";

    private static final long MAX_IMAGE_SIZE =
            5L * 1024L * 1024L;

    private RepairDAO repairDAO;
    private Repair repair;

    private long repairId;

    private ImageView imgRepair;
    private Chip chipStatus;

    private TextView tvRepairId;
    private TextView tvDevice;
    private TextView tvService;
    private TextView tvProblem;
    private TextView tvTechnician;
    private TextView tvRepairDate;
    private TextView tvUpdatedAt;
    private TextView tvCostLabel;
    private TextView tvCost;
    private TextView tvProgress;

    private ProgressBar progressRepair;
    private LinearLayout layoutPhotoActions;

    private File pendingCameraFile;
    private Uri pendingCameraUri;

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.TakePicture(),
                    success -> {

                        if (success) {
                            handleCameraResult();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(
                R.layout.activity_repair_details
        );

        repairId =
                getIntent().getLongExtra(
                        EXTRA_REPAIR_ID,
                        -1
                );

        if (repairId <= 0) {

            Toast.makeText(
                    this,
                    "Invalid repair record",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        MaterialToolbar toolbar =
                findViewById(
                        R.id.toolbarRepairDetails
                );

        imgRepair =
                findViewById(
                        R.id.imgDetailRepair
                );

        chipStatus =
                findViewById(
                        R.id.chipDetailStatus
                );

        tvRepairId =
                findViewById(
                        R.id.tvDetailRepairId
                );

        tvDevice =
                findViewById(
                        R.id.tvDetailDevice
                );

        tvService =
                findViewById(
                        R.id.tvDetailService
                );

        tvProblem =
                findViewById(
                        R.id.tvDetailProblem
                );

        tvTechnician =
                findViewById(
                        R.id.tvDetailTechnician
                );

        tvRepairDate =
                findViewById(
                        R.id.tvDetailRepairDate
                );

        tvUpdatedAt =
                findViewById(
                        R.id.tvDetailUpdatedAt
                );

        tvCostLabel =
                findViewById(
                        R.id.tvDetailCostLabel
                );

        tvCost =
                findViewById(
                        R.id.tvDetailCost
                );

        tvProgress =
                findViewById(
                        R.id.tvDetailProgress
                );

        progressRepair =
                findViewById(
                        R.id.progressDetailRepair
                );

        layoutPhotoActions =
                findViewById(
                        R.id.layoutPhotoActions
                );

        Button btnCamera =
                findViewById(
                        R.id.btnDetailCamera
                );

        Button btnGallery =
                findViewById(
                        R.id.btnDetailGallery
                );

        repairDAO =
                new RepairDAO(this);

        toolbar.setNavigationOnClickListener(
                view -> finish()
        );

        btnCamera.setOnClickListener(
                view -> openCamera()
        );

        btnGallery.setOnClickListener(
                view -> galleryLauncher.launch(
                        "image/*"
                )
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRepair();
    }

    private void loadRepair() {

        repair =
                repairDAO.getRepairById(
                        repairId
                );

        if (repair == null) {

            Toast.makeText(
                    this,
                    "Repair record not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        bindRepair();
    }

    private void bindRepair() {

        tvRepairId.setText(
                String.format(
                        Locale.getDefault(),
                        "R-%03d",
                        repair.getRepairId()
                )
        );

        chipStatus.setText(
                repair.getReadableStatus()
        );

        tvDevice.setText(
                safeText(
                        repair.getDeviceName(),
                        "Unknown device"
                )
        );

        tvService.setText(
                safeText(
                        repair.getServiceName(),
                        "Unknown service"
                )
        );

        tvProblem.setText(
                safeText(
                        repair.getProblemDescription(),
                        "No issue description"
                )
        );

        tvTechnician.setText(
                repair.getTechnicianId() > 0
                        ? "Technician #" + repair.getTechnicianId()
                        : "Not assigned yet"
        );

        tvRepairDate.setText(
                formatDate(
                        repair.getCreatedAt()
                )
        );

        tvUpdatedAt.setText(
                formatDateTime(
                        repair.getUpdatedAt()
                )
        );

        int progress =
                repair.getStatusProgress();

        progressRepair.setProgress(
                progress
        );

        tvProgress.setText(
                String.format(
                        Locale.getDefault(),
                        "%d%%",
                        progress
                )
        );

        if (repair.isCompleted()) {

            tvCostLabel.setText(
                    "Final cost"
            );

            tvCost.setText(
                    formatMoney(
                            repair.getFinalCost()
                    )
            );

            layoutPhotoActions.setVisibility(
                    View.GONE
            );

        } else {

            tvCostLabel.setText(
                    "Estimated cost"
            );

            tvCost.setText(
                    formatMoney(
                            repair.getEstimatedCost()
                    )
            );

            layoutPhotoActions.setVisibility(
                    View.VISIBLE
            );
        }

        showRepairImage();
    }

    private void showRepairImage() {

        imgRepair.setImageURI(null);
        imgRepair.setScaleType(
                ImageView.ScaleType.CENTER_INSIDE
        );

        imgRepair.setImageResource(
                android.R.drawable.ic_menu_camera
        );

        if (TextUtils.isEmpty(
                repair.getImageUri()
        )) {
            return;
        }

        try {

            imgRepair.setScaleType(
                    ImageView.ScaleType.CENTER_CROP
            );

            imgRepair.setImageURI(
                    Uri.parse(
                            repair.getImageUri()
                    )
            );

        } catch (Exception ignored) {

            imgRepair.setScaleType(
                    ImageView.ScaleType.CENTER_INSIDE
            );

            imgRepair.setImageResource(
                    android.R.drawable.ic_menu_camera
            );
        }
    }

    private void openCamera() {

        try {

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

            Toast.makeText(
                    this,
                    "Invalid camera image",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        boolean updated =
                repairDAO.updateRepairImageUri(
                        repairId,
                        pendingCameraUri.toString()
                );

        if (updated) {

            Toast.makeText(
                    this,
                    "Device photo saved",
                    Toast.LENGTH_SHORT
            ).show();

            loadRepair();
        }
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

            Uri savedUri =
                    copyGalleryImage(
                            sourceUri
                    );

            boolean updated =
                    repairDAO.updateRepairImageUri(
                            repairId,
                            savedUri.toString()
                    );

            if (updated) {

                Toast.makeText(
                        this,
                        "Device photo saved",
                        Toast.LENGTH_SHORT
                ).show();

                loadRepair();
            }

        } catch (IOException exception) {

            Toast.makeText(
                    this,
                    "Unable to save selected image",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private boolean isValidGalleryImage(
            Uri uri
    ) {

        String mimeType =
                getContentResolver()
                        .getType(uri);

        if (mimeType == null
                || !mimeType.startsWith("image/")) {

            return false;
        }

        long size = getContentSize(uri);

        return size <= 0
                || size <= MAX_IMAGE_SIZE;
    }

    private long getContentSize(Uri uri) {

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
                        && !cursor.isNull(index)) {

                    return cursor.getLong(index);
                }
            }

        } finally {
            cursor.close();
        }

        return -1;
    }

    // Gallery images are copied into app storage so the URI remains usable.
    private Uri copyGalleryImage(
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
                        "repair_"
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

            while ((read = input.read(buffer)) != -1) {

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

        if (!isValidImageFile(destination)) {

            destination.delete();

            throw new IOException(
                    "Invalid image"
            );
        }

        return FileProvider.getUriForFile(
                this,
                getPackageName()
                        + ".fileprovider",
                destination
        );
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

        return TextUtils.isEmpty(extension)
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
                "repair_"
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

        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(
                file.getAbsolutePath(),
                options
        );

        return options.outWidth > 0
                && options.outHeight > 0;
    }

    private String formatMoney(
            double amount
    ) {

        return String.format(
                Locale.getDefault(),
                "Rs. %,.2f",
                Math.max(0, amount)
        );
    }

    private String formatDate(
            String value
    ) {

        Date date = parseDate(value);

        if (date == null) {
            return safeText(value, "-");
        }

        return new SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
        ).format(date);
    }

    private String formatDateTime(
            String value
    ) {

        Date date = parseDate(value);

        if (date == null) {
            return safeText(value, "-");
        }

        return new SimpleDateFormat(
                "dd MMM yyyy, h:mm a",
                Locale.getDefault()
        ).format(date);
    }

    private Date parseDate(
            String value
    ) {

        if (TextUtils.isEmpty(value)) {
            return null;
        }

        String[] patterns = {
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm"
        };

        for (String pattern : patterns) {

            try {

                return new SimpleDateFormat(
                        pattern,
                        Locale.getDefault()
                ).parse(value);

            } catch (ParseException ignored) {
            }
        }

        return null;
    }

    private String safeText(
            String value,
            String fallback
    ) {

        return TextUtils.isEmpty(value)
                ? fallback
                : value.trim();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (repairDAO != null) {
            repairDAO.close();
        }
    }
}