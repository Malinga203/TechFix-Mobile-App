package com.techfix.app.activities;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.techfix.app.R;
import com.techfix.app.database.AppointmentDAO;
import com.techfix.app.database.AppointmentSparePartDAO;
import com.techfix.app.database.BranchDao;
import com.techfix.app.database.RepairDAO;
import com.techfix.app.database.RepairSparePartDAO;
import com.techfix.app.database.ServiceDAO;
import com.techfix.app.models.Appointment;
import com.techfix.app.models.AppointmentSparePart;
import com.techfix.app.models.Branch;
import com.techfix.app.models.Repair;
import com.techfix.app.models.RepairService;
import com.techfix.app.userauthentication.utils.SessionManager;

import java.util.List;
import java.util.Locale;

public class TechnicianVerifyAppointmentActivity
        extends AppCompatActivity {

    private EditText edtAppointmentCode;

    private Button btnVerifyAppointmentCode;
    private Button btnAcceptAsRepair;

    private LinearLayout layoutAppointmentDetails;

    private TextView txtVerificationStatus;
    private TextView txtVerifiedCode;
    private TextView txtVerifiedCustomer;
    private TextView txtVerifiedDevice;
    private TextView txtVerifiedService;
    private TextView txtVerifiedParts;
    private TextView txtVerifiedIssue;
    private TextView txtVerifiedDate;
    private TextView txtVerifiedBranch;

    private TextView txtCustomerDevicePhotoLabel;
    private ImageView imgVerifiedDevicePhoto;
    private Button btnViewVerifiedDevicePhoto;

    private AppointmentDAO appointmentDAO;
    private AppointmentSparePartDAO appointmentSparePartDAO;
    private RepairDAO repairDAO;
    private RepairSparePartDAO repairSparePartDAO;
    private ServiceDAO serviceDAO;
    private BranchDao branchDao;

    private SessionManager sessionManager;

    private Appointment verifiedAppointment;
    private RepairService verifiedService;
    private Branch verifiedBranch;

    private int technicianId;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_technician_verify_appointment
        );

        sessionManager =
                new SessionManager(this);

        technicianId =
                sessionManager.getTechnicianId();

        if (technicianId <= 0) {

            technicianId =
                    getIntent().getIntExtra(
                            TechnicianDashboardActivity.EXTRA_TECHNICIAN_ID,
                            -1
                    );
        }

        if (technicianId <= 0) {

            Toast.makeText(
                    this,
                    "Technician session not found",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }

        appointmentDAO =
                new AppointmentDAO(this);

        appointmentSparePartDAO =
                new AppointmentSparePartDAO(this);

        repairDAO =
                new RepairDAO(this);

        repairSparePartDAO =
                new RepairSparePartDAO(this);

        serviceDAO =
                new ServiceDAO(this);

        branchDao =
                new BranchDao(this);

        bindViews();

        btnVerifyAppointmentCode.setOnClickListener(
                view -> verifyAppointment()
        );

        btnAcceptAsRepair.setOnClickListener(
                view -> acceptAppointmentAsRepair()
        );
    }

    private void bindViews() {

        edtAppointmentCode =
                findViewById(
                        R.id.edtAppointmentCode
                );

        btnVerifyAppointmentCode =
                findViewById(
                        R.id.btnVerifyAppointmentCode
                );

        btnAcceptAsRepair =
                findViewById(
                        R.id.btnAcceptAsRepair
                );

        layoutAppointmentDetails =
                findViewById(
                        R.id.layoutAppointmentDetails
                );

        txtVerificationStatus =
                findViewById(
                        R.id.txtVerificationStatus
                );

        txtVerifiedCode =
                findViewById(
                        R.id.txtVerifiedCode
                );

        txtVerifiedCustomer =
                findViewById(
                        R.id.txtVerifiedCustomer
                );

        txtVerifiedDevice =
                findViewById(
                        R.id.txtVerifiedDevice
                );

        txtVerifiedService =
                findViewById(
                        R.id.txtVerifiedService
                );

        txtVerifiedParts =
                findViewById(
                        R.id.txtVerifiedParts
                );

        txtVerifiedIssue =
                findViewById(
                        R.id.txtVerifiedIssue
                );

        txtVerifiedDate =
                findViewById(
                        R.id.txtVerifiedDate
                );

        txtVerifiedBranch =
                findViewById(
                        R.id.txtVerifiedBranch
                );

        txtCustomerDevicePhotoLabel =
                findViewById(
                        R.id.txtCustomerDevicePhotoLabel
                );

        imgVerifiedDevicePhoto =
                findViewById(
                        R.id.imgVerifiedDevicePhoto
                );

        btnViewVerifiedDevicePhoto =
                findViewById(
                        R.id.btnViewVerifiedDevicePhoto
                );
    }

    private void verifyAppointment() {

        layoutAppointmentDetails.setVisibility(
                View.GONE
        );

        verifiedAppointment = null;
        verifiedService = null;
        verifiedBranch = null;

        String code =
                edtAppointmentCode
                        .getText()
                        .toString()
                        .trim()
                        .toUpperCase(
                                Locale.US
                        );

        if (code.isEmpty()) {

            edtAppointmentCode.setError(
                    "Enter appointment code"
            );

            return;
        }

        Appointment appointment =
                appointmentDAO.getAppointmentByCode(
                        code
                );

        if (appointment == null) {

            Toast.makeText(
                    this,
                    "Invalid appointment code",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Repair existingRepair =
                repairDAO.getRepairByAppointmentId(
                        appointment.getAppointmentId()
                );

        if (existingRepair != null) {

            Toast.makeText(
                    this,
                    "This appointment has already been accepted as a repair",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        if (
                "ACCEPTED".equalsIgnoreCase(
                        appointment.getStatus()
                )
        ) {

            Toast.makeText(
                    this,
                    "This appointment has already been accepted",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        verifiedAppointment =
                appointment;

        verifiedService =
                serviceDAO.getServiceById(
                        appointment.getServiceId()
                );

        verifiedBranch =
                branchDao.getBranchById(
                        appointment.getBranchId()
                );

        displayAppointment();
    }

    private void displayAppointment() {

        txtVerificationStatus.setText(
                "Valid Appointment"
        );

        txtVerifiedCode.setText(
                "Code: " +
                        verifiedAppointment.getAppointmentCode()
        );

        txtVerifiedCustomer.setText(
                "Customer ID: " +
                        verifiedAppointment.getUserId()
        );

        txtVerifiedDevice.setText(
                "Device: " +
                        verifiedAppointment.getDeviceModel()
        );

        txtVerifiedService.setText(
                "Service: " +
                        (
                                verifiedService != null
                                        ?
                                        verifiedService.getServiceName()
                                        :
                                        "Unknown Service"
                        )
        );

        List<AppointmentSparePart> selectedParts =
                appointmentSparePartDAO
                        .getPartsForAppointment(
                                verifiedAppointment.getAppointmentId()
                        );

        if (selectedParts.isEmpty()) {

            txtVerifiedParts.setText(
                    "Spare Parts: None selected"
            );

        } else {

            StringBuilder partsText =
                    new StringBuilder(
                            "Spare Parts:\n"
                    );

            for (
                    AppointmentSparePart item
                    :
                    selectedParts
            ) {

                partsText.append(
                        "• "
                );

                partsText.append(
                        item.getPartName()
                );

                partsText.append(
                        " × "
                );

                partsText.append(
                        item.getQuantity()
                );

                partsText.append(
                        "\n"
                );
            }

            txtVerifiedParts.setText(
                    partsText
                            .toString()
                            .trim()
            );
        }

        txtVerifiedIssue.setText(
                "Issue: " +
                        verifiedAppointment.getIssueDescription()
        );

        txtVerifiedDate.setText(
                "Appointment: " +
                        verifiedAppointment.getAppointmentDate() +
                        " at " +
                        verifiedAppointment.getAppointmentTime()
        );

        txtVerifiedBranch.setText(
                "Branch: " +
                        (
                                verifiedBranch != null
                                        ?
                                        verifiedBranch.getBranchName()
                                        :
                                        "Branch #" +
                                                verifiedAppointment.getBranchId()
                        )
        );

        String customerImageUri =
                verifiedAppointment.getImageUri();

        if (!TextUtils.isEmpty(customerImageUri)) {

            txtCustomerDevicePhotoLabel.setVisibility(
                    View.VISIBLE
            );

            imgVerifiedDevicePhoto.setVisibility(
                    View.VISIBLE
            );

            btnViewVerifiedDevicePhoto.setVisibility(
                    View.VISIBLE
            );

            try {

                imgVerifiedDevicePhoto.setImageURI(
                        Uri.parse(customerImageUri)
                );

            } catch (Exception ignored) {

                imgVerifiedDevicePhoto.setImageResource(
                        android.R.drawable.ic_menu_camera
                );
            }

            btnViewVerifiedDevicePhoto.setOnClickListener(
                    view -> showDevicePhoto(
                            customerImageUri
                    )
            );

            imgVerifiedDevicePhoto.setOnClickListener(
                    view -> showDevicePhoto(
                            customerImageUri
                    )
            );

        } else {

            txtCustomerDevicePhotoLabel.setVisibility(
                    View.GONE
            );

            imgVerifiedDevicePhoto.setVisibility(
                    View.GONE
            );

            btnViewVerifiedDevicePhoto.setVisibility(
                    View.GONE
            );
        }

        layoutAppointmentDetails.setVisibility(
                View.VISIBLE
        );
    }

    private void acceptAppointmentAsRepair() {

        if (verifiedAppointment == null) {

            Toast.makeText(
                    this,
                    "Verify an appointment first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Repair existingRepair =
                repairDAO.getRepairByAppointmentId(
                        verifiedAppointment.getAppointmentId()
                );

        if (existingRepair != null) {

            Toast.makeText(
                    this,
                    "Repair already exists",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        Repair repair =
                new Repair();

        repair.setAppointmentId(
                verifiedAppointment.getAppointmentId()
        );

        repair.setCustomerId(
                verifiedAppointment.getUserId()
        );

        repair.setBranchId(
                verifiedAppointment.getBranchId()
        );

        repair.setTechnicianId(
                technicianId
        );

        repair.setDeviceName(
                verifiedAppointment.getDeviceModel()
        );

        repair.setProblemDescription(
                verifiedAppointment.getIssueDescription()
        );

        repair.setImageUri(
                verifiedAppointment.getImageUri()
        );

        repair.setStatus(
                Repair.STATUS_PENDING
        );

        repair.setFinalCost(
                0.0
        );

        if (verifiedService != null) {

            repair.setServiceName(
                    verifiedService.getServiceName()
            );

            double estimatedCost =
                    verifiedService.getPrice()
                            +
                            appointmentSparePartDAO
                                    .getPartsTotalForAppointment(
                                            verifiedAppointment.getAppointmentId()
                                    );

            repair.setEstimatedCost(
                    estimatedCost
            );

        } else {

            repair.setServiceName(
                    "Repair Service"
            );

            repair.setEstimatedCost(
                    0.0
            );
        }

        long repairId =
                repairDAO.insertRepair(
                        repair
                );

        if (repairId <= 0) {

            Toast.makeText(
                    this,
                    "Failed to create repair",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        boolean partsCopied =
                repairSparePartDAO
                        .copyAppointmentPartsToRepair(
                                verifiedAppointment.getAppointmentId(),
                                repairId
                        );

        if (!partsCopied) {

            Toast.makeText(
                    this,
                    "Repair created, but selected spare parts could not be copied",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        boolean appointmentUpdated =
                appointmentDAO
                        .markAppointmentAsAccepted(
                                verifiedAppointment
                                        .getAppointmentId()
                        );

        if (!appointmentUpdated) {

            Toast.makeText(
                    this,
                    "Repair created, but appointment status could not be updated",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        Toast.makeText(
                this,
                "Appointment accepted. Repair #" +
                        repairId +
                        " created.",
                Toast.LENGTH_LONG
        ).show();

        finish();
    }

    private void showDevicePhoto(
            String imageUri
    ) {

        if (TextUtils.isEmpty(imageUri)) {
            return;
        }

        ImageView imageView =
                new ImageView(this);

        int padding =
                (int) (
                        16 *
                                getResources()
                                        .getDisplayMetrics()
                                        .density
                );

        imageView.setPadding(
                padding,
                padding,
                padding,
                padding
        );

        imageView.setAdjustViewBounds(
                true
        );

        imageView.setScaleType(
                ImageView.ScaleType.FIT_CENTER
        );

        imageView.setMinimumHeight(
                500
        );

        imageView.setImageURI(
                Uri.parse(imageUri)
        );

        new MaterialAlertDialogBuilder(this)
                .setTitle(
                        "Customer Device Photo"
                )
                .setView(
                        imageView
                )
                .setPositiveButton(
                        "Close",
                        null
                )
                .show();
    }

}