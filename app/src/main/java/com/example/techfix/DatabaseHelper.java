package com.example.techfix;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TechFix.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Inventory table
        db.execSQL("CREATE TABLE inventory (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "productName TEXT, " +
                "category TEXT, " +
                "price REAL, " +
                "quantity INTEGER)");

        // Appointment table
        db.execSQL("CREATE TABLE appointments (" +
                "appointmentId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "productService TEXT, " +
                "category TEXT, " +
                "price REAL, " +
                "branch TEXT, " +
                "appointmentDate TEXT, " +
                "appointmentTime TEXT)");

        // Sample products
        insertProduct(db, "PC Monitor", "Computer", 45000, 10);
        insertProduct(db, "Keyboard", "Computer", 5000, 25);
        insertProduct(db, "iPhone OLED Display", "Mobile", 85000, 3);
        insertProduct(db, "Laptop Battery", "Computer", 30000, 5);
        insertProduct(db, "iPhone Battery", "Mobile", 18000, 8);
        insertProduct(db, "Charging Port", "Mobile", 7500, 12);
    }

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

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        if (oldVersion < 2) {

            db.execSQL("CREATE TABLE appointments (" +
                    "appointmentId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "productService TEXT, " +
                    "category TEXT, " +
                    "price REAL, " +
                    "branch TEXT, " +
                    "appointmentDate TEXT, " +
                    "appointmentTime TEXT)");
        }
    }
}