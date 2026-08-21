package com.techfix.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.techfix.app.models.Appointment;

import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    private final DatabaseHelper databaseHelper;

    public AppointmentDAO(Context context) {
        databaseHelper =
                new DatabaseHelper(context);
    }

    public long insertAppointment(Appointment appointment) {

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

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

        values.put(
                DatabaseHelper.COLUMN_STATUS,
                appointment.getStatus()
        );

        return db.insert(
                DatabaseHelper.TABLE_APPOINTMENT,
                null,
                values
        );
    }

    public List<Appointment> getAppointmentsByUser(int userId) {

        List<Appointment> appointments =
                new ArrayList<>();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_APPOINTMENT,
                null,
                DatabaseHelper.COLUMN_APPOINTMENT_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                DatabaseHelper.COLUMN_APPOINTMENT_DATE + " DESC"
        );

        if (cursor.moveToFirst()) {

            do {

                Appointment appointment = mapCursorToAppointment(cursor);

                appointments.add(appointment);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return appointments;
    }

    public int getAppointmentCountForSlot(String date, String time) {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_APPOINTMENT,
                new String[]{"COUNT(*)"},
                DatabaseHelper.COLUMN_APPOINTMENT_DATE + " = ? AND " +
                        DatabaseHelper.COLUMN_APPOINTMENT_TIME + " = ?",
                new String[]{date, time},
                null,
                null,
                null
        );

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();

        return count;
    }

    private Appointment mapCursorToAppointment(Cursor cursor) {

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
                cursor.getColumnIndexOrThrow(
                        DatabaseHelper.COLUMN_APPOINTMENT_PART_ID
                );

        if (!cursor.isNull(partIndex)) {
            appointment.setPartId(cursor.getInt(partIndex));
        }

        appointment.setBranchId(
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_APPOINTMENT_BRANCH_ID
                        )
                )
        );

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

        appointment.setStatus(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_STATUS
                        )
                )
        );

        return appointment;
    }
}
