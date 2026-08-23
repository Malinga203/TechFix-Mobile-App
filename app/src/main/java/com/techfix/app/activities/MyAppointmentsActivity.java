package com.techfix.app.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.techfix.app.R;
import com.techfix.app.adapters.AppointmentAdapter;
import com.techfix.app.database.AppointmentDAO;
import com.techfix.app.models.Appointment;
import com.techfix.app.userauthentication.utils.SessionManager;
import com.techfix.app.utils.QRCodeUtils;

import java.util.List;

public class MyAppointmentsActivity
        extends AppCompatActivity
        implements AppointmentAdapter.OnAppointmentActionListener {

    private RecyclerView recyclerAppointments;

    private TextView txtNoAppointments;


    private AppointmentDAO appointmentDAO;

    private SessionManager sessionManager;


    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );


        setContentView(
                R.layout.activity_my_appointments
        );


        sessionManager =
                new SessionManager(
                        this
                );


        if (
                !sessionManager.isLoggedIn() ||
                        !sessionManager.isCustomer()
        ) {

            finish();

            return;
        }


        recyclerAppointments =
                findViewById(
                        R.id.recyclerAppointments
                );


        txtNoAppointments =
                findViewById(
                        R.id.txtNoAppointments
                );


        appointmentDAO =
                new AppointmentDAO(
                        this
                );


        recyclerAppointments.setLayoutManager(
                new LinearLayoutManager(
                        this
                )
        );
    }


    @Override
    protected void onResume() {

        super.onResume();


        loadAppointments();
    }


    // =========================================================
    // LOAD APPOINTMENTS
    // =========================================================

    private void loadAppointments() {

        int userId =
                sessionManager.getUserId();


        List<Appointment> appointments =
                appointmentDAO
                        .getAppointmentsByUser(
                                userId
                        );


        if (
                appointments == null ||
                        appointments.isEmpty()
        ) {

            txtNoAppointments.setVisibility(
                    View.VISIBLE
            );


            recyclerAppointments.setVisibility(
                    View.GONE
            );


            return;
        }


        txtNoAppointments.setVisibility(
                View.GONE
        );


        recyclerAppointments.setVisibility(
                View.VISIBLE
        );


        AppointmentAdapter adapter =
                new AppointmentAdapter(
                        appointments,
                        this
                );


        recyclerAppointments.setAdapter(
                adapter
        );
    }


    // =========================================================
    // VIEW QR
    // =========================================================

    @Override
    public void onViewQr(
            Appointment appointment
    ) {

        if (
                appointment == null ||
                        appointment.getAppointmentCode() == null ||
                        appointment.getAppointmentCode()
                                .trim()
                                .isEmpty()
        ) {

            Toast.makeText(
                    this,
                    "Appointment code is not available",
                    Toast.LENGTH_SHORT
            ).show();


            return;
        }


        showQrDialog(
                appointment
        );
    }


    // =========================================================
    // SHARE QR
    // =========================================================

    @Override
    public void onShareQr(
            Appointment appointment
    ) {

        if (
                appointment == null ||
                        appointment.getAppointmentCode() == null ||
                        appointment.getAppointmentCode()
                                .trim()
                                .isEmpty()
        ) {

            Toast.makeText(
                    this,
                    "Appointment code is not available",
                    Toast.LENGTH_SHORT
            ).show();


            return;
        }


        try {

            QRCodeUtils.shareAppointmentQr(
                    this,
                    appointment.getAppointmentCode()
            );


        } catch (
                Exception exception
        ) {

            Toast.makeText(
                    this,
                    "Unable to share QR code",
                    Toast.LENGTH_LONG
            ).show();
        }
    }


    // =========================================================
    // QR DIALOG
    // =========================================================

    private void showQrDialog(
            Appointment appointment
    ) {

        String appointmentCode =
                appointment
                        .getAppointmentCode()
                        .trim()
                        .toUpperCase();


        try {

            Bitmap qrBitmap =
                    QRCodeUtils
                            .generateAppointmentQr(
                                    appointmentCode
                            );


            LinearLayout container =
                    new LinearLayout(
                            this
                    );


            container.setOrientation(
                    LinearLayout.VERTICAL
            );


            int padding =
                    (int) (
                            20 *
                                    getResources()
                                            .getDisplayMetrics()
                                            .density
                    );


            container.setPadding(
                    padding,
                    padding,
                    padding,
                    padding
            );


            // =================================================
            // QR IMAGE
            // =================================================

            ImageView qrImage =
                    new ImageView(
                            this
                    );


            qrImage.setImageBitmap(
                    qrBitmap
            );


            qrImage.setAdjustViewBounds(
                    true
            );


            qrImage.setScaleType(
                    ImageView.ScaleType.FIT_CENTER
            );


            container.addView(
                    qrImage,

                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    )
            );


            // =================================================
            // CODE TEXT
            // =================================================

            TextView codeText =
                    new TextView(
                            this
                    );


            codeText.setText(
                    appointmentCode
            );


            codeText.setTextSize(
                    20
            );


            codeText.setGravity(
                    android.view.Gravity.CENTER
            );


            codeText.setPadding(
                    0,
                    padding,
                    0,
                    0
            );


            container.addView(
                    codeText
            );


            // =================================================
            // INFO
            // =================================================

            TextView infoText =
                    new TextView(
                            this
                    );


            infoText.setText(
                    "Show this QR code to the technician."
            );


            infoText.setGravity(
                    android.view.Gravity.CENTER
            );


            infoText.setPadding(
                    0,
                    padding / 2,
                    0,
                    0
            );


            container.addView(
                    infoText
            );


            new MaterialAlertDialogBuilder(
                    this
            )

                    .setTitle(
                            "Appointment QR"
                    )

                    .setView(
                            container
                    )

                    .setNeutralButton(
                            "Share QR",

                            (
                                    dialog,
                                    which
                            ) -> {

                                onShareQr(
                                        appointment
                                );
                            }
                    )

                    .setPositiveButton(
                            "Close",
                            null
                    )

                    .show();


        } catch (
                Exception exception
        ) {

            Toast.makeText(
                    this,
                    "Unable to generate QR code",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}