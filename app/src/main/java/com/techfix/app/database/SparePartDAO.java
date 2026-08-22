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
                new DatabaseHelper(
                        context.getApplicationContext()
                );
    }


    // =========================================================
    // INSERT SPARE PART
    // =========================================================

    public long insertSparePart(
            SparePart sparePart
    ) {

        if (sparePart == null) {
            return -1;
        }

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                DatabaseHelper.COLUMN_PART_NAME,
                sparePart.getPartName()
        );

        values.put(
                DatabaseHelper.COLUMN_DESCRIPTION,
                sparePart.getDescription()
        );

        values.put(
                DatabaseHelper.COLUMN_PRICE,
                sparePart.getPrice()
        );

        values.put(
                DatabaseHelper.COLUMN_COMPATIBLE_MODELS,
                sparePart.getCompatibleModels()
        );

        return db.insert(
                DatabaseHelper.TABLE_SPARE_PART,
                null,
                values
        );
    }


    // =========================================================
    // GET ALL SPARE PARTS
    // =========================================================

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

        try {

            while (
                    cursor.moveToNext()
            ) {

                spareParts.add(
                        mapCursorToSparePart(
                                cursor
                        )
                );
            }

        } finally {

            cursor.close();
        }

        return spareParts;
    }


    // =========================================================
    // GET SPARE PART BY ID
    // =========================================================

    public SparePart getSparePartById(
            int partId
    ) {

        if (partId <= 0) {
            return null;
        }

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_SPARE_PART,
                        null,

                        DatabaseHelper.COLUMN_PART_ID +
                                " = ?",

                        new String[]{
                                String.valueOf(
                                        partId
                                )
                        },

                        null,
                        null,
                        null,
                        "1"
                );

        try {

            if (
                    cursor.moveToFirst()
            ) {

                return mapCursorToSparePart(
                        cursor
                );
            }

        } finally {

            cursor.close();
        }

        return null;
    }


    // =========================================================
    // UPDATE SPARE PART
    // =========================================================

    public int updateSparePart(
            SparePart sparePart
    ) {

        if (
                sparePart == null ||
                        sparePart.getPartId() <= 0
        ) {

            return 0;
        }

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                DatabaseHelper.COLUMN_PART_NAME,
                sparePart.getPartName()
        );

        values.put(
                DatabaseHelper.COLUMN_DESCRIPTION,
                sparePart.getDescription()
        );

        values.put(
                DatabaseHelper.COLUMN_PRICE,
                sparePart.getPrice()
        );

        values.put(
                DatabaseHelper.COLUMN_COMPATIBLE_MODELS,
                sparePart.getCompatibleModels()
        );

        return db.update(
                DatabaseHelper.TABLE_SPARE_PART,
                values,

                DatabaseHelper.COLUMN_PART_ID +
                        " = ?",

                new String[]{
                        String.valueOf(
                                sparePart.getPartId()
                        )
                }
        );
    }


    // =========================================================
    // DELETE SPARE PART
    // =========================================================

    public int deleteSparePart(
            int partId
    ) {

        if (partId <= 0) {
            return 0;
        }

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        /*
         * Remove branch-stock rows first because
         * branch_spare_parts references spare_parts.
         */
        db.delete(
                DatabaseHelper.TABLE_BRANCH_SPARE_PART,

                DatabaseHelper.COLUMN_BSP_PART_ID +
                        " = ?",

                new String[]{
                        String.valueOf(
                                partId
                        )
                }
        );

        return db.delete(
                DatabaseHelper.TABLE_SPARE_PART,

                DatabaseHelper.COLUMN_PART_ID +
                        " = ?",

                new String[]{
                        String.valueOf(
                                partId
                        )
                }
        );
    }


    // =========================================================
    // CHECK PART NAME EXISTS
    // =========================================================

    public boolean partNameExists(
            String partName
    ) {

        if (
                partName == null ||
                        partName.trim().isEmpty()
        ) {

            return false;
        }

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_SPARE_PART,

                        new String[]{
                                DatabaseHelper.COLUMN_PART_ID
                        },

                        DatabaseHelper.COLUMN_PART_NAME +
                                " = ?",

                        new String[]{
                                partName.trim()
                        },

                        null,
                        null,
                        null,
                        "1"
                );

        try {

            return cursor.moveToFirst();

        } finally {

            cursor.close();
        }
    }


    // =========================================================
    // CHECK AVAILABILITY AT BRANCH
    // =========================================================

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


    // =========================================================
    // GET STOCK AT BRANCH
    // =========================================================

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

        int quantity =
                0;

        try {

            if (
                    cursor.moveToFirst()
            ) {

                quantity =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper
                                                .COLUMN_BSP_STOCK_QUANTITY
                                )
                        );
            }

        } finally {

            cursor.close();
        }

        return quantity;
    }


    // =========================================================
    // ADD OR UPDATE BRANCH STOCK
    // =========================================================

    public long addOrUpdateBranchStock(
            int branchId,
            int partId,
            int quantity
    ) {

        if (
                branchId <= 0 ||
                        partId <= 0 ||
                        quantity < 0
        ) {

            return -1;
        }

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


    // =========================================================
    // REDUCE STOCK
    // =========================================================

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

        if (
                currentStock < amount
        ) {

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


    // =========================================================
    // CURSOR -> SPARE PART
    // =========================================================

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


    // =========================================================
    // CLOSE
    // =========================================================

    public void close() {

        databaseHelper.close();
    }
}