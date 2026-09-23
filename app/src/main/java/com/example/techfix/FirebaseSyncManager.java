package com.example.techfix;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Full bidirectional Firebase Firestore <-> SQLite synchronization manager.
 *
 * Synchronizes all 10 core entities:
 * 1. inventory
 * 2. appointments
 * 3. branches
 * 4. services
 * 5. technicians
 * 6. feedback
 * 7. customers
 * 8. admins
 * 9. jobs
 * 10. payments
 *
 * AUTOMATIC OPERATION:
 * - Local SQLite INSERT/UPDATE/DELETE triggers set syncPending=1 or create tombstones.
 * - Outbound sync uploads pending local rows to Firestore and deletes tombstones.
 * - Inbound sync listens in real-time to Firestore changes and upserts/deletes SQLite rows.
 * - Echo loop prevention is handled by DatabaseHelper.beginRemoteSync() / endRemoteSync().
 */
public class FirebaseSyncManager {

    private static final String TAG = "FirebaseSyncManager";

    public static final String ACTION_SYNC_UPDATED = "com.example.techfix.ACTION_SYNC_UPDATED";
    public static final String EXTRA_TABLE_NAME = "tableName";

    public static final String[] ALL_TABLES = {
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

    private static FirebaseSyncManager instance;

    private final Context context;
    private final FirebaseFirestore firestore;
    private final DatabaseHelper databaseHelper;

    private final Map<String, ListenerRegistration> listeners = new HashMap<>();

    private final Handler autoSyncHandler = new Handler(Looper.getMainLooper());
    private static final long AUTO_SYNC_INTERVAL_MS = 10000L; // 10 seconds

    private boolean isSyncingPending = false;

    private final Runnable autoSyncRunnable = new Runnable() {
        @Override
        public void run() {
            syncPendingData();
            syncTombstones();
            autoSyncHandler.postDelayed(this, AUTO_SYNC_INTERVAL_MS);
        }
    };

    private FirebaseSyncManager(Context context) {
        this.context = context.getApplicationContext();
        this.firestore = FirebaseFirestore.getInstance();
        this.databaseHelper = new DatabaseHelper(this.context);
    }

    public static synchronized FirebaseSyncManager getInstance(Context context) {
        if (instance == null) {
            instance = new FirebaseSyncManager(context);
        }
        return instance;
    }

    // =========================================================
    // START / STOP SYNC
    // =========================================================

    public void startSync() {
        Log.d(TAG, "Starting Firebase synchronization...");
        startRealtimeListeners();
        syncPendingData();
        syncTombstones();

        autoSyncHandler.removeCallbacks(autoSyncRunnable);
        autoSyncHandler.postDelayed(autoSyncRunnable, AUTO_SYNC_INTERVAL_MS);
    }

    public void stopSync() {
        Log.d(TAG, "Stopping Firebase synchronization...");
        stopRealtimeListeners();
        autoSyncHandler.removeCallbacks(autoSyncRunnable);
    }

    // =========================================================
    // REALTIME LISTENERS (FIREBASE -> SQLITE)
    // =========================================================

    public synchronized void startRealtimeListeners() {
        for (String table : ALL_TABLES) {
            if (listeners.containsKey(table)) {
                continue;
            }

            ListenerRegistration registration = firestore
                    .collection(table)
                    .addSnapshotListener((snapshots, error) -> {
                        if (error != null) {
                            Log.e(TAG, "Realtime listener error for table: " + table, error);
                            return;
                        }

                        if (snapshots == null) {
                            return;
                        }

                        for (DocumentChange change : snapshots.getDocumentChanges()) {
                            DocumentSnapshot document = change.getDocument();
                            String firebaseId = document.getId();

                            switch (change.getType()) {
                                case ADDED:
                                case MODIFIED:
                                    syncDocumentToSQLite(table, firebaseId, document);
                                    break;

                                case REMOVED:
                                    databaseHelper.deleteFromFirebase(table, firebaseId);
                                    Log.d(TAG, "Deleted local row for " + table + ": " + firebaseId);
                                    break;
                            }
                        }

                        databaseHelper.rebindRelationships();
                        notifySyncUpdated(table);
                    });

            listeners.put(table, registration);
            Log.d(TAG, "Started realtime listener for: " + table);
        }
    }

    public synchronized void stopRealtimeListeners() {
        for (Map.Entry<String, ListenerRegistration> entry : listeners.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().remove();
            }
        }
        listeners.clear();
        Log.d(TAG, "Stopped all realtime listeners");
    }

