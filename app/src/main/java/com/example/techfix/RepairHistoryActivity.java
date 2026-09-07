package com.example.techfix;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class RepairHistoryActivity extends AppCompatActivity {

    LinearLayout historyContainer;

    DatabaseHelper databaseHelper;

    String customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_repair_history
        );

        historyContainer =
                findViewById(
                        R.id.historyContainer
                );

        databaseHelper =
                new DatabaseHelper(this);

        // Get logged-in customer ID
        customerId =
                getIntent().getStringExtra(
                        "customerId"
                );

        loadRepairHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            loadRepairHistory();
        }
    }

    // =====================================================
    // LOAD REPAIR HISTORY
    // =====================================================

    private void loadRepairHistory() {

        historyContainer.removeAllViews();

        if (customerId == null ||
                customerId.trim().isEmpty()) {

            showMessage(
                    "Customer information is missing"
            );

            return;
        }

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = null;

        try {

            /*
             * Get completed repairs for the logged-in
             * customer and retrieve the technician name.
             */
            cursor =
                    db.rawQuery(
                            "SELECT " +
                                    "j.jobId, " +
                                    "j.status, " +
                                    "j.photoPath, " +
                                    "a.appointmentId, " +
                                    "a.productService, " +
                                    "a.category, " +
                                    "a.price, " +
                                    "a.branch, " +
                                    "a.appointmentDate, " +
                                    "a.appointmentTime, " +
                                    "j.technicianId, " +
                                    "t.technicianName " +
                                    "FROM jobs j " +
                                    "INNER JOIN appointments a " +
                                    "ON j.appointmentId = a.appointmentId " +
                                    "LEFT JOIN technicians t " +
                                    "ON j.technicianId = t.technicianId " +
                                    "WHERE a.customerId = ? " +
                                    "AND j.status = 'FINISHED' " +
                                    "ORDER BY j.jobId DESC",
                            new String[]{
                                    customerId
                            }
                    );

            if (!cursor.moveToFirst()) {

                showMessage(
                        "No completed repairs yet"
                );

                return;
            }

            do {

                int jobId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "jobId"
                                )
                        );

                int appointmentId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "appointmentId"
                                )
                        );

                String productService =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "productService"
                                )
                        );

                String category =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "category"
                                )
                        );

                double price =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        "price"
                                )
                        );

                String branch =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "branch"
                                )
                        );

                String date =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "appointmentDate"
                                )
                        );

                String time =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "appointmentTime"
                                )
                        );

                String technicianId =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "technicianId"
                                )
                        );

                String technicianName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "technicianName"
                                )
                        );

                String photoPath =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "photoPath"
                                )
                        );

                createHistoryCard(
                        jobId,
                        appointmentId,
                        productService,
                        category,
                        price,
                        branch,
                        date,
                        time,
                        technicianId,
                        technicianName,
                        photoPath
                );

            } while (cursor.moveToNext());

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // =====================================================
    // CREATE HISTORY CARD
    // =====================================================

    private void createHistoryCard(
            int jobId,
            int appointmentId,
            String productService,
            String category,
            double price,
            String branch,
            String date,
            String time,
            String technicianId,
            String technicianName,
            String photoPath
    ) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                24,
                24,
                24,
                24
        );

        card.setBackgroundColor(
                Color.rgb(
                        245,
                        247,
                        250
                )
        );

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                0,
                0,
                20
        );

        card.setLayoutParams(
                cardParams
        );

        // =================================================
        // JOB TITLE
        // =================================================

        TextView title =
                new TextView(this);

        title.setText(
                "Repair #" + jobId
        );

        title.setTextSize(
                21
        );

        title.setTypeface(
                null,
                Typeface.BOLD
        );

        title.setTextColor(
                Color.BLACK
        );

        card.addView(
                title
        );

        // =================================================
        // COMPLETED STATUS
        // =================================================

        TextView status =
                new TextView(this);

        status.setText(
                "Status: FINISHED"
        );

        status.setTextSize(
                15
        );

        status.setTypeface(
                null,
                Typeface.BOLD
        );

        status.setTextColor(
                Color.rgb(
                        46,
                        125,
                        50
                )
        );

        status.setPadding(
                0,
                10,
                0,
                10
        );

        card.addView(
                status
        );

        // =================================================
        // PRODUCT / SERVICE
        // =================================================

        card.addView(
                createInfoText(
                        "Product / Service: " +
                                productService
                )
        );

        // =================================================
        // CATEGORY
        // =================================================

        card.addView(
                createInfoText(
                        "Category: " +
                                category
                )
        );

        // =================================================
        // PRICE
        // =================================================

        card.addView(
                createInfoText(
                        "Price: Rs. " +
                                String.format(
                                        "%.2f",
                                        price
                                )
                )
        );

        // =================================================
        // BRANCH
        // =================================================

        card.addView(
                createInfoText(
                        "Branch: " +
                                branch
                )
        );

        // =================================================
        // DATE / TIME
        // =================================================

        card.addView(
                createInfoText(
                        "Date: " +
                                date +
                                "    Time: " +
                                time
                )
        );

        // =================================================
        // TECHNICIAN
        // =================================================

        if (technicianName != null &&
                !technicianName.trim().isEmpty()) {

            card.addView(
                    createInfoText(
                            "Technician: " +
                                    technicianName
                    )
            );

        } else if (technicianId != null &&
                !technicianId.trim().isEmpty()) {

            // Fallback if technician name cannot be found
            card.addView(
                    createInfoText(
                            "Technician ID: " +
                                    technicianId
                    )
            );
        }

        // =================================================
        // REPAIR PHOTO
        // =================================================

        if (photoPath != null &&
                !photoPath.trim().isEmpty()) {

            File photoFile =
                    new File(
                            photoPath
                    );

            if (photoFile.exists()) {

                TextView photoLabel =
                        createInfoText(
                                "Repair Photo"
                        );

                photoLabel.setTypeface(
                        null,
                        Typeface.BOLD
                );

                photoLabel.setPadding(
                        0,
                        15,
                        0,
                        8
                );

                card.addView(
                        photoLabel
                );

                Bitmap bitmap =
                        BitmapFactory.decodeFile(
                                photoPath
                        );

                if (bitmap != null) {

                    ImageView photoView =
                            new ImageView(
                                    this
                            );

                    photoView.setImageBitmap(
                            bitmap
                    );

                    photoView.setScaleType(
                            ImageView.ScaleType.CENTER_CROP
                    );

                    LinearLayout.LayoutParams imageParams =
                            new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    220
                            );

                    imageParams.setMargins(
                            0,
                            0,
                            0,
                            12
                    );

                    photoView.setLayoutParams(
                            imageParams
                    );

                    photoView.setContentDescription(
                            "Repair photo"
                    );

                    card.addView(
                            photoView
                    );
                }

            } else {

                card.addView(
                        createInfoText(
                                "Repair photo is unavailable"
                        )
                );
            }
        }

        // =================================================
        // APPOINTMENT
        // =================================================

        card.addView(
                createInfoText(
                        "Appointment #" +
                                appointmentId
                )
        );

        historyContainer.addView(
                card
        );
    }

    // =====================================================
    // INFO TEXT
    // =====================================================

    private TextView createInfoText(
            String text
    ) {

        TextView textView =
                new TextView(this);

        textView.setText(
                text
        );

        textView.setTextSize(
                15
        );

        textView.setTextColor(
                Color.DKGRAY
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                0,
                7,
                0,
                0
        );

        textView.setLayoutParams(
                params
        );

        return textView;
    }

    // =====================================================
    // EMPTY MESSAGE
    // =====================================================

    private void showMessage(
            String message
    ) {

        TextView messageText =
                new TextView(this);

        messageText.setText(
                message
        );

        messageText.setTextSize(
                18
        );

        messageText.setTextColor(
                Color.GRAY
        );

        messageText.setGravity(
                Gravity.CENTER
        );

        messageText.setPadding(
                0,
                50,
                0,
                50
        );

        historyContainer.addView(
                messageText
        );
    }
}