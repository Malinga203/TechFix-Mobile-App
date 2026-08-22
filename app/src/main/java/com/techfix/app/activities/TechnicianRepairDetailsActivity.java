package com.techfix.app.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;
import com.techfix.app.database.RepairDAO;
import com.techfix.app.models.Repair;

import java.util.Locale;

public class TechnicianRepairDetailsActivity
        extends AppCompatActivity {

    public static final String EXTRA_REPAIR_ID =
            "extra_repair_id";

    private TextView txtRepairTitle;
    private TextView txtRepairDevice;
    private TextView txtRepairService;
    private TextView txtRepairIssue;
    private TextView txtRepairCurrentStatus;

    private EditText edtEstimatedCost;
    private EditText edtFinalCost;

    private Button btnSaveRepairCosts;
    private Button btnNextRepairStatus;

    private RepairDAO repairDAO;

    private Repair repair;

    private long repairId;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_technician_repair_details
        );

        repairId =
                getIntent().getLongExtra(
                        EXTRA_REPAIR_ID,
                        -1
                );

        repairDAO =
                new RepairDAO(this);

        txtRepairTitle =
                findViewById(
                        R.id.txtRepairTitle
                );

        txtRepairDevice =
                findViewById(
                        R.id.txtRepairDevice
                );

        txtRepairService =
                findViewById(
                        R.id.txtRepairService
                );

        txtRepairIssue =
                findViewById(
                        R.id.txtRepairIssue
                );

        txtRepairCurrentStatus =
                findViewById(
                        R.id.txtRepairCurrentStatus
                );

        edtEstimatedCost =
                findViewById(
                        R.id.edtEstimatedCost
                );

        edtFinalCost =
                findViewById(
                        R.id.edtFinalCost
                );

        btnSaveRepairCosts =
                findViewById(
                        R.id.btnSaveRepairCosts
                );

        btnNextRepairStatus =
                findViewById(
                        R.id.btnNextRepairStatus
                );

        btnSaveRepairCosts.setOnClickListener(
                view -> saveCosts()
        );

        btnNextRepairStatus.setOnClickListener(
                view -> moveToNextStatus()
        );

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
                    "Repair not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }

        txtRepairTitle.setText(
                String.format(
                        Locale.US,
                        "Repair #%d",
                        repair.getRepairId()
                )
        );

        txtRepairDevice.setText(
                "Device: " +
                        repair.getDeviceName()
        );

        txtRepairService.setText(
                "Service: " +
                        repair.getServiceName()
        );

        txtRepairIssue.setText(
                "Issue: " +
                        repair.getProblemDescription()
        );

        txtRepairCurrentStatus.setText(
                "Status: " +
                        repair.getReadableStatus()
        );

        edtEstimatedCost.setText(
                String.valueOf(
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

        updateStatusButton();
    }

    private void saveCosts() {

        double estimatedCost;
        double finalCost;

        try {

            estimatedCost =
                    Double.parseDouble(
                            edtEstimatedCost
                                    .getText()
                                    .toString()
                                    .trim()
                    );

        } catch (Exception exception) {

            estimatedCost = 0.0;
        }

        try {

            String finalValue =
                    edtFinalCost
                            .getText()
                            .toString()
                            .trim();

            finalCost =
                    finalValue.isEmpty()
                            ?
                            0.0
                            :
                            Double.parseDouble(
                                    finalValue
                            );

        } catch (Exception exception) {

            finalCost = 0.0;
        }

        boolean updated =
                repairDAO.updateRepairCosts(
                        repairId,
                        estimatedCost,
                        finalCost
                );

        if (updated) {

            Toast.makeText(
                    this,
                    "Repair cost updated",
                    Toast.LENGTH_SHORT
            ).show();

            loadRepair();

        } else {

            Toast.makeText(
                    this,
                    "Unable to update cost",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void moveToNextStatus() {

        if (repair == null) {
            return;
        }

        String nextStatus;

        switch (repair.getStatus()) {

            case Repair.STATUS_PENDING:

                nextStatus =
                        Repair.STATUS_DIAGNOSING;

                break;

            case Repair.STATUS_DIAGNOSING:

                nextStatus =
                        Repair.STATUS_REPAIRING;

                break;

            case Repair.STATUS_REPAIRING:

                nextStatus =
                        Repair.STATUS_READY_FOR_COLLECTION;

                break;

            case Repair.STATUS_READY_FOR_COLLECTION:

                if (repair.getFinalCost() <= 0) {

                    Toast.makeText(
                            this,
                            "Set the final repair cost before completing the repair",
                            Toast.LENGTH_LONG
                    ).show();

                    return;
                }

                nextStatus =
                        Repair.STATUS_COMPLETED;

                break;

            default:

                Toast.makeText(
                        this,
                        "Repair is already completed",
                        Toast.LENGTH_SHORT
                ).show();

                return;
        }

        boolean updated =
                repairDAO.updateRepairStatus(
                        repairId,
                        nextStatus
                );

        if (updated) {

            Toast.makeText(
                    this,
                    "Repair status updated",
                    Toast.LENGTH_SHORT
            ).show();

            loadRepair();

        } else {

            Toast.makeText(
                    this,
                    "Unable to update repair status",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void updateStatusButton() {

        switch (repair.getStatus()) {

            case Repair.STATUS_PENDING:

                btnNextRepairStatus.setText(
                        "Start Diagnosis"
                );

                break;

            case Repair.STATUS_DIAGNOSING:

                btnNextRepairStatus.setText(
                        "Start Repair"
                );

                break;

            case Repair.STATUS_REPAIRING:

                btnNextRepairStatus.setText(
                        "Mark Ready for Collection"
                );

                break;

            case Repair.STATUS_READY_FOR_COLLECTION:

                btnNextRepairStatus.setText(
                        "Complete Repair"
                );

                break;

            case Repair.STATUS_COMPLETED:

                btnNextRepairStatus.setText(
                        "Repair Completed"
                );

                btnNextRepairStatus.setEnabled(
                        false
                );

                break;
        }
    }
}