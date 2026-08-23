package com.techfix.app.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PaymentSuccessActivity extends AppCompatActivity {

    // =========================================================
    // INTENT EXTRAS
    // =========================================================

    public static final String EXTRA_APPOINTMENT_ID =
            "extra_success_appointment_id";

    public static final String EXTRA_REPAIR_ID =
            "extra_success_repair_id";

    public static final String EXTRA_ORDER_ID =
            "extra_success_order_id";

    public static final String EXTRA_PAYMENT_REFERENCE =
            "extra_success_payment_reference";

    public static final String EXTRA_AMOUNT =
            "extra_success_amount";

    public static final String EXTRA_PAYMENT_DATE =
            "extra_success_payment_date";


    // =========================================================
    // UI
    // =========================================================

    private TextView txtSuccessAmount;
    private TextView txtSuccessOrderId;
    private TextView txtSuccessReference;
    private TextView txtSuccessRepairId;
    private TextView txtSuccessAppointmentId;
    private TextView txtSuccessDate;

    private Button btnSaveReceipt;
    private Button btnDone;


    // =========================================================
    // PAYMENT DATA
    // =========================================================

    private int appointmentId;

    private long repairId;

    private double amount;

    private String orderId;

    private String paymentReference;

    private String paymentDate;


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_payment_success
        );


        bindViews();

        readPaymentData();

        displayPaymentData();


        // =====================================================
        // SAVE RECEIPT
        // =====================================================

        btnSaveReceipt.setOnClickListener(
                view ->
                        saveReceiptAsPdf()
        );


        // =====================================================
        // DONE
        // =====================================================

        btnDone.setOnClickListener(
                view ->
                        finish()
        );
    }


    // =========================================================
    // BIND UI
    // =========================================================

    private void bindViews() {

        txtSuccessAmount =
                findViewById(
                        R.id.txtSuccessAmount
                );


        txtSuccessOrderId =
                findViewById(
                        R.id.txtSuccessOrderId
                );


        txtSuccessReference =
                findViewById(
                        R.id.txtSuccessReference
                );


        txtSuccessRepairId =
                findViewById(
                        R.id.txtSuccessRepairId
                );


        txtSuccessAppointmentId =
                findViewById(
                        R.id.txtSuccessAppointmentId
                );


        txtSuccessDate =
                findViewById(
                        R.id.txtSuccessDate
                );


        btnSaveReceipt =
                findViewById(
                        R.id.btnSaveReceipt
                );


        btnDone =
                findViewById(
                        R.id.btnDone
                );
    }


    // =========================================================
    // READ INTENT DATA
    // =========================================================

    private void readPaymentData() {

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


        orderId =
                getIntent()
                        .getStringExtra(
                                EXTRA_ORDER_ID
                        );


        paymentReference =
                getIntent()
                        .getStringExtra(
                                EXTRA_PAYMENT_REFERENCE
                        );


        paymentDate =
                getIntent()
                        .getStringExtra(
                                EXTRA_PAYMENT_DATE
                        );


        // =====================================================
        // SAFE FALLBACKS
        // =====================================================

        if (
                orderId == null
                        ||
                        orderId.trim().isEmpty()
        ) {

            orderId =
                    "Not Available";
        }


        /*
         * Your PaymentActivity correctly allows
         * paymentReference to remain null if the
         * PayHere SDK doesn't expose a payment number.
         *
         * Do not invent a fake PayHere reference.
         */
        if (
                paymentReference == null
                        ||
                        paymentReference.trim().isEmpty()
        ) {

            paymentReference =
                    "Not provided by PayHere";
        }


        if (
                paymentDate == null
                        ||
                        paymentDate.trim().isEmpty()
        ) {

            paymentDate =
                    new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss",
                            Locale.getDefault()
                    ).format(
                            new Date()
                    );
        }
    }


    // =========================================================
    // DISPLAY PAYMENT
    // =========================================================

    private void displayPaymentData() {

        txtSuccessAmount.setText(
                String.format(
                        Locale.getDefault(),
                        "LKR %,.2f",
                        amount
                )
        );


        txtSuccessOrderId.setText(
                orderId
        );


        txtSuccessReference.setText(
                paymentReference
        );


        txtSuccessRepairId.setText(
                String.valueOf(
                        repairId
                )
        );


        txtSuccessAppointmentId.setText(
                String.valueOf(
                        appointmentId
                )
        );


        txtSuccessDate.setText(
                paymentDate
        );
    }


    // =========================================================
    // SAVE RECEIPT
    // =========================================================

    private void saveReceiptAsPdf() {

        PdfDocument document =
                new PdfDocument();


        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(
                        595,
                        842,
                        1
                ).create();


        PdfDocument.Page page =
                document.startPage(
                        pageInfo
                );


        Canvas canvas =
                page.getCanvas();


        drawReceipt(
                canvas
        );


        document.finishPage(
                page
        );


        String safeOrderId =
                orderId.replaceAll(
                        "[^a-zA-Z0-9_-]",
                        "_"
                );


        String fileName =
                "TechFix_Receipt_"
                        +
                        safeOrderId
                        +
                        ".pdf";


        try {

            ContentValues values =
                    new ContentValues();


            values.put(
                    MediaStore.MediaColumns.DISPLAY_NAME,
                    fileName
            );


            values.put(
                    MediaStore.MediaColumns.MIME_TYPE,
                    "application/pdf"
            );


            values.put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    "Download/TechFix"
            );


            Uri receiptUri =
                    getContentResolver()
                            .insert(
                                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                                    values
                            );


            if (receiptUri == null) {

                throw new Exception(
                        "Unable to create receipt file"
                );
            }


            OutputStream outputStream =
                    getContentResolver()
                            .openOutputStream(
                                    receiptUri
                            );


            if (outputStream == null) {

                throw new Exception(
                        "Unable to open receipt file"
                );
            }


            document.writeTo(
                    outputStream
            );


            outputStream.flush();

            outputStream.close();

            document.close();


            Toast.makeText(
                    this,
                    "Receipt saved to Downloads/TechFix",
                    Toast.LENGTH_LONG
            ).show();


        } catch (Exception exception) {

            document.close();


            Toast.makeText(
                    this,
                    "Unable to save receipt: "
                            +
                            exception.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }


    // =========================================================
    // DRAW RECEIPT PDF
    // =========================================================

    private void drawReceipt(
            Canvas canvas
    ) {

        Paint paint =
                new Paint(
                        Paint.ANTI_ALIAS_FLAG
                );


        // =====================================================
        // HEADER
        // =====================================================

        paint.setColor(
                Color.BLACK
        );


        paint.setTextSize(
                34
        );


        paint.setFakeBoldText(
                true
        );


        canvas.drawText(
                "TechFix",
                50,
                70,
                paint
        );


        paint.setTextSize(
                17
        );


        paint.setFakeBoldText(
                false
        );


        canvas.drawText(
                "Mobile & Computer Repair Services",
                50,
                102,
                paint
        );


        paint.setStrokeWidth(
                2
        );


        canvas.drawLine(
                50,
                135,
                545,
                135,
                paint
        );


        // =====================================================
        // SUCCESS
        // =====================================================

        paint.setTextSize(
                23
        );


        paint.setFakeBoldText(
                true
        );


        canvas.drawText(
                "PAYMENT RECEIPT",
                50,
                185,
                paint
        );


        paint.setTextSize(
                16
        );


        paint.setFakeBoldText(
                false
        );


        canvas.drawText(
                "Payment Status",
                50,
                230,
                paint
        );


        paint.setFakeBoldText(
                true
        );


        canvas.drawText(
                "SUCCESS",
                250,
                230,
                paint
        );


        paint.setFakeBoldText(
                false
        );


        // =====================================================
        // DETAILS
        // =====================================================

        int y =
                285;


        drawReceiptRow(
                canvas,
                paint,
                "Order ID",
                orderId,
                y
        );


        y += 45;


        drawReceiptRow(
                canvas,
                paint,
                "PayHere Reference",
                paymentReference,
                y
        );


        y += 45;


        drawReceiptRow(
                canvas,
                paint,
                "Appointment ID",
                String.valueOf(
                        appointmentId
                ),
                y
        );


        y += 45;


        drawReceiptRow(
                canvas,
                paint,
                "Repair ID",
                String.valueOf(
                        repairId
                ),
                y
        );


        y += 45;


        drawReceiptRow(
                canvas,
                paint,
                "Date",
                paymentDate,
                y
        );


        // =====================================================
        // AMOUNT
        // =====================================================

        y += 70;


        canvas.drawLine(
                50,
                y,
                545,
                y,
                paint
        );


        y += 50;


        paint.setTextSize(
                20
        );


        paint.setFakeBoldText(
                true
        );


        canvas.drawText(
                "TOTAL PAID",
                50,
                y,
                paint
        );


        String formattedAmount =
                String.format(
                        Locale.getDefault(),
                        "LKR %,.2f",
                        amount
                );


        canvas.drawText(
                formattedAmount,
                340,
                y,
                paint
        );


        y += 45;


        canvas.drawLine(
                50,
                y,
                545,
                y,
                paint
        );


        // =====================================================
        // FOOTER
        // =====================================================

        y += 70;


        paint.setFakeBoldText(
                false
        );


        paint.setTextSize(
                15
        );


        canvas.drawText(
                "Thank you for choosing TechFix.",
                50,
                y,
                paint
        );


        y += 28;


        paint.setTextSize(
                12
        );


        canvas.drawText(
                "This is an electronically generated payment receipt.",
                50,
                y,
                paint
        );


        y += 22;


        canvas.drawText(
                "Please keep this receipt for your records.",
                50,
                y,
                paint
        );
    }


    // =========================================================
    // RECEIPT ROW
    // =========================================================

    private void drawReceiptRow(
            Canvas canvas,
            Paint paint,
            String label,
            String value,
            int y
    ) {

        paint.setTextSize(
                15
        );


        paint.setFakeBoldText(
                false
        );


        canvas.drawText(
                label,
                50,
                y,
                paint
        );


        paint.setFakeBoldText(
                true
        );


        /*
         * Protect against long references going
         * outside the PDF page.
         */
        String displayValue =
                value;


        if (
                displayValue != null
                        &&
                        displayValue.length() > 38
        ) {

            displayValue =
                    displayValue.substring(
                            0,
                            35
                    )
                            +
                            "...";
        }


        if (displayValue == null) {

            displayValue =
                    "N/A";
        }


        canvas.drawText(
                displayValue,
                220,
                y,
                paint
        );


        paint.setFakeBoldText(
                false
        );
    }
}