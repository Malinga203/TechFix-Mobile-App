package com.techfix.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;
import com.techfix.app.database.PaymentDAO;
import com.techfix.app.database.RepairDAO;
import com.techfix.app.database.RepairSparePartDAO;
import com.techfix.app.models.Payment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;

public class PaymentActivity extends AppCompatActivity {

    // =========================================================
    // LOG
    // =========================================================

    private static final String TAG =
            "TECHFIX_PAYMENT";


    // =========================================================
    // INTENT EXTRAS
    // =========================================================

    public static final String EXTRA_APPOINTMENT_ID =
            "extra_appointment_id";

    public static final String EXTRA_REPAIR_ID =
            "extra_repair_id";

    public static final String EXTRA_AMOUNT =
            "extra_amount";


    // =========================================================
    // UI
    // =========================================================

    private TextView txtOrderId;
    private TextView txtPaymentAmount;
    private TextView txtPaymentStatus;

    private Button btnPayNow;


    // =========================================================
    // DAO
    // =========================================================

    private PaymentDAO paymentDAO;
    private RepairDAO repairDAO;
    private RepairSparePartDAO repairSparePartDAO;


    // =========================================================
    // PAYMENT VALUES
    // =========================================================

    private int appointmentId;

    private long repairId;

    private double amount;

    private String orderId;

    private String merchantId;


    // =========================================================
    // PAYHERE RESULT LAUNCHER
    // =========================================================

