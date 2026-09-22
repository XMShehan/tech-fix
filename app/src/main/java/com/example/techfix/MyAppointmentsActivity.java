package com.example.techfix;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MyAppointmentsActivity extends AppCompatActivity {

    private LinearLayout appointmentListContainer;
    private DatabaseHelper databaseHelper;

    private String customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_appointments);

        appointmentListContainer = findViewById(
                R.id.appointmentListContainer
        );

        databaseHelper = new DatabaseHelper(this);

        // Get logged-in customer ID
        customerId = getIntent().getStringExtra("customerId");

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

        if (customerId == null || customerId.trim().isEmpty()) {

            showEmptyMessage("Customer information is missing");

            return;
        }

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

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

                showEmptyMessage("No appointments found");

                return;
            }

            do {

                int appointmentId = cursor.getInt(
                        cursor.getColumnIndexOrThrow("appointmentId")
                );

                String productService = cursor.getString(
                        cursor.getColumnIndexOrThrow("productService")
                );

                String category = cursor.getString(
                        cursor.getColumnIndexOrThrow("category")
                );

                double price = cursor.getDouble(
                        cursor.getColumnIndexOrThrow("price")
                );

                String branch = cursor.getString(
                        cursor.getColumnIndexOrThrow("branch")
                );

                String date = cursor.getString(
                        cursor.getColumnIndexOrThrow("appointmentDate")
                );

                String time = cursor.getString(
                        cursor.getColumnIndexOrThrow("appointmentTime")
                );

                int jobId = cursor.getInt(
                        cursor.getColumnIndexOrThrow("jobId")
                );

                String technicianId = cursor.getString(
                        cursor.getColumnIndexOrThrow("technicianId")
                );

                String status = cursor.getString(
                        cursor.getColumnIndexOrThrow("status")
                );

                int paymentId = cursor.getInt(
                        cursor.getColumnIndexOrThrow("paymentId")
                );

                String paymentMethod = cursor.getString(
                        cursor.getColumnIndexOrThrow("paymentMethod")
                );

                String paymentStatus = cursor.getString(
                        cursor.getColumnIndexOrThrow("paymentStatus")
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

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);

        card.setPadding(
                dp(20),
                dp(20),
                dp(20),
                dp(20)
        );

        GradientDrawable cardBackground = new GradientDrawable();

        cardBackground.setColor(Color.WHITE);

        cardBackground.setCornerRadius(
                dp(18)
        );

        cardBackground.setStroke(
                dp(1),
                Color.rgb(226, 234, 240)
        );

        card.setBackground(cardBackground);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                0,
                0,
                dp(16)
        );

        card.setLayoutParams(cardParams);

        // =====================================================
        // APPOINTMENT NUMBER
        // =====================================================

        TextView appointmentText = new TextView(this);

        appointmentText.setText(
                "Appointment #" + appointmentId
        );

        appointmentText.setTextSize(20);

        appointmentText.setTextColor(
                Color.rgb(38, 50, 56)
        );

        appointmentText.setTypeface(
                null,
                Typeface.BOLD
        );

        card.addView(appointmentText);

        // =====================================================
        // PRODUCT
        // =====================================================

        TextView productText = new TextView(this);

        productText.setText(
                safeText(productService)
        );

        productText.setTextSize(17);

        productText.setTextColor(
                Color.rgb(25, 118, 210)
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
                dp(8),
                0,
                0
        );

        productText.setLayoutParams(productParams);

        card.addView(productText);

        // =====================================================
        // REPAIR STATUS
        // =====================================================

        String displayStatus;

        if (status == null || status.trim().isEmpty()) {

            displayStatus = "WAITING FOR ASSIGNMENT";

        } else {

            displayStatus = status.toUpperCase(Locale.getDefault());
        }

        TextView statusText = new TextView(this);

        statusText.setText(
                "Repair Status: " + displayStatus
        );

        statusText.setTextSize(14);

        statusText.setTypeface(
                null,
                Typeface.BOLD
        );

        statusText.setPadding(
                dp(12),
                dp(10),
                dp(12),
                dp(10)
        );

        GradientDrawable statusBackground = new GradientDrawable();

        if (displayStatus.equals("FINISHED")) {

            statusText.setTextColor(
                    Color.rgb(46, 125, 50)
            );

            statusBackground.setColor(
                    Color.rgb(232, 245, 233)
            );

        } else if (displayStatus.equals("ONGOING")) {

            statusText.setTextColor(
                    Color.rgb(239, 108, 0)
            );

            statusBackground.setColor(
                    Color.rgb(255, 243, 224)
            );

        } else if (displayStatus.equals("STARTED")) {

            statusText.setTextColor(
                    Color.rgb(25, 118, 210)
            );

            statusBackground.setColor(
                    Color.rgb(227, 242, 253)
            );

        } else {

            statusText.setTextColor(
                    Color.rgb(84, 110, 122)
            );

            statusBackground.setColor(
                    Color.rgb(241, 245, 248)
            );
        }

        statusBackground.setCornerRadius(
                dp(10)
        );

        statusText.setBackground(statusBackground);

        LinearLayout.LayoutParams statusParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        statusParams.setMargins(
                0,
                dp(14),
                0,
                dp(4)
        );

        statusText.setLayoutParams(statusParams);

        card.addView(statusText);

        // =====================================================
        // CATEGORY
        // =====================================================

        TextView categoryText = createInfoText(
                "Category: " + safeText(category)
        );

        card.addView(categoryText);

        // =====================================================
        // PRICE
        // =====================================================

        TextView priceText = createInfoText(
                "Price: Rs. " +
                        String.format(
                                Locale.getDefault(),
                                "%.2f",
                                price
                        )
        );

        card.addView(priceText);

        // =====================================================
        // BRANCH
        // =====================================================

        TextView branchText = createInfoText(
                "Branch: " + safeText(branch)
        );

        card.addView(branchText);

        // =====================================================
        // DATE / TIME
        // =====================================================

        TextView dateTimeText = createInfoText(
                "Date: " +
                        safeText(date) +
                        "    Time: " +
                        safeText(time)
        );

        card.addView(dateTimeText);

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
                    "Technician ID: " + technicianId;
        }

        TextView technicianTextView =
                createInfoText(technicianText);

        card.addView(technicianTextView);

        // =====================================================
        // DIVIDER
        // =====================================================

        View divider = new View(this);

        divider.setBackgroundColor(
                Color.rgb(230, 236, 240)
        );

        LinearLayout.LayoutParams dividerParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(1)
                );

        dividerParams.setMargins(
                0,
                dp(16),
                0,
                dp(12)
        );

        divider.setLayoutParams(dividerParams);

        card.addView(divider);

        // =====================================================
        // PAYMENT INFORMATION
        // =====================================================

        boolean hasPayment =
                paymentId > 0 &&
                        paymentStatus != null &&
                        !paymentStatus.trim().isEmpty();

        TextView paymentText = new TextView(this);

        paymentText.setTextSize(14);

        paymentText.setTypeface(
                null,
                Typeface.BOLD
        );

        if (!hasPayment) {

            paymentText.setText(
                    "Payment: NOT PAID"
            );

            paymentText.setTextColor(
                    Color.rgb(198, 40, 40)
            );

        } else {

            paymentText.setText(
                    "Payment: " +
                            paymentStatus +
                            "\nMethod: " +
                            safeText(paymentMethod)
            );

            if ("PAID".equalsIgnoreCase(paymentStatus)) {

                paymentText.setTextColor(
                        Color.rgb(46, 125, 50)
                );

            } else {

                paymentText.setTextColor(
                        Color.rgb(239, 108, 0)
                );
            }
        }

        paymentText.setPadding(
                0,
                dp(4),
                0,
                dp(4)
        );

        card.addView(paymentText);

        // =====================================================
        // MAKE PAYMENT BUTTON
        // =====================================================

        /*
         * Payment is available only after
         * the repair is FINISHED.
         */

        if (displayStatus.equals("FINISHED") &&
                !hasPayment) {

            Button btnMakePayment = new Button(this);

            btnMakePayment.setText("MAKE PAYMENT");

            btnMakePayment.setTextSize(14);

            btnMakePayment.setTextColor(Color.WHITE);

            btnMakePayment.setTypeface(
                    null,
                    Typeface.BOLD
            );

            btnMakePayment.setAllCaps(false);

            btnMakePayment.setBackgroundResource(
                    R.drawable.bg_login_button
            );

            LinearLayout.LayoutParams buttonParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            dp(52)
                    );

            buttonParams.setMargins(
                    0,
                    dp(14),
                    0,
                    0
            );

            btnMakePayment.setLayoutParams(buttonParams);

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

            card.addView(btnMakePayment);
        }

        // =====================================================
        // ADD CARD
        // =====================================================

        appointmentListContainer.addView(card);
    }

    // =====================================================
    // CREATE INFO TEXT
    // =====================================================

    private TextView createInfoText(String text) {

        TextView textView = new TextView(this);

        textView.setText(text);

        textView.setTextSize(14);

        textView.setTextColor(
                Color.rgb(96, 125, 139)
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                0,
                dp(6),
                0,
                0
        );

        textView.setLayoutParams(params);

        return textView;
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
    // EMPTY MESSAGE
    // =====================================================

    private void showEmptyMessage(String message) {

        LinearLayout emptyCard = new LinearLayout(this);

        emptyCard.setOrientation(
                LinearLayout.VERTICAL
        );

        emptyCard.setGravity(
                Gravity.CENTER
        );

        emptyCard.setPadding(
                dp(20),
                dp(40),
                dp(20),
                dp(40)
        );

        GradientDrawable emptyBackground =
                new GradientDrawable();

        emptyBackground.setColor(Color.WHITE);

        emptyBackground.setCornerRadius(
                dp(18)
        );

        emptyBackground.setStroke(
                dp(1),
                Color.rgb(226, 234, 240)
        );

        emptyCard.setBackground(
                emptyBackground
        );

        TextView emptyText =
                new TextView(this);

        emptyText.setText(message);

        emptyText.setTextSize(16);

        emptyText.setTextColor(
                Color.rgb(96, 125, 139)
        );

        emptyText.setGravity(
                Gravity.CENTER
        );

        emptyCard.addView(emptyText);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        emptyCard.setLayoutParams(params);

        appointmentListContainer.addView(
                emptyCard
        );
    }

    // =====================================================
    // DP HELPER
    // =====================================================

    private int dp(int value) {

        return (int) (
                value *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }
}