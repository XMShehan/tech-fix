package com.example.techfix;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class ManageAppointmentsActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private LinearLayout appointmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );

        setContentView(
                R.layout.activity_manage_appointments
        );

        databaseHelper =
                new DatabaseHelper(this);

        appointmentContainer =
                findViewById(
                        R.id.appointmentContainer
                );

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

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor = db.rawQuery(
                    "SELECT " +
                            "a.appointmentId, " +
                            "a.customerId, " +
                            "a.productService, " +
                            "a.category, " +
                            "a.price, " +
                            "a.finalPrice, " +
                            "a.branch, " +
                            "a.appointmentDate, " +
                            "a.appointmentTime, " +
                            "j.status, " +
                            "j.technicianId, " +
                            "t.technicianName, " +
                            "p.paymentId, " +
                            "p.paymentStatus " +

                            "FROM appointments a " +

                            "LEFT JOIN jobs j " +
                            "ON a.appointmentId = j.appointmentId " +

                            "LEFT JOIN technicians t " +
                            "ON j.technicianId = t.technicianId " +

                            "LEFT JOIN payments p " +
                            "ON a.appointmentId = p.appointmentId " +
                            "AND a.customerId = p.customerId " +

                            "ORDER BY a.appointmentId DESC",

                    null
            );

            if (!cursor.moveToFirst()) {

                showMessage(
                        "No appointments found."
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

                double estimatedPrice =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        "price"
                                )
                        );

                double finalPrice =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        "finalPrice"
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

                String status =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "status"
                                )
                        );

                String technicianName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "technicianName"
                                )
                        );

                int paymentId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "paymentId"
                                )
                        );

                String paymentStatus =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "paymentStatus"
                                )
                        );

                createAppointmentCard(
                        appointmentId,
                        customerId,
                        productService,
                        category,
                        estimatedPrice,
                        finalPrice,
                        branch,
                        appointmentDate,
                        appointmentTime,
                        status,
                        technicianName,
                        paymentId,
                        paymentStatus
                );

            } while (cursor.moveToNext());

        } finally {

            if (cursor != null) {

                cursor.close();
            }
        }
    }

    private void createAppointmentCard(
            int appointmentId,
            int customerId,
            String productService,
            String category,
            double estimatedPrice,
            double finalPrice,
            String branch,
            String appointmentDate,
            String appointmentTime,
            String status,
            String technicianName,
            int paymentId,
            String paymentStatus
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

        TextView title =
                new TextView(this);

        title.setText(
                "Appointment #" +
                        appointmentId
        );

        title.setTextSize(19);

        title.setTypeface(
                null,
                Typeface.BOLD
        );

        title.setTextColor(
                Color.rgb(
                        31,
                        31,
                        31
                )
        );

        card.addView(title);

        addText(
                card,
                "Customer ID: " +
                        customerId
        );

        addText(
                card,
                "Service: " +
                        safeText(productService)
        );

        addText(
                card,
                "Category: " +
                        safeText(category)
        );

        addText(
                card,
                "Estimated Price: Rs. " +
                        formatMoney(estimatedPrice)
        );

        addText(
                card,
                "Branch: " +
                        safeText(branch)
        );

        addText(
                card,
                "Date: " +
                        safeText(appointmentDate)
        );

        addText(
                card,
                "Time: " +
                        safeText(appointmentTime)
        );

        String displayStatus;

        if (status == null ||
                status.trim().isEmpty()) {

            displayStatus =
                    "WAITING FOR ASSIGNMENT";

        } else {

            displayStatus =
                    status.toUpperCase(
                            Locale.getDefault()
                    );
        }

        TextView statusText =
                addText(
                        card,
                        "Repair Status: " +
                                displayStatus
                );

        statusText.setTypeface(
                null,
                Typeface.BOLD
        );

        if ("FINISHED".equals(displayStatus)) {

            statusText.setTextColor(
                    Color.rgb(
                            46,
                            125,
                            50
                    )
            );
        }

        if (technicianName != null &&
                !technicianName.trim().isEmpty()) {

            addText(
                    card,
                    "Technician: " +
                            technicianName
            );

        } else {

            addText(
                    card,
                    "Technician: Not assigned yet"
            );
        }

        // =====================================================
        // FINAL PRICE SECTION
        // =====================================================

        if ("FINISHED".equals(displayStatus)) {

            TextView sectionTitle =
                    new TextView(this);

            sectionTitle.setText(
                    "Final Repair Amount"
            );

            sectionTitle.setTextSize(16);

            sectionTitle.setTypeface(
                    null,
                    Typeface.BOLD
            );

            sectionTitle.setTextColor(
                    Color.rgb(
                            25,
                            118,
                            210
                    )
            );

            LinearLayout.LayoutParams sectionParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

            sectionParams.setMargins(
                    0,
                    18,
                    0,
                    6
            );

            sectionTitle.setLayoutParams(
                    sectionParams
            );

            card.addView(sectionTitle);

            if (paymentId > 0) {

                // ---------------------------------------------
                // PAYMENT ALREADY EXISTS
                // ---------------------------------------------

                if (finalPrice > 0) {

                    TextView finalPriceText =
                            addText(
                                    card,
                                    "Final Repair Amount: Rs. " +
                                            formatMoney(
                                                    finalPrice
                                            )
                            );

                    finalPriceText.setTypeface(
                            null,
                            Typeface.BOLD
                    );

                    finalPriceText.setTextColor(
                            Color.rgb(
                                    46,
                                    125,
                                    50
                            )
                    );

                } else {

                    addText(
                            card,
                            "Final Repair Amount: " +
                                    "Not available"
                    );
                }

                addText(
                        card,
                        "Payment: " +
                                safeText(paymentStatus)
                );

                addText(
                        card,
                        "Final price is locked because a payment has already been recorded."
                );

            } else {

                // ---------------------------------------------
                // NO PAYMENT YET
                // ---------------------------------------------

                EditText finalPriceInput =
                        new EditText(this);

                finalPriceInput.setLayoutParams(
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                );

                finalPriceInput.setHint(
                        "Enter final repair amount"
                );

                finalPriceInput.setTextSize(15);

                finalPriceInput.setInputType(
                        android.text.InputType.TYPE_CLASS_NUMBER |
                                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
                );

                finalPriceInput.setSingleLine(true);

                if (finalPrice > 0) {

                    finalPriceInput.setText(
                            String.format(
                                    Locale.getDefault(),
                                    "%.2f",
                                    finalPrice
                            )
                    );
                }

                card.addView(
                        finalPriceInput
                );

                Button saveButton =
                        new Button(this);

                saveButton.setText(
                        finalPrice > 0
                                ? "UPDATE FINAL PRICE"
                                : "SET FINAL PRICE"
                );

                LinearLayout.LayoutParams saveParams =
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );

                saveParams.setMargins(
                        0,
                        10,
                        0,
                        0
                );

                saveButton.setLayoutParams(
                        saveParams
                );

                saveButton.setOnClickListener(
                        v -> saveFinalPrice(
                                appointmentId,
                                finalPriceInput
                        )
                );

                card.addView(
                        saveButton
                );

                addText(
                        card,
                        "The customer can make payment only after a final repair amount is set."
                );
            }

        } else {

            addText(
                    card,
                    "Final Repair Amount: " +
                            "Available after repair is FINISHED"
            );
        }

        appointmentContainer.addView(
                card
        );
    }

    private void saveFinalPrice(
            int appointmentId,
            EditText input
    ) {

        String amountText =
                input.getText()
                        .toString()
                        .trim();

        if (amountText.isEmpty()) {

            input.setError(
                    "Enter the final repair amount"
            );

            input.requestFocus();

            return;
        }

        double finalPrice;

        try {

            finalPrice =
                    Double.parseDouble(
                            amountText
                    );

        } catch (NumberFormatException e) {

            input.setError(
                    "Enter a valid amount"
            );

            input.requestFocus();

            return;
        }

        if (finalPrice <= 0) {

            input.setError(
                    "Amount must be greater than 0"
            );

            input.requestFocus();

            return;
        }

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                "finalPrice",
                finalPrice
        );

        int updated =
                db.update(
                        "appointments",
                        values,
                        "appointmentId = ?",
                        new String[]{
                                String.valueOf(
                                        appointmentId
                                )
                        }
                );

        if (updated > 0) {

            Toast.makeText(
                    this,
                    "Final repair amount saved.",
                    Toast.LENGTH_SHORT
            ).show();

            loadAppointments();

        } else {

            Toast.makeText(
                    this,
                    "Could not save the final amount.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private TextView addText(
            LinearLayout parent,
            String text
    ) {

        TextView textView =
                new TextView(this);

        textView.setText(text);

        textView.setTextSize(15);

        textView.setTextColor(
                Color.rgb(
                        80,
                        80,
                        80
                )
        );

        textView.setPadding(
                0,
                6,
                0,
                6
        );

        parent.addView(
                textView
        );

        return textView;
    }

    private void showMessage(
            String message
    ) {

        TextView emptyText =
                new TextView(this);

        emptyText.setText(
                message
        );

        emptyText.setTextSize(16);

        emptyText.setGravity(
                Gravity.CENTER
        );

        emptyText.setPadding(
                20,
                50,
                20,
                50
        );

        appointmentContainer.addView(
                emptyText
        );
    }

    private String safeText(
            String text
    ) {

        if (text == null ||
                text.trim().isEmpty()) {

            return "Not available";
        }

        return text;
    }

    private String formatMoney(
            double amount
    ) {

        return String.format(
                Locale.getDefault(),
                "%.2f",
                amount
        );
    }
}