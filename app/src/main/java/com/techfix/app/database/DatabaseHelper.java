package com.techfix.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "techfix.db";
    private static final int DATABASE_VERSION = 3;

    // =========================================================
    // BRANCH
    // =========================================================

    public static final String TABLE_BRANCH = "branches";

    public static final String COLUMN_BRANCH_ID = "branch_id";
    public static final String COLUMN_BRANCH_NAME = "branch_name";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    // =========================================================
    // TECHNICIAN
    // =========================================================

    public static final String TABLE_TECHNICIAN = "technicians";

    public static final String COLUMN_TECHNICIAN_ID = "technician_id";
    public static final String COLUMN_TECHNICIAN_NAME = "technician_name";
    public static final String COLUMN_TECHNICIAN_PHONE = "phone";
    public static final String COLUMN_SPECIALIZATION = "specialization";
    public static final String COLUMN_AVAILABLE = "available";
    public static final String COLUMN_TECHNICIAN_BRANCH_ID = "branch_id";

    // =========================================================
    // SERVICE
    // =========================================================

    public static final String TABLE_SERVICE = "services";

    public static final String COLUMN_SERVICE_ID = "service_id";
    public static final String COLUMN_SERVICE_NAME = "service_name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_DURATION_MINUTES = "duration_minutes";
    public static final String COLUMN_CATEGORY = "category";

    // =========================================================
    // SPARE PART
    // =========================================================

    public static final String TABLE_SPARE_PART = "spare_parts";

    public static final String COLUMN_PART_ID = "part_id";
    public static final String COLUMN_PART_NAME = "part_name";
    public static final String COLUMN_COMPATIBLE_MODELS = "compatible_models";

    // =========================================================
    // BRANCH SPARE PART
    // =========================================================

    public static final String TABLE_BRANCH_SPARE_PART =
            "branch_spare_parts";

    public static final String COLUMN_BSP_BRANCH_ID =
            "branch_id";

    public static final String COLUMN_BSP_PART_ID =
            "part_id";

    public static final String COLUMN_BSP_STOCK_QUANTITY =
            "stock_quantity";

    // =========================================================
    // APPOINTMENT
    // =========================================================

    public static final String TABLE_APPOINTMENT = "appointments";

    public static final String COLUMN_APPOINTMENT_ID = "appointment_id";
    public static final String COLUMN_APPOINTMENT_USER_ID = "user_id";
    public static final String COLUMN_APPOINTMENT_SERVICE_ID = "service_id";
    public static final String COLUMN_APPOINTMENT_PART_ID = "part_id";
    public static final String COLUMN_APPOINTMENT_BRANCH_ID = "branch_id";
    public static final String COLUMN_DEVICE_MODEL = "device_model";
    public static final String COLUMN_ISSUE_DESCRIPTION = "issue_description";
    public static final String COLUMN_APPOINTMENT_DATE = "appointment_date";
    public static final String COLUMN_APPOINTMENT_TIME = "appointment_time";
    public static final String COLUMN_STATUS = "status";

    public DatabaseHelper(Context context) {
        super(
                context,
                DATABASE_NAME,
                null,
                DATABASE_VERSION
        );
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {

        super.onConfigure(db);

        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createBranchTable =
                "CREATE TABLE " + TABLE_BRANCH + " (" +
                        COLUMN_BRANCH_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        COLUMN_BRANCH_NAME +
                        " TEXT NOT NULL, " +

                        COLUMN_ADDRESS +
                        " TEXT NOT NULL, " +

                        COLUMN_LATITUDE +
                        " REAL NOT NULL, " +

                        COLUMN_LONGITUDE +
                        " REAL NOT NULL" +

                        ")";

        String createTechnicianTable =
                "CREATE TABLE " + TABLE_TECHNICIAN + " (" +

                        COLUMN_TECHNICIAN_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        COLUMN_TECHNICIAN_NAME +
                        " TEXT NOT NULL, " +

                        COLUMN_TECHNICIAN_PHONE +
                        " TEXT NOT NULL, " +

                        COLUMN_SPECIALIZATION +
                        " TEXT NOT NULL, " +

                        COLUMN_AVAILABLE +
                        " INTEGER NOT NULL DEFAULT 1, " +

                        COLUMN_TECHNICIAN_BRANCH_ID +
                        " INTEGER NOT NULL, " +

                        "FOREIGN KEY(" +
                        COLUMN_TECHNICIAN_BRANCH_ID +
                        ") REFERENCES " +
                        TABLE_BRANCH +
                        "(" +
                        COLUMN_BRANCH_ID +
                        ")" +

                        ")";

        String createServiceTable =
                "CREATE TABLE " + TABLE_SERVICE + " (" +

                        COLUMN_SERVICE_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        COLUMN_SERVICE_NAME +
                        " TEXT NOT NULL, " +

                        COLUMN_DESCRIPTION +
                        " TEXT NOT NULL, " +

                        COLUMN_PRICE +
                        " REAL NOT NULL, " +

                        COLUMN_DURATION_MINUTES +
                        " INTEGER NOT NULL, " +

                        COLUMN_CATEGORY +
                        " TEXT NOT NULL" +

                        ")";

        String createSparePartTable =
                "CREATE TABLE " + TABLE_SPARE_PART + " (" +

                        COLUMN_PART_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        COLUMN_PART_NAME +
                        " TEXT NOT NULL, " +

                        COLUMN_DESCRIPTION +
                        " TEXT NOT NULL, " +

                        COLUMN_PRICE +
                        " REAL NOT NULL, " +

                        COLUMN_COMPATIBLE_MODELS +
                        " TEXT" +

                        ")";

        String createBranchSparePartTable =
                "CREATE TABLE " + TABLE_BRANCH_SPARE_PART + " (" +

                        COLUMN_BSP_BRANCH_ID +
                        " INTEGER NOT NULL, " +

                        COLUMN_BSP_PART_ID +
                        " INTEGER NOT NULL, " +

                        COLUMN_BSP_STOCK_QUANTITY +
                        " INTEGER NOT NULL DEFAULT 0, " +

                        "PRIMARY KEY (" +
                        COLUMN_BSP_BRANCH_ID +
                        ", " +
                        COLUMN_BSP_PART_ID +
                        "), " +

                        "FOREIGN KEY(" +
                        COLUMN_BSP_BRANCH_ID +
                        ") REFERENCES " +
                        TABLE_BRANCH +
                        "(" +
                        COLUMN_BRANCH_ID +
                        "), " +

                        "FOREIGN KEY(" +
                        COLUMN_BSP_PART_ID +
                        ") REFERENCES " +
                        TABLE_SPARE_PART +
                        "(" +
                        COLUMN_PART_ID +
                        ")" +

                        ")";

        String createAppointmentTable =
                "CREATE TABLE " + TABLE_APPOINTMENT + " (" +

                        COLUMN_APPOINTMENT_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        COLUMN_APPOINTMENT_USER_ID +
                        " INTEGER NOT NULL, " +

                        COLUMN_APPOINTMENT_SERVICE_ID +
                        " INTEGER NOT NULL, " +

                        COLUMN_APPOINTMENT_PART_ID +
                        " INTEGER, " +

                        COLUMN_APPOINTMENT_BRANCH_ID +
                        " INTEGER NOT NULL, " +

                        COLUMN_DEVICE_MODEL +
                        " TEXT NOT NULL, " +

                        COLUMN_ISSUE_DESCRIPTION +
                        " TEXT NOT NULL, " +

                        COLUMN_APPOINTMENT_DATE +
                        " TEXT NOT NULL, " +

                        COLUMN_APPOINTMENT_TIME +
                        " TEXT NOT NULL, " +

                        COLUMN_STATUS +
                        " TEXT NOT NULL DEFAULT 'PENDING', " +

                        "FOREIGN KEY(" +
                        COLUMN_APPOINTMENT_SERVICE_ID +
                        ") REFERENCES " +
                        TABLE_SERVICE +
                        "(" +
                        COLUMN_SERVICE_ID +
                        "), " +

                        "FOREIGN KEY(" +
                        COLUMN_APPOINTMENT_PART_ID +
                        ") REFERENCES " +
                        TABLE_SPARE_PART +
                        "(" +
                        COLUMN_PART_ID +
                        "), " +

                        "FOREIGN KEY(" +
                        COLUMN_APPOINTMENT_BRANCH_ID +
                        ") REFERENCES " +
                        TABLE_BRANCH +
                        "(" +
                        COLUMN_BRANCH_ID +
                        ")" +

                        ")";

        db.execSQL(createBranchTable);

        db.execSQL(createTechnicianTable);

        db.execSQL(createServiceTable);

        db.execSQL(createSparePartTable);

        db.execSQL(createBranchSparePartTable);

        db.execSQL(createAppointmentTable);

        insertInitialBranches(db);

        seedServices(db);

        seedSpareParts(db);

        seedBranchSparePartStock(db);
    }

    // =========================================================
    // BRANCH SEED DATA
    // =========================================================

    private void insertInitialBranches(
            SQLiteDatabase db
    ) {

        ContentValues colombo =
                new ContentValues();

        colombo.put(
                COLUMN_BRANCH_NAME,
                "TechFix Colombo"
        );

        colombo.put(
                COLUMN_ADDRESS,
                "Colombo, Sri Lanka"
        );

        colombo.put(
                COLUMN_LATITUDE,
                6.9271
        );

        colombo.put(
                COLUMN_LONGITUDE,
                79.8612
        );

        db.insert(
                TABLE_BRANCH,
                null,
                colombo
        );


        ContentValues galle =
                new ContentValues();

        galle.put(
                COLUMN_BRANCH_NAME,
                "TechFix Galle"
        );

        galle.put(
                COLUMN_ADDRESS,
                "Galle, Sri Lanka"
        );

        galle.put(
                COLUMN_LATITUDE,
                6.0329
        );

        galle.put(
                COLUMN_LONGITUDE,
                80.2168
        );

        db.insert(
                TABLE_BRANCH,
                null,
                galle
        );
    }

    // =========================================================
    // SERVICE SEED DATA
    // =========================================================

    private void seedServices(
            SQLiteDatabase db
    ) {

        db.execSQL(
                "INSERT INTO " +
                        TABLE_SERVICE +
                        " (" +

                        COLUMN_SERVICE_NAME + ", " +
                        COLUMN_DESCRIPTION + ", " +
                        COLUMN_PRICE + ", " +
                        COLUMN_DURATION_MINUTES + ", " +
                        COLUMN_CATEGORY +

                        ") VALUES " +

                        "('Screen Replacement', " +
                        "'Cracked or damaged screen replaced with a genuine display panel.', " +
                        "49.99, 45, 'Screen'), " +

                        "('Battery Replacement', " +
                        "'Worn-out battery replaced with a new certified battery.', " +
                        "29.99, 30, 'Battery'), " +

                        "('Water Damage Treatment', " +
                        "'Full diagnostic and corrosion treatment for liquid damage.', " +
                        "59.99, 90, 'Diagnostics'), " +

                        "('Charging Port Repair', " +
                        "'Faulty or loose charging port cleaned or replaced.', " +
                        "24.99, 40, 'Hardware'), " +

                        "('Software Troubleshooting', " +
                        "'OS reinstall, malware removal and performance tuning.', " +
                        "19.99, 60, 'Software'), " +

                        "('Keyboard Replacement', " +
                        "'Damaged keyboard or unresponsive keys replaced.', " +
                        "39.99, 75, 'Hardware')"
        );
    }

    // =========================================================
    // SPARE PART SEED DATA
    // =========================================================

    private void seedSpareParts(
            SQLiteDatabase db
    ) {

        db.execSQL(
                "INSERT INTO " +
                        TABLE_SPARE_PART +
                        " (" +

                        COLUMN_PART_NAME + ", " +
                        COLUMN_DESCRIPTION + ", " +
                        COLUMN_PRICE + ", " +
                        COLUMN_COMPATIBLE_MODELS +

                        ") VALUES " +

                        "('OLED Display Assembly', " +
                        "'Original quality OLED display with touch digitizer.', " +
                        "89.99, 'iPhone 12, iPhone 12 Pro'), " +

                        "('Li-Po Battery 3200mAh', " +
                        "'High capacity replacement battery with adhesive kit.', " +
                        "34.99, 'Samsung Galaxy S21'), " +

                        "('USB-C Charging Board', " +
                        "'Replacement USB-C flex board with microphone.', " +
                        "18.99, 'Pixel 6, Pixel 6a'), " +

                        "('Back Glass Panel', " +
                        "'Tempered back glass with pre-installed camera lens.', " +
                        "27.50, 'iPhone 13'), " +

                        "('Laptop Keyboard (US Layout)', " +
                        "'Backlit keyboard module with ribbon cable.', " +
                        "44.00, 'Dell Inspiron 15'), " +

                        "('SSD 512GB NVMe', " +
                        "'NVMe M.2 solid state drive upgrade kit.', " +
                        "55.00, 'Universal Laptop')"
        );
    }

    // =========================================================
    // BRANCH INVENTORY SEED DATA
    // =========================================================

    private void seedBranchSparePartStock(
            SQLiteDatabase db
    ) {

        // Colombo
        insertBranchStock(db, 1, 1, 8);
        insertBranchStock(db, 1, 2, 12);
        insertBranchStock(db, 1, 3, 9);
        insertBranchStock(db, 1, 4, 5);
        insertBranchStock(db, 1, 5, 3);
        insertBranchStock(db, 1, 6, 0);

        // Galle
        insertBranchStock(db, 2, 1, 4);
        insertBranchStock(db, 2, 2, 8);
        insertBranchStock(db, 2, 3, 6);
        insertBranchStock(db, 2, 4, 3);
        insertBranchStock(db, 2, 5, 2);
        insertBranchStock(db, 2, 6, 1);
    }

    private void insertBranchStock(
            SQLiteDatabase db,
            int branchId,
            int partId,
            int quantity
    ) {

        ContentValues values =
                new ContentValues();

        values.put(
                COLUMN_BSP_BRANCH_ID,
                branchId
        );

        values.put(
                COLUMN_BSP_PART_ID,
                partId
        );

        values.put(
                COLUMN_BSP_STOCK_QUANTITY,
                quantity
        );

        db.insert(
                TABLE_BRANCH_SPARE_PART,
                null,
                values
        );
    }

    // =========================================================
    // UPGRADE
    // =========================================================

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {

        db.execSQL(
                "DROP TABLE IF EXISTS " +
                        TABLE_APPOINTMENT
        );

        db.execSQL(
                "DROP TABLE IF EXISTS " +
                        TABLE_BRANCH_SPARE_PART
        );

        db.execSQL(
                "DROP TABLE IF EXISTS " +
                        TABLE_TECHNICIAN
        );

        db.execSQL(
                "DROP TABLE IF EXISTS " +
                        TABLE_SPARE_PART
        );

        db.execSQL(
                "DROP TABLE IF EXISTS " +
                        TABLE_SERVICE
        );

        db.execSQL(
                "DROP TABLE IF EXISTS " +
                        TABLE_BRANCH
        );

        onCreate(db);
    }
}