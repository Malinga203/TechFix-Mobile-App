package com.techfix.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.techfix.app.userauthentication.models.User;

public class UserDao {

    private final DatabaseHelper databaseHelper;

    public UserDao(
            Context context
    ) {

        databaseHelper =
                new DatabaseHelper(context);
    }


    public long insertUser(
            User user
    ) {

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                DatabaseHelper.COLUMN_USER_NAME,
                user.getName().trim()
        );

        values.put(
                DatabaseHelper.COLUMN_USER_EMAIL,
                user.getEmail().trim().toLowerCase()
        );

        values.put(
                DatabaseHelper.COLUMN_USER_PHONE,
                user.getPhone()
        );

        values.put(
                DatabaseHelper.COLUMN_USER_PASSWORD,
                user.getPassword()
        );

        values.put(
                DatabaseHelper.COLUMN_USER_ROLE,
                user.getRole() == null
                        ? User.ROLE_CUSTOMER
                        : user.getRole()
        );

        if (user.getTechnicianId() != null) {

            values.put(
                    DatabaseHelper.COLUMN_USER_TECHNICIAN_ID,
                    user.getTechnicianId()
            );

        } else {

            values.putNull(
                    DatabaseHelper.COLUMN_USER_TECHNICIAN_ID
            );
        }

        return db.insert(
                DatabaseHelper.TABLE_USERS,
                null,
                values
        );
    }


    public User authenticateUser(
            String email,
            String password
    ) {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_USERS,
                        null,

                        "LOWER(" +
                                DatabaseHelper.COLUMN_USER_EMAIL +
                                ") = LOWER(?) AND " +

                                DatabaseHelper.COLUMN_USER_PASSWORD +
                                " = ?",

                        new String[]{
                                email.trim(),
                                password
                        },

                        null,
                        null,
                        null
                );

        User user = null;

        if (cursor.moveToFirst()) {

            user =
                    createUserFromCursor(
                            cursor
                    );
        }

        cursor.close();

        return user;
    }


    public User getUserById(
            int userId
    ) {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_USERS,
                        null,

                        DatabaseHelper.COLUMN_USER_ID +
                                " = ?",

                        new String[]{
                                String.valueOf(userId)
                        },

                        null,
                        null,
                        null
                );

        User user = null;

        if (cursor.moveToFirst()) {

            user =
                    createUserFromCursor(
                            cursor
                    );
        }

        cursor.close();

        return user;
    }


    public boolean isEmailRegistered(
            String email
    ) {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_USERS,

                        new String[]{
                                DatabaseHelper.COLUMN_USER_ID
                        },

                        "LOWER(" +
                                DatabaseHelper.COLUMN_USER_EMAIL +
                                ") = LOWER(?)",

                        new String[]{
                                email.trim()
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


    public boolean updateUserProfile(
            int userId,
            String name,
            String phone
    ) {

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                DatabaseHelper.COLUMN_USER_NAME,
                name.trim()
        );

        values.put(
                DatabaseHelper.COLUMN_USER_PHONE,
                phone.trim()
        );

        int rows =
                db.update(
                        DatabaseHelper.TABLE_USERS,
                        values,

                        DatabaseHelper.COLUMN_USER_ID +
                                " = ?",

                        new String[]{
                                String.valueOf(userId)
                        }
                );

        return rows > 0;
    }


    public boolean verifyPassword(
            int userId,
            String password
    ) {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_USERS,

                        new String[]{
                                DatabaseHelper.COLUMN_USER_ID
                        },

                        DatabaseHelper.COLUMN_USER_ID +
                                " = ? AND " +

                                DatabaseHelper.COLUMN_USER_PASSWORD +
                                " = ?",

                        new String[]{
                                String.valueOf(userId),
                                password
                        },

                        null,
                        null,
                        null
                );

        boolean valid =
                cursor.moveToFirst();

        cursor.close();

        return valid;
    }


    public boolean changePassword(
            int userId,
            String currentPassword,
            String newPassword
    ) {

        if (
                !verifyPassword(
                        userId,
                        currentPassword
                )
        ) {

            return false;
        }

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                DatabaseHelper.COLUMN_USER_PASSWORD,
                newPassword
        );

        int rows =
                db.update(
                        DatabaseHelper.TABLE_USERS,
                        values,

                        DatabaseHelper.COLUMN_USER_ID +
                                " = ?",

                        new String[]{
                                String.valueOf(userId)
                        }
                );

        return rows > 0;
    }


    private User createUserFromCursor(
            Cursor cursor
    ) {

        int id =
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_USER_ID
                        )
                );

        String name =
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_USER_NAME
                        )
                );

        String email =
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_USER_EMAIL
                        )
                );

        String phone =
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_USER_PHONE
                        )
                );

        String password =
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_USER_PASSWORD
                        )
                );

        String role =
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_USER_ROLE
                        )
                );

        int technicianIndex =
                cursor.getColumnIndexOrThrow(
                        DatabaseHelper.COLUMN_USER_TECHNICIAN_ID
                );

        Integer technicianId =
                null;

        if (!cursor.isNull(technicianIndex)) {

            technicianId =
                    cursor.getInt(
                            technicianIndex
                    );
        }

        return new User(
                id,
                name,
                email,
                phone,
                password,
                role,
                technicianId
        );
    }
}