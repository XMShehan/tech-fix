package com.example.techfix;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;

public class MyJobsActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;

    LinearLayout jobContainer;

    String technicianId;
    String technicianName;

    boolean showHistory = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(
                R.layout.activity_my_jobs
        );

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        databaseHelper =
                new DatabaseHelper(this);

        jobContainer =
                findViewById(
                        R.id.jobContainer
                );

        technicianId =
                getIntent().getStringExtra(
                        "technicianId"
                );

        technicianName =
                getIntent().getStringExtra(
                        "technicianName"
                );

        showHistory =
                getIntent().getBooleanExtra(
                        "showHistory",
                        false
                );

        loadJobs();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            loadJobs();
        }
    }

    // =====================================================
    // LOAD JOBS
    // =====================================================

    private void loadJobs() {

        jobContainer.removeAllViews();

        if (technicianId == null ||
                technicianId.trim().isEmpty()) {

            showEmptyMessage(
                    "Technician information is missing"
            );

            return;
        }

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = null;

        try {

            String query;

            if (showHistory) {

                query =
                        "SELECT " +
                                "j.jobId, " +
                                "j.status, " +
                                "j.photoPath, " +
                                "a.appointmentId, " +
                                "a.customerId, " +
                                "a.productService, " +
                                "a.category, " +
                                "a.price, " +
                                "a.branch, " +
                                "a.appointmentDate, " +
                                "a.appointmentTime " +
                                "FROM jobs j " +
                                "INNER JOIN appointments a " +
                                "ON j.appointmentId = a.appointmentId " +
                                "WHERE j.technicianId = ? " +
                                "AND j.status = 'FINISHED' " +
                                "ORDER BY j.jobId DESC";

            } else {

                query =
                        "SELECT " +
                                "j.jobId, " +
                                "j.status, " +
                                "j.photoPath, " +
                                "a.appointmentId, " +
                                "a.customerId, " +
                                "a.productService, " +
                                "a.category, " +
                                "a.price, " +
                                "a.branch, " +
                                "a.appointmentDate, " +
                                "a.appointmentTime " +
                                "FROM jobs j " +
                                "INNER JOIN appointments a " +
                                "ON j.appointmentId = a.appointmentId " +
                                "WHERE j.technicianId = ? " +
                                "AND j.status != 'FINISHED' " +
                                "ORDER BY j.jobId DESC";
            }

            cursor =
                    db.rawQuery(
                            query,
                            new String[]{
                                    technicianId
                            }
                    );

            if (!cursor.moveToFirst()) {

                if (showHistory) {

                    showEmptyMessage(
                            "No completed jobs yet"
                    );

                } else {

                    showEmptyMessage(
                            "No jobs assigned to you"
                    );
                }

                return;
            }

            do {

                int jobId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "jobId"
                                )
                        );

                String status =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "status"
                                )
                        );

                String photoPath =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "photoPath"
                                )
                        );

                int appointmentId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "appointmentId"
                                )
                        );

                int customerId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "customerId"
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

                createJobCard(
                        jobId,
                        appointmentId,
                        customerId,
                        productService,
                        category,
                        price,
                        branch,
                        date,
                        time,
                        status,
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
    // CREATE JOB CARD
    // =====================================================

    private void createJobCard(
            int jobId,
            int appointmentId,
            int customerId,
            String productService,
            String category,
            double price,
            String branch,
            String date,
            String time,
            String status,
            String photoPath
    ) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                20,
                20,
                20,
                20
        );

        card.setBackgroundColor(
                Color.WHITE
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

        // Job title
        TextView title =
                new TextView(this);

        title.setText(
                "Job #" + jobId
        );

        title.setTextSize(20);

        title.setTextColor(
                Color.BLACK
        );

        title.setTypeface(
                null,
                Typeface.BOLD
        );

        card.addView(title);

        // Appointment
        card.addView(
                createInfoText(
                        "Appointment #" +
                                appointmentId
                )
        );

        // Customer
        card.addView(
                createInfoText(
                        "Customer ID: " +
                                customerId
                )
        );

        // Product
        card.addView(
                createInfoText(
                        "Product / Service: " +
                                productService
                )
        );

        // Category
        card.addView(
                createInfoText(
                        "Category: " +
                                category
                )
        );

        // Price
        card.addView(
                createInfoText(
                        "Price: Rs. " +
                                String.format(
                                        "%.2f",
                                        price
                                )
                )
        );

        // Branch
        card.addView(
                createInfoText(
                        "Branch: " +
                                branch
                )
        );

        // Date
        card.addView(
                createInfoText(
                        "Date: " +
                                date
                )
        );

        // Time
        card.addView(
                createInfoText(
                        "Time: " +
                                time
                )
        );

        // Status
        TextView statusText =
                createInfoText(
                        "Status: " +
                                status
                );

        statusText.setTypeface(
                null,
                Typeface.BOLD
        );

        card.addView(
                statusText
        );

        // =================================================
        // SAVED REPAIR PHOTO
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

                ImageView photoView =
                        new ImageView(
                                this
                        );

                Bitmap bitmap =
                        BitmapFactory.decodeFile(
                                photoPath
                        );

                if (bitmap != null) {

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

                    card.addView(
                            photoView
                    );
                }
            }
        }

        // =================================================
        // UPDATE JOB BUTTON
        // =================================================

        if (!showHistory) {

            Button updateButton =
                    new Button(this);

            updateButton.setText(
                    "Update Job"
            );

            updateButton.setOnClickListener(
                    v -> {

                        Intent intent =
                                new Intent(
                                        MyJobsActivity.this,
                                        UpdateJobActivity.class
                                );

                        intent.putExtra(
                                "jobId",
                                jobId
                        );

                        intent.putExtra(
                                "technicianId",
                                technicianId
                        );

                        intent.putExtra(
                                "productService",
                                productService
                        );

                        intent.putExtra(
                                "status",
                                status
                        );

                        startActivity(
                                intent
                        );
                    }
            );

            card.addView(
                    updateButton
            );
        }

        jobContainer.addView(
                card
        );
    }

    // =====================================================
    // CREATE INFO TEXT
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

        textView.setPadding(
                0,
                5,
                0,
                5
        );

        return textView;
    }

    // =====================================================
    // EMPTY MESSAGE
    // =====================================================

    private void showEmptyMessage(
            String message
    ) {

        TextView emptyText =
                new TextView(this);

        emptyText.setText(
                message
        );

        emptyText.setTextSize(
                17
        );

        emptyText.setTextColor(
                Color.GRAY
        );

        emptyText.setGravity(
                Gravity.CENTER
        );

        emptyText.setPadding(
                10,
                40,
                10,
                40
        );

        jobContainer.addView(
                emptyText
        );
    }
}