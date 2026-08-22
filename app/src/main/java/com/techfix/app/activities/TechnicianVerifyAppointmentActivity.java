package com.techfix.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;
import com.techfix.app.database.AppointmentDAO;
import com.techfix.app.database.RepairDAO;
import com.techfix.app.database.ServiceDAO;
import com.techfix.app.models.Appointment;
import com.techfix.app.models.Repair;
import com.techfix.app.models.RepairService;

public class TechnicianVerifyAppointmentActivity
        extends AppCompatActivity {

    private EditText edtAppointmentCode;

    private Button btnVerifyCode;
    private Button btnAcceptRepair;

    private LinearLayout layoutAppointmentDetails;

    private TextView txtAppointmentCustomer;
    private TextView txtAppointmentDevice;
    private TextView txtAppointmentService;
    private TextView txtAppointmentIssue;
    private TextView txtAppointmentDateTime;

    private AppointmentDAO appointmentDAO;
    private RepairDAO repairDAO;
    private ServiceDAO serviceDAO;

    private Appointment verifiedAppointment;
    private RepairService verifiedService;

    private int technicianId;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_technician_verify_appointment
        );

        technicianId =
                getIntent().getIntExtra(
                        TechnicianDashboardActivity.EXTRA_TECHNICIAN_ID,
                        1
                );

        appointmentDAO =
                new AppointmentDAO(this);

        repairDAO =
                new RepairDAO(this);

        serviceDAO =
                new ServiceDAO(this);

        edtAppointmentCode =
                findViewById(
                        R.id.edtAppointmentCode
                );

        btnVerifyCode =
                findViewById(
                        R.id.btnVerifyCode
                );

        btnAcceptRepair =
                findViewById(
                        R.id.btnAcceptRepair
                );

        layoutAppointmentDetails =
                findViewById(
                        R.id.layoutAppointmentDetails
                );

        txtAppointmentCustomer =
                findViewById(
                        R.id.txtAppointmentCustomer
                );

        txtAppointmentDevice =
                findViewById(
                        R.id.txtAppointmentDevice
                );

        txtAppointmentService =
                findViewById(
                        R.id.txtAppointmentService
                );

        txtAppointmentIssue =
                findViewById(
                        R.id.txtAppointmentIssue
                );

        txtAppointmentDateTime =
                findViewById(
                        R.id.txtAppointmentDateTime
                );

        btnVerifyCode.setOnClickListener(
                view -> verifyAppointment()
        );

        btnAcceptRepair.setOnClickListener(
                view -> acceptAsRepair()
        );
    }

    private void verifyAppointment() {

        String code =
                edtAppointmentCode
                        .getText()
                        .toString()
                        .trim();

        if (code.isEmpty()) {

            edtAppointmentCode.setError(
                    "Appointment code is required"
            );

            return;
        }

        verifiedAppointment =
                appointmentDAO.getAppointmentByCode(
                        code
                );

        if (verifiedAppointment == null) {

            layoutAppointmentDetails.setVisibility(
                    View.GONE
            );

            Toast.makeText(
                    this,
                    "Invalid appointment code",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Repair existingRepair =
                repairDAO.getRepairByAppointmentId(
                        verifiedAppointment.getAppointmentId()
                );

        if (existingRepair != null) {

            layoutAppointmentDetails.setVisibility(
                    View.GONE
            );

            Toast.makeText(
                    this,
                    "This appointment has already been accepted as a repair",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        verifiedService =
                serviceDAO.getServiceById(
                        verifiedAppointment.getServiceId()
                );

        txtAppointmentCustomer.setText(
                "Customer ID: " +
                        verifiedAppointment.getUserId()
        );

        txtAppointmentDevice.setText(
                "Device: " +
                        verifiedAppointment.getDeviceModel()
        );

        txtAppointmentService.setText(
                "Service: " +
                        (
                                verifiedService != null
                                        ?
                                        verifiedService.getServiceName()
                                        :
                                        "Unknown"
                        )
        );

        txtAppointmentIssue.setText(
                "Issue: " +
                        verifiedAppointment.getIssueDescription()
        );

        txtAppointmentDateTime.setText(
                "Appointment: " +
                        verifiedAppointment.getAppointmentDate() +
                        " " +
                        verifiedAppointment.getAppointmentTime()
        );

        layoutAppointmentDetails.setVisibility(
                View.VISIBLE
        );
    }

    private void acceptAsRepair() {

        if (verifiedAppointment == null) {

            Toast.makeText(
                    this,
                    "Verify an appointment first",
                    Toast.LENGTH_SHORT
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

        if (verifiedService != null) {

            repair.setServiceName(
                    verifiedService.getServiceName()
            );

            repair.setEstimatedCost(
                    verifiedService.getPrice()
            );

        } else {

            repair.setServiceName(
                    "Repair Service"
            );

            repair.setEstimatedCost(
                    0.0
            );
        }

        repair.setFinalCost(
                0.0
        );

        repair.setStatus(
                Repair.STATUS_PENDING
        );

        long repairId =
                repairDAO.insertRepair(
                        repair
                );

        if (repairId > 0) {

            appointmentDAO.markAppointmentAsAccepted(
                    verifiedAppointment.getAppointmentId()
            );

            Toast.makeText(
                    this,
                    "Repair created successfully",
                    Toast.LENGTH_LONG
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Unable to create repair",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}