    private final ActivityResultLauncher<Intent>
            payHereLauncher =
            registerForActivityResult(

                    new ActivityResultContracts.StartActivityForResult(),

                    result -> {

                        int resultCode =
                                result.getResultCode();

                        Intent data =
                                result.getData();


                        Log.d(
                                TAG,
                                "======================================"
                        );

                        Log.d(
                                TAG,
                                "PayHere result code = "
                                        + resultCode
                        );

                        Log.d(
                                TAG,
                                "======================================"
                        );


                        // =================================================
                        // NO DATA RETURNED
                        // =================================================

                        if (data == null) {

                            Log.e(
                                    TAG,
                                    "PayHere returned null Intent data"
                            );


                            if (
                                    resultCode
                                            ==
                                            Activity.RESULT_CANCELED
                            ) {

                                handlePaymentCancelled(
                                        "User cancelled payment"
                                );

                            } else {

                                handlePaymentFailed(
                                        "No response received from PayHere"
                                );
                            }

                            return;
                        }


                        // =================================================
                        // CHECK RESULT EXTRA
                        // =================================================

                        if (
                                !data.hasExtra(
                                        PHConstants.INTENT_EXTRA_RESULT
                                )
                        ) {

                            Log.e(
                                    TAG,
                                    "PayHere result extra is missing"
                            );


                            if (
                                    resultCode
                                            ==
                                            Activity.RESULT_CANCELED
                            ) {

                                handlePaymentCancelled(
                                        "Payment cancelled"
                                );

                            } else {

                                handlePaymentFailed(
                                        "Invalid PayHere response"
                                );
                            }

                            return;
                        }


                        // =================================================
                        // READ PAYHERE RESPONSE
                        // =================================================

                        PHResponse<StatusResponse> response;

                        try {

                            @SuppressWarnings("unchecked")
                            PHResponse<StatusResponse> payHereResponse =
                                    (PHResponse<StatusResponse>)
                                            data.getSerializableExtra(
                                                    PHConstants.INTENT_EXTRA_RESULT
                                            );

                            response =
                                    payHereResponse;

                        } catch (Exception exception) {

                            Log.e(
                                    TAG,
                                    "Unable to read PayHere response",
                                    exception
                            );

                            handlePaymentFailed(
                                    "Unable to read PayHere response"
                            );

                            return;
                        }


                        // =================================================
                        // LOG RESPONSE
                        // =================================================

                        if (response != null) {

                            Log.d(
                                    TAG,
                                    "PayHere response = "
                                            + response
                            );

                            Log.d(
                                    TAG,
                                    "PayHere success = "
                                            + response.isSuccess()
                            );

                        } else {

                            Log.e(
                                    TAG,
                                    "PayHere response object is null"
                            );
                        }


                        // =================================================
                        // RESULT OK
                        // =================================================

                        if (
                                resultCode
                                        ==
                                        Activity.RESULT_OK
                        ) {

                            /*
                             * RESULT_OK alone does NOT mean
                             * the actual payment succeeded.
                             *
                             * We must also check:
                             *
                             * response != null
                             * response.isSuccess()
                             */

                            if (
                                    response != null
                                            &&
                                            response.isSuccess()
                            ) {

                                StatusResponse statusResponse =
                                        response.getData();


                                if (statusResponse == null) {

                                    handlePaymentFailed(
                                            "Payment response data is missing"
                                    );

                                    return;
                                }


                                Log.d(
                                        TAG,
                                        "Payment data = "
                                                + statusResponse
                                );


                                /*
                                 * Extract the PayHere payment reference.
                                 *
                                 * getPaymentNo() exists in the PayHere
                                 * payment response in supported SDK versions.
                                 *
                                 * The helper method below also has a
                                 * safe fallback.
                                 */
                                String paymentReference =
                                        extractPaymentReference(
                                                statusResponse
                                        );


                                Log.d(
                                        TAG,
                                        "Payment reference = "
                                                + paymentReference
                                );


                                handlePaymentSuccess(
                                        paymentReference
                                );

                            } else {

                                String errorMessage;

                                if (response != null) {

                                    errorMessage =
                                            response.toString();

                                } else {

                                    errorMessage =
                                            "Payment failed";
                                }


                                Log.e(
                                        TAG,
                                        "PayHere payment failed: "
                                                + errorMessage
                                );


                                handlePaymentFailed(
                                        errorMessage
                                );
                            }

                            return;
                        }


                        // =================================================
                        // CANCELLED
                        // =================================================

                        if (
                                resultCode
                                        ==
                                        Activity.RESULT_CANCELED
                        ) {

                            String cancelMessage;

                            if (response != null) {

                                cancelMessage =
                                        response.toString();

                            } else {

                                cancelMessage =
                                        "User cancelled payment";
                            }


                            Log.d(
                                    TAG,
                                    "PayHere payment cancelled: "
                                            + cancelMessage
                            );


                            handlePaymentCancelled(
                                    cancelMessage
                            );

                            return;
                        }


                        // =================================================
                        // UNKNOWN RESULT
                        // =================================================

                        handlePaymentFailed(
                                "Payment was not completed"
                        );
                    }
            );


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_payment
        );


        // =====================================================
        // DAO
        // =====================================================

        paymentDAO =
                new PaymentDAO(this);

        repairDAO =
                new RepairDAO(this);

        repairSparePartDAO =
                new RepairSparePartDAO(this);


        // =====================================================
        // PAYHERE MERCHANT ID
        // =====================================================

        merchantId =
                getString(
                        R.string.payhere_merchant_id
                ).trim();


        // =====================================================
        // GET INTENT VALUES
        // =====================================================

        appointmentId =
                getIntent()
                        .getIntExtra(
                                EXTRA_APPOINTMENT_ID,
                                -1
                        );


        repairId =
                getIntent()
                        .getLongExtra(
                                EXTRA_REPAIR_ID,
                                -1
                        );


        amount =
                getIntent()
                        .getDoubleExtra(
                                EXTRA_AMOUNT,
                                0
                        );


        // =====================================================
        // BIND UI
        // =====================================================

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


        // =====================================================
        // VALIDATE APPOINTMENT
        // =====================================================

        if (appointmentId <= 0) {

            showFatalError(
                    "Invalid appointment"
            );

            return;
        }


        // =====================================================
        // VALIDATE REPAIR
        // =====================================================

        if (repairId <= 0) {

            showFatalError(
                    "Invalid repair"
            );

            return;
        }


        // =====================================================
        // VALIDATE AMOUNT
        // =====================================================

        if (amount <= 0) {

            showFatalError(
                    "Invalid payment amount"
            );

            return;
        }


        // =====================================================
        // VALIDATE PAYHERE MERCHANT ID
        // =====================================================

        if (merchantId.isEmpty()) {

            showFatalError(
                    "PayHere Merchant ID is missing"
            );

            return;
        }


        // =====================================================
        // VERIFY REPAIR
        // =====================================================

        if (
                !repairDAO.isRepairReadyForPayment(
                        repairId
                )
        ) {

            showFatalError(
                    "This repair is not ready for payment"
            );

            return;
        }


        // =====================================================
        // DISPLAY AMOUNT
        // =====================================================

        txtPaymentAmount.setText(
                String.format(
                        Locale.getDefault(),
                        "LKR %,.2f",
                        amount
                )
        );


        txtOrderId.setText(
                "Order will be created when payment starts"
        );


        txtPaymentStatus.setText(
                "Ready for payment"
        );


        // =====================================================
        // PAY BUTTON
        // =====================================================

        btnPayNow.setOnClickListener(
                view ->
                        startNewPaymentAttempt()
        );
    }


    // =========================================================
    // START NEW PAYMENT ATTEMPT
    // =========================================================

    private void startNewPaymentAttempt() {

        /*
         * Generate a NEW unique order ID
         * for every payment attempt.
         *
         * This means if a customer cancels and retries,
         * the second payment receives another order ID.
         */

        orderId =
                "TECHFIX-"
                        + repairId
                        + "-"
                        + System.currentTimeMillis();


        Log.d(
                TAG,
                "New payment order = "
                        + orderId
        );


        txtOrderId.setText(
                "Order: "
                        + orderId
        );


        // =====================================================
        // CREATE LOCAL PENDING PAYMENT
        // =====================================================

        boolean created =
                createPendingPayment();


        if (!created) {

            showError(
                    "Unable to create payment record"
            );

            return;
        }


        // =====================================================
        // DISABLE BUTTON
        // =====================================================

        btnPayNow.setEnabled(
                false
        );


        // =====================================================
        // OPEN PAYHERE
        // =====================================================

        openPayHere();
    }


    // =========================================================
    // CREATE LOCAL PENDING PAYMENT
    // =========================================================

    private boolean createPendingPayment() {

        Payment payment =
                new Payment();


        payment.setAppointmentId(
                appointmentId
        );


        payment.setOrderId(
                orderId
        );


        payment.setAmount(
                amount
        );


        payment.setCurrency(
                "LKR"
        );


        payment.setStatus(
                "PENDING"
        );


        payment.setPaymentReference(
                null
        );


        payment.setPaymentDate(
                getCurrentTimestamp()
        );


        long result =
                paymentDAO.insertPayment(
                        payment
                );


        if (result <= 0) {

            Log.e(
                    TAG,
                    "Unable to create local payment"
            );

            return false;
        }


        Log.d(
                TAG,
                "Pending payment created. ID = "
                        + result
        );


        return true;
    }


    // =========================================================
    // OPEN PAYHERE
    // =========================================================

    private void openPayHere() {

        txtPaymentStatus.setText(
                "Opening PayHere Sandbox..."
        );


        try {

            InitRequest request =
                    new InitRequest();


            // =================================================
            // MERCHANT
            // =================================================

            request.setMerchantId(
                    merchantId
            );


            /*
             * IMPORTANT
             *
             * No Merchant Secret is stored in this Activity.
             *
             * Current PayHere Android SDK documentation
             * initializes the native SDK request using the
             * Merchant ID.
             */


            // =================================================
            // PAYMENT
            // =================================================

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
                    "TechFix Repair Payment"
            );


            // =================================================
            // CUSTOM VALUES
            // =================================================

            request.setCustom1(
                    String.valueOf(
                            appointmentId
                    )
            );


            request.setCustom2(
                    String.valueOf(
                            repairId
                    )
            );


            // =================================================
            // CUSTOMER
            // =================================================

            request.getCustomer()
                    .setFirstName(
                            "TechFix"
                    );


            request.getCustomer()
                    .setLastName(
                            "Customer"
                    );


            request.getCustomer()
                    .setEmail(
                            "customer@techfix.com"
                    );


            /*
             * International format.
             */
            request.getCustomer()
                    .setPhone(
                            "+94771234567"
                    );


            // =================================================
            // ADDRESS
            // =================================================

            request.getCustomer()
                    .getAddress()
                    .setAddress(
                            "No. 1, Galle Road"
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


            // =================================================
            // NOTIFY URL
            // =================================================

            /*
             * DO NOT use:
             *
             * http://192.168.x.x/...
             *
             * PayHere servers cannot access your local LAN.
             *
             * When your backend is publicly hosted, add:
             *
             * request.setNotifyUrl(
             *     "https://your-domain.com/api/payment/notify"
             * );
             *
             * That backend must verify PayHere's md5sig
             * before considering the payment authoritative.
             */


            // =================================================
            // SANDBOX
            // =================================================

            PHConfigs.setBaseUrl(
                    PHConfigs.SANDBOX_URL
            );


            // =================================================
            // LOG
            // =================================================

            Log.d(
                    TAG,
                    "======================================"
            );


            Log.d(
                    TAG,
                    "Opening PayHere Sandbox"
            );


            Log.d(
                    TAG,
                    "Merchant = "
                            + merchantId
            );


            Log.d(
                    TAG,
                    "Order = "
                            + orderId
            );


            Log.d(
                    TAG,
                    "Amount = "
                            + amount
            );


            Log.d(
                    TAG,
                    "Repair = "
                            + repairId
            );


            Log.d(
                    TAG,
                    "Appointment = "
                            + appointmentId
            );


            Log.d(
                    TAG,
                    "======================================"
            );


            // =================================================
            // INTENT
            // =================================================

            Intent payHereIntent =
                    new Intent(
                            PaymentActivity.this,
                            PHMainActivity.class
                    );


            payHereIntent.putExtra(
                    PHConstants.INTENT_EXTRA_DATA,
                    request
            );


            payHereLauncher.launch(
                    payHereIntent
            );


        } catch (
                Exception exception
        ) {

            Log.e(
                    TAG,
                    "Unable to open PayHere",
                    exception
            );


            /*
             * The PENDING payment was already created,
             * therefore mark this particular attempt FAILED.
             */

            if (
                    orderId != null
                            &&
                            !orderId.isEmpty()
            ) {

                paymentDAO.updatePaymentStatus(
                        orderId,
                        "FAILED",
                        null
                );
            }


            showError(
                    "Unable to open PayHere: "
                            + exception.getMessage()
            );
        }
    }


    // =========================================================
    // EXTRACT PAYMENT REFERENCE
    // =========================================================

    private String extractPaymentReference(
            StatusResponse statusResponse
    ) {

        if (statusResponse == null) {

            return null;
        }


        /*
         * Different PayHere Android SDK builds have exposed
         * payment number fields slightly differently.
         *
         * Using reflection here prevents this Activity from
         * failing to compile if the SDK changes the declared
         * return type of getPaymentNo().
         */

        try {

            Object paymentNo =
                    statusResponse
                            .getClass()
                            .getMethod(
                                    "getPaymentNo"
                            )
                            .invoke(
                                    statusResponse
                            );


            if (paymentNo != null) {

                return String.valueOf(
                        paymentNo
                );
            }


        } catch (Exception exception) {

            Log.w(
                    TAG,
                    "Could not extract PayHere payment number",
                    exception
            );
        }


        /*
         * Do NOT invent a payment reference.
         *
         * If the SDK doesn't expose one,
         * simply leave the local reference null.
         */

        return null;
    }


    // =========================================================
    // PAYMENT SUCCESS
    // =========================================================

    private void handlePaymentSuccess(
            String paymentReference
    ) {

        Log.d(
                TAG,
                "======================================"
        );


        Log.d(
                TAG,
                "PAYMENT SUCCESS"
        );


        Log.d(
                TAG,
                "Order = "
                        + orderId
        );


        Log.d(
                TAG,
                "Reference = "
                        + paymentReference
        );


        Log.d(
                TAG,
                "======================================"
        );


        // =====================================================
        // PAYMENT -> SUCCESS
        // =====================================================

        int paymentRows =
                paymentDAO.updatePaymentStatus(
                        orderId,
                        "SUCCESS",
                        paymentReference
                );


        if (paymentRows <= 0) {

            showError(
                    "Payment succeeded but local payment update failed"
            );

            return;
        }


        // =====================================================
        // REPAIR -> COMPLETED
        // INVENTORY -> DEDUCT
        // =====================================================

        boolean repairCompleted =
                repairSparePartDAO
                        .completeRepairAndDeductInventory(
                                repairId
                        );


        if (!repairCompleted) {

            /*
             * IMPORTANT:
             *
             * Payment stays SUCCESS.
             *
             * We do NOT change a successful payment back
             * to FAILED just because the local repair
             * database update failed.
             */

            Log.e(
                    TAG,
                    "Payment succeeded but repair completion failed"
            );


            txtPaymentStatus.setText(
                    "Payment successful - repair update failed"
            );


            Toast.makeText(
                    this,
                    "Payment succeeded, but repair completion failed.",
                    Toast.LENGTH_LONG
            ).show();


            btnPayNow.setEnabled(
                    false
            );


            return;
        }


        // =====================================================
        // SUCCESS UI
        // =====================================================

        txtPaymentStatus.setText(
                "Payment successful"
        );


        btnPayNow.setEnabled(
                false
        );


        Toast.makeText(
                this,
                "Payment successful. Repair completed.",
                Toast.LENGTH_LONG
        ).show();


        // =====================================================
        // RETURN RESULT
        // =====================================================

        Intent resultIntent =
                new Intent();


        resultIntent.putExtra(
                "payment_order_id",
                orderId
        );


        resultIntent.putExtra(
                "payment_reference",
                paymentReference
        );


        resultIntent.putExtra(
                "repair_id",
                repairId
        );


        resultIntent.putExtra(
                "payment_amount",
                amount
        );


        setResult(
                RESULT_OK,
                resultIntent
        );


        finish();
    }


    // =========================================================
    // PAYMENT FAILED
    // =========================================================

    private void handlePaymentFailed(
            String message
    ) {

        Log.e(
                TAG,
                "Payment failed: "
                        + message
        );


        if (
                orderId != null
                        &&
                        !orderId.isEmpty()
        ) {

            paymentDAO.updatePaymentStatus(
                    orderId,
                    "FAILED",
                    null
            );
        }


        txtPaymentStatus.setText(
                "Payment failed"
        );


        btnPayNow.setEnabled(
                true
        );


        Toast.makeText(
                this,
                "Payment failed: "
                        + message,
                Toast.LENGTH_LONG
        ).show();
    }


    // =========================================================
    // PAYMENT CANCELLED
    // =========================================================

    private void handlePaymentCancelled(
            String message
    ) {

        if (
                orderId != null
                        &&
                        !orderId.isEmpty()
        ) {

            paymentDAO.updatePaymentStatus(
                    orderId,
                    "CANCELLED",
                    null
            );
        }


        /*
         * Repair remains READY_FOR_COLLECTION.
         *
         * Customer can retry.
         *
         * A new Order ID will be generated
         * when the customer presses Pay Now again.
         */


        txtPaymentStatus.setText(
                "Payment cancelled"
        );


        btnPayNow.setEnabled(
                true
        );


        Log.d(
                TAG,
                "Payment cancelled: "
                        + message
        );


        Toast.makeText(
                this,
                "Payment cancelled",
                Toast.LENGTH_SHORT
        ).show();
    }


    // =========================================================
    // ERROR
    // =========================================================

    private void showError(
            String message
    ) {

        Log.e(
                TAG,
                message
        );


        txtPaymentStatus.setText(
                message
        );


        btnPayNow.setEnabled(
                true
        );


        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }


    // =========================================================
    // FATAL ERROR
    // =========================================================

    private void showFatalError(
            String message
    ) {

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();


        finish();
    }


    // =========================================================
    // TIMESTAMP
    // =========================================================

    private String getCurrentTimestamp() {

        return new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
        ).format(
                new Date()
        );
    }


    // =========================================================
    // CLEANUP
    // =========================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();


        if (repairDAO != null) {

            repairDAO.close();
        }


        if (repairSparePartDAO != null) {

            repairSparePartDAO.close();
        }
    }
}