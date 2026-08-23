package com.techfix.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.techfix.app.models.Technician;
import com.techfix.app.utils.RepairCategoryUtils;

import java.util.ArrayList;
import java.util.List;

public class TechnicianDao {

    private final DatabaseHelper databaseHelper;


    public TechnicianDao(
            Context context
    ) {

        databaseHelper =
                new DatabaseHelper(
                        context.getApplicationContext()
                );
    }


    public long insertTechnician(
            Technician technician
    ) {

        SQLiteDatabase db =
                databaseHelper
                        .getWritableDatabase();


        long technicianId =
                -1;


        db.beginTransaction();


        try {

            technicianId =
                    db.insert(
                            DatabaseHelper.TABLE_TECHNICIAN,
                            null,
                            createValues(
                                    technician
                            )
                    );


            if (technicianId <= 0) {

                return -1;
            }


            replaceSpecializations(
                    db,
                    (int) technicianId,
                    technician
            );


            db.setTransactionSuccessful();

        } finally {

            db.endTransaction();
        }


        return technicianId;
    }


    public Technician getTechnicianById(
            int technicianId
    ) {

        SQLiteDatabase db =
                databaseHelper
                        .getReadableDatabase();


        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_TECHNICIAN,
                        null,

                        DatabaseHelper.COLUMN_TECHNICIAN_ID
                                +
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


        try {

            if (cursor.moveToFirst()) {

                technician =
                        createTechnicianFromCursor(
                                cursor
                        );


                loadSpecializations(
                        db,
                        technician
                );
            }

        } finally {

            cursor.close();
        }


        return technician;
    }


    public List<Technician> getAllTechnicians() {

        List<Technician> technicians =
                new ArrayList<>();


        SQLiteDatabase db =
                databaseHelper
                        .getReadableDatabase();


        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_TECHNICIAN,
                        null,
                        null,
                        null,
                        null,
                        null,

                        DatabaseHelper.COLUMN_TECHNICIAN_NAME
                                +
                                " ASC"
                );


        try {

            while (
                    cursor.moveToNext()
            ) {

                Technician technician =
                        createTechnicianFromCursor(
                                cursor
                        );


                loadSpecializations(
                        db,
                        technician
                );


                technicians.add(
                        technician
                );
            }

        } finally {

            cursor.close();
        }


