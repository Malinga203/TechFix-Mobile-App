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


    public ServiceDAO(
            Context context
    ) {

        databaseHelper =
                new DatabaseHelper(
                        context.getApplicationContext()
                );
    }


    public long insertService(
            RepairService service
    ) {

        if (service == null) {

            return -1;
        }


        SQLiteDatabase db =
                databaseHelper
                        .getWritableDatabase();


        ContentValues values =
                createValues(
                        service
                );


        return db.insert(
                DatabaseHelper.TABLE_SERVICE,
                null,
                values
        );
    }


    public List<RepairService> getAllServices() {

        List<RepairService> services =
                new ArrayList<>();


        SQLiteDatabase db =
                databaseHelper
                        .getReadableDatabase();


        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_SERVICE,
                        null,
                        null,
                        null,
                        null,
                        null,
                        DatabaseHelper.COLUMN_SERVICE_NAME
                                +
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


    public RepairService getServiceById(
            int serviceId
    ) {

        if (serviceId <= 0) {

            return null;
        }


        SQLiteDatabase db =
                databaseHelper
                        .getReadableDatabase();


        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_SERVICE,
                        null,

                        DatabaseHelper.COLUMN_SERVICE_ID
                                +
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


    public int updateService(
            RepairService service
    ) {

        if (
                service == null
                        ||
                        service.getServiceId()
                                <=
                                0
        ) {

            return 0;
        }


        SQLiteDatabase db =
                databaseHelper
                        .getWritableDatabase();


        return db.update(
                DatabaseHelper.TABLE_SERVICE,

                createValues(
                        service
                ),

                DatabaseHelper.COLUMN_SERVICE_ID
                        +
                        " = ?",

                new String[]{
                        String.valueOf(
                                service.getServiceId()
                        )
                }
        );
    }


    public int deleteService(
            int serviceId
    ) {

        if (serviceId <= 0) {

            return 0;
        }


        SQLiteDatabase db =
                databaseHelper
                        .getWritableDatabase();


        return db.delete(
                DatabaseHelper.TABLE_SERVICE,

                DatabaseHelper.COLUMN_SERVICE_ID
                        +
                        " = ?",

                new String[]{
                        String.valueOf(
                                serviceId
                        )
                }
        );
    }


    public boolean serviceNameExists(
            String serviceName
    ) {

        if (
                serviceName == null
                        ||
                        serviceName
                                .trim()
                                .isEmpty()
        ) {

            return false;
        }


        SQLiteDatabase db =
                databaseHelper
                        .getReadableDatabase();


        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_SERVICE,

                        new String[]{
                                DatabaseHelper.COLUMN_SERVICE_ID
                        },

                        DatabaseHelper.COLUMN_SERVICE_NAME
                                +
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


    private ContentValues createValues(
            RepairService service
    ) {

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
                DatabaseHelper.COLUMN_SERVICE_TYPE,
                service.getServiceType()
        );


        values.put(
                DatabaseHelper.COLUMN_CATEGORY,
                service.getCategory()
        );


        return values;
    }


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


        String serviceType =
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_SERVICE_TYPE
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
                serviceType,
                category
        );
    }


    public void close() {

        databaseHelper.close();
    }
}