    // =========================================================
    // FIREBASE DOCUMENT -> SQLITE
    // =========================================================

    private void syncDocumentToSQLite(String table, String firebaseId, DocumentSnapshot doc) {
        try {
            if (doc == null || !doc.exists()) {
                return;
            }

            Long clientLastModified = doc.getLong("lastModified");
            long remoteTimestamp = getRemoteTimestamp(doc, clientLastModified);
            ContentValues values = new ContentValues();

            switch (table) {

                case "inventory":
                    values.put("productName", getString(doc, "productName"));
                    values.put("category", getString(doc, "category"));
                    values.put("price", getDouble(doc, "price"));
                    values.put("quantity", getInt(doc, "quantity"));
                    break;

                case "appointments": {
                    String customerFirebaseId = getString(doc, "customerFirebaseId");
                    long localCustomerId = 0;
                    if (customerFirebaseId != null && !customerFirebaseId.isEmpty()) {
                        localCustomerId = databaseHelper.getLocalIdByFirebaseId("customers", "customerId", customerFirebaseId);
                    }
                    if (localCustomerId <= 0) {
                        localCustomerId = getInt(doc, "customerId");
                    }

                    values.put("customerId", localCustomerId);
                    values.put("customerFirebaseId", customerFirebaseId);
                    values.put("productService", getString(doc, "productService"));
                    values.put("category", getString(doc, "category"));
                    values.put("price", getDouble(doc, "price"));
                    values.put("finalPrice", getDouble(doc, "finalPrice"));
                    values.put("branch", getString(doc, "branch"));
                    values.put("appointmentDate", getString(doc, "appointmentDate"));
                    values.put("appointmentTime", getString(doc, "appointmentTime"));
                    values.put("deviceModel", getString(doc, "deviceModel"));
                    values.put("problemDescription", getString(doc, "problemDescription"));
                    values.put("photoPath", getString(doc, "photoPath"));
                    break;
                }

                case "branches":
                    values.put("branchCode", getString(doc, "branchCode"));
                    values.put("branchName", getString(doc, "branchName"));
                    values.put("address", getString(doc, "address"));
                    values.put("phone", getString(doc, "phone"));
                    values.put("email", getString(doc, "email"));
                    values.put("status", getString(doc, "status", "Active"));
                    values.put("latitude", getDouble(doc, "latitude"));
                    values.put("longitude", getDouble(doc, "longitude"));
                    break;

                case "services":
                    values.put("serviceId", getString(doc, "serviceId", firebaseId));
                    values.put("serviceName", getString(doc, "serviceName"));
                    values.put("description", getString(doc, "description"));
                    values.put("price", getDouble(doc, "price"));
                    values.put("duration", getString(doc, "duration"));
                    values.put("status", getString(doc, "status", "Active"));
                    values.put("category", getString(doc, "category", "Other"));
                    break;

                case "technicians":
                    values.put("technicianId", getString(doc, "technicianId", firebaseId));
                    values.put("technicianName", getString(doc, "technicianName"));
                    values.put("phone", getString(doc, "phone"));
                    values.put("email", getString(doc, "email"));
                    values.put("branch", getString(doc, "branch", "Unassigned"));

                    if (databaseHelper.getLocalIdByFirebaseId("technicians", "technicianId", firebaseId) == -1) {
                        values.put("password", "tech123");
                    }
                    break;

                case "feedback": {
                    String customerFirebaseId = getString(doc, "customerFirebaseId");
                    String jobFirebaseId = getString(doc, "jobFirebaseId");

                    long localCustomerId = 0;
                    if (customerFirebaseId != null && !customerFirebaseId.isEmpty()) {
                        localCustomerId = databaseHelper.getLocalIdByFirebaseId("customers", "customerId", customerFirebaseId);
                    }

                    long localJobId = 0;
                    if (jobFirebaseId != null && !jobFirebaseId.isEmpty()) {
                        localJobId = databaseHelper.getLocalIdByFirebaseId("jobs", "jobId", jobFirebaseId);
                    }

                    values.put("customerId", localCustomerId);
                    values.put("customerFirebaseId", customerFirebaseId);
                    values.put("jobId", localJobId);
                    values.put("jobFirebaseId", jobFirebaseId);
                    values.put("customerName", getString(doc, "customerName"));
                    values.put("rating", getInt(doc, "rating"));
                    values.put("comment", getString(doc, "comment"));
                    values.put("date", getString(doc, "date"));
                    break;
                }

                case "customers":
                    values.put("customerName", getString(doc, "customerName"));
                    values.put("email", getString(doc, "email"));
                    values.put("phone", getString(doc, "phone"));

                    if (databaseHelper.getLocalIdByFirebaseId("customers", "customerId", firebaseId) == -1) {
                        values.put("password", "customer123");
                    }
                    break;

                case "admins":
                    values.put("adminName", getString(doc, "adminName"));
                    values.put("email", getString(doc, "email"));
                    values.put("phone", getString(doc, "phone"));

                    if (databaseHelper.getLocalIdByFirebaseId("admins", "adminId", firebaseId) == -1) {
                        values.put("password", "admin123");
                    }
                    break;

                case "jobs": {
                    String appointmentFirebaseId = getString(doc, "appointmentFirebaseId");
                    long localAppointmentId = 0;
                    if (appointmentFirebaseId != null && !appointmentFirebaseId.isEmpty()) {
                        localAppointmentId = databaseHelper.getLocalIdByFirebaseId("appointments", "appointmentId", appointmentFirebaseId);
                    }

                    values.put("appointmentId", localAppointmentId);
                    values.put("appointmentFirebaseId", appointmentFirebaseId);
                    values.put("technicianId", getString(doc, "technicianId"));
                    values.put("status", getString(doc, "status", "PENDING"));
                    values.put("photoPath", getString(doc, "photoPath"));
                    values.put("updatedAt", getString(doc, "updatedAt"));
                    break;
                }

                case "payments": {
                    String appointmentFirebaseId = getString(doc, "appointmentFirebaseId");
                    String customerFirebaseId = getString(doc, "customerFirebaseId");

                    long localAppointmentId = 0;
                    if (appointmentFirebaseId != null && !appointmentFirebaseId.isEmpty()) {
                        localAppointmentId = databaseHelper.getLocalIdByFirebaseId("appointments", "appointmentId", appointmentFirebaseId);
                    }

                    long localCustomerId = 0;
                    if (customerFirebaseId != null && !customerFirebaseId.isEmpty()) {
                        localCustomerId = databaseHelper.getLocalIdByFirebaseId("customers", "customerId", customerFirebaseId);
                    }

                    values.put("appointmentId", localAppointmentId);
                    values.put("appointmentFirebaseId", appointmentFirebaseId);
                    values.put("customerId", localCustomerId);
                    values.put("customerFirebaseId", customerFirebaseId);
                    values.put("amount", getDouble(doc, "amount"));
                    values.put("paymentMethod", getString(doc, "paymentMethod"));
                    values.put("paymentStatus", getString(doc, "paymentStatus", "PENDING"));
                    values.put("paymentDate", getString(doc, "paymentDate"));
                    break;
                }
            }

            databaseHelper.upsertFromFirebase(table, firebaseId, values, remoteTimestamp);
            Log.d(TAG, "Upserted row from Firebase for table " + table + ": " + firebaseId);

        } catch (Exception e) {
            Log.e(TAG, "Error upserting Firebase document for table " + table, e);
        }
    }

