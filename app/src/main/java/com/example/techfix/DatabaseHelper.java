package com.example.techfix;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TechFix.db";
    private static final int DATABASE_VERSION = 8;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Inventory table
        db.execSQL("CREATE TABLE inventory (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "productName TEXT NOT NULL, " +
                "category TEXT NOT NULL, " +
                "price REAL NOT NULL, " +
                "quantity INTEGER NOT NULL)");

        // Appointments table
        db.execSQL("CREATE TABLE appointments (" +
                "appointmentId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "productService TEXT NOT NULL, " +
                "category TEXT NOT NULL, " +
                "price REAL NOT NULL, " +
                "branch TEXT, " +
                "appointmentDate TEXT, " +
                "appointmentTime TEXT)");

        // Branches table
        db.execSQL("CREATE TABLE branches (" +
                "branchId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "branchCode TEXT NOT NULL, " +
                "branchName TEXT NOT NULL, " +
                "address TEXT, " +
                "phone TEXT, " +
                "email TEXT, " +
                "status TEXT DEFAULT 'Active', " +
                "latitude REAL, " +
                "longitude REAL)");

        // Services table
        db.execSQL("CREATE TABLE services (" +
                "serviceId TEXT PRIMARY KEY, " +
                "serviceName TEXT NOT NULL, " +
                "description TEXT, " +
                "price REAL NOT NULL, " +
                "duration TEXT, " +
                "status TEXT DEFAULT 'Active')");

        // Technicians table
        db.execSQL("CREATE TABLE technicians (" +
                "technicianId TEXT PRIMARY KEY, " +
                "technicianName TEXT NOT NULL, " +
                "phone TEXT, " +
                "email TEXT NOT NULL, " +
                "password TEXT NOT NULL)");

        // Feedback table
        db.execSQL("CREATE TABLE feedback (" +
                "feedbackId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customerName TEXT, " +
                "rating INTEGER, " +
                "comment TEXT, " +
                "date TEXT)");

        // Customers table
        db.execSQL("CREATE TABLE customers (" +
                "customerId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customerName TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "phone TEXT, " +
                "password TEXT NOT NULL)");

        // Admins table
        db.execSQL("CREATE TABLE admins (" +
                "adminId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "adminName TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "phone TEXT, " +
                "password TEXT NOT NULL)");

        // Insert default admin
        insertDefaultAdmin(db);

        // Insert sample products
        insertProduct(db, "PC Monitor", "Computer", 45000, 10);
        insertProduct(db, "Keyboard", "Computer", 5000, 25);
        insertProduct(db, "iPhone OLED Display", "Mobile", 85000, 3);
        insertProduct(db, "Laptop Battery", "Computer", 30000, 5);
        insertProduct(db, "iPhone Battery", "Mobile", 18000, 8);
        insertProduct(db, "Charging Port", "Mobile", 7500, 12);

        // Insert sample branches
        insertBranch(db, "B001", "Colombo",
                "Colombo 06",
                "0112345678",
                "colombo@techfix.com",
                "Active",
                6.927079,
                79.861244);

        insertBranch(db, "B002", "Gampaha",
                "Gampaha",
                "0332345678",
                "gampaha@techfix.com",
                "Active",
                7.0840,
                80.0098);

        insertBranch(db, "B003", "Kandy",
                "Kandy",
                "0812345678",
                "kandy@techfix.com",
                "Active",
                7.2906,
                80.6337);

        insertBranch(db, "B004", "Negombo",
                "Negombo",
                "0312345678",
                "negombo@techfix.com",
                "Active",
                7.2083,
                79.8358);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        /*
         * Version 2
         * Add appointments table
         */
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS appointments (" +
                    "appointmentId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "productService TEXT NOT NULL, " +
                    "category TEXT NOT NULL, " +
                    "price REAL NOT NULL, " +
                    "branch TEXT, " +
                    "appointmentDate TEXT, " +
                    "appointmentTime TEXT)");
        }

        /*
         * Version 3
         * Add branches table
         */
        if (oldVersion < 3) {
            db.execSQL("CREATE TABLE IF NOT EXISTS branches (" +
                    "branchId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "branchCode TEXT NOT NULL, " +
                    "branchName TEXT NOT NULL, " +
                    "address TEXT, " +
                    "phone TEXT, " +
                    "email TEXT, " +
                    "status TEXT DEFAULT 'Active', " +
                    "latitude REAL, " +
                    "longitude REAL)");
        }

        /*
         * Version 4
         * Add branch details
         *
         * Kept here for compatibility with previous database versions.
         */
        if (oldVersion < 4) {

            try {
                db.execSQL("ALTER TABLE branches ADD COLUMN address TEXT");
            } catch (Exception ignored) {
            }

            try {
                db.execSQL("ALTER TABLE branches ADD COLUMN phone TEXT");
            } catch (Exception ignored) {
            }

            try {
                db.execSQL("ALTER TABLE branches ADD COLUMN email TEXT");
            } catch (Exception ignored) {
            }

            try {
                db.execSQL("ALTER TABLE branches ADD COLUMN status TEXT DEFAULT 'Active'");
            } catch (Exception ignored) {
            }
        }

        /*
         * Version 5
         * Add services, technicians and feedback tables
         */
        if (oldVersion < 5) {

            db.execSQL("CREATE TABLE IF NOT EXISTS services (" +
                    "serviceId TEXT PRIMARY KEY, " +
                    "serviceName TEXT NOT NULL, " +
                    "description TEXT, " +
                    "price REAL NOT NULL, " +
                    "duration TEXT, " +
                    "status TEXT DEFAULT 'Active')");

            db.execSQL("CREATE TABLE IF NOT EXISTS technicians (" +
                    "technicianId TEXT PRIMARY KEY, " +
                    "technicianName TEXT NOT NULL, " +
                    "phone TEXT, " +
                    "email TEXT, " +
                    "specialization TEXT)");

            db.execSQL("CREATE TABLE IF NOT EXISTS feedback (" +
                    "feedbackId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "customerName TEXT, " +
                    "rating INTEGER, " +
                    "comment TEXT, " +
                    "date TEXT)");
        }

        /*
         * Version 6
         * Add customers and admins tables
         */
        if (oldVersion < 6) {

            db.execSQL("CREATE TABLE IF NOT EXISTS customers (" +
                    "customerId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "customerName TEXT NOT NULL, " +
                    "email TEXT UNIQUE NOT NULL, " +
                    "phone TEXT, " +
                    "password TEXT NOT NULL)");

            db.execSQL("CREATE TABLE IF NOT EXISTS admins (" +
                    "adminId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "adminName TEXT NOT NULL, " +
                    "email TEXT UNIQUE NOT NULL, " +
                    "phone TEXT, " +
                    "password TEXT NOT NULL)");
        }

        /*
         * Version 7
         * Insert default admin
         */
        if (oldVersion < 7) {
            insertDefaultAdmin(db);
        }

        /*
         * Version 8
         *
         * Technician table changed:
         * - Remove specialization
         * - Add password
         */
        if (oldVersion < 8) {

            db.execSQL("CREATE TABLE technicians_new (" +
                    "technicianId TEXT PRIMARY KEY, " +
                    "technicianName TEXT NOT NULL, " +
                    "phone TEXT, " +
                    "email TEXT NOT NULL, " +
                    "password TEXT NOT NULL)");

            /*
             * Existing technicians receive a temporary password:
             * tech123
             *
             * They can later be given a proper password.
             */
            db.execSQL("INSERT INTO technicians_new " +
                    "(technicianId, technicianName, phone, email, password) " +
                    "SELECT technicianId, technicianName, phone, email, 'tech123' " +
                    "FROM technicians");

            db.execSQL("DROP TABLE technicians");

            db.execSQL("ALTER TABLE technicians_new RENAME TO technicians");
        }
    }

    // -----------------------------
    // PRODUCT METHODS
    // -----------------------------

    public boolean insertProduct(String productName,
                                 String category,
                                 double price,
                                 int quantity) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("productName", productName);
        values.put("category", category);
        values.put("price", price);
        values.put("quantity", quantity);

        long result = db.insert("inventory", null, values);

        return result != -1;
    }

    private void insertProduct(SQLiteDatabase db,
                               String productName,
                               String category,
                               double price,
                               int quantity) {

        ContentValues values = new ContentValues();
        values.put("productName", productName);
        values.put("category", category);
        values.put("price", price);
        values.put("quantity", quantity);

        db.insert("inventory", null, values);
    }

    // -----------------------------
    // BRANCH METHODS
    // -----------------------------

    public boolean insertBranch(String branchCode,
                                String branchName,
                                String address,
                                String phone,
                                String email,
                                String status,
                                double latitude,
                                double longitude) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("branchCode", branchCode);
        values.put("branchName", branchName);
        values.put("address", address);
        values.put("phone", phone);
        values.put("email", email);
        values.put("status", status);
        values.put("latitude", latitude);
        values.put("longitude", longitude);

        long result = db.insert("branches", null, values);

        return result != -1;
    }

    private void insertBranch(SQLiteDatabase db,
                              String branchCode,
                              String branchName,
                              String address,
                              String phone,
                              String email,
                              String status,
                              double latitude,
                              double longitude) {

        ContentValues values = new ContentValues();

        values.put("branchCode", branchCode);
        values.put("branchName", branchName);
        values.put("address", address);
        values.put("phone", phone);
        values.put("email", email);
        values.put("status", status);
        values.put("latitude", latitude);
        values.put("longitude", longitude);

        db.insert("branches", null, values);
    }

    // -----------------------------
    // DEFAULT ADMIN
    // -----------------------------

    private void insertDefaultAdmin(SQLiteDatabase db) {

        Cursor cursor = db.rawQuery(
                "SELECT adminId FROM admins WHERE email = ?",
                new String[]{"admin@techfix.com"}
        );

        if (cursor.moveToFirst()) {
            cursor.close();
            return;
        }

        cursor.close();

        ContentValues values = new ContentValues();

        values.put("adminName", "TechFix Admin");
        values.put("email", "admin@techfix.com");
        values.put("phone", "0771234567");
        values.put("password", "admin123");

        db.insert("admins", null, values);
    }
}