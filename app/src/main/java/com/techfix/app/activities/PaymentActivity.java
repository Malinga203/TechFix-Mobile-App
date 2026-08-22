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
import com.techfix.app.models.Payment;
import com.techfix.app.models.Repair;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.model.InitRequest;

public class PaymentActivity extends AppCompatActivity {

    // =========================================================
    // LOG
    // =========================================================

    private static final String TAG =
            "TECHFIX_PAYMENT";


    // =========================================================
    // BACKEND
    // =========================================================

    private static final String BACKEND_BASE_URL =
            "http://192.168.8.193:3000";

    private static final String HASH_URL =
            BACKEND_BASE_URL +
                    "/api/payment/hash";


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


    // =========================================================
    // PAYMENT VALUES
    // =========================================================

    private int appointmentId;

    private long repairId;

    private double amount;

    private String orderId;

    private String merchantId;

    private String merchantSecret;

    private String backendHash;


    // =========================================================
    // PAYHERE RESULT
    // =========================================================

    private final ActivityResultLauncher<Intent>
            payHereLauncher =
            registerForActivityResult(

                    new ActivityResultContracts.StartActivityForResult(),

                    result -> {

                        int resultCode =
                                result.getResultCode();

                        Log.d(
                                TAG,
                                "PayHere result code = "
                                        + resultCode
                        );


                        // =================================================
                        // SUCCESS
                        // =================================================

                        if (
                                resultCode
                                        ==
                                        Activity.RESULT_OK
                        ) {

                            Log.d(
                                    TAG,
                                    "PayHere payment successful"
                            );

                            handlePaymentSuccess();

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

                            Log.d(
                                    TAG,
                                    "PayHere payment cancelled"
                            );

                            handlePaymentCancelled();

                            return;
                        }


                        // =================================================
                        // UNKNOWN
                        // =================================================

                        txtPaymentStatus.setText(
                                "Payment was not completed"
                        );

                        btnPayNow.setEnabled(
                                true
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


        // =====================================================
        // PAYHERE CREDENTIALS
        // =====================================================

        merchantId =
                getString(
                        R.string.payhere_merchant_id
                ).trim();

        merchantSecret =
                getString(
                        R.string.payhere_merchant_secret
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
        // VALIDATE
        // =====================================================

        if (appointmentId <= 0) {

            showFatalError(
                    "Invalid appointment"
            );

            return;
        }


        if (repairId <= 0) {

            showFatalError(
                    "Invalid repair"
            );

            return;
        }


        if (amount <= 0) {

            showFatalError(
                    "Invalid payment amount"
            );

            return;
        }


        if (
                merchantId.isEmpty()
                        ||
                        merchantSecret.isEmpty()
        ) {

            showFatalError(
                    "PayHere credentials are missing"
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
        // ORDER ID
        // =====================================================

        orderId =
                "TECHFIX-"
                        + repairId
                        + "-"
                        + System.currentTimeMillis();


        // =====================================================
        // DISPLAY
        // =====================================================

        txtOrderId.setText(
                "Order: "
                        + orderId
        );


        txtPaymentAmount.setText(
                String.format(
                        Locale.getDefault(),
                        "LKR %,.2f",
                        amount
                )
        );


        txtPaymentStatus.setText(
                "Ready for payment"
        );


        // =====================================================
        // LOCAL PAYMENT RECORD
        // =====================================================

        createPendingPayment();


        // =====================================================
        // PAY BUTTON
        // =====================================================

        btnPayNow.setOnClickListener(
                view ->
                        requestBackendPayment()
        );
    }


    // =========================================================
    // CREATE LOCAL PENDING PAYMENT
    // =========================================================

    private void createPendingPayment() {

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
        }
    }


    // =========================================================
    // REQUEST BACKEND HASH
    // =========================================================

    private void requestBackendPayment() {

        btnPayNow.setEnabled(
                false
        );

        txtPaymentStatus.setText(
                "Connecting to payment server..."
        );


        new Thread(
                () -> {

                    HttpURLConnection connection =
                            null;

                    try {

                        URL url =
                                new URL(
                                        HASH_URL
                                );


                        connection =
                                (HttpURLConnection)
                                        url.openConnection();


                        connection.setRequestMethod(
                                "POST"
                        );


                        connection.setRequestProperty(
                                "Content-Type",
                                "application/json"
                        );


                        connection.setRequestProperty(
                                "Accept",
                                "application/json"
                        );


                        connection.setConnectTimeout(
                                10000
                        );


                        connection.setReadTimeout(
                                10000
                        );


                        connection.setDoOutput(
                                true
                        );


                        // =================================================
                        // REQUEST BODY
                        // =================================================

                        JSONObject body =
                                new JSONObject();


                        body.put(
                                "orderId",
                                orderId
                        );


                        body.put(
                                "amount",
                                amount
                        );


                        body.put(
                                "currency",
                                "LKR"
                        );


                        byte[] requestBytes =
                                body
                                        .toString()
                                        .getBytes(
                                                StandardCharsets.UTF_8
                                        );


                        OutputStream outputStream =
                                connection
                                        .getOutputStream();


                        outputStream.write(
                                requestBytes
                        );


                        outputStream.flush();

                        outputStream.close();


                        // =================================================
                        // RESPONSE
                        // =================================================

                        int responseCode =
                                connection
                                        .getResponseCode();


                        InputStream inputStream;


                        if (
                                responseCode >= 200
                                        &&
                                        responseCode < 300
                        ) {

                            inputStream =
                                    connection
                                            .getInputStream();

                        } else {

                            inputStream =
                                    connection
                                            .getErrorStream();
                        }


                        String response =
                                readInputStream(
                                        inputStream
                                );


                        Log.d(
                                TAG,
                                "Backend response code = "
                                        + responseCode
                        );


                        Log.d(
                                TAG,
                                "Backend response = "
                                        + response
                        );


                        if (
                                responseCode < 200
                                        ||
                                        responseCode >= 300
                        ) {

                            runOnUiThread(
                                    () ->
                                            showError(
                                                    "Payment server rejected the request"
                                            )
                            );

                            return;
                        }


                        JSONObject responseJson =
                                new JSONObject(
                                        response
                                );


                        boolean success =
                                responseJson.optBoolean(
                                        "success",
                                        false
                                );


                        if (!success) {

                            runOnUiThread(
                                    () ->
                                            showError(
                                                    "Unable to initialize payment"
                                            )
                            );

                            return;
                        }


                        // =================================================
                        // READ BACKEND VALUES
                        // =================================================

                        String backendMerchantId =
                                responseJson.getString(
                                        "merchantId"
                                );


                        String backendOrderId =
                                responseJson.getString(
                                        "orderId"
                                );


                        backendHash =
                                responseJson.getString(
                                        "hash"
                                );


                        // =================================================
                        // VERIFY BACKEND RESPONSE
                        // =================================================

                        if (
                                !merchantId.equals(
                                        backendMerchantId
                                )
                        ) {

                            runOnUiThread(
                                    () ->
                                            showError(
                                                    "Merchant ID mismatch"
                                            )
                            );

                            return;
                        }


                        if (
                                !orderId.equals(
                                        backendOrderId
                                )
                        ) {

                            runOnUiThread(
                                    () ->
                                            showError(
                                                    "Order ID mismatch"
                                            )
                            );

                            return;
                        }


                        if (
                                backendHash == null
                                        ||
                                        backendHash.isEmpty()
                        ) {

                            runOnUiThread(
                                    () ->
                                            showError(
                                                    "Payment hash missing"
                                            )
                            );

                            return;
                        }


                        Log.d(
                                TAG,
                                "Backend payment initialized"
                        );


                        Log.d(
                                TAG,
                                "Hash received = "
                                        + backendHash
                        );


                        // =================================================
                        // OPEN PAYHERE
                        // =================================================

                        runOnUiThread(
                                this::openPayHere
                        );


                    } catch (
                            Exception exception
                    ) {

                        Log.e(
                                TAG,
                                "Backend connection failed",
                                exception
                        );


                        runOnUiThread(
                                () ->
                                        showError(
                                                "Cannot connect to payment server: "
                                                        + exception.getMessage()
                                        )
                        );


                    } finally {

                        if (
                                connection != null
                        ) {

                            connection.disconnect();
                        }
                    }
                }
        ).start();
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


            request.setMerchantSecret(
                    merchantSecret
            );


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


            request.getCustomer()
                    .setPhone(
                            "0771234567"
                    );


            // =================================================
            // ADDRESS
            // =================================================

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


            // =================================================
            // IMPORTANT
            //
            // Do NOT set local notify URL here.
            //
            // PayHere cannot access:
            // 192.168.8.193
            //
            // We will enable notify URL after Railway.
            // =================================================


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
                    "Opening PayHere"
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


            showError(
                    "Unable to open PayHere: "
                            + exception.getMessage()
            );
        }
    }


    // =========================================================
    // PAYMENT SUCCESS
    // =========================================================

    private void handlePaymentSuccess() {

        // =====================================================
        // PAYMENT -> SUCCESS
        // =====================================================

        int paymentRows =
                paymentDAO.updatePaymentStatus(
                        orderId,
                        "SUCCESS",
                        null
                );


        if (paymentRows <= 0) {

            showError(
                    "Payment succeeded but local payment update failed"
            );

            return;
        }


        // =====================================================
        // REPAIR -> COMPLETED
        // =====================================================

        boolean repairCompleted =
                repairDAO.updateRepairStatus(
                        repairId,
                        Repair.STATUS_COMPLETED
                );


        if (!repairCompleted) {

            showError(
                    "Payment succeeded but repair completion failed"
            );

            return;
        }


        // =====================================================
        // SUCCESS
        // =====================================================

        txtPaymentStatus.setText(
                "Payment successful"
        );


        Toast.makeText(
                this,
                "Payment successful. Repair completed.",
                Toast.LENGTH_LONG
        ).show();


        setResult(
                RESULT_OK
        );


        finish();
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


        /*
         * Repair remains READY_FOR_COLLECTION.
         *
         * Customer can retry the payment.
         */


        txtPaymentStatus.setText(
                "Payment cancelled"
        );


        btnPayNow.setEnabled(
                true
        );


        Toast.makeText(
                this,
                "Payment cancelled",
                Toast.LENGTH_SHORT
        ).show();
    }


    // =========================================================
    // READ HTTP RESPONSE
    // =========================================================

    private String readInputStream(
            InputStream inputStream
    ) throws Exception {

        if (
                inputStream == null
        ) {

            return "";
        }


        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(
                                inputStream,
                                StandardCharsets.UTF_8
                        )
                );


        StringBuilder result =
                new StringBuilder();


        String line;


        while (
                (line = reader.readLine())
                        !=
                        null
        ) {

            result.append(
                    line
            );
        }


        reader.close();


        return result.toString();
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


        if (
                repairDAO != null
        ) {

            repairDAO.close();
        }
    }
}