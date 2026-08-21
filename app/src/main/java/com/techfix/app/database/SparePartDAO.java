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

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_SPARE_PART,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_PART_NAME + " ASC"
        );

        if (cursor.moveToFirst()) {

            do {

                SparePart sparePart = mapCursorToSparePart(cursor);

                spareParts.add(sparePart);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return spareParts;
    }

    public SparePart getSparePartById(int partId) {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_SPARE_PART,
                null,
                DatabaseHelper.COLUMN_PART_ID + " = ?",
                new String[]{String.valueOf(partId)},
                null,
                null,
                null
        );

        SparePart sparePart = null;

        if (cursor.moveToFirst()) {
            sparePart = mapCursorToSparePart(cursor);
        }

        cursor.close();

        return sparePart;
    }

    public int reduceStock(int partId, int quantity) {

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                DatabaseHelper.COLUMN_STOCK_QUANTITY,
                quantity
        );

        return db.update(
                DatabaseHelper.TABLE_SPARE_PART,
                values,
                DatabaseHelper.COLUMN_PART_ID + " = ?",
                new String[]{String.valueOf(partId)}
        );
    }

    private SparePart mapCursorToSparePart(Cursor cursor) {

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

        int stock =
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_STOCK_QUANTITY
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
                stock,
                compatibleModels
        );
    }
}
