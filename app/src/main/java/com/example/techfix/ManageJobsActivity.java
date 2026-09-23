package com.example.techfix;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ManageJobsActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    private LinearLayout jobContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(
                R.layout.activity_manage_jobs
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

        // =====================================================
        // DATABASE
        // =====================================================

        databaseHelper =
                new DatabaseHelper(this);

        // =====================================================
        // JOB CONTAINER
        // =====================================================

        jobContainer =
                findViewById(
                        R.id.jobContainer
                );

        // =====================================================
        // LOAD JOBS
        // =====================================================

        loadAppointments();
    }

    // =========================================================
    // REFRESH
    // =========================================================

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            loadAppointments();
        }
    }

    // =========================================================
    // LOAD APPOINTMENTS / JOBS
    // =========================================================

    private void loadAppointments() {

        jobContainer.removeAllViews();

        SQLiteDatabase db =
                databaseHelper
                        .getReadableDatabase();

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

    // =========================================================
    // CREATE JOB CARD
    // =========================================================

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

        // =====================================================
        // APPOINTMENT
        // =====================================================

        TextView title =
                new TextView(this);

        title.setText(
                "Appointment #" +
                        appointmentId
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

        // =====================================================
        // CUSTOMER
        // =====================================================

        card.addView(
                createInfoText(
                        "Customer ID: " +
                                customerId
                )
        );

        // =====================================================
        // PRODUCT / SERVICE
        // =====================================================

        card.addView(
                createInfoText(
                        "Product / Service: " +
                                productService
                )
        );

        // =====================================================
        // CATEGORY
        // =====================================================

        card.addView(
                createInfoText(
                        "Category: " +
                                category
                )
        );

        // =====================================================
        // PRICE
        // =====================================================

        card.addView(
                createInfoText(
                        "Estimated Price: Rs. " +
                                String.format(
                                        "%.2f",
                                        price
                                )
                )
        );

        // =====================================================
        // BRANCH
        // =====================================================

        card.addView(
                createInfoText(
                        "Branch: " +
                                safeText(branch)
                )
        );

        // =====================================================
        // DATE
        // =====================================================

        card.addView(
                createInfoText(
                        "Date: " +
                                safeText(
                                        appointmentDate
                                )
                )
        );

        // =====================================================
        // TIME
        // =====================================================

        card.addView(
                createInfoText(
                        "Time: " +
                                safeText(
                                        appointmentTime
                                )
                )
        );

        // =====================================================
        // NORMALIZE STATUS
        // =====================================================

        String displayStatus;

        if (jobId <= 0) {

            displayStatus =
                    "NOT ASSIGNED";

        } else if (jobStatus == null ||
                jobStatus.trim().isEmpty()) {

            displayStatus =
                    "PENDING";

        } else {

            displayStatus =
                    jobStatus.trim().toUpperCase();
        }

        // =====================================================
        // JOB STATUS
        // =====================================================

        TextView statusText =
                createInfoText(
                        "Job Status: " +
                                displayStatus
                );

        statusText.setTypeface(
                null,
                Typeface.BOLD
        );

        card.addView(
                statusText
        );

        // =====================================================
        // TECHNICIAN DETAILS
        // =====================================================

        String technicianText;

        if (assignedTechnicianId == null ||
                assignedTechnicianId.trim().isEmpty()) {

            technicianText =
                    "Technician: Not assigned yet";

        } else {

            technicianText =
                    "Technician ID: " +
                            assignedTechnicianId;
        }

        TextView technicianTextView =
                createInfoText(
                        technicianText
                );

        technicianTextView.setTypeface(
                null,
                Typeface.BOLD
        );

        card.addView(
                technicianTextView
        );

        // =====================================================
        // TECHNICIAN ASSIGNMENT
        // =====================================================

        /*
         * Technician can only be assigned/changed while
         * the job has NOT started.
         *
         * Allowed:
         *      NOT ASSIGNED
         *      PENDING
         *
         * Locked:
         *      STARTED
         *      ONGOING
         *      FINISHED
         */

        boolean technicianAssignmentAllowed =
                displayStatus.equals("NOT ASSIGNED") ||
                        displayStatus.equals("PENDING");

        if (technicianAssignmentAllowed) {

            // -------------------------------------------------
            // LABEL
            // -------------------------------------------------

            TextView technicianLabel =
                    createInfoText(
                            "Assign Technician"
                    );

            technicianLabel.setTypeface(
                    null,
                    Typeface.BOLD
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

            // -------------------------------------------------
            // LOAD TECHNICIANS FOR THIS BRANCH
            // -------------------------------------------------

            ArrayList<String> technicianIds =
                    new ArrayList<>();

            ArrayList<String> technicianNames =
                    new ArrayList<>();

            loadTechniciansForBranch(
                    branch,
                    technicianIds,
                    technicianNames
            );

            // -------------------------------------------------
            // SPINNER
            // -------------------------------------------------

            Spinner technicianSpinner =
                    new Spinner(this);

            ArrayList<String> spinnerNames =
                    new ArrayList<>();

            spinnerNames.add(
                    "Select Technician"
            );

            if (technicianNames.isEmpty()) {

                spinnerNames.add(
                        "No technicians available at this branch"
                );

            } else {

                spinnerNames.addAll(
                        technicianNames
                );
            }

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

            if (technicianNames.isEmpty()) {

                technicianSpinner.setEnabled(
                        false
                );
            }

            // -------------------------------------------------
            // SELECT CURRENT TECHNICIAN
            // -------------------------------------------------

            if (assignedTechnicianId != null &&
                    !assignedTechnicianId.trim().isEmpty()) {

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

            // -------------------------------------------------
            // ASSIGN / UPDATE BUTTON
            // -------------------------------------------------

            Button assignButton =
                    new Button(this);

            if (jobId > 0) {

                assignButton.setText(
                        "Update Technician"
                );

            } else {

                assignButton.setText(
                        "Assign Technician"
                );
            }

            if (technicianNames.isEmpty()) {

                assignButton.setEnabled(
                        false
                );
            }

            assignButton.setOnClickListener(
                    v -> {

                        int selectedPosition =
                                technicianSpinner
                                        .getSelectedItemPosition();

                        if (selectedPosition <= 0) {

                            Toast.makeText(
                                    ManageJobsActivity.this,
                                    "Please select a technician",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        if (technicianIds.isEmpty()) {

                            Toast.makeText(
                                    ManageJobsActivity.this,
                                    "No technicians available at this branch",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        if (selectedPosition - 1 >=
                                technicianIds.size()) {

                            Toast.makeText(
                                    ManageJobsActivity.this,
                                    "Invalid technician selection",
                                    Toast.LENGTH_SHORT
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

        } else {

            // =================================================
            // LOCKED MESSAGE
            // =================================================

            TextView lockedText =
                    createInfoText(
                            "Technician assignment is locked because the repair has started."
                    );

            lockedText.setTextColor(
                    Color.DKGRAY
            );

            lockedText.setTypeface(
                    null,
                    Typeface.ITALIC
            );

            lockedText.setPadding(
                    0,
                    16,
                    0,
                    4
            );

            card.addView(
                    lockedText
            );
        }

        // =====================================================
        // ADD CARD
        // =====================================================

        jobContainer.addView(
                card
        );
    }

    // =========================================================
    // LOAD TECHNICIANS FOR SELECTED BRANCH
    // =========================================================

    private void loadTechniciansForBranch(
            String branch,
            ArrayList<String> technicianIds,
            ArrayList<String> technicianNames
    ) {

        technicianIds.clear();
        technicianNames.clear();

        if (branch == null ||
                branch.trim().isEmpty()) {

            return;
        }

        SQLiteDatabase db =
                databaseHelper
                        .getReadableDatabase();

        Cursor cursor = null;

        try {

            /*
             * Only technicians belonging to the
             * appointment branch are loaded.
             *
             * Example:
             *
             * Job Branch = Gampaha
             * ↓
             * Only Gampaha technicians appear.
             */

            cursor =
                    db.rawQuery(
                            "SELECT technicianId, technicianName " +
                                    "FROM technicians " +
                                    "WHERE LOWER(TRIM(COALESCE(branch, ''))) " +
                                    "= LOWER(TRIM(?)) " +
                                    "ORDER BY technicianName",

                            new String[]{
                                    branch.trim()
                            }
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

    // =========================================================
    // ASSIGN TECHNICIAN
    // =========================================================

    private void assignTechnician(
            int appointmentId,
            int jobId,
            String technicianId
    ) {

        SQLiteDatabase db =
                databaseHelper
                        .getWritableDatabase();

        // =====================================================
        // EXTRA SAFETY CHECK
        // =====================================================

        /*
         * Even if an old screen somehow remains open,
         * do not allow technician changes after the repair
         * has started.
         */

        if (jobId > 0) {

            Cursor statusCursor = null;

            try {

                statusCursor =
                        db.rawQuery(
                                "SELECT status " +
                                        "FROM jobs " +
                                        "WHERE jobId = ?",

                                new String[]{
                                        String.valueOf(
                                                jobId
                                        )
                                }
                        );

                if (statusCursor.moveToFirst()) {

                    String currentStatus =
                            statusCursor.getString(0);

                    if (currentStatus != null) {

                        currentStatus =
                                currentStatus
                                        .trim()
                                        .toUpperCase();
                    }

                    if ("STARTED".equals(
                            currentStatus
                    ) ||
                            "ONGOING".equals(
                                    currentStatus
                            ) ||
                            "FINISHED".equals(
                                    currentStatus
                            )) {

                        Toast.makeText(
                                this,
                                "Technician cannot be changed after the repair has started",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }
                }

            } finally {

                if (statusCursor != null) {
                    statusCursor.close();
                }
            }
        }

        // =====================================================
        // VALUES
        // =====================================================

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

        /*
         * A newly assigned job starts as PENDING.
         */
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

        // =====================================================
        // UPDATE EXISTING JOB
        // =====================================================

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

            // =================================================
            // CREATE NEW JOB
            // =================================================

            result =
                    db.insert(
                            "jobs",
                            null,
                            values
                    );
        }

        // =====================================================
        // RESULT
        // =====================================================

        if (result > 0) {

            Toast.makeText(
                    this,
                    "Technician assigned successfully",
                    Toast.LENGTH_SHORT
            ).show();

            loadAppointments();

        } else {

            Toast.makeText(
                    this,
                    "Failed to assign technician",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // =========================================================
    // INFO TEXT
    // =========================================================

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

    // =========================================================
    // SAFE TEXT
    // =========================================================

    private String safeText(
            String value
    ) {

        if (value == null ||
                value.trim().isEmpty()) {

            return "Not provided";
        }

        return value;
    }
}