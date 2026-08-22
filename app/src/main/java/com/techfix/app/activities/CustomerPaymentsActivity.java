package com.techfix.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;
import com.techfix.app.adapters.PaymentRepairAdapter;
import com.techfix.app.database.RepairDAO;
import com.techfix.app.models.Repair;
import com.techfix.app.userauthentication.utils.SessionManager;

import java.util.List;

public class CustomerPaymentsActivity
        extends AppCompatActivity
        implements PaymentRepairAdapter.OnPaymentClickListener {

    private RecyclerView recyclerPaymentRepairs;

    private TextView txtNoPayments;

    private RepairDAO repairDAO;

    private SessionManager sessionManager;


    // =========================================================
    // CREATE
    // =========================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_customer_payments
        );


        // =====================================================
        // SESSION
        // =====================================================

        sessionManager =
                new SessionManager(this);

        if (
                !sessionManager.isLoggedIn() ||
                        !sessionManager.isCustomer()
        ) {

            Toast.makeText(
                    this,
                    "Customer login required",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }


        // =====================================================
        // DAO
        // =====================================================

        repairDAO =
                new RepairDAO(this);


        // =====================================================
        // VIEWS
        // =====================================================

        recyclerPaymentRepairs =
                findViewById(
                        R.id.recyclerPaymentRepairs
                );

        txtNoPayments =
                findViewById(
                        R.id.txtNoPayments
                );


        // =====================================================
        // RECYCLER VIEW
        // =====================================================

        recyclerPaymentRepairs.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerPaymentRepairs.setHasFixedSize(
                false
        );
    }


    // =========================================================
    // RELOAD WHEN RETURNING FROM PAYMENT
    // =========================================================

    @Override
    protected void onResume() {

        super.onResume();

        if (repairDAO != null) {

            loadPaymentRepairs();
        }
    }


    // =========================================================
    // LOAD READY FOR COLLECTION
    // =========================================================

    private void loadPaymentRepairs() {

        int customerId =
                sessionManager.getUserId();

        if (customerId <= 0) {

            Toast.makeText(
                    this,
                    "Customer session not found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        List<Repair> repairs =
                repairDAO
                        .getReadyForCollectionRepairsByCustomer(
                                customerId
                        );


        // =====================================================
        // EMPTY
        // =====================================================

        if (
                repairs == null ||
                        repairs.isEmpty()
        ) {

            recyclerPaymentRepairs.setVisibility(
                    View.GONE
            );

            txtNoPayments.setVisibility(
                    View.VISIBLE
            );

            return;
        }


        // =====================================================
        // SHOW LIST
        // =====================================================

        txtNoPayments.setVisibility(
                View.GONE
        );

        recyclerPaymentRepairs.setVisibility(
                View.VISIBLE
        );


        PaymentRepairAdapter adapter =
                new PaymentRepairAdapter(
                        repairs,
                        this
                );

        recyclerPaymentRepairs.setAdapter(
                adapter
        );
    }


    // =========================================================
    // PAYMENT CLICK
    // =========================================================

    @Override
    public void onPaymentClick(
            Repair repair
    ) {

        if (repair == null) {
            return;
        }


        // =====================================================
        // VERIFY REPAIR STATUS
        // =====================================================

        boolean readyForPayment =
                repairDAO.isRepairReadyForPayment(
                        repair.getRepairId()
                );

        if (!readyForPayment) {

            Toast.makeText(
                    this,
                    "This repair is not ready for payment",
                    Toast.LENGTH_LONG
            ).show();

            loadPaymentRepairs();

            return;
        }


        // =====================================================
        // VERIFY FINAL COST
        // =====================================================

        if (repair.getFinalCost() <= 0) {

            Toast.makeText(
                    this,
                    "The final repair cost has not been entered",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        // =====================================================
        // OPEN PAYMENT
        // =====================================================

        Intent intent =
                new Intent(
                        CustomerPaymentsActivity.this,
                        PaymentActivity.class
                );


        // Appointment ID
        intent.putExtra(
                PaymentActivity.EXTRA_APPOINTMENT_ID,
                (int) repair.getAppointmentId()
        );


        // Repair ID
        intent.putExtra(
                PaymentActivity.EXTRA_REPAIR_ID,
                repair.getRepairId()
        );


        // Final amount
        intent.putExtra(
                PaymentActivity.EXTRA_AMOUNT,
                repair.getFinalCost()
        );


        startActivity(
                intent
        );
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