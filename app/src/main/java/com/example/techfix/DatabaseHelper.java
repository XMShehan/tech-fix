package com.example.techfix;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TechFix.db";
    private static final int DATABASE_VERSION = 5;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // =========================
        // Inventory Table
        // =========================
        db.execSQL("CREATE TABLE IF NOT EXISTS inventory (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "productName TEXT, " +
                "category TEXT, " +
                "price REAL, " +
                "quantity INTEGER)");

        // =========================
        // Appointment Table
        // =========================
        db.execSQL("CREATE TABLE IF NOT EXISTS appointments (" +
                "appointmentId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "productService TEXT, " +
                "category TEXT, " +
                "price REAL, " +
                "branch TEXT, " +
                "appointmentDate TEXT, " +
                "appointmentTime TEXT)");

        // =========================
        // Branch Table
        // =========================
        db.execSQL("CREATE TABLE IF NOT EXISTS branches (" +
                "branchId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "branchCode TEXT, " +
                "branchName TEXT, " +
                "address TEXT, " +
                "phone TEXT, " +
                "email TEXT, " +
                "status TEXT DEFAULT 'Active', " +
                "latitude REAL, " +
                "longitude REAL)");

        // =========================
        // Services Table
        // =========================
        db.execSQL("CREATE TABLE IF NOT EXISTS services (" +
                "serviceId TEXT PRIMARY KEY, " +
                "serviceName TEXT NOT NULL, " +
                "description TEXT, " +
                "price REAL NOT NULL, " +
                "duration TEXT, " +
                "status TEXT)");

        // =========================
        // Technicians Table
        // =========================
        db.execSQL("CREATE TABLE IF NOT EXISTS technicians (" +
                "technicianId TEXT PRIMARY KEY, " +
                "technicianName TEXT NOT NULL, " +
                "phone TEXT, " +
                "email TEXT, " +
                "specialization TEXT)");

        // =========================
        // Feedback Table
        // =========================
        db.execSQL("CREATE TABLE IF NOT EXISTS feedback (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customerName TEXT NOT NULL, " +
                "customerEmail TEXT NOT NULL, " +
                "feedback TEXT NOT NULL)");

        // =========================
        // Sample Products
        // =========================
        insertProduct(db, "PC Monitor", "Computer", 45000, 10);
        insertProduct(db, "Keyboard", "Computer", 5000, 25);
        insertProduct(db, "iPhone OLED Display", "Mobile", 85000, 3);
        insertProduct(db, "Laptop Battery", "Computer", 30000, 5);
        insertProduct(db, "iPhone Battery", "Mobile", 18000, 8);
        insertProduct(db, "Charging Port", "Mobile", 7500, 12);

        // =========================
        // Sample Branches
        // =========================
        insertBranch(
                db,
                "B001",
                "TechFix - Colombo",
                "Colombo",
                "",
                "",
                "Active",
                6.927079,
                79.861244
        );

        insertBranch(
                db,
                "B002",
                "TechFix - Gampaha",
                "Gampaha",
                "",
                "",
                "Active",
                7.0840,
                80.0098
        );

        insertBranch(
                db,
                "B003",
                "TechFix - Kandy",
                "Kandy",
                "",
                "",
                "Active",
                7.2906,
                80.6337
        );

        insertBranch(
                db,
                "B004",
                "TechFix - Negombo",
                "Negombo",
                "",
                "",
                "Active",
                7.2083,
                79.8358
        );
    }

    // =====================================================
    // INSERT PRODUCT
    // =====================================================

    private void insertProduct(
            SQLiteDatabase db,
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

    // =====================================================
    // INSERT BRANCH
    // =====================================================

    public void insertBranch(
            SQLiteDatabase db,
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

    // =====================================================
    // CONVENIENCE INSERT BRANCH
    // =====================================================

    public void insertBranch(
            String branchCode,
            String branchName,
            String address,
            String phone,
            String email,
            String status) {

        SQLiteDatabase db = getWritableDatabase();

        insertBranch(
                db,
                branchCode,
                branchName,
                address,
                phone,
                email,
                status,
                0.0,
                0.0
        );
    }

    // =====================================================
    // DATABASE UPGRADE
    // =====================================================

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        // =========================
        // Version 2
        // Appointments
        // =========================
        if (oldVersion < 2) {

            db.execSQL("CREATE TABLE IF NOT EXISTS appointments (" +
                    "appointmentId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "productService TEXT, " +
                    "category TEXT, " +
                    "price REAL, " +
                    "branch TEXT, " +
                    "appointmentDate TEXT, " +
                    "appointmentTime TEXT)");
        }

        // =========================
        // Version 3
        // Branches
        // =========================
        if (oldVersion < 3) {

            db.execSQL("CREATE TABLE IF NOT EXISTS branches (" +
                    "branchId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "branchCode TEXT, " +
                    "branchName TEXT, " +
                    "address TEXT, " +
                    "phone TEXT, " +
                    "email TEXT, " +
                    "status TEXT DEFAULT 'Active', " +
                    "latitude REAL, " +
                    "longitude REAL)");

            insertBranch(
                    db,
                    "B001",
                    "TechFix - Colombo",
                    "Colombo",
                    "",
                    "",
                    "Active",
                    6.927079,
                    79.861244
            );

            insertBranch(
                    db,
                    "B002",
                    "TechFix - Gampaha",
                    "Gampaha",
                    "",
                    "",
                    "Active",
                    7.0840,
                    80.0098
            );

            insertBranch(
                    db,
                    "B003",
                    "TechFix - Kandy",
                    "Kandy",
                    "",
                    "",
                    "Active",
                    7.2906,
                    80.6337
            );

            insertBranch(
                    db,
                    "B004",
                    "TechFix - Negombo",
                    "Negombo",
                    "",
                    "",
                    "Active",
                    7.2083,
                    79.8358
            );
        }

        // =========================
        // Version 4
        // Branch Columns
        // =========================
        if (oldVersion < 4) {

            try {
                db.execSQL(
                        "ALTER TABLE branches ADD COLUMN branchCode TEXT"
                );
            } catch (Exception ignored) {
            }

            try {
                db.execSQL(
                        "ALTER TABLE branches ADD COLUMN address TEXT"
                );
            } catch (Exception ignored) {
            }

            try {
                db.execSQL(
                        "ALTER TABLE branches ADD COLUMN phone TEXT"
                );
            } catch (Exception ignored) {
            }

            try {
                db.execSQL(
                        "ALTER TABLE branches ADD COLUMN email TEXT"
                );
            } catch (Exception ignored) {
            }

            try {
                db.execSQL(
                        "ALTER TABLE branches ADD COLUMN status TEXT DEFAULT 'Active'"
                );
            } catch (Exception ignored) {
            }

            try {
                db.execSQL(
                        "ALTER TABLE branches ADD COLUMN latitude REAL"
                );
            } catch (Exception ignored) {
            }

            try {
                db.execSQL(
                        "ALTER TABLE branches ADD COLUMN longitude REAL"
                );
            } catch (Exception ignored) {
            }
        }

        // =========================
        // Version 5
        // Services, Technicians
        // and Feedback
        // =========================
        if (oldVersion < 5) {

            db.execSQL("CREATE TABLE IF NOT EXISTS services (" +
                    "serviceId TEXT PRIMARY KEY, " +
                    "serviceName TEXT NOT NULL, " +
                    "description TEXT, " +
                    "price REAL NOT NULL, " +
                    "duration TEXT, " +
                    "status TEXT)");

            db.execSQL("CREATE TABLE IF NOT EXISTS technicians (" +
                    "technicianId TEXT PRIMARY KEY, " +
                    "technicianName TEXT NOT NULL, " +
                    "phone TEXT, " +
                    "email TEXT, " +
                    "specialization TEXT)");

            db.execSQL("CREATE TABLE IF NOT EXISTS feedback (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "customerName TEXT NOT NULL, " +
                    "customerEmail TEXT NOT NULL, " +
                    "feedback TEXT NOT NULL)");
        }
    }
}