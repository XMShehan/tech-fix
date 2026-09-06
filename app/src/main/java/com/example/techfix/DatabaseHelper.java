package com.example.techfix;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TechFix.db";
    private static final int DATABASE_VERSION = 4;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // =========================
        // Inventory table
        // =========================
        db.execSQL("CREATE TABLE inventory (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "productName TEXT, " +
                "category TEXT, " +
                "price REAL, " +
                "quantity INTEGER)");

        // =========================
        // Services table
        // =========================
        db.execSQL("CREATE TABLE services (" +
                "serviceId TEXT PRIMARY KEY, " +
                "serviceName TEXT NOT NULL, " +
                "description TEXT, " +
                "price REAL NOT NULL, " +
                "duration TEXT, " +
                "status TEXT)");

        // =========================
        // Technicians table
        // =========================
        db.execSQL("CREATE TABLE technicians (" +
                "technicianId TEXT PRIMARY KEY, " +
                "technicianName TEXT NOT NULL, " +
                "phone TEXT, " +
                "email TEXT, " +
                "specialization TEXT)");

        // =========================
        // Customer Feedback table
        // =========================
        db.execSQL("CREATE TABLE feedback (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customerName TEXT NOT NULL, " +
                "customerEmail TEXT NOT NULL, " +
                "feedback TEXT NOT NULL)");
    }

    // =========================
    // Insert Inventory Product
    // =========================
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

        db.insert(
                "inventory",
                null,
                values
        );
    }

    // =========================
    // Database Upgrade
    // =========================
    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        // Version 2
        if (oldVersion < 2) {

            db.execSQL("CREATE TABLE services (" +
                    "serviceId TEXT PRIMARY KEY, " +
                    "serviceName TEXT NOT NULL, " +
                    "description TEXT, " +
                    "price REAL NOT NULL, " +
                    "duration TEXT, " +
                    "status TEXT)");
        }

        // Version 3
        if (oldVersion < 3) {

            db.execSQL("CREATE TABLE technicians (" +
                    "technicianId TEXT PRIMARY KEY, " +
                    "technicianName TEXT NOT NULL, " +
                    "phone TEXT, " +
                    "email TEXT, " +
                    "specialization TEXT)");
        }

        // Version 4
        if (oldVersion < 4) {

            db.execSQL("CREATE TABLE feedback (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "customerName TEXT NOT NULL, " +
                    "customerEmail TEXT NOT NULL, " +
                    "feedback TEXT NOT NULL)");
        }
    }
}