package com.techfix.app.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;
import com.techfix.app.database.RepairDAO;
import com.techfix.app.models.Repair;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TechnicianRepairDetailsActivity
        extends AppCompatActivity {

    public static final String EXTRA_REPAIR_ID =
            "extra_repair_id";

    private TextView txtRepairId;
    private TextView txtRepairDevice;
    private TextView txtRepairService;
    private TextView txtRepairProblem;
    private TextView txtCurrentStatus;
    private TextView txtEstimatedCost;

    private Spinner spinnerRepairStatus;

    private EditText edtFinalCost;

    private Button btnUpdateRepair;

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

        repairDAO =
                new RepairDAO(this);

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

        btnUpdateRepair.setOnClickListener(
                view -> updateRepair()
        );
    }


    // =========================================================
    // BIND
    // =========================================================

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
    }


    // =========================================================
    // STATUS OPTIONS
    // =========================================================

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


    // =========================================================
    // LOAD
    // =========================================================

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

        /*
         * Once payment completes the repair, technician
         * should no longer modify it.
         */
        if (
                Repair.STATUS_COMPLETED.equals(
                        repair.getStatus()
                )
        ) {

            spinnerRepairStatus.setEnabled(
                    false
            );

            edtFinalCost.setEnabled(
                    false
            );

            btnUpdateRepair.setEnabled(
                    false
            );

            btnUpdateRepair.setText(
                    "Repair Completed"
            );
        }
    }


    // =========================================================
    // SELECT CURRENT STATUS
    // =========================================================

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


    // =========================================================
    // UPDATE
    // =========================================================

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


        // -----------------------------------------------------
        // FINAL COST
        // -----------------------------------------------------

        double finalCost =
                repair.getFinalCost();

        if (!finalCostText.isEmpty()) {

            try {

                finalCost =
                        Double.parseDouble(
                                finalCostText
                        );

            } catch (
                    NumberFormatException exception
            ) {

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


        // -----------------------------------------------------
        // READY FOR COLLECTION VALIDATION
        // -----------------------------------------------------

        if (
                Repair.STATUS_READY_FOR_COLLECTION.equals(
                        selectedStatus
                )
        ) {

            if (finalCost <= 0) {

                edtFinalCost.setError(
                        "Enter the final cost before marking the repair ready for collection"
                );

                edtFinalCost.requestFocus();

                return;
            }
        }


        // -----------------------------------------------------
        // CHECK STATUS TRANSITION
        // -----------------------------------------------------

        if (
                !Repair.canTransition(
                        repair.getStatus(),
                        selectedStatus
                )
        ) {

            Toast.makeText(
                    this,
                    "You cannot move the repair back to a previous status",
                    Toast.LENGTH_LONG
            ).show();

            selectCurrentStatus();

            return;
        }


        // -----------------------------------------------------
        // SAVE FINAL COST
        // -----------------------------------------------------

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


        // -----------------------------------------------------
        // SAVE STATUS
        // -----------------------------------------------------

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


        // -----------------------------------------------------
        // SUCCESS
        // -----------------------------------------------------

        if (
                Repair.STATUS_READY_FOR_COLLECTION.equals(
                        selectedStatus
                )
        ) {

            Toast.makeText(
                    this,
                    "Repair is ready for collection. Customer can now make payment.",
                    Toast.LENGTH_LONG
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


    // =========================================================
    // CLOSE
    // =========================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (repairDAO != null) {

            repairDAO.close();
        }
    }
}