package com.example.techfix;

import android.app.Application;
import android.util.Log;

/**
 * Global Application class for TechFix.
 *
 * Automatically initializes and starts full real-time
 * bidirectional SQLite <-> Firebase Firestore synchronization.
 */
public class TechFixApp extends Application {

    private static final String TAG = "TechFixApp";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Initializing TechFix Application and Firebase Sync...");
        FirebaseSyncManager.getInstance(this).startSync();
    }
}
