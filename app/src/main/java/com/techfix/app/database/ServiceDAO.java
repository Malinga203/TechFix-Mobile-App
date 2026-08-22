package com.techfix.app.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.techfix.app.models.RepairService;

import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

    private final DatabaseHelper databaseHelper;

    public ServiceDAO(Context context) {
        databaseHelper =
                new DatabaseHelper(context);
    }

    public List<RepairService> getAllServices() {

        List<RepairService> services =
                new ArrayList<>();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_SERVICE,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_SERVICE_NAME + " ASC"
        );

        if (cursor.moveToFirst()) {

            do {

                RepairService service = mapCursorToService(cursor);

                services.add(service);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return services;
    }

    public RepairService getServiceById(int serviceId) {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_SERVICE,
                null,
                DatabaseHelper.COLUMN_SERVICE_ID + " = ?",
                new String[]{String.valueOf(serviceId)},
                null,
                null,
                null
        );

        RepairService service = null;

        if (cursor.moveToFirst()) {
            service = mapCursorToService(cursor);
        }

        cursor.close();

        return service;
    }

    private RepairService mapCursorToService(Cursor cursor) {

        int id =
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_SERVICE_ID
                        )
                );

        String name =
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_SERVICE_NAME
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

        int duration =
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_DURATION_MINUTES
                        )
                );

        String category =
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_CATEGORY
                        )
                );

        return new RepairService(
                id,
                name,
                description,
                price,
                duration,
                category
        );
    }
}
