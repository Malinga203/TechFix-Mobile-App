package com.techfix.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "techfix.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_BRANCH = "branches";

    public static final String COLUMN_BRANCH_ID = "branch_id";
    public static final String COLUMN_BRANCH_NAME = "branch_name";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    public static final String TABLE_SERVICE = "services";

    public static final String COLUMN_SERVICE_ID = "service_id";
    public static final String COLUMN_SERVICE_NAME = "service_name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_DURATION_MINUTES = "duration_minutes";
    public static final String COLUMN_CATEGORY = "category";

    public static final String TABLE_SPARE_PART = "spare_parts";

    public static final String COLUMN_PART_ID = "part_id";
    public static final String COLUMN_PART_NAME = "part_name";
    public static final String COLUMN_STOCK_QUANTITY = "stock_quantity";
    public static final String COLUMN_COMPATIBLE_MODELS = "compatible_models";

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
        db.execSQL("PRAGMA foreign_keys=ON");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createBranchTable =
                "CREATE TABLE " + TABLE_BRANCH + " (" +
                        COLUMN_BRANCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_BRANCH_NAME + " TEXT NOT NULL, " +
                        COLUMN_ADDRESS + " TEXT NOT NULL, " +
                        COLUMN_LATITUDE + " REAL NOT NULL, " +
                        COLUMN_LONGITUDE + " REAL NOT NULL" +
                        ")";

        String createServiceTable =
                "CREATE TABLE " + TABLE_SERVICE + " (" +
                        COLUMN_SERVICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_SERVICE_NAME + " TEXT NOT NULL, " +
                        COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                        COLUMN_PRICE + " REAL NOT NULL, " +
                        COLUMN_DURATION_MINUTES + " INTEGER NOT NULL, " +
                        COLUMN_CATEGORY + " TEXT NOT NULL" +
                        ")";

        String createSparePartTable =
                "CREATE TABLE " + TABLE_SPARE_PART + " (" +
                        COLUMN_PART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_PART_NAME + " TEXT NOT NULL, " +
                        COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                        COLUMN_PRICE + " REAL NOT NULL, " +
                        COLUMN_STOCK_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
                        COLUMN_COMPATIBLE_MODELS + " TEXT" +
                        ")";

        String createAppointmentTable =
                "CREATE TABLE " + TABLE_APPOINTMENT + " (" +
                        COLUMN_APPOINTMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_APPOINTMENT_USER_ID + " INTEGER NOT NULL, " +
                        COLUMN_APPOINTMENT_SERVICE_ID + " INTEGER NOT NULL, " +
                        COLUMN_APPOINTMENT_PART_ID + " INTEGER, " +
                        COLUMN_APPOINTMENT_BRANCH_ID + " INTEGER NOT NULL, " +
                        COLUMN_DEVICE_MODEL + " TEXT NOT NULL, " +
                        COLUMN_ISSUE_DESCRIPTION + " TEXT NOT NULL, " +
                        COLUMN_APPOINTMENT_DATE + " TEXT NOT NULL, " +
                        COLUMN_APPOINTMENT_TIME + " TEXT NOT NULL, " +
                        COLUMN_STATUS + " TEXT NOT NULL DEFAULT 'PENDING', " +
                        "FOREIGN KEY(" + COLUMN_APPOINTMENT_SERVICE_ID + ") REFERENCES " +
                        TABLE_SERVICE + "(" + COLUMN_SERVICE_ID + "), " +
                        "FOREIGN KEY(" + COLUMN_APPOINTMENT_PART_ID + ") REFERENCES " +
                        TABLE_SPARE_PART + "(" + COLUMN_PART_ID + "), " +
                        "FOREIGN KEY(" + COLUMN_APPOINTMENT_BRANCH_ID + ") REFERENCES " +
                        TABLE_BRANCH + "(" + COLUMN_BRANCH_ID + ")" +
                        ")";

        db.execSQL(createBranchTable);

        db.execSQL(createServiceTable);

        db.execSQL(createSparePartTable);

        db.execSQL(createAppointmentTable);

        seedDefaultData(db);
        insertInitialBranches(db);
    }

    private void insertInitialBranches(SQLiteDatabase db) {

        ContentValues colombo = new ContentValues();

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


        ContentValues galle = new ContentValues();

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

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {

        db.execSQL(
                "DROP TABLE IF EXISTS " + TABLE_APPOINTMENT
        );

        db.execSQL(
                "DROP TABLE IF EXISTS " + TABLE_SPARE_PART
        );

        db.execSQL(
                "DROP TABLE IF EXISTS " + TABLE_SERVICE
        );

        db.execSQL(
                "DROP TABLE IF EXISTS " + TABLE_BRANCH
        );

        onCreate(db);
    }

    private void seedDefaultData(SQLiteDatabase db) {

        db.execSQL(
                "INSERT INTO " + TABLE_SERVICE + " (" +
                        COLUMN_SERVICE_NAME + ", " +
                        COLUMN_DESCRIPTION + ", " +
                        COLUMN_PRICE + ", " +
                        COLUMN_DURATION_MINUTES + ", " +
                        COLUMN_CATEGORY + ") VALUES " +
                        "('Screen Replacement', 'Cracked or damaged screen replaced with a genuine display panel.', 49.99, 45, 'Screen'), " +
                        "('Battery Replacement', 'Worn-out battery replaced with a new certified battery.', 29.99, 30, 'Battery'), " +
                        "('Water Damage Treatment', 'Full diagnostic and corrosion treatment for liquid damage.', 59.99, 90, 'Diagnostics'), " +
                        "('Charging Port Repair', 'Faulty or loose charging port cleaned or replaced.', 24.99, 40, 'Hardware'), " +
                        "('Software Troubleshooting', 'OS reinstall, malware removal and performance tuning.', 19.99, 60, 'Software'), " +
                        "('Keyboard Replacement', 'Damaged keyboard or unresponsive keys replaced.', 39.99, 75, 'Hardware')"
        );

        db.execSQL(
                "INSERT INTO " + TABLE_SPARE_PART + " (" +
                        COLUMN_PART_NAME + ", " +
                        COLUMN_DESCRIPTION + ", " +
                        COLUMN_PRICE + ", " +
                        COLUMN_STOCK_QUANTITY + ", " +
                        COLUMN_COMPATIBLE_MODELS + ") VALUES " +
                        "('OLED Display Assembly', 'Original quality OLED display with touch digitizer.', 89.99, 12, 'iPhone 12, iPhone 12 Pro'), " +
                        "('Li-Po Battery 3200mAh', 'High capacity replacement battery with adhesive kit.', 34.99, 20, 'Samsung Galaxy S21'), " +
                        "('USB-C Charging Board', 'Replacement USB-C flex board with microphone.', 18.99, 15, 'Pixel 6, Pixel 6a'), " +
                        "('Back Glass Panel', 'Tempered back glass with pre-installed camera lens.', 27.50, 8, 'iPhone 13'), " +
                        "('Laptop Keyboard (US Layout)', 'Backlit keyboard module with ribbon cable.', 44.00, 5, 'Dell Inspiron 15'), " +
                        "('SSD 512GB NVMe', 'NVMe M.2 solid state drive upgrade kit.', 55.00, 0, 'Universal Laptop')"
        );
    }
}
