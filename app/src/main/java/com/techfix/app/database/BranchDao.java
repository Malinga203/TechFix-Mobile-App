package com.techfix.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.techfix.app.models.Branch;

import java.util.ArrayList;
import java.util.List;

public class BranchDao {

    private final DatabaseHelper databaseHelper;

    public BranchDao(Context context) {

        databaseHelper =
                new DatabaseHelper(
                        context.getApplicationContext()
                );
    }


    // =========================================================
    // INSERT BRANCH
    // =========================================================

    public long insertBranch(
            Branch branch
    ) {

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                DatabaseHelper.COLUMN_BRANCH_NAME,
                branch.getBranchName()
        );

        values.put(
                DatabaseHelper.COLUMN_ADDRESS,
                branch.getAddress()
        );

        values.put(
                DatabaseHelper.COLUMN_LATITUDE,
                branch.getLatitude()
        );

        values.put(
                DatabaseHelper.COLUMN_LONGITUDE,
                branch.getLongitude()
        );

        return db.insert(
                DatabaseHelper.TABLE_BRANCH,
                null,
                values
        );
    }


    // =========================================================
    // GET ALL BRANCHES
    // =========================================================

    public List<Branch> getAllBranches() {

        List<Branch> branches =
                new ArrayList<>();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_BRANCH,
                        null,
                        null,
                        null,
                        null,
                        null,
                        DatabaseHelper.COLUMN_BRANCH_NAME +
                                " ASC"
                );

        try {

            while (cursor.moveToNext()) {

                branches.add(
                        createBranchFromCursor(
                                cursor
                        )
                );
            }

        } finally {

            cursor.close();
        }

        return branches;
    }


    // =========================================================
    // GET BRANCH BY ID
    // =========================================================

    public Branch getBranchById(
            int branchId
    ) {

        if (branchId <= 0) {
            return null;
        }

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_BRANCH,
                        null,

                        DatabaseHelper.COLUMN_BRANCH_ID +
                                " = ?",

                        new String[]{
                                String.valueOf(
                                        branchId
                                )
                        },

                        null,
                        null,
                        null,
                        "1"
                );

        try {

            if (cursor.moveToFirst()) {

                return createBranchFromCursor(
                        cursor
                );
            }

        } finally {

            cursor.close();
        }

        return null;
    }


    // =========================================================
    // UPDATE BRANCH
    // =========================================================

    public int updateBranch(
            Branch branch
    ) {

        if (
                branch == null ||
                        branch.getBranchId() <= 0
        ) {

            return 0;
        }

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                DatabaseHelper.COLUMN_BRANCH_NAME,
                branch.getBranchName()
        );

        values.put(
                DatabaseHelper.COLUMN_ADDRESS,
                branch.getAddress()
        );

        values.put(
                DatabaseHelper.COLUMN_LATITUDE,
                branch.getLatitude()
        );

        values.put(
                DatabaseHelper.COLUMN_LONGITUDE,
                branch.getLongitude()
        );

        return db.update(
                DatabaseHelper.TABLE_BRANCH,
                values,

                DatabaseHelper.COLUMN_BRANCH_ID +
                        " = ?",

                new String[]{
                        String.valueOf(
                                branch.getBranchId()
                        )
                }
        );
    }


    // =========================================================
    // DELETE BRANCH
    // =========================================================

    public int deleteBranch(
            int branchId
    ) {

        if (branchId <= 0) {
            return 0;
        }

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        return db.delete(
                DatabaseHelper.TABLE_BRANCH,

                DatabaseHelper.COLUMN_BRANCH_ID +
                        " = ?",

                new String[]{
                        String.valueOf(
                                branchId
                        )
                }
        );
    }


    // =========================================================
    // CURSOR -> BRANCH
    // =========================================================

    private Branch createBranchFromCursor(
            Cursor cursor
    ) {

        int branchId =
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_BRANCH_ID
                        )
                );

        String branchName =
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_BRANCH_NAME
                        )
                );

        String address =
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_ADDRESS
                        )
                );

        double latitude =
                cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_LATITUDE
                        )
                );

        double longitude =
                cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_LONGITUDE
                        )
                );

        return new Branch(
                branchId,
                branchName,
                address,
                latitude,
                longitude
        );
    }


    // =========================================================
    // CLOSE
    // =========================================================

    public void close() {

        databaseHelper.close();
    }
}