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

    public TechnicianDao(
            Context context
    ) {

        databaseHelper =
                new DatabaseHelper(context);
    }


    public long insertTechnician(
            Technician technician
    ) {

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                createValues(
                        technician
                );

        return db.insert(
                DatabaseHelper.TABLE_TECHNICIAN,
                null,
                values
        );
    }


    public Technician getTechnicianById(
            int technicianId
    ) {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_TECHNICIAN,
                        null,

                        DatabaseHelper.COLUMN_TECHNICIAN_ID +
                                " = ?",

                        new String[]{
                                String.valueOf(
                                        technicianId
                                )
                        },

                        null,
                        null,
                        null
                );

        Technician technician =
                null;

        if (cursor.moveToFirst()) {

            technician =
                    createTechnicianFromCursor(
                            cursor
                    );
        }

        cursor.close();

        return technician;
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
                        DatabaseHelper.COLUMN_TECHNICIAN_NAME +
                                " ASC"
                );

        while (cursor.moveToNext()) {

            technicians.add(
                    createTechnicianFromCursor(
                            cursor
                    )
            );
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

                        DatabaseHelper.COLUMN_TECHNICIAN_BRANCH_ID +
                                " = ?",

                        new String[]{
                                String.valueOf(
                                        branchId
                                )
                        },

                        null,
                        null,

                        DatabaseHelper.COLUMN_TECHNICIAN_NAME +
                                " ASC"
                );

        while (cursor.moveToNext()) {

            technicians.add(
                    createTechnicianFromCursor(
                            cursor
                    )
            );
        }

        cursor.close();

        return technicians;
    }


    public int updateTechnician(
            Technician technician
    ) {

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        return db.update(
                DatabaseHelper.TABLE_TECHNICIAN,

                createValues(
                        technician
                ),

                DatabaseHelper.COLUMN_TECHNICIAN_ID +
                        " = ?",

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

                DatabaseHelper.COLUMN_TECHNICIAN_ID +
                        " = ?",

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

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_TECHNICIAN,
                        null,

                        DatabaseHelper.COLUMN_TECHNICIAN_BRANCH_ID +
                                " = ? AND " +

                                DatabaseHelper.COLUMN_SPECIALIZATION +
                                " = ? AND " +

                                DatabaseHelper.COLUMN_AVAILABLE +
                                " = 1",

                        new String[]{
                                String.valueOf(branchId),
                                specialization
                        },

                        null,
                        null,
                        null
                );

        boolean exists =
                cursor.moveToFirst();

        cursor.close();

        return exists;
    }


    private ContentValues createValues(
            Technician technician
    ) {

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
                technician.isAvailable()
                        ? 1
                        : 0
        );

        values.put(
                DatabaseHelper.COLUMN_TECHNICIAN_BRANCH_ID,
                technician.getBranchId()
        );

        return values;
    }


    private Technician createTechnicianFromCursor(
            Cursor cursor
    ) {

        return new Technician(

                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_TECHNICIAN_ID
                        )
                ),

                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_TECHNICIAN_NAME
                        )
                ),

                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_TECHNICIAN_PHONE
                        )
                ),

                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_SPECIALIZATION
                        )
                ),

                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_AVAILABLE
                        )
                ) == 1,

                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_TECHNICIAN_BRANCH_ID
                        )
                )
        );
    }
}