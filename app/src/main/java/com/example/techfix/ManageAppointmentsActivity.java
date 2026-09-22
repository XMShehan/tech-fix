package com.example.techfix;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ManageAppointmentsActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private LinearLayout appointmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manage_appointments);

        databaseHelper = new DatabaseHelper(this);

        appointmentContainer =
                findViewById(R.id.appointmentContainer);

        loadAppointments();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (appointmentContainer != null) {
            loadAppointments();
        }
    }

    private void loadAppointments() {

        appointmentContainer.removeAllViews();

        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(
                "SELECT a.appointmentId, " +
                        "a.customerId, " +
                        "a.productService, " +
                        "a.category, " +
                        "a.price, " +
                        "a.branch, " +
                        "a.appointmentDate, " +
                        "a.appointmentTime, " +
                        "j.status, " +
                        "j.technicianId, " +
                        "t.technicianName " +
                        "FROM appointments a " +
                        "LEFT JOIN jobs j " +
                        "ON a.appointmentId = j.appointmentId " +
                        "LEFT JOIN technicians t " +
                        "ON j.technicianId = t.technicianId " +
                        "ORDER BY a.appointmentId DESC",
                null
        );

        if (cursor.getCount() == 0) {

            TextView emptyText = new TextView(this);

            emptyText.setText("No appointments found.");
            emptyText.setTextSize(16);
            emptyText.setGravity(Gravity.CENTER);
            emptyText.setPadding(20, 40, 20, 40);

            appointmentContainer.addView(emptyText);

            cursor.close();
            return;
        }

        while (cursor.moveToNext()) {

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

            title.setText(
                    "Appointment #" + appointmentId
            );

            title.setTextSize(18);
            title.setTypeface(null, android.graphics.Typeface.BOLD);

            card.addView(title);

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
                    "Date: " + appointmentDate
            );

            addText(
                    card,
                    "Time: " + appointmentTime
            );

            String displayStatus;

            if (status == null) {
                displayStatus = "WAITING FOR ASSIGNMENT";
            } else {
                displayStatus = status;
            }

            addText(
                    card,
                    "Repair Status: " + displayStatus
            );

            if (technicianName != null
                    && !technicianName.isEmpty()) {

                addText(
                        card,
                        "Technician: " + technicianName
                );

            } else {

                addText(
                        card,
                        "Technician: Not assigned yet"
                );
            }

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            params.setMargins(0, 0, 0, 20);

            appointmentContainer.addView(card, params);
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
        textView.setPadding(0, 6, 0, 6);

        parent.addView(textView);
    }
}