    // =========================================================
    // SQLITE -> FIREBASE (OUTBOUND SYNC)
    // =========================================================

    public synchronized void syncPendingData() {
        if (isSyncingPending) {
            return;
        }

        isSyncingPending = true;

        try {
            for (String table : ALL_TABLES) {
                syncPendingTableData(table);
            }
        } finally {
            isSyncingPending = false;
        }
    }

    private void syncPendingTableData(String table) {
        Cursor cursor = null;
        try {
            cursor = databaseHelper.getPendingRows(table);
            if (cursor == null) {
                return;
            }

            while (cursor.moveToNext()) {
                String firebaseId = getStringFromCursor(cursor, "firebaseId");
                long lastModified = getLongFromCursor(cursor, "lastModified");
                int isDeleted = getIntFromCursor(cursor, "isDeleted");

                if (firebaseId == null || firebaseId.trim().isEmpty()) {
                    continue;
                }

                if (isDeleted == 1) {
                    deleteDocumentFromFirebase(table, firebaseId);
                    continue;
                }

                Map<String, Object> data = buildMapFromCursorRow(table, cursor);
                if (data.isEmpty()) {
                    continue;
                }

                data.put("lastModified", lastModified);
                data.put("updatedAt", FieldValue.serverTimestamp());

                final String currentTable = table;
                final String currentFirebaseId = firebaseId;

                firestore.collection(table)
                        .document(firebaseId)
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                            databaseHelper.markRowSynced(currentTable, currentFirebaseId, System.currentTimeMillis());
                            Log.d(TAG, "Uploaded " + currentTable + " row to Firebase: " + currentFirebaseId);
                        })
                        .addOnFailureListener(e -> Log.e(TAG, "Failed to upload " + currentTable + " row: " + currentFirebaseId, e));
            }

        } catch (Exception e) {
            Log.e(TAG, "Error reading pending rows for table: " + table, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private Map<String, Object> buildMapFromCursorRow(String table, Cursor cursor) {
        Map<String, Object> data = new HashMap<>();

        switch (table) {
            case "inventory":
                data.put("productName", getStringFromCursor(cursor, "productName"));
                data.put("category", getStringFromCursor(cursor, "category"));
                data.put("price", getDoubleFromCursor(cursor, "price"));
                data.put("quantity", getIntFromCursor(cursor, "quantity"));
                break;

            case "appointments": {
                long customerId = getLongFromCursor(cursor, "customerId");
                String customerFirebaseId = getStringFromCursor(cursor, "customerFirebaseId");
                if ((customerFirebaseId == null || customerFirebaseId.isEmpty()) && customerId > 0) {
                    customerFirebaseId = databaseHelper.getFirebaseIdByLocalId("customers", "customerId", customerId);
                }

                data.put("customerId", customerId);
                data.put("customerFirebaseId", customerFirebaseId != null ? customerFirebaseId : "");
                data.put("productService", getStringFromCursor(cursor, "productService"));
                data.put("category", getStringFromCursor(cursor, "category"));
                data.put("price", getDoubleFromCursor(cursor, "price"));
                data.put("finalPrice", getDoubleFromCursor(cursor, "finalPrice"));
                data.put("branch", getStringFromCursor(cursor, "branch"));
                data.put("appointmentDate", getStringFromCursor(cursor, "appointmentDate"));
                data.put("appointmentTime", getStringFromCursor(cursor, "appointmentTime"));
                data.put("deviceModel", getStringFromCursor(cursor, "deviceModel"));
                data.put("problemDescription", getStringFromCursor(cursor, "problemDescription"));
                data.put("photoPath", getStringFromCursor(cursor, "photoPath"));
                break;
            }

            case "branches":
                data.put("branchCode", getStringFromCursor(cursor, "branchCode"));
                data.put("branchName", getStringFromCursor(cursor, "branchName"));
                data.put("address", getStringFromCursor(cursor, "address"));
                data.put("phone", getStringFromCursor(cursor, "phone"));
                data.put("email", getStringFromCursor(cursor, "email"));
                data.put("status", getStringFromCursor(cursor, "status"));
                data.put("latitude", getDoubleFromCursor(cursor, "latitude"));
                data.put("longitude", getDoubleFromCursor(cursor, "longitude"));
                break;

            case "services":
                data.put("serviceId", getStringFromCursor(cursor, "serviceId"));
                data.put("serviceName", getStringFromCursor(cursor, "serviceName"));
                data.put("description", getStringFromCursor(cursor, "description"));
                data.put("price", getDoubleFromCursor(cursor, "price"));
                data.put("duration", getStringFromCursor(cursor, "duration"));
                data.put("status", getStringFromCursor(cursor, "status"));
                data.put("category", getStringFromCursor(cursor, "category"));
                break;

            case "technicians":
                data.put("technicianId", getStringFromCursor(cursor, "technicianId"));
                data.put("technicianName", getStringFromCursor(cursor, "technicianName"));
                data.put("phone", getStringFromCursor(cursor, "phone"));
                data.put("email", getStringFromCursor(cursor, "email"));
                data.put("branch", getStringFromCursor(cursor, "branch"));
                break;

            case "feedback": {
                long customerId = getLongFromCursor(cursor, "customerId");
                String customerFirebaseId = getStringFromCursor(cursor, "customerFirebaseId");
                if ((customerFirebaseId == null || customerFirebaseId.isEmpty()) && customerId > 0) {
                    customerFirebaseId = databaseHelper.getFirebaseIdByLocalId("customers", "customerId", customerId);
                }

                long jobId = getLongFromCursor(cursor, "jobId");
                String jobFirebaseId = getStringFromCursor(cursor, "jobFirebaseId");
                if ((jobFirebaseId == null || jobFirebaseId.isEmpty()) && jobId > 0) {
                    jobFirebaseId = databaseHelper.getFirebaseIdByLocalId("jobs", "jobId", jobId);
                }

                data.put("customerId", customerId);
                data.put("customerFirebaseId", customerFirebaseId != null ? customerFirebaseId : "");
                data.put("jobId", jobId);
                data.put("jobFirebaseId", jobFirebaseId != null ? jobFirebaseId : "");
                data.put("customerName", getStringFromCursor(cursor, "customerName"));
                data.put("rating", getIntFromCursor(cursor, "rating"));
                data.put("comment", getStringFromCursor(cursor, "comment"));
                data.put("date", getStringFromCursor(cursor, "date"));
                break;
            }

            case "customers":
                data.put("customerName", getStringFromCursor(cursor, "customerName"));
                data.put("email", getStringFromCursor(cursor, "email"));
                data.put("phone", getStringFromCursor(cursor, "phone"));
                break;

            case "admins":
                data.put("adminName", getStringFromCursor(cursor, "adminName"));
                data.put("email", getStringFromCursor(cursor, "email"));
                data.put("phone", getStringFromCursor(cursor, "phone"));
                break;

            case "jobs": {
                long appointmentId = getLongFromCursor(cursor, "appointmentId");
                String appointmentFirebaseId = getStringFromCursor(cursor, "appointmentFirebaseId");
                if ((appointmentFirebaseId == null || appointmentFirebaseId.isEmpty()) && appointmentId > 0) {
                    appointmentFirebaseId = databaseHelper.getFirebaseIdByLocalId("appointments", "appointmentId", appointmentId);
                }

                data.put("appointmentId", appointmentId);
                data.put("appointmentFirebaseId", appointmentFirebaseId != null ? appointmentFirebaseId : "");
                data.put("technicianId", getStringFromCursor(cursor, "technicianId"));
                data.put("status", getStringFromCursor(cursor, "status"));
                data.put("photoPath", getStringFromCursor(cursor, "photoPath"));
                data.put("updatedAt", getStringFromCursor(cursor, "updatedAt"));
                break;
            }

            case "payments": {
                long appointmentId = getLongFromCursor(cursor, "appointmentId");
                String appointmentFirebaseId = getStringFromCursor(cursor, "appointmentFirebaseId");
                if ((appointmentFirebaseId == null || appointmentFirebaseId.isEmpty()) && appointmentId > 0) {
                    appointmentFirebaseId = databaseHelper.getFirebaseIdByLocalId("appointments", "appointmentId", appointmentId);
                }

                long customerId = getLongFromCursor(cursor, "customerId");
                String customerFirebaseId = getStringFromCursor(cursor, "customerFirebaseId");
                if ((customerFirebaseId == null || customerFirebaseId.isEmpty()) && customerId > 0) {
                    customerFirebaseId = databaseHelper.getFirebaseIdByLocalId("customers", "customerId", customerId);
                }

                data.put("appointmentId", appointmentId);
                data.put("appointmentFirebaseId", appointmentFirebaseId != null ? appointmentFirebaseId : "");
                data.put("customerId", customerId);
                data.put("customerFirebaseId", customerFirebaseId != null ? customerFirebaseId : "");
                data.put("amount", getDoubleFromCursor(cursor, "amount"));
                data.put("paymentMethod", getStringFromCursor(cursor, "paymentMethod"));
                data.put("paymentStatus", getStringFromCursor(cursor, "paymentStatus"));
                data.put("paymentDate", getStringFromCursor(cursor, "paymentDate"));
                break;
            }
        }

        return data;
    }

    // =========================================================
    // SQLITE TOMBSTONES -> FIREBASE DELETES
    // =========================================================

    public synchronized void syncTombstones() {
        Cursor cursor = null;

        try {
            cursor = databaseHelper.getPendingTombstones();
            if (cursor == null) {
                return;
            }

            while (cursor.moveToNext()) {
                String tableName = getStringFromCursor(cursor, "tableName");
                String firebaseId = getStringFromCursor(cursor, "firebaseId");
                long tombstoneId = getLongFromCursor(cursor, "id");

                if (tableName == null || tableName.trim().isEmpty() || firebaseId == null || firebaseId.trim().isEmpty()) {
                    databaseHelper.markTombstoneSynced(tombstoneId);
                    continue;
                }

                final long currentTombstoneId = tombstoneId;
                firestore.collection(tableName)
                        .document(firebaseId)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            databaseHelper.markTombstoneSynced(currentTombstoneId);
                            Log.d(TAG, "Synced tombstone delete for " + tableName + ": " + firebaseId);
                        })
                        .addOnFailureListener(e -> Log.e(TAG, "Failed to sync tombstone for " + tableName + ": " + firebaseId, e));
            }

            databaseHelper.cleanupSyncedTombstones();

        } catch (Exception e) {
            Log.e(TAG, "Error syncing tombstones", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void deleteDocumentFromFirebase(String table, String firebaseId) {
        if (firebaseId == null || firebaseId.trim().isEmpty()) {
            return;
        }

        firestore.collection(table)
                .document(firebaseId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Deleted " + table + " document from Firebase: " + firebaseId))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to delete " + table + " document: " + firebaseId, e));
    }

    // =========================================================
    // BROADCAST NOTIFICATION
    // =========================================================

    private void notifySyncUpdated(String table) {
        try {
            Intent intent = new Intent(ACTION_SYNC_UPDATED);
            intent.putExtra(EXTRA_TABLE_NAME, table);
            context.sendBroadcast(intent);
        } catch (Exception ignored) {
        }
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    private long getRemoteTimestamp(DocumentSnapshot doc, Long clientLastModified) {
        try {
            com.google.firebase.Timestamp timestamp = doc.getTimestamp("updatedAt");
            if (timestamp != null) {
                return timestamp.toDate().getTime();
            }
        } catch (Exception ignored) {
        }

        if (clientLastModified != null && clientLastModified > 0) {
            return clientLastModified;
        }

        return System.currentTimeMillis();
    }

    private String getString(DocumentSnapshot doc, String field) {
        return doc.getString(field);
    }

    private String getString(DocumentSnapshot doc, String field, String defaultValue) {
        String value = doc.getString(field);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    private int getInt(DocumentSnapshot doc, String field) {
        Long value = doc.getLong(field);
        return value != null ? value.intValue() : 0;
    }

    private double getDouble(DocumentSnapshot doc, String field) {
        Double value = doc.getDouble(field);
        return value != null ? value : 0.0;
    }

    private String getStringFromCursor(Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        if (index < 0 || cursor.isNull(index)) {
            return null;
        }
        return cursor.getString(index);
    }

    private int getIntFromCursor(Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        if (index < 0 || cursor.isNull(index)) {
            return 0;
        }
        return cursor.getInt(index);
    }

    private long getLongFromCursor(Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        if (index < 0 || cursor.isNull(index)) {
            return 0;
        }
        return cursor.getLong(index);
    }

    private double getDoubleFromCursor(Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        if (index < 0 || cursor.isNull(index)) {
            return 0.0;
        }
        return cursor.getDouble(index);
    }
}
