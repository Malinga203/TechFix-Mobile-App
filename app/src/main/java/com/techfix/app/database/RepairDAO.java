package com.techfix.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.techfix.app.models.Repair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RepairDAO {

    private final DatabaseHelper databaseHelper;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public RepairDAO(Context context) {

        databaseHelper =
                new DatabaseHelper(
                        context.getApplicationContext()
                );
    }


    // =========================================================
    // INSERT REPAIR
    // =========================================================

    public long insertRepair(Repair repair) {

        if (!isValidRepair(repair)) {
            return -1;
        }

        if (TextUtils.isEmpty(repair.getStatus())) {

            repair.setStatus(
                    Repair.STATUS_PENDING
            );
        }

        String now =
                getCurrentTimestamp();

        if (TextUtils.isEmpty(
                repair.getCreatedAt()
        )) {

            repair.setCreatedAt(now);
        }

        if (TextUtils.isEmpty(
                repair.getUpdatedAt()
        )) {

            repair.setUpdatedAt(now);
        }

        if (
                Repair.STATUS_COMPLETED.equals(
                        repair.getStatus()
                )
                        &&
                        TextUtils.isEmpty(
                                repair.getCompletedAt()
                        )
        ) {

            repair.setCompletedAt(now);
        }


        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();


        // Appointment ID
        putOptionalLong(
                values,
                DatabaseHelper.COLUMN_REPAIR_APPOINTMENT_ID,
                repair.getAppointmentId()
        );


        // Customer ID
        putOptionalLong(
                values,
                DatabaseHelper.COLUMN_REPAIR_CUSTOMER_ID,
                repair.getCustomerId()
        );


        // Branch ID
        putOptionalLong(
                values,
                DatabaseHelper.COLUMN_REPAIR_BRANCH_ID,
                repair.getBranchId()
        );


        // Technician ID
        putOptionalLong(
                values,
                DatabaseHelper.COLUMN_REPAIR_TECHNICIAN_ID,
                repair.getTechnicianId()
        );


        // Device
        values.put(
                DatabaseHelper.COLUMN_REPAIR_DEVICE_NAME,
                repair.getDeviceName().trim()
        );


        // Service
        values.put(
                DatabaseHelper.COLUMN_REPAIR_SERVICE_NAME,
                repair.getServiceName().trim()
        );


        // Problem description
        values.put(
                DatabaseHelper.COLUMN_REPAIR_PROBLEM_DESCRIPTION,
                safeTrim(
                        repair.getProblemDescription()
                )
        );


        // Status
        values.put(
                DatabaseHelper.COLUMN_REPAIR_STATUS,
                repair.getStatus()
        );


        // Image
        if (!TextUtils.isEmpty(
                repair.getImageUri()
        )) {

            values.put(
                    DatabaseHelper.COLUMN_REPAIR_IMAGE_URI,
                    repair.getImageUri().trim()
            );
        }


        // Estimated cost
        values.put(
                DatabaseHelper.COLUMN_REPAIR_ESTIMATED_COST,
                repair.getEstimatedCost()
        );


        // Final cost
        values.put(
                DatabaseHelper.COLUMN_REPAIR_FINAL_COST,
                repair.getFinalCost()
        );


        // Created
        values.put(
                DatabaseHelper.COLUMN_REPAIR_CREATED_AT,
                repair.getCreatedAt()
        );


        // Updated
        values.put(
                DatabaseHelper.COLUMN_REPAIR_UPDATED_AT,
                repair.getUpdatedAt()
        );


        // Completed
        if (!TextUtils.isEmpty(
                repair.getCompletedAt()
        )) {

            values.put(
                    DatabaseHelper.COLUMN_REPAIR_COMPLETED_AT,
                    repair.getCompletedAt()
            );
        }


        return db.insert(
                DatabaseHelper.TABLE_REPAIR,
                null,
                values
        );
    }


    // =========================================================
    // GET ACTIVE REPAIRS
    // =========================================================

    public List<Repair> getActiveRepairs() {

        List<Repair> repairs =
                new ArrayList<>();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_REPAIR,
                        null,

                        DatabaseHelper.COLUMN_REPAIR_STATUS +
                                " != ?",

                        new String[]{
                                Repair.STATUS_COMPLETED
                        },

                        null,
                        null,

                        DatabaseHelper.COLUMN_REPAIR_CREATED_AT +
                                " DESC"
                );

        try {

            while (cursor.moveToNext()) {

                repairs.add(
                        cursorToRepair(cursor)
                );
            }

        } finally {

            cursor.close();
        }

        return repairs;
    }


    // =========================================================
    // GET REPAIR HISTORY
    // =========================================================

    public List<Repair> getRepairHistory() {

        List<Repair> repairs =
                new ArrayList<>();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_REPAIR,
                        null,

                        DatabaseHelper.COLUMN_REPAIR_STATUS +
                                " = ?",

                        new String[]{
                                Repair.STATUS_COMPLETED
                        },

                        null,
                        null,

                        DatabaseHelper.COLUMN_REPAIR_COMPLETED_AT +
                                " DESC"
                );

        try {

            while (cursor.moveToNext()) {

                repairs.add(
                        cursorToRepair(cursor)
                );
            }

        } finally {

            cursor.close();
        }

        return repairs;
    }


    // =========================================================
    // GET REPAIR BY ID
    // =========================================================

    public Repair getRepairById(
            long repairId
    ) {

        if (repairId <= 0) {
            return null;
        }

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_REPAIR,
                        null,

                        DatabaseHelper.COLUMN_REPAIR_ID +
                                " = ?",

                        new String[]{
                                String.valueOf(repairId)
                        },

                        null,
                        null,
                        null,
                        "1"
                );

        try {

            if (cursor.moveToFirst()) {

                return cursorToRepair(
                        cursor
                );
            }

        } finally {

            cursor.close();
        }

        return null;
    }


    // =========================================================
    // UPDATE REPAIR STATUS
    // =========================================================

    public boolean updateRepairStatus(
            long repairId,
            String newStatus
    ) {

        if (
                repairId <= 0
                        ||
                        !Repair.isValidStatus(
                                newStatus
                        )
        ) {

            return false;
        }


        Repair currentRepair =
                getRepairById(
                        repairId
                );

        if (currentRepair == null) {
            return false;
        }


        if (!Repair.canTransition(
                currentRepair.getStatus(),
                newStatus
        )) {

            return false;
        }


        String now =
                getCurrentTimestamp();

        ContentValues values =
                new ContentValues();


        values.put(
                DatabaseHelper.COLUMN_REPAIR_STATUS,
                newStatus
        );


        values.put(
                DatabaseHelper.COLUMN_REPAIR_UPDATED_AT,
                now
        );


        if (Repair.STATUS_COMPLETED.equals(
                newStatus
        )) {

            values.put(
                    DatabaseHelper.COLUMN_REPAIR_COMPLETED_AT,
                    now
            );
        }


        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();


        int rows =
                db.update(
                        DatabaseHelper.TABLE_REPAIR,
                        values,

                        DatabaseHelper.COLUMN_REPAIR_ID +
                                " = ?",

                        new String[]{
                                String.valueOf(
                                        repairId
                                )
                        }
                );


        return rows > 0;
    }


    // =========================================================
    // UPDATE REPAIR IMAGE
    // =========================================================

    public boolean updateRepairImageUri(
            long repairId,
            String imageUri
    ) {

        if (
                repairId <= 0
                        ||
                        TextUtils.isEmpty(
                                imageUri
                        )
        ) {

            return false;
        }


        ContentValues values =
                new ContentValues();


        values.put(
                DatabaseHelper.COLUMN_REPAIR_IMAGE_URI,
                imageUri.trim()
        );


        values.put(
                DatabaseHelper.COLUMN_REPAIR_UPDATED_AT,
                getCurrentTimestamp()
        );


        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();


        int rows =
                db.update(
                        DatabaseHelper.TABLE_REPAIR,
                        values,

                        DatabaseHelper.COLUMN_REPAIR_ID +
                                " = ?",

                        new String[]{
                                String.valueOf(
                                        repairId
                                )
                        }
                );


        return rows > 0;
    }


    // =========================================================
    // VALIDATE REPAIR
    // =========================================================

    private boolean isValidRepair(
            Repair repair
    ) {

        if (repair == null) {
            return false;
        }


        if (
                TextUtils.isEmpty(
                        repair.getDeviceName()
                )
                        ||
                        repair.getDeviceName()
                                .trim()
                                .length() < 2
        ) {

            return false;
        }


        if (
                TextUtils.isEmpty(
                        repair.getServiceName()
                )
                        ||
                        repair.getServiceName()
                                .trim()
                                .length() < 2
        ) {

            return false;
        }


        if (
                !TextUtils.isEmpty(
                        repair.getProblemDescription()
                )
                        &&
                        repair.getProblemDescription()
                                .trim()
                                .length() > 500
        ) {

            return false;
        }


        if (
                !TextUtils.isEmpty(
                        repair.getStatus()
                )
                        &&
                        !Repair.isValidStatus(
                                repair.getStatus()
                        )
        ) {

            return false;
        }


        return repair.getEstimatedCost() >= 0
                &&
                repair.getFinalCost() >= 0;
    }


    // =========================================================
    // OPTIONAL LONG
    // =========================================================

    private void putOptionalLong(
            ContentValues values,
            String column,
            long value
    ) {

        if (value > 0) {

            values.put(
                    column,
                    value
            );

        } else {

            values.putNull(
                    column
            );
        }
    }


    // =========================================================
    // CURSOR -> REPAIR
    // =========================================================

    private Repair cursorToRepair(
            Cursor cursor
    ) {

        Repair repair =
                new Repair();


        // Repair ID
        repair.setRepairId(
                cursor.getLong(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_REPAIR_ID
                        )
                )
        );


        // Appointment
        repair.setAppointmentId(
                getNullableLong(
                        cursor,
                        DatabaseHelper.COLUMN_REPAIR_APPOINTMENT_ID
                )
        );


        // Customer
        repair.setCustomerId(
                getNullableLong(
                        cursor,
                        DatabaseHelper.COLUMN_REPAIR_CUSTOMER_ID
                )
        );


        // Branch
        repair.setBranchId(
                getNullableLong(
                        cursor,
                        DatabaseHelper.COLUMN_REPAIR_BRANCH_ID
                )
        );


        // Technician
        repair.setTechnicianId(
                getNullableLong(
                        cursor,
                        DatabaseHelper.COLUMN_REPAIR_TECHNICIAN_ID
                )
        );


        // Device
        repair.setDeviceName(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_REPAIR_DEVICE_NAME
                        )
                )
        );


        // Service
        repair.setServiceName(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_REPAIR_SERVICE_NAME
                        )
                )
        );


        // Problem
        repair.setProblemDescription(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_REPAIR_PROBLEM_DESCRIPTION
                        )
                )
        );


        // Status
        repair.setStatus(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_REPAIR_STATUS
                        )
                )
        );


        // Image
        repair.setImageUri(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_REPAIR_IMAGE_URI
                        )
                )
        );


        // Estimated cost
        repair.setEstimatedCost(
                cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_REPAIR_ESTIMATED_COST
                        )
                )
        );


        // Final cost
        repair.setFinalCost(
                cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_REPAIR_FINAL_COST
                        )
                )
        );


        // Created
        repair.setCreatedAt(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_REPAIR_CREATED_AT
                        )
                )
        );


        // Updated
        repair.setUpdatedAt(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_REPAIR_UPDATED_AT
                        )
                )
        );


        // Completed
        repair.setCompletedAt(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_REPAIR_COMPLETED_AT
                        )
                )
        );


        return repair;
    }


    // =========================================================
    // NULLABLE LONG
    // =========================================================

    private long getNullableLong(
            Cursor cursor,
            String columnName
    ) {

        int index =
                cursor.getColumnIndexOrThrow(
                        columnName
                );


        if (cursor.isNull(index)) {
            return 0;
        }


        return cursor.getLong(
                index
        );
    }


    // =========================================================
    // SAFE TRIM
    // =========================================================

    private String safeTrim(
            String value
    ) {

        if (value == null) {
            return "";
        }

        return value.trim();
    }


    // =========================================================
    // CURRENT TIMESTAMP
    // =========================================================

    private String getCurrentTimestamp() {

        return new SimpleDateFormat(
                "yyyy-MM-dd HH:mm",
                Locale.getDefault()
        ).format(
                new Date()
        );
    }

    public Repair getRepairByAppointmentId(
            long appointmentId
    ) {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_REPAIR,
                        null,
                        DatabaseHelper.COLUMN_REPAIR_APPOINTMENT_ID +
                                " = ?",
                        new String[]{
                                String.valueOf(
                                        appointmentId
                                )
                        },
                        null,
                        null,
                        null,
                        "1"
                );

        try {

            if (cursor.moveToFirst()) {

                return cursorToRepair(
                        cursor
                );
            }

        } finally {

            cursor.close();
        }

        return null;
    }

    public List<Repair> getRepairsByTechnician(
            long technicianId
    ) {

        List<Repair> repairs =
                new ArrayList<>();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_REPAIR,
                        null,
                        DatabaseHelper.COLUMN_REPAIR_TECHNICIAN_ID +
                                " = ?",
                        new String[]{
                                String.valueOf(
                                        technicianId
                                )
                        },
                        null,
                        null,
                        DatabaseHelper.COLUMN_REPAIR_UPDATED_AT +
                                " DESC"
                );

        try {

            while (cursor.moveToNext()) {

                repairs.add(
                        cursorToRepair(
                                cursor
                        )
                );
            }

        } finally {

            cursor.close();
        }

        return repairs;
    }

    public boolean updateRepairCosts(
            long repairId,
            double estimatedCost,
            double finalCost
    ) {

        if (
                repairId <= 0 ||
                        estimatedCost < 0 ||
                        finalCost < 0
        ) {

            return false;
        }

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                DatabaseHelper.COLUMN_REPAIR_ESTIMATED_COST,
                estimatedCost
        );

        values.put(
                DatabaseHelper.COLUMN_REPAIR_FINAL_COST,
                finalCost
        );

        values.put(
                DatabaseHelper.COLUMN_REPAIR_UPDATED_AT,
                getCurrentTimestamp()
        );

        int rows =
                db.update(
                        DatabaseHelper.TABLE_REPAIR,
                        values,
                        DatabaseHelper.COLUMN_REPAIR_ID +
                                " = ?",
                        new String[]{
                                String.valueOf(
                                        repairId
                                )
                        }
                );

        return rows > 0;
    }

    // =========================================================
    // CLOSE
    // =========================================================

    public void close() {

        databaseHelper.close();
    }
}