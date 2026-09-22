package com.example.techfix;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.graphics.Color;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ManageJobsActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;

    LinearLayout jobContainer;

    ArrayList<String> technicianIds;
    ArrayList<String> technicianNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_manage_jobs);

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
                findViewById(R.id.jobContainer);

        technicianIds =
                new ArrayList<>();

        technicianNames =
                new ArrayList<>();

        // Load technicians first
        loadTechnicians();

        // Then load appointments
        loadAppointments();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {

            technicianIds.clear();
            technicianNames.clear();

            loadTechnicians();
            loadAppointments();
        }
    }

    // =====================================================
    // LOAD TECHNICIANS
    // =====================================================

    private void loadTechnicians() {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor =
                    db.rawQuery(
                            "SELECT technicianId, technicianName " +
                                    "FROM technicians " +
                                    "ORDER BY technicianName",
                            null
                    );

            while (cursor.moveToNext()) {

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

                technicianIds.add(
                        technicianId
                );

                technicianNames.add(
                        technicianName
                );
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // =====================================================
    // LOAD APPOINTMENTS
    // =====================================================

    private void loadAppointments() {

        jobContainer.removeAllViews();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor =
                    db.rawQuery(
                            "SELECT " +
                                    "a.appointmentId, " +
                                    "a.customerId, " +
                                    "a.productService, " +
                                    "a.category, " +
                                    "a.price, " +
                                    "a.branch, " +
                                    "a.appointmentDate, " +
                                    "a.appointmentTime, " +
                                    "j.jobId, " +
                                    "j.technicianId, " +
                                    "j.status " +
                                    "FROM appointments a " +
                                    "LEFT JOIN jobs j " +
                                    "ON a.appointmentId = j.appointmentId " +
                                    "ORDER BY a.appointmentId DESC",
                            null
                    );

            if (!cursor.moveToFirst()) {

                TextView emptyText =
                        new TextView(this);

                emptyText.setText(
                        "No appointments available"
                );

                emptyText.setTextSize(17);

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

                return;
            }

            do {

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

                String appointmentDate =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "appointmentDate"
                                )
                        );

                String appointmentTime =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "appointmentTime"
                                )
                        );

                int jobId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "jobId"
                                )
                        );

                String assignedTechnicianId =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "technicianId"
                                )
                        );

                String jobStatus =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "status"
                                )
                        );

                createJobCard(
                        appointmentId,
                        customerId,
                        productService,
                        category,
                        price,
                        branch,
                        appointmentDate,
                        appointmentTime,
                        jobId,
                        assignedTechnicianId,
                        jobStatus
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
            int appointmentId,
            int customerId,
            String productService,
            String category,
            double price,
            String branch,
            String appointmentDate,
            String appointmentTime,
            int jobId,
            String assignedTechnicianId,
            String jobStatus
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
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
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
        // APPOINTMENT
        // =================================================

        TextView title =
                new TextView(this);

        title.setText(
                "Appointment #" + appointmentId
        );

        title.setTextSize(20);

        title.setTextColor(
                Color.BLACK
        );

        title.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        card.addView(title);

        // Customer ID
        TextView customerText =
                createInfoText(
                        "Customer ID: " + customerId
                );

        card.addView(customerText);

        // Product / Service
        TextView productText =
                createInfoText(
                        "Product / Service: " +
                                productService
                );

        card.addView(productText);

        // Category
        TextView categoryText =
                createInfoText(
                        "Category: " +
                                category
                );

        card.addView(categoryText);

        // Price
        TextView priceText =
                createInfoText(
                        "Price: Rs. " +
                                String.format(
                                        "%.2f",
                                        price
                                )
                );

        card.addView(priceText);

        // Branch
        TextView branchText =
                createInfoText(
                        "Branch: " +
                                branch
                );

        card.addView(branchText);

        // Date
        TextView dateText =
                createInfoText(
                        "Date: " +
                                appointmentDate
                );

        card.addView(dateText);

        // Time
        TextView timeText =
                createInfoText(
                        "Time: " +
                                appointmentTime
                );

        card.addView(timeText);

        // =================================================
        // CURRENT JOB STATUS
        // =================================================

        TextView statusText =
                createInfoText(
                        "Job Status: " +
                                (
                                        jobStatus == null
                                                ? "NOT ASSIGNED"
                                                : jobStatus
                                )
                );

        statusText.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        card.addView(statusText);

        // =================================================
        // TECHNICIAN LABEL
        // =================================================

        TextView technicianLabel =
                createInfoText(
                        "Assign Technician"
                );

        technicianLabel.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        technicianLabel.setPadding(
                0,
                20,
                0,
                8
        );

        card.addView(
                technicianLabel
        );

        // =================================================
        // TECHNICIAN SPINNER
        // =================================================

        Spinner technicianSpinner =
                new Spinner(this);

        ArrayList<String> spinnerNames =
                new ArrayList<>();

        spinnerNames.add(
                "Select Technician"
        );

        spinnerNames.addAll(
                technicianNames
        );

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        spinnerNames
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        technicianSpinner.setAdapter(
                adapter
        );

        // Select currently assigned technician
        if (assignedTechnicianId != null) {

            for (
                    int i = 0;
                    i < technicianIds.size();
                    i++
            ) {

                if (technicianIds
                        .get(i)
                        .equals(
                                assignedTechnicianId
                        )) {

                    technicianSpinner.setSelection(
                            i + 1
                    );

                    break;
                }
            }
        }

        card.addView(
                technicianSpinner
        );

        // =================================================
        // ASSIGN BUTTON
        // =================================================

        Button assignButton =
                new Button(this);

        assignButton.setText(
                jobId > 0
                        ? "Update Technician"
                        : "Assign Technician"
        );

        assignButton.setOnClickListener(
                v -> {

                    int selectedPosition =
                            technicianSpinner
                                    .getSelectedItemPosition();

                    if (selectedPosition <= 0) {

                        android.widget.Toast.makeText(
                                ManageJobsActivity.this,
                                "Please select a technician",
                                android.widget.Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    String selectedTechnicianId =
                            technicianIds.get(
                                    selectedPosition - 1
                            );

                    assignTechnician(
                            appointmentId,
                            jobId,
                            selectedTechnicianId
                    );
                }
        );

        card.addView(
                assignButton
        );

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

        textView.setText(text);

        textView.setTextSize(15);

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
    // ASSIGN TECHNICIAN
    // =====================================================

    private void assignTechnician(
            int appointmentId,
            int jobId,
            String technicianId
    ) {

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                "appointmentId",
                appointmentId
        );

        values.put(
                "technicianId",
                technicianId
        );

        values.put(
                "status",
                "PENDING"
        );

        values.put(
                "updatedAt",
                String.valueOf(
                        System.currentTimeMillis()
                )
        );

        long result;

        // -------------------------------------------------
        // UPDATE EXISTING JOB
        // -------------------------------------------------

        if (jobId > 0) {

            result =
                    db.update(
                            "jobs",
                            values,
                            "jobId = ?",
                            new String[]{
                                    String.valueOf(
                                            jobId
                                    )
                            }
                    );

        } else {

            // -------------------------------------------------
            // CREATE NEW JOB
            // -------------------------------------------------

            result =
                    db.insert(
                            "jobs",
                            null,
                            values
                    );
        }

        if (result > 0) {

            android.widget.Toast.makeText(
                    this,
                    "Technician assigned successfully",
                    android.widget.Toast.LENGTH_SHORT
            ).show();

            loadAppointments();

        } else {

            android.widget.Toast.makeText(
                    this,
                    "Failed to assign technician",
                    android.widget.Toast.LENGTH_SHORT
            ).show();
        }
    }
}