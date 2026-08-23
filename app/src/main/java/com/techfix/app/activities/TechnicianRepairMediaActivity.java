package com.techfix.app.activities;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.techfix.app.R;
import com.techfix.app.adapters.RepairMediaAdapter;
import com.techfix.app.database.RepairDAO;
import com.techfix.app.database.RepairMediaDAO;
import com.techfix.app.models.Repair;
import com.techfix.app.models.RepairMedia;
import com.techfix.app.userauthentication.utils.SessionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TechnicianRepairMediaActivity
        extends AppCompatActivity {

    public static final String EXTRA_REPAIR_ID =
            "repair_id";

    private static final long MAX_IMAGE_SIZE =
            5L * 1024L * 1024L;


    private RepairDAO repairDAO;

    private RepairMediaDAO repairMediaDAO;


    private Repair repair;


    private long repairId;

    private int technicianId;


    private EditText edtCaption;

    private TextView tvRepairInfo;

    private RecyclerView recyclerMedia;


    private ImageView imgSelectedRepairMedia;

    private MaterialButton btnSubmitRepairMedia;


    private RepairMediaAdapter adapter;


    private File pendingCameraFile;

    private Uri pendingCameraUri;


    private String selectedImageUri;


    private final ActivityResultLauncher<Uri>
            cameraLauncher =
            registerForActivityResult(

                    new ActivityResultContracts
                            .TakePicture(),

                    success -> {

                        if (success) {

                            if (
                                    isValidImageFile(
                                            pendingCameraFile
                                    )
                            ) {

                                selectedImageUri =
                                        pendingCameraUri
                                                .toString();


                                imgSelectedRepairMedia
                                        .setImageURI(
                                                pendingCameraUri
                                        );


                                imgSelectedRepairMedia
                                        .setVisibility(
                                                android.view.View.VISIBLE
                                        );


                                btnSubmitRepairMedia
                                        .setEnabled(
                                                true
                                        );

                            } else {

                                Toast.makeText(
                                        this,
                                        "Invalid camera image",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                    }
            );


    private final ActivityResultLauncher<String>
            galleryLauncher =
            registerForActivityResult(

                    new ActivityResultContracts
                            .GetContent(),

                    uri -> {

                        if (uri == null) {

                            return;
                        }


                        if (
                                !isValidGalleryImage(
                                        uri
                                )
                        ) {

                            Toast.makeText(
                                    this,
                                    "Please select an image under 5 MB",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }


                        try {

                            Uri savedUri =
                                    copyGalleryImage(
                                            uri
                                    );


                            selectedImageUri =
                                    savedUri
                                            .toString();


                            imgSelectedRepairMedia
                                    .setImageURI(
                                            savedUri
                                    );


                            imgSelectedRepairMedia
                                    .setVisibility(
                                            android.view.View.VISIBLE
                                    );


                            btnSubmitRepairMedia
                                    .setEnabled(
                                            true
                                    );


                        } catch (
                                IOException exception
                        ) {

                            Toast.makeText(
                                    this,
                                    "Unable to save selected image",
                                    Toast.LENGTH_SHORT
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
                R.layout.activity_technician_repair_media
        );


        SessionManager sessionManager =
                new SessionManager(
                        this
                );


        if (
                !sessionManager.isLoggedIn() ||
                        !sessionManager.isTechnician()
        ) {

            Toast.makeText(
                    this,
                    "Technician login required",
                    Toast.LENGTH_SHORT
            ).show();


            finish();

            return;
        }


        technicianId =
                sessionManager
                        .getTechnicianId();


        repairId =
                getIntent()
                        .getLongExtra(
                                EXTRA_REPAIR_ID,
                                -1
                        );


        if (
                repairId <= 0 ||
                        technicianId <= 0
        ) {

            finish();

            return;
        }


        repairDAO =
                new RepairDAO(
                        this
                );


        repairMediaDAO =
                new RepairMediaDAO(
                        this
                );


        repair =
                repairDAO
                        .getRepairById(
                                repairId
                        );


        if (
                repair == null ||
                        repair.getTechnicianId()
                                != technicianId
        ) {

            Toast.makeText(
                    this,
                    "This repair is not assigned to you",
                    Toast.LENGTH_LONG
            ).show();


            finish();

            return;
        }


        bindViews();

        setupActions();


        adapter =
                new RepairMediaAdapter();


        recyclerMedia.setLayoutManager(
                new LinearLayoutManager(
                        this
                )
        );


        recyclerMedia.setAdapter(
                adapter
        );


        showRepairInfo();

        loadMedia();
    }


    private void bindViews() {

        MaterialToolbar toolbar =
                findViewById(
                        R.id.toolbarTechnicianMedia
                );


        toolbar.setNavigationOnClickListener(
                view -> finish()
        );


        tvRepairInfo =
                findViewById(
                        R.id.tvTechnicianMediaRepair
                );


        edtCaption =
                findViewById(
                        R.id.edtMediaCaption
                );


        recyclerMedia =
                findViewById(
                        R.id.recyclerTechnicianMedia
                );


        imgSelectedRepairMedia =
                findViewById(
                        R.id.imgSelectedRepairMedia
                );


        btnSubmitRepairMedia =
                findViewById(
                        R.id.btnSubmitRepairMedia
                );
    }


    private void setupActions() {

        Button btnCamera =
                findViewById(
                        R.id.btnTechnicianMediaCamera
                );


        Button btnGallery =
                findViewById(
                        R.id.btnTechnicianMediaGallery
                );


        btnCamera.setOnClickListener(
                view -> openCamera()
        );


        btnGallery.setOnClickListener(
                view ->
                        galleryLauncher.launch(
                                "image/*"
                        )
        );


        btnSubmitRepairMedia
                .setOnClickListener(
                        view ->
                                submitRepairMedia()
                );
    }


    private void showRepairInfo() {

        tvRepairInfo.setText(

                "R-" +

                        String.format(
                                "%03d",
                                repair.getRepairId()
                        ) +

                        " • " +

                        repair.getDeviceName() +

                        "\nCurrent stage: " +

                        repair.getReadableStatus()
        );
    }


    private void submitRepairMedia() {

        if (repair == null) {

            return;
        }


        if (
                TextUtils.isEmpty(
                        selectedImageUri
                )
        ) {

            Toast.makeText(
                    this,
                    "Select a photo first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        String caption =
                edtCaption
                        .getText()
                        .toString()
                        .trim();


        RepairMedia media =
                new RepairMedia();


        media.setRepairId(
                repair.getRepairId()
        );


        media.setTechnicianId(
                technicianId
        );


        media.setImageUri(
                selectedImageUri
        );


        media.setCaption(
                caption
        );


        /*
         * Technician only uploads repair media.
         */
        media.setMediaType(
                RepairMedia.TYPE_PROGRESS
        );


        media.setRepairStage(
                repair.getStatus()
        );


        /*
         * Only administrator can change this to true.
         */
        media.setSample(
                false
        );


        long result =
                repairMediaDAO
                        .insertMedia(
                                media
                        );


        if (result <= 0) {

            Toast.makeText(
                    this,
                    "Unable to submit repair photo",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        Toast.makeText(
                this,
                "Repair photo uploaded. Admin can select it as a sample.",
                Toast.LENGTH_LONG
        ).show();


        selectedImageUri =
                null;


        imgSelectedRepairMedia
                .setImageURI(
                        null
                );


        imgSelectedRepairMedia
                .setVisibility(
                        android.view.View.GONE
                );


        edtCaption.setText(
                ""
        );


        btnSubmitRepairMedia
                .setEnabled(
                        false
                );


        loadMedia();
    }


    private void loadMedia() {

        List<RepairMedia> items =
                repairMediaDAO
                        .getMediaForRepair(
                                repairId
                        );


        adapter.setItems(
                items
        );
    }


    private void openCamera() {

        try {

            pendingCameraFile =
                    createImageFile();


            pendingCameraUri =
                    FileProvider.getUriForFile(

                            this,

                            getPackageName() +
                                    ".fileprovider",

                            pendingCameraFile
                    );


            cameraLauncher.launch(
                    pendingCameraUri
            );


        } catch (
                IOException exception
        ) {

            Toast.makeText(
                    this,
                    "Unable to create image file",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    private File createImageFile()
            throws IOException {

        File pictures =
                getExternalFilesDir(
                        Environment.DIRECTORY_PICTURES
                );


        if (pictures == null) {

            throw new IOException(
                    "Pictures directory unavailable"
            );
        }


        File folder =
                new File(
                        pictures,
                        "repair_media"
                );


        if (
                !folder.exists() &&
                        !folder.mkdirs()
        ) {

            throw new IOException(
                    "Unable to create repair media directory"
            );
        }


        return File.createTempFile(
                "techfix_media_",
                ".jpg",
                folder
        );
    }


    private boolean isValidImageFile(
            File file
    ) {

        if (
                file == null ||
                        !file.exists() ||
                        file.length() <= 0 ||
                        file.length() > MAX_IMAGE_SIZE
        ) {

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


        return options.outWidth > 0 &&
                options.outHeight > 0;
    }


    private boolean isValidGalleryImage(
            Uri uri
    ) {

        String mime =
                getContentResolver()
                        .getType(
                                uri
                        );


        if (
                TextUtils.isEmpty(
                        mime
                ) ||
                        !mime.startsWith(
                                "image/"
                        )
        ) {

            return false;
        }


        long size =
                getContentSize(
                        uri
                );


        return size <= 0 ||
                size <= MAX_IMAGE_SIZE;
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


            if (
                    cursor != null &&
                            cursor.moveToFirst()
            ) {

                int index =
                        cursor.getColumnIndex(
                                OpenableColumns.SIZE
                        );


                if (
                        index >= 0 &&
                                !cursor.isNull(
                                        index
                                )
                ) {

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


    private Uri copyGalleryImage(
            Uri sourceUri
    ) throws IOException {

        File pictures =
                getExternalFilesDir(
                        Environment.DIRECTORY_PICTURES
                );


        if (pictures == null) {

            throw new IOException();
        }


        File folder =
                new File(
                        pictures,
                        "repair_media"
                );


        if (
                !folder.exists() &&
                        !folder.mkdirs()
        ) {

            throw new IOException();
        }


        String extension =
                getImageExtension(
                        sourceUri
                );


        File destination =
                new File(

                        folder,

                        "techfix_media_" +

                                System.currentTimeMillis() +

                                "." +

                                extension
                );


        try (

                InputStream inputStream =
                        getContentResolver()
                                .openInputStream(
                                        sourceUri
                                );

                FileOutputStream outputStream =
                        new FileOutputStream(
                                destination
                        )

        ) {


            if (inputStream == null) {

                throw new IOException();
            }


            byte[] buffer =
                    new byte[8192];


            long total =
                    0;


            int read;


            while (
                    (
                            read =
                                    inputStream.read(
                                            buffer
                                    )
                    ) != -1
            ) {

                total +=
                        read;


                if (
                        total >
                                MAX_IMAGE_SIZE
                ) {

                    destination.delete();


                    throw new IOException(
                            "Image is too large"
                    );
                }


                outputStream.write(
                        buffer,
                        0,
                        read
                );
            }
        }


        if (
                !isValidImageFile(
                        destination
                )
        ) {

            destination.delete();

            throw new IOException();
        }


        return FileProvider.getUriForFile(

                this,

                getPackageName() +
                        ".fileprovider",

                destination
        );
    }


    private String getImageExtension(
            Uri uri
    ) {

        String mime =
                getContentResolver()
                        .getType(
                                uri
                        );


        String extension =
                MimeTypeMap
                        .getSingleton()
                        .getExtensionFromMimeType(
                                mime
                        );


        return TextUtils.isEmpty(
                extension
        )
                ? "jpg"
                : extension;
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();


        if (repairDAO != null) {

            repairDAO.close();
        }


        if (repairMediaDAO != null) {

            repairMediaDAO.close();
        }
    }
}