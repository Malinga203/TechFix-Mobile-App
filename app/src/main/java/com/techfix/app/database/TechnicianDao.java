package com.techfix.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.techfix.app.models.Technician;

import java.util.ArrayList;
import java.util.List;

public class TechnicianDao {

    private final DatabaseHelper databaseHelper;

    public TechnicianDao(Context context) {
        databaseHelper =
                new DatabaseHelper(context);
    }

    public long insertTechnician(
            Technician technician
    ) {

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                DatabaseHelper.COLUMN_TECHNICIAN_NAME,
                technician.getName()
        );

        values.put(
                DatabaseHelper.COLUMN_TECHNICIAN_PHONE,
                technician.getPhone()
        );

        values.put(
                DatabaseHelper.COLUMN_SPECIALIZATION,
                technician.getSpecialization()
        );

        values.put(
                DatabaseHelper.COLUMN_AVAILABLE,
                technician.isAvailable() ? 1 : 0
        );

        values.put(
                DatabaseHelper.COLUMN_TECHNICIAN_BRANCH_ID,
                technician.getBranchId()
        );

        return db.insert(
                DatabaseHelper.TABLE_TECHNICIAN,
                null,
                values
        );
    }

    public List<Technician> getAllTechnicians() {

        List<Technician> technicians =
                new ArrayList<>();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_TECHNICIAN,
                        null,
                        null,
                        null,
                        null,
                        null,
                        DatabaseHelper.COLUMN_TECHNICIAN_NAME + " ASC"
                );

        if (cursor.moveToFirst()) {

            do {

                Technician technician =
                        createTechnicianFromCursor(
                                cursor
                        );

                technicians.add(
                        technician
                );

            } while (cursor.moveToNext());
        }

        cursor.close();

        return technicians;
    }

    public List<Technician> getTechniciansByBranch(
            int branchId
    ) {

        List<Technician> technicians =
                new ArrayList<>();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_TECHNICIAN,
                        null,
                        DatabaseHelper.COLUMN_TECHNICIAN_BRANCH_ID + " = ?",
                        new String[]{
                                String.valueOf(branchId)
                        },
                        null,
                        null,
                        DatabaseHelper.COLUMN_TECHNICIAN_NAME + " ASC"
                );

        if (cursor.moveToFirst()) {

            do {

                technicians.add(
                        createTechnicianFromCursor(
                                cursor
                        )
                );

            } while (cursor.moveToNext());
        }

        cursor.close();

        return technicians;
    }

    public int updateTechnician(
            Technician technician
    ) {

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                DatabaseHelper.COLUMN_TECHNICIAN_NAME,
                technician.getName()
        );

        values.put(
                DatabaseHelper.COLUMN_TECHNICIAN_PHONE,
                technician.getPhone()
        );

        values.put(
                DatabaseHelper.COLUMN_SPECIALIZATION,
                technician.getSpecialization()
        );

        values.put(
                DatabaseHelper.COLUMN_AVAILABLE,
                technician.isAvailable() ? 1 : 0
        );

        values.put(
                DatabaseHelper.COLUMN_TECHNICIAN_BRANCH_ID,
                technician.getBranchId()
        );

        return db.update(
                DatabaseHelper.TABLE_TECHNICIAN,
                values,
                DatabaseHelper.COLUMN_TECHNICIAN_ID + " = ?",
                new String[]{
                        String.valueOf(
                                technician.getTechnicianId()
                        )
                }
        );
    }

    public int deleteTechnician(
            int technicianId
    ) {

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        return db.delete(
                DatabaseHelper.TABLE_TECHNICIAN,
                DatabaseHelper.COLUMN_TECHNICIAN_ID + " = ?",
                new String[]{
                        String.valueOf(
                                technicianId
                        )
                }
        );
    }

    public boolean isTechnicianAvailable(
            int branchId,
            String specialization
    ) {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        String selection =
                DatabaseHelper.COLUMN_TECHNICIAN_BRANCH_ID + " = ? AND " +
                        DatabaseHelper.COLUMN_SPECIALIZATION + " = ? AND " +
                        DatabaseHelper.COLUMN_AVAILABLE + " = 1";

        String[] selectionArgs = {
                String.valueOf(branchId),
                specialization
        };

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_TECHNICIAN,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );

        boolean available =
                cursor.getCount() > 0;

        cursor.close();

        return available;
    }

    private Technician createTechnicianFromCursor(
            Cursor cursor
    ) {

        int technicianId =
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_TECHNICIAN_ID
                        )
                );

        String name =
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_TECHNICIAN_NAME
                        )
                );

        String phone =
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_TECHNICIAN_PHONE
                        )
                );

        String specialization =
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_SPECIALIZATION
                        )
                );

        int availableValue =
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_AVAILABLE
                        )
                );

        int branchId =
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_TECHNICIAN_BRANCH_ID
                        )
                );

        return new Technician(
                technicianId,
                name,
                phone,
                specialization,
                availableValue == 1,
                branchId
        );
    }
}