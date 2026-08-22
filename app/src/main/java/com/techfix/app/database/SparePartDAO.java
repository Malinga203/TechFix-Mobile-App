package com.techfix.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.techfix.app.models.SparePart;

import java.util.ArrayList;
import java.util.List;

public class SparePartDAO {

    private final DatabaseHelper databaseHelper;

    public SparePartDAO(Context context) {

        databaseHelper =
                new DatabaseHelper(context);
    }

    public List<SparePart> getAllSpareParts() {

        List<SparePart> spareParts =
                new ArrayList<>();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_SPARE_PART,
                        null,
                        null,
                        null,
                        null,
                        null,
                        DatabaseHelper.COLUMN_PART_NAME +
                                " ASC"
                );

        if (cursor.moveToFirst()) {

            do {

                spareParts.add(
                        mapCursorToSparePart(
                                cursor
                        )
                );

            } while (cursor.moveToNext());
        }

        cursor.close();

        return spareParts;
    }

    public SparePart getSparePartById(
            int partId
    ) {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_SPARE_PART,
                        null,
                        DatabaseHelper.COLUMN_PART_ID +
                                " = ?",
                        new String[]{
                                String.valueOf(partId)
                        },
                        null,
                        null,
                        null
                );

        SparePart sparePart =
                null;

        if (cursor.moveToFirst()) {

            sparePart =
                    mapCursorToSparePart(
                            cursor
                    );
        }

        cursor.close();

        return sparePart;
    }

    public boolean isPartAvailableAtBranch(
            int partId,
            int branchId
    ) {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_BRANCH_SPARE_PART,

                        new String[]{
                                DatabaseHelper.COLUMN_BSP_STOCK_QUANTITY
                        },

                        DatabaseHelper.COLUMN_BSP_PART_ID +
                                " = ? AND " +

                                DatabaseHelper.COLUMN_BSP_BRANCH_ID +
                                " = ? AND " +

                                DatabaseHelper.COLUMN_BSP_STOCK_QUANTITY +
                                " > 0",

                        new String[]{
                                String.valueOf(partId),
                                String.valueOf(branchId)
                        },

                        null,
                        null,
                        null
                );

        boolean available =
                cursor.moveToFirst();

        cursor.close();

        return available;
    }

    public int getPartStockAtBranch(
            int partId,
            int branchId
    ) {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_BRANCH_SPARE_PART,

                        new String[]{
                                DatabaseHelper.COLUMN_BSP_STOCK_QUANTITY
                        },

                        DatabaseHelper.COLUMN_BSP_PART_ID +
                                " = ? AND " +

                                DatabaseHelper.COLUMN_BSP_BRANCH_ID +
                                " = ?",

                        new String[]{
                                String.valueOf(partId),
                                String.valueOf(branchId)
                        },

                        null,
                        null,
                        null
                );

        int quantity = 0;

        if (cursor.moveToFirst()) {

            quantity =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper
                                            .COLUMN_BSP_STOCK_QUANTITY
                            )
                    );
        }

        cursor.close();

        return quantity;
    }

    public long addOrUpdateBranchStock(
            int branchId,
            int partId,
            int quantity
    ) {

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                DatabaseHelper.COLUMN_BSP_BRANCH_ID,
                branchId
        );

        values.put(
                DatabaseHelper.COLUMN_BSP_PART_ID,
                partId
        );

        values.put(
                DatabaseHelper.COLUMN_BSP_STOCK_QUANTITY,
                quantity
        );

        return db.insertWithOnConflict(
                DatabaseHelper.TABLE_BRANCH_SPARE_PART,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }

    public boolean reduceBranchStock(
            int branchId,
            int partId,
            int amount
    ) {

        if (amount <= 0) {
            return false;
        }

        int currentStock =
                getPartStockAtBranch(
                        partId,
                        branchId
                );

        if (currentStock < amount) {
            return false;
        }

        int newQuantity =
                currentStock - amount;

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                DatabaseHelper.COLUMN_BSP_STOCK_QUANTITY,
                newQuantity
        );

        int result =
                db.update(
                        DatabaseHelper.TABLE_BRANCH_SPARE_PART,
                        values,

                        DatabaseHelper.COLUMN_BSP_BRANCH_ID +
                                " = ? AND " +

                                DatabaseHelper.COLUMN_BSP_PART_ID +
                                " = ?",

                        new String[]{
                                String.valueOf(branchId),
                                String.valueOf(partId)
                        }
                );

        return result > 0;
    }

    private SparePart mapCursorToSparePart(
            Cursor cursor
    ) {

        int id =
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_PART_ID
                        )
                );

        String name =
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_PART_NAME
                        )
                );

        String description =
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_DESCRIPTION
                        )
                );

        double price =
                cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_PRICE
                        )
                );

        String compatibleModels =
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_COMPATIBLE_MODELS
                        )
                );

        return new SparePart(
                id,
                name,
                description,
                price,
                compatibleModels
        );
    }
}