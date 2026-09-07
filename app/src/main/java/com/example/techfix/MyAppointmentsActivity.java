package com.example.techfix;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MyAppointmentsActivity extends AppCompatActivity {

    LinearLayout appointmentListContainer;

    DatabaseHelper databaseHelper;

    String customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_my_appointments
        );

        appointmentListContainer =
                findViewById(
                        R.id.appointmentListContainer
                );

        databaseHelper =
                new DatabaseHelper(this);

        // Get logged-in customer ID
        customerId =
                getIntent().getStringExtra(
                        "customerId"
                );

        loadAppointments();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            loadAppointments();
        }
    }

    // =====================================================
    // LOAD CUSTOMER APPOINTMENTS
    // =====================================================

    private void loadAppointments() {

        appointmentListContainer.removeAllViews();

        if (customerId == null ||
                customerId.trim().isEmpty()) {

            showEmptyMessage(
                    "Customer information is missing"
            );

            return;
        }

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = null;

        try {

            /*
             * Get only appointments belonging to
             * the logged-in customer.
             *
             * LEFT JOIN allows appointments to appear
             * even when the admin has not assigned a
             * technician yet.
             */
            cursor = db.rawQuery(
                    "SELECT " +
                            "a.appointmentId, " +
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
                            "WHERE a.customerId = ? " +
                            "ORDER BY a.appointmentId DESC",
                    new String[]{
                            customerId
                    }
            );

            if (!cursor.moveToFirst()) {

                showEmptyMessage(
                        "No appointments found"
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

                int jobId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "jobId"
                                )
                        );

                String technicianId =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "technicianId"
                                )
                        );

                String status =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "status"
                                )
                        );

                createAppointmentCard(
                        appointmentId,
                        productService,
                        category,
                        price,
                        branch,
                        date,
                        time,
                        jobId,
                        technicianId,
                        status
                );

            } while (cursor.moveToNext());

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // =====================================================
    // CREATE APPOINTMENT CARD
    // =====================================================

    private void createAppointmentCard(
            int appointmentId,
            String productService,
            String category,
            double price,
            String branch,
            String date,
            String time,
            int jobId,
            String technicianId,
            String status
    ) {

        // Main card
        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                25,
                25,
                25,
                25
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
        // APPOINTMENT NUMBER
        // =================================================

        TextView appointmentText =
                new TextView(this);

        appointmentText.setText(
                "Appointment #" +
                        appointmentId
        );

        appointmentText.setTextSize(
                21
        );

        appointmentText.setTypeface(
                null,
                Typeface.BOLD
        );

        card.addView(
                appointmentText
        );

        // =================================================
        // PRODUCT / SERVICE
        // =================================================

        TextView productText =
                new TextView(this);

        productText.setText(
                productService
        );

        productText.setTextSize(
                18
        );

        productText.setTypeface(
                null,
                Typeface.BOLD
        );

        LinearLayout.LayoutParams productParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        productParams.setMargins(
                0,
                10,
                0,
                0
        );

        productText.setLayoutParams(
                productParams
        );

        card.addView(
                productText
        );

        // =================================================
        // REPAIR STATUS
        // =================================================

        String displayStatus;

        if (status == null ||
                status.trim().isEmpty()) {

            displayStatus =
                    "WAITING FOR ASSIGNMENT";

        } else {

            displayStatus =
                    status;
        }

        TextView statusText =
                new TextView(this);

        statusText.setText(
                "Repair Status: " +
                        displayStatus
        );

        statusText.setTextSize(
                15
        );

        statusText.setTypeface(
                null,
                Typeface.BOLD
        );

        statusText.setPadding(
                0,
                10,
                0,
                10
        );

        // Status color
        if (displayStatus.equals(
                "FINISHED"
        )) {

            statusText.setTextColor(
                    Color.rgb(
                            46,
                            125,
                            50
                    )
            );

        } else if (displayStatus.equals(
                "ONGOING"
        )) {

            statusText.setTextColor(
                    Color.rgb(
                            230,
                            126,
                            34
                    )
            );

        } else if (displayStatus.equals(
                "STARTED"
        )) {

            statusText.setTextColor(
                    Color.rgb(
                            41,
                            98,
                            255
                    )
            );

        } else {

            statusText.setTextColor(
                    Color.rgb(
                            103,
                            80,
                            164
                    )
            );
        }

        card.addView(
                statusText
        );

        // =================================================
        // CATEGORY
        // =================================================

        TextView categoryText =
                createInfoText(
                        "Category: " +
                                category
                );

        card.addView(
                categoryText
        );

        // =================================================
        // PRICE
        // =================================================

        TextView priceText =
                createInfoText(
                        "Price: Rs. " +
                                String.format(
                                        "%.2f",
                                        price
                                )
                );

        card.addView(
                priceText
        );

        // =================================================
        // BRANCH
        // =================================================

        TextView branchText =
                createInfoText(
                        "Branch: " +
                                branch
                );

        card.addView(
                branchText
        );

        // =================================================
        // DATE / TIME
        // =================================================

        TextView dateTimeText =
                createInfoText(
                        "Date: " +
                                date +
                                "    Time: " +
                                time
                );

        card.addView(
                dateTimeText
        );

        // =================================================
        // TECHNICIAN
        // =================================================

        String technicianText;

        if (technicianId == null ||
                technicianId.trim().isEmpty()) {

            technicianText =
                    "Technician: Not assigned yet";

        } else {

            technicianText =
                    "Technician ID: " +
                            technicianId;
        }

        TextView technicianTextView =
                createInfoText(
                        technicianText
                );

        card.addView(
                technicianTextView
        );

        appointmentListContainer.addView(
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

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                0,
                8,
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

    private void showEmptyMessage(
            String message
    ) {

        TextView emptyText =
                new TextView(this);

        emptyText.setText(
                message
        );

        emptyText.setTextSize(
                18
        );

        emptyText.setGravity(
                Gravity.CENTER
        );

        emptyText.setPadding(
                0,
                50,
                0,
                50
        );

        appointmentListContainer.addView(
                emptyText
        );
    }
}