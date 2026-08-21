package com.techfix.app.userauthentication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.techfix.app.userauthentication.models.User;

public class AuthDatabaseHelper extends SQLiteOpenHelper {

    // Database information
    private static final String DATABASE_NAME = "techfix_auth_sample.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TAG = "AUTH_DB_TEST";

    // Table
    public static final String TABLE_USERS = "users";

    // Columns
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_PASSWORD = "password";

    public AuthDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create database table
    @Override
    public void onCreate(SQLiteDatabase db) {

        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_EMAIL + " TEXT NOT NULL UNIQUE, " +
                COLUMN_PHONE + " TEXT NOT NULL, " +
                COLUMN_PASSWORD + " TEXT NOT NULL" +
                ")";

        db.execSQL(createUsersTable);
    }

    // Upgrade database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        onCreate(db);
    }

    // =========================================================
    // INSERT USER
    // =========================================================

    public long insertUser(User user) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PHONE, user.getPhone());
        values.put(COLUMN_PASSWORD, user.getPassword());

        try {

            return db.insertOrThrow(
                    TABLE_USERS,
                    null,
                    values
            );

        } catch (Exception e) {

            Log.e(
                    TAG,
                    "Database insert error: " + e.getMessage(),
                    e
            );

            return -1;
        }
    }

    // =========================================================
    // GET USER BY EMAIL
    // =========================================================

    public User getUserByEmail(String email) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor = db.query(
                    TABLE_USERS,
                    null,
                    COLUMN_EMAIL + " = ?",
                    new String[]{email},
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(COLUMN_ID)
                );

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_NAME)
                );

                String userEmail = cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_EMAIL)
                );

                String phone = cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_PHONE)
                );

                String password = cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)
                );

                return new User(
                        id,
                        name,
                        userEmail,
                        phone,
                        password
                );
            }

            return null;

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // =========================================================
    // CHECK IF EMAIL IS REGISTERED
    // =========================================================

    public boolean isEmailRegistered(String email) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor = db.query(
                    TABLE_USERS,
                    new String[]{COLUMN_ID},
                    COLUMN_EMAIL + " = ?",
                    new String[]{email},
                    null,
                    null,
                    null
            );

            return cursor.moveToFirst();

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // =========================================================
    // AUTHENTICATE USER
    // =========================================================

    public User authenticateUser(String email, String password) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor = db.query(
                    TABLE_USERS,
                    null,
                    COLUMN_EMAIL + " = ? AND " +
                            COLUMN_PASSWORD + " = ?",
                    new String[]{email, password},
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(COLUMN_ID)
                );

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_NAME)
                );

                String userEmail = cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_EMAIL)
                );

                String phone = cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_PHONE)
                );

                String userPassword = cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)
                );

                return new User(
                        id,
                        name,
                        userEmail,
                        phone,
                        userPassword
                );
            }

            return null;

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }
}