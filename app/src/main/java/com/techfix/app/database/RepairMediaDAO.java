package com.techfix.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.techfix.app.models.RepairMedia;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RepairMediaDAO {

    private static final String DISPLAY_DEVICE =
            "repair_device_name";

    private static final String DISPLAY_SERVICE =
            "repair_service_name";

    private final DatabaseHelper databaseHelper;

    public RepairMediaDAO(Context context) {

        databaseHelper =
                new DatabaseHelper(
                        context.getApplicationContext()
                );
    }

    public long insertMedia(
            RepairMedia media
    ) {

        if (media == null
                || media.getRepairId() <= 0
                || TextUtils.isEmpty(media.getImageUri())
                || !isValidType(media.getMediaType())) {

            return -1;
        }

        String now =
                getCurrentTimestamp();

        String approvalStatus =
                RepairMedia.TYPE_SAMPLE.equals(
                        media.getMediaType()
                )
                        ? RepairMedia.APPROVAL_PENDING
                        : RepairMedia.APPROVAL_APPROVED;

        ContentValues values =
                new ContentValues();

        values.put(
                DatabaseHelper.COLUMN_MEDIA_REPAIR_ID,
                media.getRepairId()
        );

        if (media.getTechnicianId() > 0) {

            values.put(
                    DatabaseHelper.COLUMN_MEDIA_TECHNICIAN_ID,
                    media.getTechnicianId()
            );

        } else {

            values.putNull(
                    DatabaseHelper.COLUMN_MEDIA_TECHNICIAN_ID
            );
        }

        values.put(
                DatabaseHelper.COLUMN_MEDIA_IMAGE_URI,
                media.getImageUri().trim()
        );

        values.put(
                DatabaseHelper.COLUMN_MEDIA_CAPTION,
                safeText(
                        media.getCaption()
                )
        );

        values.put(
                DatabaseHelper.COLUMN_MEDIA_TYPE,
                media.getMediaType()
        );

        values.put(
                DatabaseHelper.COLUMN_MEDIA_REPAIR_STAGE,
                safeText(
                        media.getRepairStage()
                )
        );

        values.put(
                DatabaseHelper.COLUMN_MEDIA_APPROVAL_STATUS,
                approvalStatus
        );

        values.put(
                DatabaseHelper.COLUMN_MEDIA_CREATED_AT,
                now
        );

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        return db.insert(
                DatabaseHelper.TABLE_REPAIR_MEDIA,
                null,
                values
        );
    }

    public List<RepairMedia> getProgressImagesForRepair(
            long repairId
    ) {

        if (repairId <= 0) {
            return new ArrayList<>();
        }

        String where =
                "m." +
                        DatabaseHelper.COLUMN_MEDIA_REPAIR_ID +
                        " = ? AND m." +
                        DatabaseHelper.COLUMN_MEDIA_TYPE +
                        " = ?";

        String[] args = {
                String.valueOf(repairId),
                RepairMedia.TYPE_PROGRESS
        };

        return queryMedia(
                where,
                args
        );
    }

    public List<RepairMedia> getApprovedSampleImages() {

        String where =
                "m." +
                        DatabaseHelper.COLUMN_MEDIA_TYPE +
                        " = ? AND m." +
                        DatabaseHelper.COLUMN_MEDIA_APPROVAL_STATUS +
                        " = ?";

        String[] args = {
                RepairMedia.TYPE_SAMPLE,
                RepairMedia.APPROVAL_APPROVED
        };

        return queryMedia(
                where,
                args
        );
    }

    public List<RepairMedia> getPendingSampleImages() {

        String where =
                "m." +
                        DatabaseHelper.COLUMN_MEDIA_TYPE +
                        " = ? AND m." +
                        DatabaseHelper.COLUMN_MEDIA_APPROVAL_STATUS +
                        " = ?";

        String[] args = {
                RepairMedia.TYPE_SAMPLE,
                RepairMedia.APPROVAL_PENDING
        };

        return queryMedia(
                where,
                args
        );
    }

    public List<RepairMedia> getMediaForRepair(
            long repairId
    ) {

        if (repairId <= 0) {
            return new ArrayList<>();
        }

        String where =
                "m." +
                        DatabaseHelper.COLUMN_MEDIA_REPAIR_ID +
                        " = ?";

        return queryMedia(
                where,
                new String[]{
                        String.valueOf(repairId)
                }
        );
    }

    public boolean updateSampleApproval(
            long mediaId,
            String approvalStatus
    ) {

        if (mediaId <= 0
                || (!RepairMedia.APPROVAL_APPROVED.equals(
                approvalStatus
        )
                && !RepairMedia.APPROVAL_REJECTED.equals(
                approvalStatus
        ))) {

            return false;
        }

        ContentValues values =
                new ContentValues();

        values.put(
                DatabaseHelper.COLUMN_MEDIA_APPROVAL_STATUS,
                approvalStatus
        );

        values.put(
                DatabaseHelper.COLUMN_MEDIA_APPROVED_AT,
                getCurrentTimestamp()
        );

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        int rows =
                db.update(
                        DatabaseHelper.TABLE_REPAIR_MEDIA,
                        values,

                        DatabaseHelper.COLUMN_MEDIA_ID +
                                " = ? AND " +
                                DatabaseHelper.COLUMN_MEDIA_TYPE +
                                " = ?",

                        new String[]{
                                String.valueOf(mediaId),
                                RepairMedia.TYPE_SAMPLE
                        }
                );

        return rows > 0;
    }

    private List<RepairMedia> queryMedia(
            String where,
            String[] args
    ) {

        List<RepairMedia> result =
                new ArrayList<>();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        String sql =
                "SELECT m.*, " +

                        "r." +
                        DatabaseHelper.COLUMN_REPAIR_DEVICE_NAME +
                        " AS " +
                        DISPLAY_DEVICE +
                        ", " +

                        "r." +
                        DatabaseHelper.COLUMN_REPAIR_SERVICE_NAME +
                        " AS " +
                        DISPLAY_SERVICE +

                        " FROM " +
                        DatabaseHelper.TABLE_REPAIR_MEDIA +
                        " m INNER JOIN " +
                        DatabaseHelper.TABLE_REPAIR +
                        " r ON m." +
                        DatabaseHelper.COLUMN_MEDIA_REPAIR_ID +
                        " = r." +
                        DatabaseHelper.COLUMN_REPAIR_ID +

                        " WHERE " +
                        where +

                        " ORDER BY m." +
                        DatabaseHelper.COLUMN_MEDIA_CREATED_AT +
                        " DESC";

        Cursor cursor =
                db.rawQuery(
                        sql,
                        args
                );

        try {

            while (cursor.moveToNext()) {

                result.add(
                        cursorToMedia(
                                cursor
                        )
                );
            }

        } finally {

            cursor.close();
        }

        return result;
    }

    private RepairMedia cursorToMedia(
            Cursor cursor
    ) {

        RepairMedia media =
                new RepairMedia();

        media.setMediaId(
                cursor.getLong(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_MEDIA_ID
                        )
                )
        );

        media.setRepairId(
                cursor.getLong(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_MEDIA_REPAIR_ID
                        )
                )
        );

        int technicianIndex =
                cursor.getColumnIndexOrThrow(
                        DatabaseHelper.COLUMN_MEDIA_TECHNICIAN_ID
                );

        media.setTechnicianId(
                cursor.isNull(
                        technicianIndex
                )
                        ? 0
                        : cursor.getLong(
                        technicianIndex
                )
        );

        media.setImageUri(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_MEDIA_IMAGE_URI
                        )
                )
        );

        media.setCaption(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_MEDIA_CAPTION
                        )
                )
        );

        media.setMediaType(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_MEDIA_TYPE
                        )
                )
        );

        media.setRepairStage(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_MEDIA_REPAIR_STAGE
                        )
                )
        );

        media.setApprovalStatus(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_MEDIA_APPROVAL_STATUS
                        )
                )
        );

        media.setCreatedAt(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_MEDIA_CREATED_AT
                        )
                )
        );

        media.setApprovedAt(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_MEDIA_APPROVED_AT
                        )
                )
        );

        media.setDeviceName(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DISPLAY_DEVICE
                        )
                )
        );

        media.setServiceName(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DISPLAY_SERVICE
                        )
                )
        );

        return media;
    }

    private boolean isValidType(
            String mediaType
    ) {

        return RepairMedia.TYPE_PROGRESS.equals(
                mediaType
        )
                || RepairMedia.TYPE_SAMPLE.equals(
                mediaType
        );
    }

    private String safeText(
            String value
    ) {

        return value == null
                ? ""
                : value.trim();
    }

    private String getCurrentTimestamp() {

        return new SimpleDateFormat(
                "yyyy-MM-dd HH:mm",
                Locale.getDefault()
        ).format(
                new Date()
        );
    }

    public void close() {
        databaseHelper.close();
    }
}