package com.techfix.app.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.techfix.app.models.AppointmentSparePart;
import com.techfix.app.models.PartSelection;

import java.util.ArrayList;
import java.util.List;

public class AppointmentSparePartDAO {

    private final DatabaseHelper databaseHelper;

    public AppointmentSparePartDAO(
            Context context
    ) {
        databaseHelper =
                new DatabaseHelper(
                        context.getApplicationContext()
                );
    }

    public List<AppointmentSparePart> getPartsForAppointment(
            long appointmentId
    ) {

        List<AppointmentSparePart> result =
                new ArrayList<>();

        if (appointmentId <= 0) {
            return result;
        }

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        String sql =
                "SELECT asp." +
                        DatabaseHelper.COLUMN_ASP_APPOINTMENT_ID +
                        ", asp." +
                        DatabaseHelper.COLUMN_ASP_PART_ID +
                        ", asp." +
                        DatabaseHelper.COLUMN_ASP_QUANTITY +
                        ", p." +
                        DatabaseHelper.COLUMN_PART_NAME +
                        ", p." +
                        DatabaseHelper.COLUMN_PRICE +
                        " FROM " +
                        DatabaseHelper.TABLE_APPOINTMENT_SPARE_PART +
                        " asp INNER JOIN " +
                        DatabaseHelper.TABLE_SPARE_PART +
                        " p ON asp." +
                        DatabaseHelper.COLUMN_ASP_PART_ID +
                        " = p." +
                        DatabaseHelper.COLUMN_PART_ID +
                        " WHERE asp." +
                        DatabaseHelper.COLUMN_ASP_APPOINTMENT_ID +
                        " = ? ORDER BY p." +
                        DatabaseHelper.COLUMN_PART_NAME +
                        " ASC";

        Cursor cursor =
                db.rawQuery(
                        sql,
                        new String[]{
                                String.valueOf(
                                        appointmentId
                                )
                        }
                );

        try {

            while (cursor.moveToNext()) {

                AppointmentSparePart item =
                        new AppointmentSparePart();

                item.setAppointmentId(
                        cursor.getLong(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_ASP_APPOINTMENT_ID
                                )
                        )
                );

                item.setPartId(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_ASP_PART_ID
                                )
                        )
                );

                item.setQuantity(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_ASP_QUANTITY
                                )
                        )
                );

                item.setPartName(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_PART_NAME
                                )
                        )
                );

                item.setUnitPrice(
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_PRICE
                                )
                        )
                );

                result.add(item);
            }

        } finally {

            cursor.close();
        }

        return result;
    }

    public ArrayList<PartSelection> getSelectionsForAppointment(
            long appointmentId
    ) {

        ArrayList<PartSelection> selections =
                new ArrayList<>();

        for (
                AppointmentSparePart item
                :
                getPartsForAppointment(
                        appointmentId
                )
        ) {

            selections.add(
                    new PartSelection(
                            item.getPartId(),
                            item.getPartName(),
                            item.getUnitPrice(),
                            item.getQuantity()
                    )
            );
        }

        return selections;
    }

    public double getPartsTotalForAppointment(
            long appointmentId
    ) {

        double total =
                0.0;

        for (
                AppointmentSparePart item
                :
                getPartsForAppointment(
                        appointmentId
                )
        ) {

            total +=
                    item.getTotalPrice();
        }

        return total;
    }

    public void close() {
        databaseHelper.close();
    }
}
