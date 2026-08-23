package com.techfix.app.utils;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.core.content.FileProvider;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class QRCodeUtils {

    private static final int DEFAULT_QR_SIZE =
            900;


    private QRCodeUtils() {

        // Utility class
    }


    // =========================================================
    // GENERATE QR
    // =========================================================

    public static Bitmap generateAppointmentQr(
            String appointmentCode
    ) throws WriterException {

        return generateAppointmentQr(
                appointmentCode,
                DEFAULT_QR_SIZE
        );
    }


    public static Bitmap generateAppointmentQr(
            String appointmentCode,
            int size
    ) throws WriterException {

        if (
                appointmentCode == null ||
                        appointmentCode.trim().isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Appointment code is required"
            );
        }


        String cleanCode =
                appointmentCode
                        .trim()
                        .toUpperCase();


        /*
         * IMPORTANT:
         *
         * The QR contains ONLY the appointment code.
         *
         * Example QR payload:
         *
         * TF-APT-000025
         *
         * No URL.
         * No customer information.
         * No appointment ID.
         * No JSON.
         */
        BarcodeEncoder barcodeEncoder =
                new BarcodeEncoder();


        return barcodeEncoder.encodeBitmap(
                cleanCode,
                BarcodeFormat.QR_CODE,
                size,
                size
        );
    }


    // =========================================================
    // SAVE QR FOR SHARING
    // =========================================================

    public static Uri createShareableQrUri(
            Context context,
            String appointmentCode
    ) throws WriterException, IOException {

        Bitmap bitmap =
                generateAppointmentQr(
                        appointmentCode
                );


        File qrDirectory =
                new File(
                        context.getCacheDir(),
                        "qr_codes"
                );


        if (
                !qrDirectory.exists() &&
                        !qrDirectory.mkdirs()
        ) {

            throw new IOException(
                    "Unable to create QR directory"
            );
        }


        String safeCode =
                appointmentCode
                        .trim()
                        .toUpperCase()
                        .replaceAll(
                                "[^A-Z0-9_-]",
                                "_"
                        );


        File qrFile =
                new File(
                        qrDirectory,
                        safeCode + ".png"
                );


        try (
                FileOutputStream outputStream =
                        new FileOutputStream(
                                qrFile
                        )
        ) {

            boolean success =
                    bitmap.compress(
                            Bitmap.CompressFormat.PNG,
                            100,
                            outputStream
                    );


            outputStream.flush();


            if (!success) {

                throw new IOException(
                        "Unable to create QR image"
                );
            }
        }


        return FileProvider.getUriForFile(
                context,
                context.getPackageName()
                        + ".fileprovider",
                qrFile
        );
    }


    // =========================================================
    // SHARE QR
    // =========================================================

    public static void shareAppointmentQr(
            Context context,
            String appointmentCode
    ) throws WriterException, IOException {

        Uri qrUri =
                createShareableQrUri(
                        context,
                        appointmentCode
                );


        String cleanCode =
                appointmentCode
                        .trim()
                        .toUpperCase();


        Intent shareIntent =
                new Intent(
                        Intent.ACTION_SEND
                );


        shareIntent.setType(
                "image/png"
        );


        shareIntent.putExtra(
                Intent.EXTRA_STREAM,
                qrUri
        );


        /*
         * Shared text is also only the appointment code.
         */
        shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                cleanCode
        );


        shareIntent.setClipData(
                ClipData.newRawUri(
                        "TechFix Appointment QR",
                        qrUri
                )
        );


        shareIntent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
        );


        Intent chooser =
                Intent.createChooser(
                        shareIntent,
                        "Share Appointment QR"
                );


        chooser.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
        );


        context.startActivity(
                chooser
        );
    }
}