package com.example.techfix;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

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

        // =====================================================
        // GET LOGGED-IN CUSTOMER ID
        // =====================================================

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
                            "j.status, " +
                            "p.paymentId, " +
                            "p.paymentMethod, " +
                            "p.paymentStatus " +

                            "FROM appointments a " +

                            "LEFT JOIN jobs j " +
                            "ON a.appointmentId = j.appointmentId " +

                            "LEFT JOIN payments p " +
                            "ON a.appointmentId = p.appointmentId " +
                            "AND a.customerId = p.customerId " +

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

                int paymentId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "paymentId"
                                )
                        );

                String paymentMethod =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "paymentMethod"
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
                        productService,
                        category,
                        price,
                        branch,
                        date,
                        time,
                        jobId,
                        technicianId,
                        status,
                        paymentId,
                        paymentMethod,
                        paymentStatus
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
            String status,
            int paymentId,
            String paymentMethod,
            String paymentStatus) {

        // =====================================================
        // MAIN CARD
        // =====================================================

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

        // =====================================================
        // APPOINTMENT NUMBER
        // =====================================================

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

        // =====================================================
        // PRODUCT / SERVICE
        // =====================================================

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

        // =====================================================
        // REPAIR STATUS
        // =====================================================

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

        // =====================================================
        // CATEGORY
        // =====================================================

        TextView categoryText =
                createInfoText(
                        "Category: " +
                                safeText(category)
                );

        card.addView(
                categoryText
        );

        // =====================================================
        // PRICE
        // =====================================================

        TextView priceText =
                createInfoText(
                        "Price: Rs. " +
                                String.format(
                                        Locale.getDefault(),
                                        "%.2f",
                                        price
                                )
                );

        card.addView(
                priceText
        );

        // =====================================================
        // BRANCH
        // =====================================================

        TextView branchText =
                createInfoText(
                        "Branch: " +
                                safeText(branch)
                );

        card.addView(
                branchText
        );

        // =====================================================
        // DATE / TIME
        // =====================================================

        TextView dateTimeText =
                createInfoText(
                        "Date: " +
                                safeText(date) +
                                "    Time: " +
                                safeText(time)
                );

        card.addView(
                dateTimeText
        );

        // =====================================================
        // TECHNICIAN
        // =====================================================

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

        // =====================================================
        // PAYMENT INFORMATION
        // =====================================================

        boolean hasPayment =
                paymentId > 0 &&
                        paymentStatus != null &&
                        !paymentStatus.trim().isEmpty();

        TextView paymentText =
                createInfoText(
                        ""
                );

        paymentText.setTypeface(
                null,
                Typeface.BOLD
        );

        if (!hasPayment) {

            paymentText.setText(
                    "Payment: NOT PAID"
            );

            paymentText.setTextColor(
                    Color.rgb(
                            198,
                            40,
                            40
                    )
            );

        } else {

            paymentText.setText(
                    "Payment: " +
                            paymentStatus +
                            "\nMethod: " +
                            safeText(paymentMethod)
            );

            if (paymentStatus.equals(
                    "PAID"
            )) {

                paymentText.setTextColor(
                        Color.rgb(
                                46,
                                125,
                                50
                        )
                );

            } else {

                paymentText.setTextColor(
                        Color.rgb(
                                230,
                                126,
                                34
                        )
                );
            }
        }

        paymentText.setPadding(
                0,
                12,
                0,
                5
        );

        card.addView(
                paymentText
        );

        // =====================================================
        // MAKE PAYMENT BUTTON
        // =====================================================

        /*
         * Payment should only be available after
         * the repair is FINISHED.
         */

        if (displayStatus.equals("FINISHED") &&
                !hasPayment) {

            Button btnMakePayment =
                    new Button(this);

            btnMakePayment.setText(
                    "Make Payment"
            );

            LinearLayout.LayoutParams buttonParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

            buttonParams.setMargins(
                    0,
                    15,
                    0,
                    0
            );

            btnMakePayment.setLayoutParams(
                    buttonParams
            );

            btnMakePayment.setOnClickListener(v -> {

                Intent intent =
                        new Intent(
                                MyAppointmentsActivity.this,
                                PaymentActivity.class
                        );

                intent.putExtra(
                        "appointmentId",
                        appointmentId
                );

                intent.putExtra(
                        "customerId",
                        customerId
                );

                intent.putExtra(
                        "amount",
                        price
                );

                intent.putExtra(
                        "productService",
                        productService
                );

                startActivity(intent);
            });

            card.addView(
                    btnMakePayment
            );
        }

        // =====================================================
        // ADD CARD
        // =====================================================

        appointmentListContainer.addView(
                card
        );
    }

    // =====================================================
    // SAFE TEXT
    // =====================================================

    private String safeText(String text) {

        if (text == null ||
                text.trim().isEmpty()) {

            return "Not available";
        }

        return text;
    }

    // =====================================================
    // CREATE INFO TEXT
    // =====================================================

    private TextView createInfoText(
            String text) {

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
            String message) {

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