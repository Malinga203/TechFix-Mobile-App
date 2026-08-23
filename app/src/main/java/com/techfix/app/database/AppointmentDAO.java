package com.techfix.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.techfix.app.models.Appointment;
import com.techfix.app.models.PartSelection;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AppointmentDAO {

    private final DatabaseHelper databaseHelper;

    public AppointmentDAO(
            Context context
    ) {
        databaseHelper =
                new DatabaseHelper(
                        context.getApplicationContext()
                );
    }

    /*
     * Existing single-appointment insert is kept for
     * compatibility with the rest of the application.
     */
    public long insertAppointment(
            Appointment appointment
    ) {
        return insertAppointmentWithParts(
                appointment,
                null
        );
    }

    /*
     * NEW:
     * Inserts the appointment and all selected spare parts
     * in one SQLite transaction.
     */
    public long insertAppointmentWithParts(
            Appointment appointment,
            List<PartSelection> selectedParts
    ) {

        if (appointment == null) {
            return -1;
        }

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        db.beginTransaction();

        try {

            ContentValues values =
                    new ContentValues();

            values.put(
                    DatabaseHelper.COLUMN_APPOINTMENT_USER_ID,
                    appointment.getUserId()
            );

            values.put(
                    DatabaseHelper.COLUMN_APPOINTMENT_SERVICE_ID,
                    appointment.getServiceId()
            );

            /*
             * Legacy single-part column is retained so older
             * installations/data still work. New bookings store
             * parts in appointment_spare_parts instead.
             */
            if (
                    selectedParts == null
                            ||
                            selectedParts.isEmpty()
            ) {

                if (appointment.getPartId() != null) {

                    values.put(
                            DatabaseHelper.COLUMN_APPOINTMENT_PART_ID,
                            appointment.getPartId()
                    );

                } else {

                    values.putNull(
                            DatabaseHelper.COLUMN_APPOINTMENT_PART_ID
                    );
                }

            } else {

                values.putNull(
                        DatabaseHelper.COLUMN_APPOINTMENT_PART_ID
                );
            }

            values.put(
                    DatabaseHelper.COLUMN_APPOINTMENT_BRANCH_ID,
                    appointment.getBranchId()
            );

            values.put(
                    DatabaseHelper.COLUMN_DEVICE_MODEL,
                    appointment.getDeviceModel()
            );

            values.put(
                    DatabaseHelper.COLUMN_ISSUE_DESCRIPTION,
                    appointment.getIssueDescription()
            );

            values.put(
                    DatabaseHelper.COLUMN_APPOINTMENT_DATE,
                    appointment.getAppointmentDate()
            );

            values.put(
                    DatabaseHelper.COLUMN_APPOINTMENT_TIME,
                    appointment.getAppointmentTime()
            );

            if (
                    appointment.getImageUri() != null
                            &&
                            !appointment.getImageUri()
                                    .trim()
                                    .isEmpty()
            ) {

                values.put(
                        DatabaseHelper.COLUMN_APPOINTMENT_IMAGE_URI,
                        appointment.getImageUri()
                );

            } else {

                values.putNull(
                        DatabaseHelper.COLUMN_APPOINTMENT_IMAGE_URI
                );
            }

            values.put(
                    DatabaseHelper.COLUMN_STATUS,
                    appointment.getStatus()
            );

            long id =
                    db.insert(
                            DatabaseHelper.TABLE_APPOINTMENT,
                            null,
                            values
                    );

            if (id <= 0) {
                return -1;
            }

            String code =
                    String.format(
                            Locale.US,
                            "TF-APT-%06d",
                            id
                    );

            ContentValues codeValues =
                    new ContentValues();

            codeValues.put(
                    DatabaseHelper.COLUMN_APPOINTMENT_CODE,
                    code
            );

            int codeUpdated =
                    db.update(
                            DatabaseHelper.TABLE_APPOINTMENT,
                            codeValues,
                            DatabaseHelper.COLUMN_APPOINTMENT_ID +
                                    " = ?",
                            new String[]{
                                    String.valueOf(
                                            id
                                    )
                            }
                    );

            if (codeUpdated <= 0) {
                return -1;
            }

            if (
                    selectedParts != null
                            &&
                            !selectedParts.isEmpty()
            ) {

                for (
                        PartSelection selection
                        :
                        selectedParts
                ) {

                    if (
                            selection == null
                                    ||
                                    selection.getPartId() <= 0
                                    ||
                                    selection.getQuantity() <= 0
                    ) {

                        return -1;
                    }

                    ContentValues partValues =
                            new ContentValues();

                    partValues.put(
                            DatabaseHelper.COLUMN_ASP_APPOINTMENT_ID,
                            id
                    );

                    partValues.put(
                            DatabaseHelper.COLUMN_ASP_PART_ID,
                            selection.getPartId()
                    );

                    partValues.put(
                            DatabaseHelper.COLUMN_ASP_QUANTITY,
                            selection.getQuantity()
                    );

                    long partResult =
                            db.insertWithOnConflict(
                                    DatabaseHelper.TABLE_APPOINTMENT_SPARE_PART,
                                    null,
                                    partValues,
                                    SQLiteDatabase.CONFLICT_REPLACE
                            );

                    if (partResult == -1) {
                        return -1;
                    }
                }
            }

            db.setTransactionSuccessful();

            return id;

        } finally {

            db.endTransaction();
        }
    }

    public Appointment getAppointmentByCode(
            String appointmentCode
    ) {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_APPOINTMENT,
                        null,
                        DatabaseHelper.COLUMN_APPOINTMENT_CODE +
                                " = ?",
                        new String[]{
                                appointmentCode
                        },
                        null,
                        null,
                        null
                );

        try {

            if (cursor.moveToFirst()) {

                return mapCursorToAppointment(
                        cursor
                );
            }

        } finally {

            cursor.close();
        }

        return null;
    }

    public Appointment getAppointmentById(
            int appointmentId
    ) {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_APPOINTMENT,
                        null,
                        DatabaseHelper.COLUMN_APPOINTMENT_ID +
                                " = ?",
                        new String[]{
                                String.valueOf(
                                        appointmentId
                                )
                        },
                        null,
                        null,
                        null
                );

        try {

            if (cursor.moveToFirst()) {

                return mapCursorToAppointment(
                        cursor
                );
            }

        } finally {

            cursor.close();
        }

        return null;
    }

    public boolean markAppointmentAsAccepted(
            int appointmentId
    ) {

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                DatabaseHelper.COLUMN_STATUS,
                "ACCEPTED"
        );

        int rows =
                db.update(
                        DatabaseHelper.TABLE_APPOINTMENT,
                        values,
                        DatabaseHelper.COLUMN_APPOINTMENT_ID +
                                " = ?",
                        new String[]{
                                String.valueOf(
                                        appointmentId
                                )
                        }
                );

        return rows > 0;
    }

    public List<Appointment> getAppointmentsByUser(
            int userId
    ) {

        List<Appointment> appointments =
                new ArrayList<>();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        String today =
                new java.text.SimpleDateFormat(
                        "yyyy-MM-dd",
                        java.util.Locale.US
                ).format(
                        new java.util.Date()
                );

        String selection =
                DatabaseHelper.COLUMN_APPOINTMENT_USER_ID +
                        " = ? AND " +
                        DatabaseHelper.COLUMN_APPOINTMENT_DATE +
                        " >= ?";

        String[] selectionArgs = {
                String.valueOf(
                        userId
                ),
                today
        };

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_APPOINTMENT,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        DatabaseHelper.COLUMN_APPOINTMENT_DATE +
                                " ASC, " +
                                DatabaseHelper.COLUMN_APPOINTMENT_TIME +
                                " ASC"
                );

        try {

            while (cursor.moveToNext()) {

                appointments.add(
                        mapCursorToAppointment(
                                cursor
                        )
                );
            }

        } finally {

            cursor.close();
        }

        return appointments;
    }

    public int getAppointmentCountForSlot(
            String date,
            String time
    ) {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_APPOINTMENT,
                        new String[]{
                                "COUNT(*)"
                        },
                        DatabaseHelper.COLUMN_APPOINTMENT_DATE +
                                " = ? AND " +
                                DatabaseHelper.COLUMN_APPOINTMENT_TIME +
                                " = ?",
                        new String[]{
                                date,
                                time
                        },
                        null,
                        null,
                        null
                );

        try {

            if (cursor.moveToFirst()) {

                return cursor.getInt(
                        0
                );
            }

        } finally {

            cursor.close();
        }

        return 0;
    }

    private Appointment mapCursorToAppointment(
            Cursor cursor
    ) {

        Appointment appointment =
                new Appointment();

        appointment.setAppointmentId(
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_APPOINTMENT_ID
                        )
                )
        );

        appointment.setUserId(
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_APPOINTMENT_USER_ID
                        )
                )
        );

        appointment.setServiceId(
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_APPOINTMENT_SERVICE_ID
                        )
                )
        );

        int partIndex =
                cursor.getColumnIndex(
                        DatabaseHelper.COLUMN_APPOINTMENT_PART_ID
                );

        if (
                partIndex >= 0
                        &&
                        !cursor.isNull(
                                partIndex
                        )
        ) {

            appointment.setPartId(
                    cursor.getInt(
                            partIndex
                    )
            );
        }

        appointment.setBranchId(
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_APPOINTMENT_BRANCH_ID
                        )
                )
        );

        int codeIndex =
                cursor.getColumnIndex(
                        DatabaseHelper.COLUMN_APPOINTMENT_CODE
                );

        if (
                codeIndex >= 0
                        &&
                        !cursor.isNull(
                                codeIndex
                        )
        ) {

            appointment.setAppointmentCode(
                    cursor.getString(
                            codeIndex
                    )
            );
        }

        appointment.setDeviceModel(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_DEVICE_MODEL
                        )
                )
        );

        appointment.setIssueDescription(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_ISSUE_DESCRIPTION
                        )
                )
        );

        appointment.setAppointmentDate(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_APPOINTMENT_DATE
                        )
                )
        );

        appointment.setAppointmentTime(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_APPOINTMENT_TIME
                        )
                )
        );

        int imageIndex =
                cursor.getColumnIndex(
                        DatabaseHelper.COLUMN_APPOINTMENT_IMAGE_URI
                );

        if (
                imageIndex >= 0
                        &&
                        !cursor.isNull(
                                imageIndex
                        )
        ) {

            appointment.setImageUri(
                    cursor.getString(
                            imageIndex
                    )
            );
        }

        appointment.setStatus(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_STATUS
                        )
                )
        );

        return appointment;
    }

    public void close() {
        databaseHelper.close();
    }
}
