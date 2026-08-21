package com.techfix.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.techfix.app.models.Branch;

import java.util.ArrayList;
import java.util.List;

public class BranchDAO {

    private final DatabaseHelper databaseHelper;

    public BranchDAO(Context context) {
        databaseHelper =
                new DatabaseHelper(context);
    }

    public long insertBranch(Branch branch) {

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

    public List<Branch> getAllBranches() {

        List<Branch> branches =
                new ArrayList<>();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_BRANCH,
                null,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {

            do {

                int id =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_BRANCH_ID
                                )
                        );

                String name =
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

                Branch branch =
                        new Branch(
                                id,
                                name,
                                address,
                                latitude,
                                longitude
                        );

                branches.add(branch);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return branches;
    }
}