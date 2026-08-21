package com.techfix.app.userauthentication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.techfix.app.userauthentication.models.User;

public class AuthDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME =
            "techfix_auth_sample.db";

    private static final int DATABASE_VERSION = 1;

    private static final String TAG = "AUTH_DB_TEST";

    // =========================================================
    // TABLE
    // =========================================================

    public static final String TABLE_USERS = "users";

    // =========================================================
    // COLUMNS
    // =========================================================

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_PASSWORD = "password";

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AuthDatabaseHelper(Context context) {
        super(
                context,
                DATABASE_NAME,
                null,
                DATABASE_VERSION
        );
    }

    // =========================================================
    // CREATE TABLE
    // =========================================================

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createUsersTable =
                "CREATE TABLE " + TABLE_USERS + " (" +

                        COLUMN_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        COLUMN_NAME +
                        " TEXT NOT NULL, " +

                        COLUMN_EMAIL +
                        " TEXT NOT NULL UNIQUE, " +

                        COLUMN_PHONE +
                        " TEXT NOT NULL, " +

                        COLUMN_PASSWORD +
                        " TEXT NOT NULL" +

                        ")";

        db.execSQL(createUsersTable);

        Log.d(
                TAG,
                "Users table created."
        );
    }

    // =========================================================
    // DATABASE UPGRADE
    // =========================================================

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        db.execSQL(
                "DROP TABLE IF EXISTS " + TABLE_USERS
        );

        onCreate(db);
    }

    // =========================================================
    // INSERT USER
    // =========================================================

    public long insertUser(User user) {

        SQLiteDatabase db =
                getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                COLUMN_NAME,
                user.getName().trim()
        );

        values.put(
                COLUMN_EMAIL,
                user.getEmail().trim()
        );

        values.put(
                COLUMN_PHONE,
                user.getPhone().trim()
        );

        values.put(
                COLUMN_PASSWORD,
                user.getPassword()
        );

        try {

            long result = db.insertOrThrow(
                    TABLE_USERS,
                    null,
                    values
            );

            Log.d(
                    TAG,
                    "User inserted successfully. ID: "
                            + result
            );

            return result;

        } catch (Exception e) {

            Log.e(
                    TAG,
                    "Database insert error: "
                            + e.getMessage(),
                    e
            );

            return -1;
        }
    }

    // =========================================================
    // GET USER BY ID
    // =========================================================

    public User getUserById(int userId) {

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor = db.query(
                    TABLE_USERS,
                    null,
                    COLUMN_ID + " = ?",
                    new String[]{
                            String.valueOf(userId)
                    },
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {

                return createUserFromCursor(cursor);
            }

            return null;

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // =========================================================
    // GET USER BY EMAIL
    // =========================================================

    public User getUserByEmail(String email) {

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor = db.query(
                    TABLE_USERS,
                    null,
                    COLUMN_EMAIL + " = ?",
                    new String[]{
                            email.trim()
                    },
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {

                return createUserFromCursor(cursor);
            }

            return null;

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // =========================================================
    // GET USER BY NAME
    // =========================================================

    public User getUserByName(String name) {

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor = db.query(
                    TABLE_USERS,
                    null,
                    "LOWER(" + COLUMN_NAME + ") = LOWER(?)",
                    new String[]{
                            name.trim()
                    },
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {

                return createUserFromCursor(cursor);
            }

            return null;

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // =========================================================
    // CHECK EMAIL
    // =========================================================

    public boolean isEmailRegistered(String email) {

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor = db.query(
                    TABLE_USERS,
                    new String[]{COLUMN_ID},
                    COLUMN_EMAIL + " = ?",
                    new String[]{
                            email.trim()
                    },
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
    // AUTHENTICATE BY NAME + PASSWORD
    // =========================================================

    public User authenticateUserByName(
            String name,
            String password) {

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor = db.query(
                    TABLE_USERS,
                    null,
                    "LOWER(" + COLUMN_NAME + ") = LOWER(?)"
                            + " AND "
                            + COLUMN_PASSWORD + " = ?",
                    new String[]{
                            name.trim(),
                            password
                    },
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {

                return createUserFromCursor(cursor);
            }

            return null;

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // =========================================================
    // AUTHENTICATE BY EMAIL + PASSWORD
    // =========================================================

    public User authenticateUser(
            String email,
            String password) {

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor = db.query(
                    TABLE_USERS,
                    null,
                    COLUMN_EMAIL + " = ? AND "
                            + COLUMN_PASSWORD + " = ?",
                    new String[]{
                            email.trim(),
                            password
                    },
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {

                return createUserFromCursor(cursor);
            }

            return null;

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // =========================================================
    // UPDATE PROFILE
    // =========================================================

    public boolean updateUserProfile(
            int userId,
            String name,
            String phone) {

        SQLiteDatabase db =
                getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                COLUMN_NAME,
                name.trim()
        );

        values.put(
                COLUMN_PHONE,
                phone.trim()
        );

        int rowsUpdated = db.update(
                TABLE_USERS,
                values,
                COLUMN_ID + " = ?",
                new String[]{
                        String.valueOf(userId)
                }
        );

        Log.d(
                TAG,
                "Profile update rows: "
                        + rowsUpdated
        );

        return rowsUpdated > 0;
    }

    // =========================================================
    // CHANGE PASSWORD
    // =========================================================

    public boolean updatePassword(
            int userId,
            String newPassword) {

        SQLiteDatabase db =
                getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                COLUMN_PASSWORD,
                newPassword
        );

        int rowsUpdated = db.update(
                TABLE_USERS,
                values,
                COLUMN_ID + " = ?",
                new String[]{
                        String.valueOf(userId)
                }
        );

        Log.d(
                TAG,
                "Password update rows: "
                        + rowsUpdated
        );

        return rowsUpdated > 0;
    }

    // =========================================================
    // CREATE USER FROM CURSOR
    // =========================================================

    private User createUserFromCursor(
            Cursor cursor) {

        int id =
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                COLUMN_ID
                        )
                );

        String name =
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                COLUMN_NAME
                        )
                );

        String email =
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                COLUMN_EMAIL
                        )
                );

        String phone =
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                COLUMN_PHONE
                        )
                );

        String password =
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                COLUMN_PASSWORD
                        )
                );

        return new User(
                id,
                name,
                email,
                phone,
                password
        );
    }
}