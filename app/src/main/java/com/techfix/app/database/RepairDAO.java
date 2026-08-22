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

    public RepairDAO(Context context) {
        databaseHelper =
                new DatabaseHelper(
                        context.getApplicationContext()
                );
    }

    public long insertRepair(Repair repair) {

        if (!isValidRepair(repair)) {
            return -1;
        }

        if (TextUtils.isEmpty(repair.getStatus())) {
            repair.setStatus(Repair.STATUS_PENDING);
        }

        String now = getCurrentTimestamp();

        if (TextUtils.isEmpty(repair.getCreatedAt())) {
            repair.setCreatedAt(now);
        }

        if (TextUtils.isEmpty(repair.getUpdatedAt())) {
            repair.setUpdatedAt(now);
        }

        if (Repair.STATUS_COMPLETED.equals(repair.getStatus())
                && TextUtils.isEmpty(repair.getCompletedAt())) {

            repair.setCompletedAt(now);
        }

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        putOptionalLong(
                values,
                DatabaseHelper.COLUMN_APPOINTMENT_ID,
                repair.getAppointmentId()
        );

        putOptionalLong(
                values,
                DatabaseHelper.COLUMN_CUSTOMER_ID,
                repair.getCustomerId()
        );

        putOptionalLong(
                values,
                DatabaseHelper.COLUMN_BRANCH_ID,
                repair.getBranchId()
        );

        putOptionalLong(
                values,
                DatabaseHelper.COLUMN_TECHNICIAN_ID,
                repair.getTechnicianId()
        );

        values.put(
                DatabaseHelper.COLUMN_DEVICE_NAME,
                repair.getDeviceName().trim()
        );

        values.put(
                DatabaseHelper.COLUMN_SERVICE_NAME,
                repair.getServiceName().trim()
        );

        values.put(
                DatabaseHelper.COLUMN_PROBLEM_DESCRIPTION,
                safeTrim(
                        repair.getProblemDescription()
                )
        );

        values.put(
                DatabaseHelper.COLUMN_STATUS,
                repair.getStatus()
        );

        if (!TextUtils.isEmpty(repair.getImageUri())) {

            values.put(
                    DatabaseHelper.COLUMN_IMAGE_URI,
                    repair.getImageUri().trim()
            );
        }

        values.put(
                DatabaseHelper.COLUMN_ESTIMATED_COST,
                repair.getEstimatedCost()
        );

        values.put(
                DatabaseHelper.COLUMN_FINAL_COST,
                repair.getFinalCost()
        );

        values.put(
                DatabaseHelper.COLUMN_CREATED_AT,
                repair.getCreatedAt()
        );

        values.put(
                DatabaseHelper.COLUMN_UPDATED_AT,
                repair.getUpdatedAt()
        );

        if (!TextUtils.isEmpty(repair.getCompletedAt())) {

            values.put(
                    DatabaseHelper.COLUMN_COMPLETED_AT,
                    repair.getCompletedAt()
            );
        }

        return db.insert(
                DatabaseHelper.TABLE_REPAIR,
                null,
                values
        );
    }

    public List<Repair> getActiveRepairs() {

        List<Repair> repairs =
                new ArrayList<>();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_REPAIR,
                        null,
                        DatabaseHelper.COLUMN_STATUS + " != ?",
                        new String[]{
                                Repair.STATUS_COMPLETED
                        },
                        null,
                        null,
                        DatabaseHelper.COLUMN_CREATED_AT + " DESC"
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

    public List<Repair> getRepairHistory() {

        List<Repair> repairs =
                new ArrayList<>();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_REPAIR,
                        null,
                        DatabaseHelper.COLUMN_STATUS + " = ?",
                        new String[]{
                                Repair.STATUS_COMPLETED
                        },
                        null,
                        null,
                        DatabaseHelper.COLUMN_COMPLETED_AT + " DESC"
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

    public Repair getRepairById(long repairId) {

        if (repairId <= 0) {
            return null;
        }

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_REPAIR,
                        null,
                        DatabaseHelper.COLUMN_REPAIR_ID + " = ?",
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
                return cursorToRepair(cursor);
            }

        } finally {

            cursor.close();
        }

        return null;
    }

    public boolean updateRepairStatus(
            long repairId,
            String newStatus
    ) {

        if (repairId <= 0
                || !Repair.isValidStatus(newStatus)) {

            return false;
        }

        Repair currentRepair =
                getRepairById(repairId);

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
                DatabaseHelper.COLUMN_STATUS,
                newStatus
        );

        values.put(
                DatabaseHelper.COLUMN_UPDATED_AT,
                now
        );

        if (Repair.STATUS_COMPLETED.equals(newStatus)) {

            values.put(
                    DatabaseHelper.COLUMN_COMPLETED_AT,
                    now
            );
        }

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        int rows =
                db.update(
                        DatabaseHelper.TABLE_REPAIR,
                        values,
                        DatabaseHelper.COLUMN_REPAIR_ID + " = ?",
                        new String[]{
                                String.valueOf(repairId)
                        }
                );

        return rows > 0;
    }

    public boolean updateRepairImageUri(
            long repairId,
            String imageUri
    ) {

        if (repairId <= 0
                || TextUtils.isEmpty(imageUri)) {

            return false;
        }

        ContentValues values =
                new ContentValues();

        values.put(
                DatabaseHelper.COLUMN_IMAGE_URI,
                imageUri.trim()
        );

        values.put(
                DatabaseHelper.COLUMN_UPDATED_AT,
                getCurrentTimestamp()
        );

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        int rows =
                db.update(
                        DatabaseHelper.TABLE_REPAIR,
                        values,
                        DatabaseHelper.COLUMN_REPAIR_ID + " = ?",
                        new String[]{
                                String.valueOf(repairId)
                        }
                );

        return rows > 0;
    }

    private boolean isValidRepair(Repair repair) {

        if (repair == null) {
            return false;
        }

        if (TextUtils.isEmpty(repair.getDeviceName())
                || repair.getDeviceName().trim().length() < 2) {

            return false;
        }

        if (TextUtils.isEmpty(repair.getServiceName())
                || repair.getServiceName().trim().length() < 2) {

            return false;
        }

        if (!TextUtils.isEmpty(repair.getProblemDescription())
                && repair.getProblemDescription()
                .trim()
                .length() > 500) {

            return false;
        }

        if (!TextUtils.isEmpty(repair.getStatus())
                && !Repair.isValidStatus(
                repair.getStatus()
        )) {

            return false;
        }

        return repair.getEstimatedCost() >= 0
                && repair.getFinalCost() >= 0;
    }

    // Optional IDs remain null until another module assigns them.
    private void putOptionalLong(
            ContentValues values,
            String column,
            long value
    ) {

        if (value > 0) {
            values.put(column, value);
        } else {
            values.putNull(column);
        }
    }

    private Repair cursorToRepair(
            Cursor cursor
    ) {

        Repair repair =
                new Repair();

        repair.setRepairId(
                cursor.getLong(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_REPAIR_ID
                        )
                )
        );

        repair.setAppointmentId(
                getNullableLong(
                        cursor,
                        DatabaseHelper.COLUMN_APPOINTMENT_ID
                )
        );

        repair.setCustomerId(
                getNullableLong(
                        cursor,
                        DatabaseHelper.COLUMN_CUSTOMER_ID
                )
        );

        repair.setBranchId(
                getNullableLong(
                        cursor,
                        DatabaseHelper.COLUMN_BRANCH_ID
                )
        );

        repair.setTechnicianId(
                getNullableLong(
                        cursor,
                        DatabaseHelper.COLUMN_TECHNICIAN_ID
                )
        );

        repair.setDeviceName(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_DEVICE_NAME
                        )
                )
        );

        repair.setServiceName(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_SERVICE_NAME
                        )
                )
        );

        repair.setProblemDescription(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_PROBLEM_DESCRIPTION
                        )
                )
        );

        repair.setStatus(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_STATUS
                        )
                )
        );

        repair.setImageUri(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_IMAGE_URI
                        )
                )
        );

        repair.setEstimatedCost(
                cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_ESTIMATED_COST
                        )
                )
        );

        repair.setFinalCost(
                cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_FINAL_COST
                        )
                )
        );

        repair.setCreatedAt(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_CREATED_AT
                        )
                )
        );

        repair.setUpdatedAt(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_UPDATED_AT
                        )
                )
        );

        repair.setCompletedAt(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_COMPLETED_AT
                        )
                )
        );

        return repair;
    }

    private long getNullableLong(
            Cursor cursor,
            String columnName
    ) {

        int index =
                cursor.getColumnIndexOrThrow(
                        columnName
                );

        return cursor.isNull(index)
                ? 0
                : cursor.getLong(index);
    }

    private String safeTrim(String value) {

        return value == null
                ? ""
                : value.trim();
    }

    private String getCurrentTimestamp() {

        return new SimpleDateFormat(
                "yyyy-MM-dd HH:mm",
                Locale.getDefault()
        ).format(new Date());
    }

    public void close() {
        databaseHelper.close();
    }
}