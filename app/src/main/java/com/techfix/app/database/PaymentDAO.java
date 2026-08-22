package com.techfix.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.techfix.app.models.Payment;

public class PaymentDAO {

    private final DatabaseHelper databaseHelper;

    public PaymentDAO(Context context) {
        databaseHelper =
                new DatabaseHelper(context);
    }

    public long insertPayment(
            Payment payment
    ) {

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                DatabaseHelper.COLUMN_PAYMENT_APPOINTMENT_ID,
                payment.getAppointmentId()
        );

        values.put(
                DatabaseHelper.COLUMN_PAYMENT_ORDER_ID,
                payment.getOrderId()
        );

        values.put(
                DatabaseHelper.COLUMN_PAYMENT_AMOUNT,
                payment.getAmount()
        );

        values.put(
                DatabaseHelper.COLUMN_PAYMENT_CURRENCY,
                payment.getCurrency()
        );

        values.put(
                DatabaseHelper.COLUMN_PAYMENT_STATUS,
                payment.getStatus()
        );

        values.put(
                DatabaseHelper.COLUMN_PAYMENT_REFERENCE,
                payment.getPaymentReference()
        );

        values.put(
                DatabaseHelper.COLUMN_PAYMENT_DATE,
                payment.getPaymentDate()
        );

        return db.insert(
                DatabaseHelper.TABLE_PAYMENT,
                null,
                values
        );
    }

    public int updatePaymentStatus(
            String orderId,
            String status,
            String paymentReference
    ) {

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                DatabaseHelper.COLUMN_PAYMENT_STATUS,
                status
        );

        values.put(
                DatabaseHelper.COLUMN_PAYMENT_REFERENCE,
                paymentReference
        );

        return db.update(
                DatabaseHelper.TABLE_PAYMENT,
                values,
                DatabaseHelper.COLUMN_PAYMENT_ORDER_ID + " = ?",
                new String[]{
                        orderId
                }
        );
    }

    public Payment getPaymentByOrderId(
            String orderId
    ) {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_PAYMENT,
                        null,
                        DatabaseHelper.COLUMN_PAYMENT_ORDER_ID + " = ?",
                        new String[]{
                                orderId
                        },
                        null,
                        null,
                        null
                );

        Payment payment =
                null;

        if (cursor.moveToFirst()) {

            payment =
                    mapCursorToPayment(
                            cursor
                    );
        }

        cursor.close();

        return payment;
    }

    private Payment mapCursorToPayment(
            Cursor cursor
    ) {

        Payment payment =
                new Payment();

        payment.setPaymentId(
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_PAYMENT_ID
                        )
                )
        );

        payment.setAppointmentId(
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_PAYMENT_APPOINTMENT_ID
                        )
                )
        );

        payment.setOrderId(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_PAYMENT_ORDER_ID
                        )
                )
        );

        payment.setAmount(
                cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_PAYMENT_AMOUNT
                        )
                )
        );

        payment.setCurrency(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_PAYMENT_CURRENCY
                        )
                )
        );

        payment.setStatus(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_PAYMENT_STATUS
                        )
                )
        );

        int referenceIndex =
                cursor.getColumnIndexOrThrow(
                        DatabaseHelper.COLUMN_PAYMENT_REFERENCE
                );

        if (!cursor.isNull(referenceIndex)) {

            payment.setPaymentReference(
                    cursor.getString(
                            referenceIndex
                    )
            );
        }

        payment.setPaymentDate(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_PAYMENT_DATE
                        )
                )
        );

        return payment;
    }
}