        return technicians;
    }


    public List<Technician> getTechniciansByBranch(
            int branchId
    ) {

        List<Technician> technicians =
                new ArrayList<>();


        SQLiteDatabase db =
                databaseHelper
                        .getReadableDatabase();


        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_TECHNICIAN,
                        null,

                        DatabaseHelper.COLUMN_TECHNICIAN_BRANCH_ID
                                +
                                " = ?",

                        new String[]{
                                String.valueOf(
                                        branchId
                                )
                        },

                        null,
                        null,

                        DatabaseHelper.COLUMN_TECHNICIAN_NAME
                                +
                                " ASC"
                );


        try {

            while (
                    cursor.moveToNext()
            ) {

                Technician technician =
                        createTechnicianFromCursor(
                                cursor
                        );


                loadSpecializations(
                        db,
                        technician
                );


                technicians.add(
                        technician
                );
            }

        } finally {

            cursor.close();
        }


        return technicians;
    }


    public int updateTechnician(
            Technician technician
    ) {

        SQLiteDatabase db =
                databaseHelper
                        .getWritableDatabase();


        int rows =
                0;


        db.beginTransaction();


        try {

            rows =
                    db.update(
                            DatabaseHelper.TABLE_TECHNICIAN,

                            createValues(
                                    technician
                            ),

                            DatabaseHelper.COLUMN_TECHNICIAN_ID
                                    +
                                    " = ?",

                            new String[]{
                                    String.valueOf(
                                            technician
                                                    .getTechnicianId()
                                    )
                            }
                    );


            if (rows > 0) {

                replaceSpecializations(
                        db,
                        technician.getTechnicianId(),
                        technician
                );
            }


            db.setTransactionSuccessful();

        } finally {

            db.endTransaction();
        }


        return rows;
    }


    public int deleteTechnician(
            int technicianId
    ) {

        SQLiteDatabase db =
                databaseHelper
                        .getWritableDatabase();


        return db.delete(
                DatabaseHelper.TABLE_TECHNICIAN,

                DatabaseHelper.COLUMN_TECHNICIAN_ID
                        +
                        " = ?",

                new String[]{
                        String.valueOf(
                                technicianId
                        )
                }
        );
    }


    /*
     * New matching method.
     *
     * A technician must match:
     *
     * branch
     * + availability
     * + device type
     * + category
     */
    public boolean isTechnicianAvailable(
            int branchId,
            String serviceType,
            String category
    ) {

        SQLiteDatabase db =
                databaseHelper
                        .getReadableDatabase();


        String sql =
                "SELECT 1 " +

                        "FROM "
                        +
                        DatabaseHelper.TABLE_TECHNICIAN
                        +
                        " t " +

                        "INNER JOIN "
                        +
                        DatabaseHelper.TABLE_TECHNICIAN_SPECIALIZATION
                        +
                        " ts ON t."
                        +
                        DatabaseHelper.COLUMN_TECHNICIAN_ID
                        +
                        " = ts."
                        +
                        DatabaseHelper.COLUMN_TS_TECHNICIAN_ID
                        +
                        " " +

                        "WHERE t."
                        +
                        DatabaseHelper.COLUMN_TECHNICIAN_BRANCH_ID
                        +
                        " = ? " +

                        "AND t."
                        +
                        DatabaseHelper.COLUMN_AVAILABLE
                        +
                        " = 1 " +

                        "AND ts."
                        +
                        DatabaseHelper.COLUMN_SPECIALIZATION_TYPE
                        +
                        " = ? " +

                        "AND ts."
                        +
                        DatabaseHelper.COLUMN_SPECIALIZATION_CATEGORY
                        +
                        " = ? " +

                        "LIMIT 1";


        Cursor cursor =
                db.rawQuery(
                        sql,

                        new String[]{
                                String.valueOf(
                                        branchId
                                ),

                                serviceType,

                                category
                        }
                );


        try {

            return cursor.moveToFirst();

        } finally {

            cursor.close();
        }
    }


    /*
     * Kept so older code will still compile.
     */
    public boolean isTechnicianAvailable(
            int branchId,
            String category
    ) {

        SQLiteDatabase db =
                databaseHelper
                        .getReadableDatabase();


        String sql =
                "SELECT 1 " +

                        "FROM "
                        +
                        DatabaseHelper.TABLE_TECHNICIAN
                        +
                        " t " +

                        "INNER JOIN "
                        +
                        DatabaseHelper.TABLE_TECHNICIAN_SPECIALIZATION
                        +
                        " ts ON t."
                        +
                        DatabaseHelper.COLUMN_TECHNICIAN_ID
                        +
                        " = ts."
                        +
                        DatabaseHelper.COLUMN_TS_TECHNICIAN_ID
                        +
                        " " +

                        "WHERE t."
                        +
                        DatabaseHelper.COLUMN_TECHNICIAN_BRANCH_ID
                        +
                        " = ? " +

                        "AND t."
                        +
                        DatabaseHelper.COLUMN_AVAILABLE
                        +
                        " = 1 " +

                        "AND ts."
                        +
                        DatabaseHelper.COLUMN_SPECIALIZATION_CATEGORY
                        +
                        " = ? " +

                        "LIMIT 1";


        Cursor cursor =
                db.rawQuery(
                        sql,

                        new String[]{
                                String.valueOf(
                                        branchId
                                ),

                                category
                        }
                );


        try {

            return cursor.moveToFirst();

        } finally {

            cursor.close();
        }
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


        /*
         * Existing specialization column is kept
         * as a readable summary for compatibility.
         */
        values.put(
                DatabaseHelper.COLUMN_SPECIALIZATION,
                technician.getSpecialization()
        );


        values.put(
                DatabaseHelper.COLUMN_AVAILABLE,
                technician.isAvailable()
                        ?
                        1
                        :
                        0
        );


        values.put(
                DatabaseHelper.COLUMN_TECHNICIAN_BRANCH_ID,
                technician.getBranchId()
        );


        return values;
    }


    private void replaceSpecializations(
            SQLiteDatabase db,
            int technicianId,
            Technician technician
    ) {

        db.delete(
                DatabaseHelper.TABLE_TECHNICIAN_SPECIALIZATION,

                DatabaseHelper.COLUMN_TS_TECHNICIAN_ID
                        +
                        " = ?",

                new String[]{
                        String.valueOf(
                                technicianId
                        )
                }
        );


        for (
                String category
                :
                technician.getMobileSpecializations()
        ) {

            insertSpecialization(
                    db,
                    technicianId,
                    RepairCategoryUtils.TYPE_MOBILE,
                    category
            );
        }


        for (
                String category
                :
                technician.getComputerSpecializations()
        ) {

            insertSpecialization(
                    db,
                    technicianId,
                    RepairCategoryUtils.TYPE_COMPUTER,
                    category
            );
        }
    }


    private void insertSpecialization(
            SQLiteDatabase db,
            int technicianId,
            String type,
            String category
    ) {

        ContentValues values =
                new ContentValues();


        values.put(
                DatabaseHelper.COLUMN_TS_TECHNICIAN_ID,
                technicianId
        );


        values.put(
                DatabaseHelper.COLUMN_SPECIALIZATION_TYPE,
                type
        );


        values.put(
                DatabaseHelper.COLUMN_SPECIALIZATION_CATEGORY,
                category
        );


        db.insertWithOnConflict(
                DatabaseHelper.TABLE_TECHNICIAN_SPECIALIZATION,
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
        );
    }


    private void loadSpecializations(
            SQLiteDatabase db,
            Technician technician
    ) {

        List<String> mobile =
                new ArrayList<>();


        List<String> computer =
                new ArrayList<>();


        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_TECHNICIAN_SPECIALIZATION,

                        null,

                        DatabaseHelper.COLUMN_TS_TECHNICIAN_ID
                                +
                                " = ?",

                        new String[]{
                                String.valueOf(
                                        technician
                                                .getTechnicianId()
                                )
                        },

                        null,
                        null,

                        DatabaseHelper.COLUMN_SPECIALIZATION_TYPE
                                +
                                " ASC, "
                                +
                                DatabaseHelper.COLUMN_SPECIALIZATION_CATEGORY
                                +
                                " ASC"
                );


        try {

            while (
                    cursor.moveToNext()
            ) {

                String type =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_SPECIALIZATION_TYPE
                                )
                        );


                String category =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_SPECIALIZATION_CATEGORY
                                )
                        );


                if (
                        RepairCategoryUtils.TYPE_COMPUTER
                                .equalsIgnoreCase(
                                        type
                                )
                ) {

                    computer.add(
                            category
                    );

                } else {

                    mobile.add(
                            category
                    );
                }
            }

        } finally {

            cursor.close();
        }


        technician.setMobileSpecializations(
                mobile
        );


        technician.setComputerSpecializations(
                computer
        );
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
                )
                        ==
                        1,

                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_TECHNICIAN_BRANCH_ID
                        )
                )
        );
    }
}