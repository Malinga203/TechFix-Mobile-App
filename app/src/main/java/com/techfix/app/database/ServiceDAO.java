package com.techfix.app.database;

import android.content.ContentValues;
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
                new DatabaseHelper(
                        context.getApplicationContext()
                );
    }


    // =========================================================
    // INSERT SERVICE
    // =========================================================

    public long insertService(
            RepairService service
    ) {

        if (service == null) {
            return -1;
        }

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                DatabaseHelper.COLUMN_SERVICE_NAME,
                service.getServiceName()
        );

        values.put(
                DatabaseHelper.COLUMN_DESCRIPTION,
                service.getDescription()
        );

        values.put(
                DatabaseHelper.COLUMN_PRICE,
                service.getPrice()
        );

        values.put(
                DatabaseHelper.COLUMN_DURATION_MINUTES,
                service.getDurationMinutes()
        );

        values.put(
                DatabaseHelper.COLUMN_CATEGORY,
                service.getCategory()
        );

        return db.insert(
                DatabaseHelper.TABLE_SERVICE,
                null,
                values
        );
    }


    // =========================================================
    // GET ALL SERVICES
    // =========================================================

    public List<RepairService> getAllServices() {

        List<RepairService> services =
                new ArrayList<>();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_SERVICE,
                        null,
                        null,
                        null,
                        null,
                        null,
                        DatabaseHelper.COLUMN_SERVICE_NAME +
                                " ASC"
                );

        try {

            while (
                    cursor.moveToNext()
            ) {

                services.add(
                        mapCursorToService(
                                cursor
                        )
                );
            }

        } finally {

            cursor.close();
        }

        return services;
    }


    // =========================================================
    // GET SERVICE BY ID
    // =========================================================

    public RepairService getServiceById(
            int serviceId
    ) {

        if (serviceId <= 0) {
            return null;
        }

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_SERVICE,
                        null,

                        DatabaseHelper.COLUMN_SERVICE_ID +
                                " = ?",

                        new String[]{
                                String.valueOf(
                                        serviceId
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

                return mapCursorToService(
                        cursor
                );
            }

        } finally {

            cursor.close();
        }

        return null;
    }


    // =========================================================
    // UPDATE SERVICE
    // =========================================================

    public int updateService(
            RepairService service
    ) {

        if (
                service == null ||
                        service.getServiceId() <= 0
        ) {

            return 0;
        }

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                DatabaseHelper.COLUMN_SERVICE_NAME,
                service.getServiceName()
        );

        values.put(
                DatabaseHelper.COLUMN_DESCRIPTION,
                service.getDescription()
        );

        values.put(
                DatabaseHelper.COLUMN_PRICE,
                service.getPrice()
        );

        values.put(
                DatabaseHelper.COLUMN_DURATION_MINUTES,
                service.getDurationMinutes()
        );

        values.put(
                DatabaseHelper.COLUMN_CATEGORY,
                service.getCategory()
        );

        return db.update(
                DatabaseHelper.TABLE_SERVICE,
                values,

                DatabaseHelper.COLUMN_SERVICE_ID +
                        " = ?",

                new String[]{
                        String.valueOf(
                                service.getServiceId()
                        )
                }
        );
    }


    // =========================================================
    // DELETE SERVICE
    // =========================================================

    public int deleteService(
            int serviceId
    ) {

        if (serviceId <= 0) {
            return 0;
        }

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        return db.delete(
                DatabaseHelper.TABLE_SERVICE,

                DatabaseHelper.COLUMN_SERVICE_ID +
                        " = ?",

                new String[]{
                        String.valueOf(
                                serviceId
                        )
                }
        );
    }


    // =========================================================
    // CHECK NAME
    // =========================================================

    public boolean serviceNameExists(
            String serviceName
    ) {

        if (
                serviceName == null ||
                        serviceName.trim().isEmpty()
        ) {

            return false;
        }

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_SERVICE,

                        new String[]{
                                DatabaseHelper.COLUMN_SERVICE_ID
                        },

                        DatabaseHelper.COLUMN_SERVICE_NAME +
                                " = ?",

                        new String[]{
                                serviceName.trim()
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
    // CURSOR -> SERVICE
    // =========================================================

    private RepairService mapCursorToService(
            Cursor cursor
    ) {

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


    // =========================================================
    // CLOSE
    // =========================================================

    public void close() {

        databaseHelper.close();
    }
}