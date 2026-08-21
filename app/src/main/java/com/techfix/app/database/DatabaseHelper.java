package com.techfix.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "techfix.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_BRANCH = "branches";

    public static final String COLUMN_BRANCH_ID = "branch_id";
    public static final String COLUMN_BRANCH_NAME = "branch_name";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    public static final String TABLE_REPAIR = "repairs";

    public static final String COLUMN_REPAIR_ID = "repair_id";
    public static final String COLUMN_APPOINTMENT_ID = "appointment_id";
    public static final String COLUMN_CUSTOMER_ID = "customer_id";
    public static final String COLUMN_TECHNICIAN_ID = "technician_id";

    public static final String COLUMN_DEVICE_NAME = "device_name";
    public static final String COLUMN_SERVICE_NAME = "service_name";
    public static final String COLUMN_PROBLEM_DESCRIPTION = "problem_description";

    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_IMAGE_URI = "image_uri";

    public static final String COLUMN_ESTIMATED_COST = "estimated_cost";
    public static final String COLUMN_FINAL_COST = "final_cost";

    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";
    public static final String COLUMN_COMPLETED_AT = "completed_at";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createBranchTable(db);
        createRepairTable(db);
    }

    private void createBranchTable(SQLiteDatabase db) {

        String sql =
                "CREATE TABLE IF NOT EXISTS " + TABLE_BRANCH + " (" +
                        COLUMN_BRANCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_BRANCH_NAME + " TEXT NOT NULL, " +
                        COLUMN_ADDRESS + " TEXT NOT NULL, " +
                        COLUMN_LATITUDE + " REAL NOT NULL, " +
                        COLUMN_LONGITUDE + " REAL NOT NULL" +
                        ")";

        db.execSQL(sql);
    }

    private void createRepairTable(SQLiteDatabase db) {

        String sql =
                "CREATE TABLE IF NOT EXISTS " + TABLE_REPAIR + " (" +
                        COLUMN_REPAIR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_APPOINTMENT_ID + " INTEGER, " +
                        COLUMN_CUSTOMER_ID + " INTEGER, " +
                        COLUMN_BRANCH_ID + " INTEGER, " +
                        COLUMN_TECHNICIAN_ID + " INTEGER, " +
                        COLUMN_DEVICE_NAME + " TEXT NOT NULL, " +
                        COLUMN_SERVICE_NAME + " TEXT NOT NULL, " +
                        COLUMN_PROBLEM_DESCRIPTION + " TEXT, " +
                        COLUMN_STATUS + " TEXT NOT NULL DEFAULT 'PENDING', " +
                        COLUMN_IMAGE_URI + " TEXT, " +
                        COLUMN_ESTIMATED_COST + " REAL NOT NULL DEFAULT 0, " +
                        COLUMN_FINAL_COST + " REAL NOT NULL DEFAULT 0, " +
                        COLUMN_CREATED_AT + " TEXT, " +
                        COLUMN_UPDATED_AT + " TEXT, " +
                        COLUMN_COMPLETED_AT + " TEXT" +
                        ")";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {

        // Repair management was introduced with database version 2.
        if (oldVersion < 2) {
            createRepairTable(db);
        }
    }
}