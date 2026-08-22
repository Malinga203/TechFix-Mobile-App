package com.techfix.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;
import com.techfix.app.database.PaymentDAO;
import com.techfix.app.models.Payment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.model.InitRequest;

public class PaymentActivity extends AppCompatActivity {

    public static final String EXTRA_APPOINTMENT_ID =
            "extra_appointment_id";

    public static final String EXTRA_AMOUNT =
            "extra_amount";

    // Sandbox Merchant ID
    private static final String MERCHANT_ID =
            "1237646";

    private TextView txtOrderId;
    private TextView txtPaymentAmount;
    private TextView txtPaymentStatus;

    private Button btnPayNow;

    private PaymentDAO paymentDAO;

    // Temporary testing values
    private int appointmentId = 1;
    private double amount = 5000.00;

    private String orderId;


    // =========================================================
    // PAYHERE RESULT
    // =========================================================

    private final ActivityResultLauncher<Intent> payHereLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK) {

                            handlePaymentCompleted();

                        } else if (
                                result.getResultCode() == RESULT_CANCELED
                        ) {

                            handlePaymentCancelled();

                        } else {

                            handlePaymentFailed();
                        }
                    }
            );


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_payment
        );

        bindViews();

        paymentDAO =
                new PaymentDAO(this);

        // Temporary values for direct testing
        readTestData();

        createOrderId();

        displayPaymentInformation();

        createPendingPayment();

        // Force button enabled during testing
        btnPayNow.setEnabled(true);

        btnPayNow.setOnClickListener(
                view -> {

                    Toast.makeText(
                            PaymentActivity.this,
                            "Pay button clicked",
                            Toast.LENGTH_SHORT
                    ).show();

                    launchPayHere();
                }
        );
    }


    // =========================================================
    // BIND VIEWS
    // =========================================================

    private void bindViews() {

        txtOrderId =
                findViewById(
                        R.id.txtOrderId
                );

        txtPaymentAmount =
                findViewById(
                        R.id.txtPaymentAmount
                );

        txtPaymentStatus =
                findViewById(
                        R.id.txtPaymentStatus
                );

        btnPayNow =
                findViewById(
                        R.id.btnPayNow
                );
    }


    // =========================================================
    // TEMPORARY TEST DATA
    // =========================================================

    private void readTestData() {

        appointmentId =
                getIntent().getIntExtra(
                        EXTRA_APPOINTMENT_ID,
                        1
                );

        amount =
                getIntent().getDoubleExtra(
                        EXTRA_AMOUNT,
                        5000.00
                );
    }


    // =========================================================
    // CREATE ORDER ID
    // =========================================================

    private void createOrderId() {

        orderId =
                "TECHFIX-" +
                        appointmentId +
                        "-" +
                        System.currentTimeMillis();
    }


    // =========================================================
    // DISPLAY PAYMENT DETAILS
    // =========================================================

    private void displayPaymentInformation() {

        txtOrderId.setText(
                "Order: " + orderId
        );

        txtPaymentAmount.setText(
                String.format(
                        Locale.US,
                        "LKR %.2f",
                        amount
                )
        );

        txtPaymentStatus.setText(
                "Ready for sandbox payment"
        );
    }


    // =========================================================
    // CREATE LOCAL PAYMENT
    // =========================================================

    private void createPendingPayment() {

        String paymentDate =
                new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.US
                ).format(
                        new Date()
                );

        Payment payment =
                new Payment(
                        0,
                        appointmentId,
                        orderId,
                        amount,
                        "LKR",
                        "PENDING",
                        null,
                        paymentDate
                );

        long result =
                paymentDAO.insertPayment(
                        payment
                );

        if (result == -1) {

            Toast.makeText(
                    this,
                    "Test payment record could not be saved locally",
                    Toast.LENGTH_SHORT
            ).show();

            /*
             * Do NOT disable the button.
             *
             * Appointment ID 1 might not exist in the database,
             * but we still want to test PayHere.
             */
        }
    }


    // =========================================================
    // OPEN PAYHERE SANDBOX
    // =========================================================

    private void launchPayHere() {

        Toast.makeText(
                this,
                "Preparing PayHere...",
                Toast.LENGTH_SHORT
        ).show();

        if (amount <= 0) {

            showError(
                    "Invalid payment amount"
            );

            return;
        }

        btnPayNow.setEnabled(false);

        txtPaymentStatus.setText(
                "Opening PayHere Sandbox..."
        );

        try {

            InitRequest request =
                    new InitRequest();


            // -------------------------------------------------
            // MERCHANT
            // -------------------------------------------------

            request.setMerchantId(
                    MERCHANT_ID
            );


            // -------------------------------------------------
            // PAYMENT DETAILS
            // -------------------------------------------------

            request.setCurrency(
                    "LKR"
            );

            request.setAmount(
                    amount
            );

            request.setOrderId(
                    orderId
            );

            request.setItemsDescription(
                    "TechFix Repair Appointment"
            );


            // -------------------------------------------------
            // CUSTOM VALUES
            // -------------------------------------------------

            request.setCustom1(
                    String.valueOf(
                            appointmentId
                    )
            );

            request.setCustom2(
                    "TechFix Mobile App"
            );


            // -------------------------------------------------
            // CUSTOMER DETAILS
            // -------------------------------------------------

            request.getCustomer()
                    .setFirstName(
                            "Test"
                    );

            request.getCustomer()
                    .setLastName(
                            "Customer"
                    );

            request.getCustomer()
                    .setEmail(
                            "test@example.com"
                    );

            request.getCustomer()
                    .setPhone(
                            "0771234567"
                    );


            // -------------------------------------------------
            // CUSTOMER ADDRESS
            // -------------------------------------------------

            request.getCustomer()
                    .getAddress()
                    .setAddress(
                            "Colombo"
                    );

            request.getCustomer()
                    .getAddress()
                    .setCity(
                            "Colombo"
                    );

            request.getCustomer()
                    .getAddress()
                    .setCountry(
                            "Sri Lanka"
                    );


            // -------------------------------------------------
            // PAYHERE SANDBOX
            // -------------------------------------------------

            PHConfigs.setBaseUrl(
                    PHConfigs.SANDBOX_URL
            );


            // -------------------------------------------------
            // OPEN PAYHERE ACTIVITY
            // -------------------------------------------------

            Intent payHereIntent =
                    new Intent(
                            PaymentActivity.this,
                            PHMainActivity.class
                    );

            payHereIntent.putExtra(
                    PHConstants.INTENT_EXTRA_DATA,
                    request
            );

            Toast.makeText(
                    this,
                    "Opening PayHere...",
                    Toast.LENGTH_SHORT
            ).show();

            payHereLauncher.launch(
                    payHereIntent
            );

        } catch (Exception exception) {

            String message =
                    exception.getMessage();

            if (message == null) {
                message = "Unknown PayHere error";
            }

            showError(
                    "PayHere Error: " + message
            );
        }
    }


    // =========================================================
    // PAYMENT COMPLETED
    // =========================================================

    private void handlePaymentCompleted() {

        paymentDAO.updatePaymentStatus(
                orderId,
                "SUCCESS",
                null
        );

        txtPaymentStatus.setText(
                "Sandbox payment completed"
        );

        Toast.makeText(
                this,
                "Sandbox payment completed",
                Toast.LENGTH_LONG
        ).show();

        btnPayNow.setEnabled(true);
    }


    // =========================================================
    // PAYMENT CANCELLED
    // =========================================================

    private void handlePaymentCancelled() {

        paymentDAO.updatePaymentStatus(
                orderId,
                "CANCELLED",
                null
        );

        txtPaymentStatus.setText(
                "Payment cancelled"
        );

        btnPayNow.setEnabled(true);

        Toast.makeText(
                this,
                "Payment cancelled",
                Toast.LENGTH_SHORT
        ).show();
    }


    // =========================================================
    // PAYMENT FAILED
    // =========================================================

    private void handlePaymentFailed() {

        paymentDAO.updatePaymentStatus(
                orderId,
                "FAILED",
                null
        );

        txtPaymentStatus.setText(
                "Payment failed"
        );

        btnPayNow.setEnabled(true);

        Toast.makeText(
                this,
                "Payment failed",
                Toast.LENGTH_LONG
        ).show();
    }


    // =========================================================
    // ERROR
    // =========================================================

    private void showError(String message) {

        txtPaymentStatus.setText(
                message
        );

        btnPayNow.setEnabled(true);

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }
}