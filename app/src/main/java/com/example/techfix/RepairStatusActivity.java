package com.example.techfix;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RepairStatusActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private LinearLayout repairContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_repair_status);

        databaseHelper = new DatabaseHelper(this);

        repairContainer = findViewById(R.id.repairContainer);

        loadRepairStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (repairContainer != null) {
            loadRepairStatus();
        }
    }

    private void loadRepairStatus() {

        repairContainer.removeAllViews();

        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(
                "SELECT " +
                        "j.jobId, " +
                        "j.status, " +
                        "j.updatedAt, " +
                        "a.appointmentId, " +
                        "a.customerId, " +
                        "a.productService, " +
                        "a.category, " +
                        "a.price, " +
                        "a.branch, " +
                        "a.appointmentDate, " +
                        "a.appointmentTime, " +
                        "t.technicianName " +
                        "FROM jobs j " +
                        "INNER JOIN appointments a " +
                        "ON j.appointmentId = a.appointmentId " +
                        "LEFT JOIN technicians t " +
                        "ON j.technicianId = t.technicianId " +
                        "ORDER BY j.jobId DESC",
                null
        );

        if (cursor.getCount() == 0) {

            TextView emptyText = new TextView(this);

            emptyText.setText("No repair jobs found.");
            emptyText.setTextSize(16);
            emptyText.setGravity(Gravity.CENTER);
            emptyText.setPadding(20, 40, 20, 40);

            repairContainer.addView(emptyText);

            cursor.close();
            return;
        }

        while (cursor.moveToNext()) {

            int jobId =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow("jobId")
                    );

            int appointmentId =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow("appointmentId")
                    );

            int customerId =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow("customerId")
                    );

            String productService =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("productService")
                    );

            String category =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("category")
                    );

            double price =
                    cursor.getDouble(
                            cursor.getColumnIndexOrThrow("price")
                    );

            String branch =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("branch")
                    );

            String appointmentDate =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("appointmentDate")
                    );

            String appointmentTime =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("appointmentTime")
                    );

            String status =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("status")
                    );

            String updatedAt =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("updatedAt")
                    );

            String technicianName =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("technicianName")
                    );

            LinearLayout card =
                    new LinearLayout(this);

            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(24, 24, 24, 24);

            TextView title =
                    new TextView(this);

            title.setText("Job #" + jobId);
            title.setTextSize(19);
            title.setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
            );

            card.addView(title);

            addText(
                    card,
                    "Appointment #: " + appointmentId
            );

            addText(
                    card,
                    "Customer ID: " + customerId
            );

            addText(
                    card,
                    "Service: " + productService
            );

            addText(
                    card,
                    "Category: " + category
            );

            addText(
                    card,
                    "Price: Rs. " + price
            );

            addText(
                    card,
                    "Branch: " + branch
            );

            addText(
                    card,
                    "Appointment Date: " + appointmentDate
            );

            addText(
                    card,
                    "Appointment Time: " + appointmentTime
            );

            String displayTechnician;

            if (technicianName != null
                    && !technicianName.isEmpty()) {

                displayTechnician = technicianName;

            } else {

                displayTechnician = "Not assigned";
            }

            addText(
                    card,
                    "Technician: " + displayTechnician
            );

            String displayStatus;

            if (status == null || status.isEmpty()) {

                displayStatus = "WAITING FOR ASSIGNMENT";

            } else {

                displayStatus = status;
            }

            TextView statusText =
                    new TextView(this);

            statusText.setText(
                    "Repair Status: " + displayStatus
            );

            statusText.setTextSize(16);
            statusText.setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
            );

            statusText.setPadding(0, 10, 0, 10);

            card.addView(statusText);

            if (updatedAt != null
                    && !updatedAt.isEmpty()) {

                addText(
                        card,
                        "Last Updated: " + updatedAt
                );
            }

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            params.setMargins(0, 0, 0, 20);

            repairContainer.addView(
                    card,
                    params
            );
        }

        cursor.close();
    }

    private void addText(
            LinearLayout parent,
            String text
    ) {

        TextView textView =
                new TextView(this);

        textView.setText(text);
        textView.setTextSize(15);
        textView.setPadding(0, 5, 0, 5);

        parent.addView(textView);
    }
}