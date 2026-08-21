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

            Log.d(
                    TAG,
                    "Saved name: "
                            + user.getName()
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
                    new String[]{email.trim()},
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
                    new String[]{email.trim()},
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
                    new String[]{name.trim()},
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {

                User user =
                        createUserFromCursor(cursor);

                Log.d(
                        TAG,
                        "User found by name: "
                                + user.getName()
                );

                return user;
            }

            Log.d(
                    TAG,
                    "No user found with name: "
                            + name
            );

            return null;

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // =========================================================
    // AUTHENTICATE USING NAME + PASSWORD
    // =========================================================

    public User authenticateUserByName(
            String name,
            String password) {

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor = null;

        String cleanName = name.trim();

        try {

            // First find the user by name.
            cursor = db.query(
                    TABLE_USERS,
                    null,
                    "LOWER(" + COLUMN_NAME + ") = LOWER(?)",
                    new String[]{cleanName},
                    null,
                    null,
                    null
            );

            if (!cursor.moveToFirst()) {

                Log.e(
                        TAG,
                        "LOGIN FAILED: Name not found: "
                                + cleanName
                );

                return null;
            }

            // Read stored password.
            String storedPassword =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    COLUMN_PASSWORD
                            )
                    );

            String storedName =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    COLUMN_NAME
                            )
                    );

            Log.d(
                    TAG,
                    "Login name entered: "
                            + cleanName
            );

            Log.d(
                    TAG,
                    "Login name stored: "
                            + storedName
            );

            Log.d(
                    TAG,
                    "Password entered length: "
                            + password.length()
            );

            Log.d(
                    TAG,
                    "Password stored length: "
                            + storedPassword.length()
            );

            // Compare password.
            if (!storedPassword.equals(password)) {

                Log.e(
                        TAG,
                        "LOGIN FAILED: Password does not match."
                );

                return null;
            }

            User user =
                    createUserFromCursor(cursor);

            Log.d(
                    TAG,
                    "LOGIN SUCCESS: "
                            + user.getName()
            );

            return user;

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // =========================================================
    // EMAIL + PASSWORD AUTHENTICATION
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