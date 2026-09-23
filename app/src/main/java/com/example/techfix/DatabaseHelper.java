package com.example.techfix;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * TechFix local SQLite database.
 *
 * IMPORTANT:
 * 1. SQLite remains the local/offline database.
 * 2. Every synchronized table has:
 *      - firebaseId   : global Firebase document ID
 *      - lastModified : local change timestamp in milliseconds
 *      - syncPending  : 1 when a local change still needs Firebase sync
 *      - isDeleted    : soft-delete/tombstone state
 * 3. SQLite triggers automatically mark direct local INSERT/UPDATE/DELETE
 *    operations as pending, so existing Activities that use SQLite directly
 *    can still participate in the sync queue.
 * 4. Firebase-originated writes must be wrapped with:
 *      beginRemoteSync();
 *      ... write to SQLite ...
 *      endRemoteSync();
 *    so they do NOT get pushed back to Firebase.
 *
 * IMPORTANT SECURITY NOTE:
 * Passwords remain in SQLite only for compatibility with the existing app.
 * Do NOT upload password fields to Firestore. Use Firebase Authentication
 * for real authentication.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TechFix.db";

    // Version 15:
    // Add Firebase synchronization metadata and local/cloud relationship IDs.
    private static final int DATABASE_VERSION = 15;

    private static final long DEFAULT_TIMESTAMP = 0L;

    /*
     * Tables that are allowed to use the generic Firebase-sync helper methods.
     * This prevents accidental SQL injection through a dynamic table name.
     */
    private static final Set<String> SYNC_TABLES = new HashSet<>(Arrays.asList(
            "inventory",
            "appointments",
            "branches",
            "services",
            "technicians",
            "feedback",
            "customers",
            "admins",
            "jobs",
            "payments"
    ));

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // =========================================================
    // CREATE DATABASE
    // =========================================================

    @Override
    public void onCreate(SQLiteDatabase db) {

        // =====================================================
        // INVENTORY
        // =====================================================

        db.execSQL("CREATE TABLE inventory (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "productName TEXT NOT NULL, " +
                "category TEXT NOT NULL, " +
                "price REAL NOT NULL, " +
                "quantity INTEGER NOT NULL, " +
                "firebaseId TEXT, " +
                "lastModified INTEGER NOT NULL DEFAULT 0, " +
                "syncPending INTEGER NOT NULL DEFAULT 1, " +
                "isDeleted INTEGER NOT NULL DEFAULT 0)");

        // =====================================================
        // APPOINTMENTS
        // =====================================================

        db.execSQL("CREATE TABLE appointments (" +
                "appointmentId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customerId INTEGER NOT NULL DEFAULT 0, " +
                "customerFirebaseId TEXT, " +
                "productService TEXT NOT NULL, " +
                "category TEXT NOT NULL, " +
                "price REAL NOT NULL, " +
                "finalPrice REAL NOT NULL DEFAULT 0, " +
                "branch TEXT, " +
                "appointmentDate TEXT, " +
                "appointmentTime TEXT, " +
                "deviceModel TEXT, " +
                "problemDescription TEXT, " +
                "photoPath TEXT, " +
                "firebaseId TEXT, " +
                "lastModified INTEGER NOT NULL DEFAULT 0, " +
                "syncPending INTEGER NOT NULL DEFAULT 1, " +
                "isDeleted INTEGER NOT NULL DEFAULT 0)");

        // =====================================================
        // BRANCHES
        // =====================================================

        db.execSQL("CREATE TABLE branches (" +
                "branchId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "branchCode TEXT NOT NULL, " +
                "branchName TEXT NOT NULL, " +
                "address TEXT, " +
                "phone TEXT, " +
                "email TEXT, " +
                "status TEXT DEFAULT 'Active', " +
                "latitude REAL, " +
                "longitude REAL, " +
                "firebaseId TEXT, " +
                "lastModified INTEGER NOT NULL DEFAULT 0, " +
                "syncPending INTEGER NOT NULL DEFAULT 1, " +
                "isDeleted INTEGER NOT NULL DEFAULT 0)");

        // =====================================================
        // SERVICES
        // =====================================================

        db.execSQL("CREATE TABLE services (" +
                "serviceId TEXT PRIMARY KEY, " +
                "serviceName TEXT NOT NULL, " +
                "description TEXT, " +
                "price REAL NOT NULL, " +
                "duration TEXT, " +
                "status TEXT DEFAULT 'Active', " +
                "category TEXT NOT NULL DEFAULT 'Other', " +
                "firebaseId TEXT, " +
                "lastModified INTEGER NOT NULL DEFAULT 0, " +
                "syncPending INTEGER NOT NULL DEFAULT 1, " +
                "isDeleted INTEGER NOT NULL DEFAULT 0)");

        // =====================================================
        // TECHNICIANS
        // =====================================================

        db.execSQL("CREATE TABLE technicians (" +
                "technicianId TEXT PRIMARY KEY, " +
                "technicianName TEXT NOT NULL, " +
                "phone TEXT, " +
                "email TEXT NOT NULL, " +
                "password TEXT NOT NULL, " +
                "branch TEXT DEFAULT 'Unassigned', " +
                "firebaseId TEXT, " +
                "lastModified INTEGER NOT NULL DEFAULT 0, " +
                "syncPending INTEGER NOT NULL DEFAULT 1, " +
                "isDeleted INTEGER NOT NULL DEFAULT 0)");

        // =====================================================
        // FEEDBACK
        // =====================================================

        db.execSQL("CREATE TABLE feedback (" +
                "feedbackId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customerId INTEGER NOT NULL DEFAULT 0, " +
                "customerFirebaseId TEXT, " +
                "jobId INTEGER NOT NULL DEFAULT 0, " +
                "jobFirebaseId TEXT, " +
                "customerName TEXT NOT NULL, " +
                "rating INTEGER NOT NULL, " +
                "comment TEXT NOT NULL, " +
                "date TEXT NOT NULL, " +
                "firebaseId TEXT, " +
                "lastModified INTEGER NOT NULL DEFAULT 0, " +
                "syncPending INTEGER NOT NULL DEFAULT 1, " +
                "isDeleted INTEGER NOT NULL DEFAULT 0)");

        // =====================================================
        // CUSTOMERS
        // =====================================================

        db.execSQL("CREATE TABLE customers (" +
                "customerId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customerName TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "phone TEXT, " +
                "password TEXT NOT NULL, " +
                "firebaseId TEXT, " +
                "lastModified INTEGER NOT NULL DEFAULT 0, " +
                "syncPending INTEGER NOT NULL DEFAULT 1, " +
                "isDeleted INTEGER NOT NULL DEFAULT 0)");

        // =====================================================
        // ADMINS
        // =====================================================

        db.execSQL("CREATE TABLE admins (" +
                "adminId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "adminName TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "phone TEXT, " +
                "password TEXT NOT NULL, " +
                "firebaseId TEXT, " +
                "lastModified INTEGER NOT NULL DEFAULT 0, " +
                "syncPending INTEGER NOT NULL DEFAULT 1, " +
                "isDeleted INTEGER NOT NULL DEFAULT 0)");

        // =====================================================
        // JOBS
        // =====================================================

        /*
         * updatedAt already existed in the original schema as TEXT.
         * Therefore this sync layer uses a separate lastModified INTEGER
         * rather than changing the original updatedAt column.
         */
        db.execSQL("CREATE TABLE jobs (" +
                "jobId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "appointmentId INTEGER NOT NULL, " +
                "appointmentFirebaseId TEXT, " +
                "technicianId TEXT, " +
                "status TEXT NOT NULL DEFAULT 'PENDING', " +
                "photoPath TEXT, " +
                "updatedAt TEXT, " +
                "firebaseId TEXT, " +
                "lastModified INTEGER NOT NULL DEFAULT 0, " +
                "syncPending INTEGER NOT NULL DEFAULT 1, " +
                "isDeleted INTEGER NOT NULL DEFAULT 0)");

        // =====================================================
        // PAYMENTS
        // =====================================================

        db.execSQL("CREATE TABLE payments (" +
                "paymentId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "appointmentId INTEGER NOT NULL, " +
                "appointmentFirebaseId TEXT, " +
                "customerId INTEGER NOT NULL, " +
                "customerFirebaseId TEXT, " +
                "amount REAL NOT NULL, " +
                "paymentMethod TEXT NOT NULL, " +
                "paymentStatus TEXT NOT NULL DEFAULT 'PENDING', " +
                "paymentDate TEXT NOT NULL, " +
                "firebaseId TEXT, " +
                "lastModified INTEGER NOT NULL DEFAULT 0, " +
                "syncPending INTEGER NOT NULL DEFAULT 1, " +
                "isDeleted INTEGER NOT NULL DEFAULT 0)");

        // =====================================================
        // SYNC METADATA
        // =====================================================

        createSyncInfrastructure(db);

        // =====================================================
        // DEFAULT ADMIN
        // =====================================================

        insertDefaultAdmin(db);

        // =====================================================
        // SAMPLE INVENTORY
        // =====================================================

        insertProduct(
                db,
                "PC Monitor",
                "Computer",
                45000,
                10
        );

        insertProduct(
                db,
                "Keyboard",
                "Computer",
                5000,
                25
        );

        insertProduct(
                db,
                "iPhone OLED Display",
                "Mobile",
                85000,
                3
        );

        insertProduct(
                db,
                "Laptop Battery",
                "Computer",
                30000,
                5
        );

        insertProduct(
                db,
                "iPhone Battery",
                "Mobile",
                18000,
                8
        );

        insertProduct(
                db,
                "Charging Port",
                "Mobile",
                7500,
                12
        );

        // =====================================================
        // SAMPLE BRANCHES
        // =====================================================

        insertBranch(
                db,
                "B001",
                "Colombo",
                "Colombo 06",
                "0112345678",
                "colombo@techfix.com",
                "Active",
                6.927079,
                79.861244
        );

        insertBranch(
                db,
                "B002",
                "Gampaha",
                "Gampaha",
                "0332345678",
                "gampaha@techfix.com",
                "Active",
                7.0840,
                80.0098
        );

        insertBranch(
                db,
                "B003",
                "Kandy",
                "Kandy",
                "0812345678",
                "kandy@techfix.com",
                "Active",
                7.2906,
                80.6337
        );

        insertBranch(
                db,
                "B004",
                "Negombo",
                "Negombo",
                "0312345678",
                "negombo@techfix.com",
                "Active",
                7.2083,
                79.8358
        );
    }

    // =========================================================
    // DATABASE UPGRADE
    // =========================================================

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        // =====================================================
        // VERSION 2
        // Add appointments
        // =====================================================

        if (oldVersion < 2) {

            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS appointments (" +
                            "appointmentId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "productService TEXT NOT NULL, " +
                            "category TEXT NOT NULL, " +
                            "price REAL NOT NULL, " +
                            "branch TEXT, " +
                            "appointmentDate TEXT, " +
                            "appointmentTime TEXT)"
            );
        }

        // =====================================================
        // VERSION 3
        // Add branches
        // =====================================================

        if (oldVersion < 3) {

            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS branches (" +
                            "branchId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "branchCode TEXT NOT NULL, " +
                            "branchName TEXT NOT NULL, " +
                            "address TEXT, " +
                            "phone TEXT, " +
                            "email TEXT, " +
                            "status TEXT DEFAULT 'Active', " +
                            "latitude REAL, " +
                            "longitude REAL)"
            );
        }

        // =====================================================
        // VERSION 4
        // Add branch details
        // =====================================================

        if (oldVersion < 4) {

            addColumnSafely(
                    db,
                    "branches",
                    "address TEXT"
            );

            addColumnSafely(
                    db,
                    "branches",
                    "phone TEXT"
            );

            addColumnSafely(
                    db,
                    "branches",
                    "email TEXT"
            );

            addColumnSafely(
                    db,
                    "branches",
                    "status TEXT DEFAULT 'Active'"
            );
        }

        // =====================================================
        // VERSION 5
        // Add services, technicians and feedback
        // =====================================================

        if (oldVersion < 5) {

            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS services (" +
                            "serviceId TEXT PRIMARY KEY, " +
                            "serviceName TEXT NOT NULL, " +
                            "description TEXT, " +
                            "price REAL NOT NULL, " +
                            "duration TEXT, " +
                            "status TEXT DEFAULT 'Active')"
            );

            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS technicians (" +
                            "technicianId TEXT PRIMARY KEY, " +
                            "technicianName TEXT NOT NULL, " +
                            "phone TEXT, " +
                            "email TEXT, " +
                            "specialization TEXT)"
            );

            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS feedback (" +
                            "feedbackId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "customerName TEXT, " +
                            "rating INTEGER, " +
                            "comment TEXT, " +
                            "date TEXT)"
            );
        }

        // =====================================================
        // VERSION 6
        // Add customers and admins
        // =====================================================

        if (oldVersion < 6) {

            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS customers (" +
                            "customerId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "customerName TEXT NOT NULL, " +
                            "email TEXT UNIQUE NOT NULL, " +
                            "phone TEXT, " +
                            "password TEXT NOT NULL)"
            );

            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS admins (" +
                            "adminId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "adminName TEXT NOT NULL, " +
                            "email TEXT UNIQUE NOT NULL, " +
                            "phone TEXT, " +
                            "password TEXT NOT NULL)"
            );
        }

        // =====================================================
        // VERSION 7
        // Insert default admin
        // =====================================================

        if (oldVersion < 7) {

            insertDefaultAdmin(db);
        }

        // =====================================================
        // VERSION 8
        // Change technician table
        // Remove specialization
        // Add password
        // =====================================================

        if (oldVersion < 8) {

            db.execSQL(
                    "CREATE TABLE technicians_new (" +
                            "technicianId TEXT PRIMARY KEY, " +
                            "technicianName TEXT NOT NULL, " +
                            "phone TEXT, " +
                            "email TEXT NOT NULL, " +
                            "password TEXT NOT NULL)"
            );

            db.execSQL(
                    "INSERT INTO technicians_new " +
                            "(technicianId, technicianName, phone, email, password) " +
                            "SELECT technicianId, technicianName, phone, email, 'tech123' " +
                            "FROM technicians"
            );

            db.execSQL("DROP TABLE technicians");

            db.execSQL(
                    "ALTER TABLE technicians_new RENAME TO technicians"
            );
        }

        // =====================================================
        // VERSION 9
        // Add customerId to appointments
        // Create jobs table
        // =====================================================

        if (oldVersion < 9) {

            addColumnSafely(
                    db,
                    "appointments",
                    "customerId INTEGER NOT NULL DEFAULT 0"
            );

            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS jobs (" +
                            "jobId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "appointmentId INTEGER NOT NULL, " +
                            "technicianId TEXT, " +
                            "status TEXT NOT NULL DEFAULT 'PENDING', " +
                            "photoPath TEXT, " +
                            "updatedAt TEXT)"
            );
        }

        // =====================================================
        // VERSION 10
        // Add customerId to feedback
        // =====================================================

        if (oldVersion < 10) {

            addColumnSafely(
                    db,
                    "feedback",
                    "customerId INTEGER NOT NULL DEFAULT 0"
            );
        }

        // =====================================================
        // VERSION 11
        // Add payments table
        // =====================================================

        if (oldVersion < 11) {

            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS payments (" +
                            "paymentId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "appointmentId INTEGER NOT NULL, " +
                            "customerId INTEGER NOT NULL, " +
                            "amount REAL NOT NULL, " +
                            "paymentMethod TEXT NOT NULL, " +
                            "paymentStatus TEXT NOT NULL DEFAULT 'PENDING', " +
                            "paymentDate TEXT NOT NULL)"
            );
        }

        // =====================================================
        // VERSION 12
        // Add jobId to feedback
        // =====================================================

        if (oldVersion < 12) {

            addColumnSafely(
                    db,
                    "feedback",
                    "jobId INTEGER NOT NULL DEFAULT 0"
            );
        }

        // =====================================================
        // VERSION 13
        // CUSTOMER BOOKING REDESIGN
        // =====================================================

        if (oldVersion < 13) {

            addColumnSafely(
                    db,
                    "services",
                    "category TEXT NOT NULL DEFAULT 'Other'"
            );

            addColumnSafely(
                    db,
                    "technicians",
                    "branch TEXT DEFAULT 'Unassigned'"
            );

            addColumnSafely(
                    db,
                    "appointments",
                    "deviceModel TEXT"
            );

            addColumnSafely(
                    db,
                    "appointments",
                    "problemDescription TEXT"
            );

            addColumnSafely(
                    db,
                    "appointments",
                    "photoPath TEXT"
            );

            try {
                db.execSQL(
                        "CREATE INDEX IF NOT EXISTS idx_appointments_slot " +
                                "ON appointments(branch, appointmentDate, appointmentTime)"
                );
            } catch (Exception ignored) {
            }
        }

        // =====================================================
        // VERSION 14
        // FINAL REPAIR AMOUNT
        // =====================================================

        if (oldVersion < 14) {

            addColumnSafely(
                    db,
                    "appointments",
                    "finalPrice REAL NOT NULL DEFAULT 0"
            );

            try {
                db.execSQL(
                        "UPDATE appointments " +
                                "SET finalPrice = (" +
                                "SELECT p.amount " +
                                "FROM payments p " +
                                "WHERE p.appointmentId = appointments.appointmentId " +
                                "AND p.customerId = appointments.customerId " +
                                "ORDER BY p.paymentId DESC " +
                                "LIMIT 1" +
                                ") " +
                                "WHERE EXISTS (" +
                                "SELECT 1 " +
                                "FROM payments p2 " +
                                "WHERE p2.appointmentId = appointments.appointmentId " +
                                "AND p2.customerId = appointments.customerId" +
                                ")"
                );
            } catch (Exception ignored) {
            }
        }

        // =====================================================
        // VERSION 15
        // FIREBASE SYNC SUPPORT
        // =====================================================

        if (oldVersion < 15) {

            /*
             * Add global Firebase document IDs and synchronization metadata.
             */
            addColumnSafely(
                    db,
                    "inventory",
                    "firebaseId TEXT"
            );

            addColumnSafely(
                    db,
                    "inventory",
                    "lastModified INTEGER NOT NULL DEFAULT 0"
            );

            addColumnSafely(
                    db,
                    "inventory",
                    "syncPending INTEGER NOT NULL DEFAULT 1"
            );

            addColumnSafely(
                    db,
                    "inventory",
                    "isDeleted INTEGER NOT NULL DEFAULT 0"
            );


            addColumnSafely(
                    db,
                    "appointments",
                    "customerFirebaseId TEXT"
            );

            addColumnSafely(
                    db,
                    "appointments",
                    "firebaseId TEXT"
            );

            addColumnSafely(
                    db,
                    "appointments",
                    "lastModified INTEGER NOT NULL DEFAULT 0"
            );

            addColumnSafely(
                    db,
                    "appointments",
                    "syncPending INTEGER NOT NULL DEFAULT 1"
            );

            addColumnSafely(
                    db,
                    "appointments",
                    "isDeleted INTEGER NOT NULL DEFAULT 0"
            );


            addColumnSafely(
                    db,
                    "branches",
                    "firebaseId TEXT"
            );

            addColumnSafely(
                    db,
                    "branches",
                    "lastModified INTEGER NOT NULL DEFAULT 0"
            );

            addColumnSafely(
                    db,
                    "branches",
                    "syncPending INTEGER NOT NULL DEFAULT 1"
            );

            addColumnSafely(
                    db,
                    "branches",
                    "isDeleted INTEGER NOT NULL DEFAULT 0"
            );


            addColumnSafely(
                    db,
                    "services",
                    "firebaseId TEXT"
            );

            addColumnSafely(
                    db,
                    "services",
                    "lastModified INTEGER NOT NULL DEFAULT 0"
            );

            addColumnSafely(
                    db,
                    "services",
                    "syncPending INTEGER NOT NULL DEFAULT 1"
            );

            addColumnSafely(
                    db,
                    "services",
                    "isDeleted INTEGER NOT NULL DEFAULT 0"
            );


            addColumnSafely(
                    db,
                    "technicians",
                    "firebaseId TEXT"
            );

            addColumnSafely(
                    db,
                    "technicians",
                    "lastModified INTEGER NOT NULL DEFAULT 0"
            );

            addColumnSafely(
                    db,
                    "technicians",
                    "syncPending INTEGER NOT NULL DEFAULT 1"
            );

            addColumnSafely(
                    db,
                    "technicians",
                    "isDeleted INTEGER NOT NULL DEFAULT 0"
            );


            addColumnSafely(
                    db,
                    "feedback",
                    "customerFirebaseId TEXT"
            );

            addColumnSafely(
                    db,
                    "feedback",
                    "jobFirebaseId TEXT"
            );

            addColumnSafely(
                    db,
                    "feedback",
                    "firebaseId TEXT"
            );

            addColumnSafely(
                    db,
                    "feedback",
                    "lastModified INTEGER NOT NULL DEFAULT 0"
            );

            addColumnSafely(
                    db,
                    "feedback",
                    "syncPending INTEGER NOT NULL DEFAULT 1"
            );

            addColumnSafely(
                    db,
                    "feedback",
                    "isDeleted INTEGER NOT NULL DEFAULT 0"
            );


            addColumnSafely(
                    db,
                    "customers",
                    "firebaseId TEXT"
            );

            addColumnSafely(
                    db,
                    "customers",
                    "lastModified INTEGER NOT NULL DEFAULT 0"
            );

            addColumnSafely(
                    db,
                    "customers",
                    "syncPending INTEGER NOT NULL DEFAULT 1"
            );

            addColumnSafely(
                    db,
                    "customers",
                    "isDeleted INTEGER NOT NULL DEFAULT 0"
            );


            addColumnSafely(
                    db,
                    "admins",
                    "firebaseId TEXT"
            );

            addColumnSafely(
                    db,
                    "admins",
                    "lastModified INTEGER NOT NULL DEFAULT 0"
            );

            addColumnSafely(
                    db,
                    "admins",
                    "syncPending INTEGER NOT NULL DEFAULT 1"
            );

            addColumnSafely(
                    db,
                    "admins",
                    "isDeleted INTEGER NOT NULL DEFAULT 0"
            );


            addColumnSafely(
                    db,
                    "jobs",
                    "appointmentFirebaseId TEXT"
            );

            addColumnSafely(
                    db,
                    "jobs",
                    "firebaseId TEXT"
            );

            addColumnSafely(
                    db,
                    "jobs",
                    "lastModified INTEGER NOT NULL DEFAULT 0"
            );

            addColumnSafely(
                    db,
                    "jobs",
                    "syncPending INTEGER NOT NULL DEFAULT 1"
            );

            addColumnSafely(
                    db,
                    "jobs",
                    "isDeleted INTEGER NOT NULL DEFAULT 0"
            );


            addColumnSafely(
                    db,
                    "payments",
                    "appointmentFirebaseId TEXT"
            );

            addColumnSafely(
                    db,
                    "payments",
                    "customerFirebaseId TEXT"
            );

            addColumnSafely(
                    db,
                    "payments",
                    "firebaseId TEXT"
            );

            addColumnSafely(
                    db,
                    "payments",
                    "lastModified INTEGER NOT NULL DEFAULT 0"
            );

            addColumnSafely(
                    db,
                    "payments",
                    "syncPending INTEGER NOT NULL DEFAULT 1"
            );

            addColumnSafely(
                    db,
                    "payments",
                    "isDeleted INTEGER NOT NULL DEFAULT 0"
            );


            /*
             * Back-fill cloud IDs for existing rows.
             * Existing data becomes syncPending=1 so the first Firebase
             * synchronization can upload it.
             */
            backfillFirebaseIds(db);

            // Rebuild indexes and triggers after the old-schema migration.
            createSyncInfrastructure(db);
        }
    }

    // =========================================================
    // SAFE MIGRATION HELPERS
    // =========================================================

    private void addColumnSafely(
            SQLiteDatabase db,
            String table,
            String columnDefinition) {

        try {
            db.execSQL(
                    "ALTER TABLE " +
                            table +
                            " ADD COLUMN " +
                            columnDefinition
            );
        } catch (Exception ignored) {
            // Column already exists or table is not applicable.
        }
    }

    // =========================================================
    // FIREBASE SYNC INFRASTRUCTURE
    // =========================================================

    private void createSyncInfrastructure(
            SQLiteDatabase db) {

        /*
         * Controls whether a SQLite operation came from Firebase.
         *
         * remoteSync = 0 -> Local application write
         * remoteSync = 1 -> Firebase-originated write
         */
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS sync_meta (" +
                        "id INTEGER PRIMARY KEY CHECK (id = 1), " +
                        "remoteSync INTEGER NOT NULL DEFAULT 0)"
        );

        db.execSQL(
                "INSERT OR IGNORE INTO sync_meta(id, remoteSync) " +
                        "VALUES(1, 0)"
        );

        /*
         * Stores delete operations separately because an actual DELETE
         * removes the original row and therefore removes its firebaseId.
         */
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS sync_tombstones (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "tableName TEXT NOT NULL, " +
                        "firebaseId TEXT NOT NULL, " +
                        "deletedAt INTEGER NOT NULL, " +
                        "synced INTEGER NOT NULL DEFAULT 0)"
        );

        createSyncIndexes(db);

        /*
         * Triggers are created per table.
         * They allow existing code that directly calls db.insert(),
         * db.update(), or db.delete() to participate in synchronization.
         */
        createSyncTriggers(
                db,
                "inventory",
                "id"
        );

        createSyncTriggers(
                db,
                "appointments",
                "appointmentId"
        );

        createSyncTriggers(
                db,
                "branches",
                "branchId"
        );

        createSyncTriggers(
                db,
                "services",
                "serviceId"
        );

        createSyncTriggers(
                db,
                "technicians",
                "technicianId"
        );

        createSyncTriggers(
                db,
                "feedback",
                "feedbackId"
        );

        createSyncTriggers(
                db,
                "customers",
                "customerId"
        );

        createSyncTriggers(
                db,
                "admins",
                "adminId"
        );

        createSyncTriggers(
                db,
                "jobs",
                "jobId"
        );

        createSyncTriggers(
                db,
                "payments",
                "paymentId"
        );
    }

    private void createSyncIndexes(
            SQLiteDatabase db) {

        String[] tables = {
                "inventory",
                "appointments",
                "branches",
                "services",
                "technicians",
                "feedback",
                "customers",
                "admins",
                "jobs",
                "payments"
        };

        for (String table : tables) {

            try {
                db.execSQL(
                        "CREATE UNIQUE INDEX IF NOT EXISTS idx_" +
                                table +
                                "_firebaseId " +
                                "ON " +
                                table +
                                "(firebaseId)"
                );
            } catch (Exception ignored) {
            }
        }

        try {
            db.execSQL(
                    "CREATE INDEX IF NOT EXISTS idx_sync_tombstones_pending " +
                            "ON sync_tombstones(synced, tableName)"
            );
        } catch (Exception ignored) {
        }
    }

    private void createSyncTriggers(
            SQLiteDatabase db,
            String table,
            String primaryKeyColumn) {

        /*
         * INSERT trigger:
         * If a local INSERT does not provide a firebaseId,
         * generate one automatically. It then marks the row as pending.
         */
        String insertTrigger =
                "CREATE TRIGGER IF NOT EXISTS trg_" +
                        table +
                        "_sync_insert " +
                        "AFTER INSERT ON " +
                        table + " " +
                        "WHEN (SELECT remoteSync FROM sync_meta WHERE id=1)=0 " +
                        "BEGIN " +
                        "UPDATE " +
                        table +
                        " SET " +
                        "firebaseId = CASE " +
                        "WHEN NEW.firebaseId IS NULL OR NEW.firebaseId = '' " +
                        "THEN lower(hex(randomblob(16))) " +
                        "ELSE NEW.firebaseId END, " +
                        "lastModified = CASE " +
                        "WHEN NEW.lastModified IS NULL OR NEW.lastModified = 0 " +
                        "THEN CAST(strftime('%s','now') AS INTEGER) * 1000 " +
                        "ELSE NEW.lastModified END, " +
                        "syncPending = 1, " +
                        "isDeleted = 0 " +
                        "WHERE " +
                        primaryKeyColumn +
                        " = NEW." +
                        primaryKeyColumn +
                        "; " +
                        "END";

        /*
         * UPDATE trigger:
         * Local updates are automatically marked for Firebase.
         * The trigger only fires when the update did not already change
         * the synchronization fields, preventing an endless trigger loop.
         */
        String updateTrigger =
                "CREATE TRIGGER IF NOT EXISTS trg_" +
                        table +
                        "_sync_update " +
                        "AFTER UPDATE ON " +
                        table + " " +
                        "WHEN (SELECT remoteSync FROM sync_meta WHERE id=1)=0 " +
                        "AND NEW.lastModified = OLD.lastModified " +
                        "AND NEW.syncPending = OLD.syncPending " +
                        "BEGIN " +
                        "UPDATE " +
                        table +
                        " SET " +
                        "lastModified = CAST(strftime('%s','now') AS INTEGER) * 1000, " +
                        "syncPending = 1 " +
                        "WHERE " +
                        primaryKeyColumn +
                        " = NEW." +
                        primaryKeyColumn +
                        "; " +
                        "END";

        /*
         * DELETE trigger:
         * Save a tombstone so FirebaseSyncManager can delete the matching
         * Firestore document even though the SQLite row has disappeared.
         */
        String deleteTrigger =
                "CREATE TRIGGER IF NOT EXISTS trg_" +
                        table +
                        "_sync_delete " +
                        "AFTER DELETE ON " +
                        table + " " +
                        "WHEN (SELECT remoteSync FROM sync_meta WHERE id=1)=0 " +
                        "AND OLD.firebaseId IS NOT NULL " +
                        "AND OLD.firebaseId <> '' " +
                        "BEGIN " +
                        "INSERT INTO sync_tombstones(" +
                        "tableName, firebaseId, deletedAt, synced" +
                        ") VALUES(" +
                        "'" + table + "', " +
                        "OLD.firebaseId, " +
                        "CAST(strftime('%s','now') AS INTEGER) * 1000, " +
                        "0); " +
                        "END";

        try {
            db.execSQL(insertTrigger);
        } catch (Exception ignored) {
        }

        try {
            db.execSQL(updateTrigger);
        } catch (Exception ignored) {
        }

        try {
            db.execSQL(deleteTrigger);
        } catch (Exception ignored) {
        }
    }

    // =========================================================
    // BACK-FILL EXISTING DATA FOR FIREBASE
    // =========================================================

    private void backfillFirebaseIds(
            SQLiteDatabase db) {

        long now =
                System.currentTimeMillis();

        // -----------------------------------------------------
        // AUTO-INCREMENT TABLES
        // -----------------------------------------------------

        fillGeneratedFirebaseIds(
                db,
                "inventory"
        );

        fillGeneratedFirebaseIds(
                db,
                "appointments"
        );

        fillGeneratedFirebaseIds(
                db,
                "branches"
        );

        fillGeneratedFirebaseIds(
                db,
                "feedback"
        );

        fillGeneratedFirebaseIds(
                db,
                "customers"
        );

        fillGeneratedFirebaseIds(
                db,
                "admins"
        );

        fillGeneratedFirebaseIds(
                db,
                "jobs"
        );

        fillGeneratedFirebaseIds(
                db,
                "payments"
        );

        // -----------------------------------------------------
        // TEXT-ID TABLES
        // Use existing logical IDs as Firebase document IDs.
        // -----------------------------------------------------

        try {
            db.execSQL(
                    "UPDATE services " +
                            "SET firebaseId = serviceId " +
                            "WHERE firebaseId IS NULL OR firebaseId = ''"
            );
        } catch (Exception ignored) {
        }

        try {
            db.execSQL(
                    "UPDATE technicians " +
                            "SET firebaseId = technicianId " +
                            "WHERE firebaseId IS NULL OR firebaseId = ''"
            );
        } catch (Exception ignored) {
        }

        // -----------------------------------------------------
        // Mark all existing records for initial cloud upload.
        // -----------------------------------------------------

        String[] tables = {
                "inventory",
                "appointments",
                "branches",
                "services",
                "technicians",
                "feedback",
                "customers",
                "admins",
                "jobs",
                "payments"
        };

        for (String table : tables) {

            try {
                db.execSQL(
                        "UPDATE " +
                                table +
                                " SET lastModified = ? " +
                                "WHERE lastModified = 0 OR lastModified IS NULL",
                        new Object[]{now}
                );
            } catch (Exception ignored) {
            }

            try {
                db.execSQL(
                        "UPDATE " +
                                table +
                                " SET syncPending = 1"
                );
            } catch (Exception ignored) {
            }
        }

        /*
         * Back-fill centralized relationship IDs.
         * These let Firebase relationships remain stable across devices
         * even though local integer IDs can differ from device to device.
         */
        try {
            db.execSQL(
                    "UPDATE appointments " +
                            "SET customerFirebaseId = (" +
                            "SELECT c.firebaseId " +
                            "FROM customers c " +
                            "WHERE c.customerId = appointments.customerId" +
                            ") " +
                            "WHERE customerId <> 0"
            );
        } catch (Exception ignored) {
        }

        try {
            db.execSQL(
                    "UPDATE jobs " +
                            "SET appointmentFirebaseId = (" +
                            "SELECT a.firebaseId " +
                            "FROM appointments a " +
                            "WHERE a.appointmentId = jobs.appointmentId" +
                            ") " +
                            "WHERE appointmentId <> 0"
            );
        } catch (Exception ignored) {
        }

        try {
            db.execSQL(
                    "UPDATE payments " +
                            "SET appointmentFirebaseId = (" +
                            "SELECT a.firebaseId " +
                            "FROM appointments a " +
                            "WHERE a.appointmentId = payments.appointmentId" +
                            ") " +
                            "WHERE appointmentId <> 0"
            );
        } catch (Exception ignored) {
        }

        try {
            db.execSQL(
                    "UPDATE payments " +
                            "SET customerFirebaseId = (" +
                            "SELECT c.firebaseId " +
                            "FROM customers c " +
                            "WHERE c.customerId = payments.customerId" +
                            ") " +
                            "WHERE customerId <> 0"
            );
        } catch (Exception ignored) {
        }

        try {
            db.execSQL(
                    "UPDATE feedback " +
                            "SET customerFirebaseId = (" +
                            "SELECT c.firebaseId " +
                            "FROM customers c " +
                            "WHERE c.customerId = feedback.customerId" +
                            ") " +
                            "WHERE customerId <> 0"
            );
        } catch (Exception ignored) {
        }

        try {
            db.execSQL(
                    "UPDATE feedback " +
                            "SET jobFirebaseId = (" +
                            "SELECT j.firebaseId " +
                            "FROM jobs j " +
                            "WHERE j.jobId = feedback.jobId" +
                            ") " +
                            "WHERE jobId <> 0"
            );
        } catch (Exception ignored) {
        }
    }

    public void rebindRelationships() {
        SQLiteDatabase db = getWritableDatabase();
        beginRemoteSync();
        try {
            db.execSQL(
                    "UPDATE appointments " +
                            "SET customerId = (" +
                            "SELECT c.customerId " +
                            "FROM customers c " +
                            "WHERE c.firebaseId = appointments.customerFirebaseId" +
                            ") " +
                            "WHERE customerFirebaseId IS NOT NULL AND customerFirebaseId <> '' " +
                            "AND (customerId = 0 OR customerId IS NULL OR NOT EXISTS (" +
                            "SELECT 1 FROM customers c2 WHERE c2.customerId = appointments.customerId" +
                            "))"
            );

            db.execSQL(
                    "UPDATE jobs " +
                            "SET appointmentId = (" +
                            "SELECT a.appointmentId " +
                            "FROM appointments a " +
                            "WHERE a.firebaseId = jobs.appointmentFirebaseId" +
                            ") " +
                            "WHERE appointmentFirebaseId IS NOT NULL AND appointmentFirebaseId <> '' " +
                            "AND (appointmentId = 0 OR appointmentId IS NULL OR NOT EXISTS (" +
                            "SELECT 1 FROM appointments a2 WHERE a2.appointmentId = jobs.appointmentId" +
                            "))"
            );

            db.execSQL(
                    "UPDATE payments " +
                            "SET appointmentId = (" +
                            "SELECT a.appointmentId " +
                            "FROM appointments a " +
                            "WHERE a.firebaseId = payments.appointmentFirebaseId" +
                            ") " +
                            "WHERE appointmentFirebaseId IS NOT NULL AND appointmentFirebaseId <> '' " +
                            "AND (appointmentId = 0 OR appointmentId IS NULL OR NOT EXISTS (" +
                            "SELECT 1 FROM appointments a2 WHERE a2.appointmentId = payments.appointmentId" +
                            "))"
            );

            db.execSQL(
                    "UPDATE payments " +
                            "SET customerId = (" +
                            "SELECT c.customerId " +
                            "FROM customers c " +
                            "WHERE c.firebaseId = payments.customerFirebaseId" +
                            ") " +
                            "WHERE customerFirebaseId IS NOT NULL AND customerFirebaseId <> '' " +
                            "AND (customerId = 0 OR customerId IS NULL OR NOT EXISTS (" +
                            "SELECT 1 FROM customers c2 WHERE c2.customerId = payments.customerId" +
                            "))"
            );

            db.execSQL(
                    "UPDATE feedback " +
                            "SET customerId = (" +
                            "SELECT c.customerId " +
                            "FROM customers c " +
                            "WHERE c.firebaseId = feedback.customerFirebaseId" +
                            ") " +
                            "WHERE customerFirebaseId IS NOT NULL AND customerFirebaseId <> '' " +
                            "AND (customerId = 0 OR customerId IS NULL OR NOT EXISTS (" +
                            "SELECT 1 FROM customers c2 WHERE c2.customerId = feedback.customerId" +
                            "))"
            );

            db.execSQL(
                    "UPDATE feedback " +
                            "SET jobId = (" +
                            "SELECT j.jobId " +
                            "FROM jobs j " +
                            "WHERE j.firebaseId = feedback.jobFirebaseId" +
                            ") " +
                            "WHERE jobFirebaseId IS NOT NULL AND jobFirebaseId <> '' " +
                            "AND (jobId = 0 OR jobId IS NULL OR NOT EXISTS (" +
                            "SELECT 1 FROM jobs j2 WHERE j2.jobId = feedback.jobId" +
                            "))"
            );
        } catch (Exception ignored) {
        } finally {
            endRemoteSync();
        }
    }

    private void fillGeneratedFirebaseIds(
            SQLiteDatabase db,
            String table) {

        try {
            db.execSQL(
                    "UPDATE " +
                            table +
                            " SET firebaseId = lower(hex(randomblob(16))) " +
                            "WHERE firebaseId IS NULL OR firebaseId = ''"
            );
        } catch (Exception ignored) {
        }
    }

    // =========================================================
    // REMOTE SYNC CONTROL
    // =========================================================

    /**
     * Call before writing Firebase data into SQLite.
     */
    public synchronized void beginRemoteSync() {

        SQLiteDatabase db =
                getWritableDatabase();

        db.execSQL(
                "UPDATE sync_meta " +
                        "SET remoteSync = 1 " +
                        "WHERE id = 1"
        );
    }

    /**
     * Call after writing Firebase data into SQLite.
     */
    public synchronized void endRemoteSync() {

        SQLiteDatabase db =
                getWritableDatabase();

        db.execSQL(
                "UPDATE sync_meta " +
                        "SET remoteSync = 0 " +
                        "WHERE id = 1"
        );
    }

    // =========================================================
    // GENERIC SYNC QUERY METHODS
    // =========================================================

    public Cursor getPendingRows(
            String tableName) {

        validateSyncTable(tableName);

        SQLiteDatabase db =
                getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " +
                        tableName +
                        " WHERE syncPending = 1 " +
                        "ORDER BY lastModified ASC",
                null
        );
    }

    public Cursor getPendingTombstones() {

        SQLiteDatabase db =
                getReadableDatabase();

        return db.rawQuery(
                "SELECT id, tableName, firebaseId, deletedAt " +
                        "FROM sync_tombstones " +
                        "WHERE synced = 0 " +
                        "ORDER BY deletedAt ASC",
                null
        );
    }

    public boolean markRowSynced(
            String tableName,
            String firebaseId,
            long remoteTimestamp) {

        validateSyncTable(tableName);

        SQLiteDatabase db =
                getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                "syncPending",
                0
        );

        values.put(
                "lastModified",
                remoteTimestamp > 0
                        ? remoteTimestamp
                        : System.currentTimeMillis()
        );

        int updated =
                db.update(
                        tableName,
                        values,
                        "firebaseId = ?",
                        new String[]{firebaseId}
                );

        return updated > 0;
    }

    public boolean markTombstoneSynced(
            long tombstoneId) {

        SQLiteDatabase db =
                getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                "synced",
                1
        );

        int updated =
                db.update(
                        "sync_tombstones",
                        values,
                        "id = ?",
                        new String[]{
                                String.valueOf(tombstoneId)
                        }
                );

        return updated > 0;
    }

    public void cleanupSyncedTombstones() {

        SQLiteDatabase db =
                getWritableDatabase();

        db.delete(
                "sync_tombstones",
                "synced = 1",
                null
        );
    }

    // =========================================================
    // FIREBASE -> SQLITE GENERIC UPSERT
    // =========================================================

    /**
     * Upsert a Firestore document into the local table.
     *
     * The caller should provide all normal application fields through
     * ContentValues, but should NOT insert password values from Firestore.
     *
     * This method temporarily switches the DB into remoteSync mode so
     * SQLite triggers will not create another outbound sync operation.
     */
    public long upsertFromFirebase(
            String tableName,
            String firebaseId,
            ContentValues values,
            long remoteTimestamp) {

        validateSyncTable(tableName);

        if (firebaseId == null ||
                firebaseId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "firebaseId cannot be empty"
            );
        }

        SQLiteDatabase db =
                getWritableDatabase();

        beginRemoteSync();

        try {

            ContentValues safeValues =
                    new ContentValues();

            safeValues.putAll(values);

            safeValues.put(
                    "firebaseId",
                    firebaseId
            );

            safeValues.put(
                    "lastModified",
                    remoteTimestamp > 0
                            ? remoteTimestamp
                            : System.currentTimeMillis()
            );

            safeValues.put(
                    "syncPending",
                    0
            );

            safeValues.put(
                    "isDeleted",
                    0
            );

            Cursor cursor =
                    db.rawQuery(
                            "SELECT rowid FROM " +
                                    tableName +
                                    " WHERE firebaseId = ? " +
                                    "LIMIT 1",
                            new String[]{firebaseId}
                    );

            boolean exists =
                    cursor.moveToFirst();

            cursor.close();

            if (exists) {

                int updated =
                        db.update(
                                tableName,
                                safeValues,
                                "firebaseId = ?",
                                new String[]{firebaseId}
                        );

                return updated > 0 ? 1 : -1;

            } else {

                return db.insert(
                        tableName,
                        null,
                        safeValues
                );
            }

        } finally {

            endRemoteSync();
        }
    }

    /**
     * Delete a document locally because it was deleted in Firebase.
     */
    public boolean deleteFromFirebase(
            String tableName,
            String firebaseId) {

        validateSyncTable(tableName);

        if (firebaseId == null ||
                firebaseId.trim().isEmpty()) {

            return false;
        }

        SQLiteDatabase db =
                getWritableDatabase();

        beginRemoteSync();

        try {

            int deleted =
                    db.delete(
                            tableName,
                            "firebaseId = ?",
                            new String[]{firebaseId}
                    );

            return deleted > 0;

        } finally {

            endRemoteSync();
        }
    }

    /**
     * Find a local Firebase document ID.
     */
    public String getFirebaseIdByLocalId(
            String tableName,
            String localIdColumn,
            long localId) {

        validateSyncTable(tableName);

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT firebaseId " +
                                "FROM " +
                                tableName +
                                " WHERE " +
                                localIdColumn +
                                " = ? " +
                                "LIMIT 1",
                        new String[]{
                                String.valueOf(localId)
                        }
                );

        try {

            if (cursor.moveToFirst()) {

                return cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "firebaseId"
                        )
                );
            }

            return null;

        } finally {

            cursor.close();
        }
    }

    /**
     * Find a local numeric ID using a Firebase ID.
     */
    public long getLocalIdByFirebaseId(
            String tableName,
            String localIdColumn,
            String firebaseId) {

        validateSyncTable(tableName);

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT " +
                                localIdColumn +
                                " FROM " +
                                tableName +
                                " WHERE firebaseId = ? " +
                                "LIMIT 1",
                        new String[]{firebaseId}
                );

        try {

            if (cursor.moveToFirst()) {

                return cursor.getLong(
                        cursor.getColumnIndexOrThrow(
                                localIdColumn
                        )
                );
            }

            return -1;

        } finally {

            cursor.close();
        }
    }

    // =========================================================
    // PRODUCT METHODS
    // =========================================================

    public boolean insertProduct(
            String productName,
            String category,
            double price,
            int quantity) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                "productName",
                productName
        );

        values.put(
                "category",
                category
        );

        values.put(
                "price",
                price
        );

        values.put(
                "quantity",
                quantity
        );

        long result =
                db.insert(
                        "inventory",
                        null,
                        values
                );

        return result != -1;
    }

    private void insertProduct(
            SQLiteDatabase db,
            String productName,
            String category,
            double price,
            int quantity) {

        ContentValues values =
                new ContentValues();

        values.put(
                "productName",
                productName
        );

        values.put(
                "category",
                category
        );

        values.put(
                "price",
                price
        );

        values.put(
                "quantity",
                quantity
        );

        db.insert(
                "inventory",
                null,
                values
        );
    }

    // =========================================================
    // BRANCH METHODS
    // =========================================================

    public boolean insertBranch(
            String branchCode,
            String branchName,
            String address,
            String phone,
            String email,
            String status,
            double latitude,
            double longitude) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                "branchCode",
                branchCode
        );

        values.put(
                "branchName",
                branchName
        );

        values.put(
                "address",
                address
        );

        values.put(
                "phone",
                phone
        );

        values.put(
                "email",
                email
        );

        values.put(
                "status",
                status
        );

        values.put(
                "latitude",
                latitude
        );

        values.put(
                "longitude",
                longitude
        );

        long result =
                db.insert(
                        "branches",
                        null,
                        values
                );

        return result != -1;
    }

    private void insertBranch(
            SQLiteDatabase db,
            String branchCode,
            String branchName,
            String address,
            String phone,
            String email,
            String status,
            double latitude,
            double longitude) {

        ContentValues values =
                new ContentValues();

        values.put(
                "branchCode",
                branchCode
        );

        values.put(
                "branchName",
                branchName
        );

        values.put(
                "address",
                address
        );

        values.put(
                "phone",
                phone
        );

        values.put(
                "email",
                email
        );

        values.put(
                "status",
                status
        );

        values.put(
                "latitude",
                latitude
        );

        values.put(
                "longitude",
                longitude
        );

        db.insert(
                "branches",
                null,
                values
        );
    }

    // =========================================================
    // DEFAULT ADMIN
    // =========================================================

    private void insertDefaultAdmin(
            SQLiteDatabase db) {

        Cursor cursor =
                db.rawQuery(
                        "SELECT adminId " +
                                "FROM admins " +
                                "WHERE email = ?",
                        new String[]{
                                "admin@techfix.com"
                        }
                );

        if (cursor.moveToFirst()) {

            cursor.close();

            return;
        }

        cursor.close();

        ContentValues values =
                new ContentValues();

        values.put(
                "adminName",
                "TechFix Admin"
        );

        values.put(
                "email",
                "admin@techfix.com"
        );

        values.put(
                "phone",
                "0771234567"
        );

        values.put(
                "password",
                "admin123"
        );

        db.insert(
                "admins",
                null,
                values
        );
    }

    // =========================================================
    // VALIDATION
    // =========================================================

    private void validateSyncTable(
            String tableName) {

        if (tableName == null ||
                !SYNC_TABLES.contains(tableName)) {

            throw new IllegalArgumentException(
                    "Invalid sync table: " +
                            tableName
            );
        }
    }
}
