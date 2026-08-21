package com.techfix.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "techfix.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_BRANCH = "branches";

    public static final String COLUMN_BRANCH_ID = "branch_id";
    public static final String COLUMN_BRANCH_NAME = "branch_name";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createBranchTable =
                "CREATE TABLE " + TABLE_BRANCH + " (" +
                        COLUMN_BRANCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_BRANCH_NAME + " TEXT NOT NULL, " +
                        COLUMN_ADDRESS + " TEXT NOT NULL, " +
                        COLUMN_LATITUDE + " REAL NOT NULL, " +
                        COLUMN_LONGITUDE + " REAL NOT NULL" +
                        ")";

        db.execSQL(createBranchTable);

        insertInitialBranches(db);
    }

    private void insertInitialBranches(SQLiteDatabase db) {

        ContentValues colomboValues = new ContentValues();

        colomboValues.put(
                COLUMN_BRANCH_NAME,
                "TechFix Colombo"
        );

        colomboValues.put(
                COLUMN_ADDRESS,
                "Colombo, Sri Lanka"
        );

        colomboValues.put(
                COLUMN_LATITUDE,
                6.9271
        );

        colomboValues.put(
                COLUMN_LONGITUDE,
                79.8612
        );

        db.insert(
                TABLE_BRANCH,
                null,
                colomboValues
        );


        ContentValues galleValues = new ContentValues();

        galleValues.put(
                COLUMN_BRANCH_NAME,
                "TechFix Galle"
        );

        galleValues.put(
                COLUMN_ADDRESS,
                "Galle, Sri Lanka"
        );

        galleValues.put(
                COLUMN_LATITUDE,
                6.0329
        );

        galleValues.put(
                COLUMN_LONGITUDE,
                80.2168
        );

        db.insert(
                TABLE_BRANCH,
                null,
                galleValues
        );
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {

        db.execSQL(
                "DROP TABLE IF EXISTS " + TABLE_BRANCH
        );

        onCreate(db);
    }
}