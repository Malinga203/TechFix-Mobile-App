package com.techfix.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.techfix.app.models.Repair;
import com.techfix.app.models.RepairSparePart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RepairSparePartDAO {

    private final DatabaseHelper databaseHelper;

    public RepairSparePartDAO(
            Context context
    ) {
        databaseHelper =
                new DatabaseHelper(
                        context.getApplicationContext()
                );
    }

    public boolean copyAppointmentPartsToRepair(
            long appointmentId,
            long repairId
    ) {

        if (
                appointmentId <= 0 ||
                        repairId <= 0
        ) {
            return false;
        }

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        db.beginTransaction();

        try {

            db.delete(
                    DatabaseHelper.TABLE_REPAIR_SPARE_PART,
                    DatabaseHelper.COLUMN_RSP_REPAIR_ID +
                            " = ?",
                    new String[]{
                            String.valueOf(
                                    repairId
                            )
                    }
            );

            String sql =
                    "INSERT OR REPLACE INTO " +
                            DatabaseHelper.TABLE_REPAIR_SPARE_PART +
                            " (" +
                            DatabaseHelper.COLUMN_RSP_REPAIR_ID +
                            ", " +
                            DatabaseHelper.COLUMN_RSP_PART_ID +
                            ", " +
                            DatabaseHelper.COLUMN_RSP_QUANTITY +
                            ") SELECT ?, " +
                            DatabaseHelper.COLUMN_ASP_PART_ID +
                            ", " +
                            DatabaseHelper.COLUMN_ASP_QUANTITY +
                            " FROM " +
                            DatabaseHelper.TABLE_APPOINTMENT_SPARE_PART +
                            " WHERE " +
                            DatabaseHelper.COLUMN_ASP_APPOINTMENT_ID +
                            " = ?";

            db.execSQL(
                    sql,
                    new Object[]{
                            repairId,
                            appointmentId
                    }
            );

            db.setTransactionSuccessful();

            return true;

        } finally {

            db.endTransaction();
        }
    }

    public List<RepairSparePart> getPartsForRepair(
            long repairId
    ) {

        List<RepairSparePart> result =
                new ArrayList<>();

        if (repairId <= 0) {
            return result;
        }

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        String sql =
                "SELECT rsp." +
                        DatabaseHelper.COLUMN_RSP_REPAIR_ID +
                        ", rsp." +
                        DatabaseHelper.COLUMN_RSP_PART_ID +
                        ", rsp." +
                        DatabaseHelper.COLUMN_RSP_QUANTITY +
                        ", p." +
                        DatabaseHelper.COLUMN_PART_NAME +
                        ", p." +
                        DatabaseHelper.COLUMN_PRICE +
                        " FROM " +
                        DatabaseHelper.TABLE_REPAIR_SPARE_PART +
                        " rsp INNER JOIN " +
                        DatabaseHelper.TABLE_SPARE_PART +
                        " p ON rsp." +
                        DatabaseHelper.COLUMN_RSP_PART_ID +
                        " = p." +
                        DatabaseHelper.COLUMN_PART_ID +
                        " WHERE rsp." +
                        DatabaseHelper.COLUMN_RSP_REPAIR_ID +
                        " = ? ORDER BY p." +
                        DatabaseHelper.COLUMN_PART_NAME +
                        " ASC";

        Cursor cursor =
                db.rawQuery(
                        sql,
                        new String[]{
                                String.valueOf(
                                        repairId
                                )
                        }
                );

        try {

            while (cursor.moveToNext()) {

                RepairSparePart item =
                        new RepairSparePart();

                item.setRepairId(
                        cursor.getLong(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_RSP_REPAIR_ID
                                )
                        )
                );

                item.setPartId(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_RSP_PART_ID
                                )
                        )
                );

                item.setQuantity(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_RSP_QUANTITY
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

    public boolean completeRepairAndDeductInventory(
            long repairId
    ) {

        if (repairId <= 0) {
            return false;
        }

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        db.beginTransaction();

        Cursor repairCursor =
                null;

        Cursor partsCursor =
                null;

        try {

            repairCursor =
                    db.query(
                            DatabaseHelper.TABLE_REPAIR,
                            new String[]{
                                    DatabaseHelper.COLUMN_REPAIR_BRANCH_ID,
                                    DatabaseHelper.COLUMN_REPAIR_STATUS,
                                    DatabaseHelper.COLUMN_REPAIR_INVENTORY_DEDUCTED
                            },
                            DatabaseHelper.COLUMN_REPAIR_ID +
                                    " = ?",
                            new String[]{
                                    String.valueOf(
                                            repairId
                                    )
                            },
                            null,
                            null,
                            null,
                            "1"
                    );

            if (!repairCursor.moveToFirst()) {
                return false;
            }

            int branchIndex =
                    repairCursor.getColumnIndexOrThrow(
                            DatabaseHelper.COLUMN_REPAIR_BRANCH_ID
                    );

            long branchId =
                    repairCursor.isNull(
                            branchIndex
                    )
                            ? 0
                            : repairCursor.getLong(
                            branchIndex
                    );

            String currentStatus =
                    repairCursor.getString(
                            repairCursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COLUMN_REPAIR_STATUS
                            )
                    );

            boolean inventoryAlreadyDeducted =
                    repairCursor.getInt(
                            repairCursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COLUMN_REPAIR_INVENTORY_DEDUCTED
                            )
                    ) == 1;

            /*
             * Idempotent success:
             * if payment completion is handled twice,
             * stock must not be reduced twice.
             */
            if (
                    Repair.STATUS_COMPLETED.equals(
                            currentStatus
                    )
                            &&
                            inventoryAlreadyDeducted
            ) {

                db.setTransactionSuccessful();

                return true;
            }

            if (
                    !Repair.STATUS_READY_FOR_COLLECTION.equals(
                            currentStatus
                    )
                            &&
                            !Repair.STATUS_COMPLETED.equals(
                                    currentStatus
                            )
            ) {

                return false;
            }

            partsCursor =
                    db.query(
                            DatabaseHelper.TABLE_REPAIR_SPARE_PART,
                            new String[]{
                                    DatabaseHelper.COLUMN_RSP_PART_ID,
                                    DatabaseHelper.COLUMN_RSP_QUANTITY
                            },
                            DatabaseHelper.COLUMN_RSP_REPAIR_ID +
                                    " = ?",
                            new String[]{
                                    String.valueOf(
                                            repairId
                                    )
                            },
                            null,
                            null,
                            null
                    );

            /*
             * First verify that every required part
             * still has enough stock.
             */
            if (!inventoryAlreadyDeducted) {

                while (partsCursor.moveToNext()) {

                    int partId =
                            partsCursor.getInt(
                                    partsCursor.getColumnIndexOrThrow(
                                            DatabaseHelper.COLUMN_RSP_PART_ID
                                    )
                            );

                    int quantity =
                            partsCursor.getInt(
                                    partsCursor.getColumnIndexOrThrow(
                                            DatabaseHelper.COLUMN_RSP_QUANTITY
                                    )
                            );

                    if (
                            branchId <= 0 ||
                                    quantity <= 0
                    ) {
                        return false;
                    }

                    Cursor stockCursor =
                            db.query(
                                    DatabaseHelper.TABLE_BRANCH_SPARE_PART,
                                    new String[]{
                                            DatabaseHelper.COLUMN_BSP_STOCK_QUANTITY
                                    },
                                    DatabaseHelper.COLUMN_BSP_BRANCH_ID +
                                            " = ? AND " +
                                            DatabaseHelper.COLUMN_BSP_PART_ID +
                                            " = ?",
                                    new String[]{
                                            String.valueOf(
                                                    branchId
                                            ),
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

                        if (!stockCursor.moveToFirst()) {
                            return false;
                        }

                        int stock =
                                stockCursor.getInt(
                                        stockCursor.getColumnIndexOrThrow(
                                                DatabaseHelper.COLUMN_BSP_STOCK_QUANTITY
                                        )
                                );

                        if (stock < quantity) {
                            return false;
                        }

                    } finally {

                        stockCursor.close();
                    }
                }

                /*
                 * Reset cursor and now perform deductions.
                 */
                partsCursor.moveToPosition(-1);

                while (partsCursor.moveToNext()) {

                    int partId =
                            partsCursor.getInt(
                                    partsCursor.getColumnIndexOrThrow(
                                            DatabaseHelper.COLUMN_RSP_PART_ID
                                    )
                            );

                    int quantity =
                            partsCursor.getInt(
                                    partsCursor.getColumnIndexOrThrow(
                                            DatabaseHelper.COLUMN_RSP_QUANTITY
                                    )
                            );

                    String updateSql =
                            "UPDATE " +
                                    DatabaseHelper.TABLE_BRANCH_SPARE_PART +
                                    " SET " +
                                    DatabaseHelper.COLUMN_BSP_STOCK_QUANTITY +
                                    " = " +
                                    DatabaseHelper.COLUMN_BSP_STOCK_QUANTITY +
                                    " - ? WHERE " +
                                    DatabaseHelper.COLUMN_BSP_BRANCH_ID +
                                    " = ? AND " +
                                    DatabaseHelper.COLUMN_BSP_PART_ID +
                                    " = ? AND " +
                                    DatabaseHelper.COLUMN_BSP_STOCK_QUANTITY +
                                    " >= ?";

                    db.execSQL(
                            updateSql,
                            new Object[]{
                                    quantity,
                                    branchId,
                                    partId,
                                    quantity
                            }
                    );

                    /*
                     * Confirm the deduction happened.
                     */
                    Cursor verifyCursor =
                            db.query(
                                    DatabaseHelper.TABLE_BRANCH_SPARE_PART,
                                    new String[]{
                                            DatabaseHelper.COLUMN_BSP_STOCK_QUANTITY
                                    },
                                    DatabaseHelper.COLUMN_BSP_BRANCH_ID +
                                            " = ? AND " +
                                            DatabaseHelper.COLUMN_BSP_PART_ID +
                                            " = ?",
                                    new String[]{
                                            String.valueOf(
                                                    branchId
                                            ),
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

                        if (!verifyCursor.moveToFirst()) {
                            return false;
                        }

                    } finally {

                        verifyCursor.close();
                    }
                }
            }

            String now =
                    new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm",
                            Locale.getDefault()
                    ).format(
                            new Date()
                    );

            ContentValues repairValues =
                    new ContentValues();

            repairValues.put(
                    DatabaseHelper.COLUMN_REPAIR_STATUS,
                    Repair.STATUS_COMPLETED
            );

            repairValues.put(
                    DatabaseHelper.COLUMN_REPAIR_UPDATED_AT,
                    now
            );

            repairValues.put(
                    DatabaseHelper.COLUMN_REPAIR_COMPLETED_AT,
                    now
            );

            repairValues.put(
                    DatabaseHelper.COLUMN_REPAIR_INVENTORY_DEDUCTED,
                    1
            );

            int updated =
                    db.update(
                            DatabaseHelper.TABLE_REPAIR,
                            repairValues,
                            DatabaseHelper.COLUMN_REPAIR_ID +
                                    " = ?",
                            new String[]{
                                    String.valueOf(
                                            repairId
                                    )
                            }
                    );

            if (updated <= 0) {
                return false;
            }

            db.setTransactionSuccessful();

            return true;

        } finally {

            if (repairCursor != null) {
                repairCursor.close();
            }

            if (partsCursor != null) {
                partsCursor.close();
            }

            db.endTransaction();
        }
    }

    public void close() {
        databaseHelper.close();
    }
}
