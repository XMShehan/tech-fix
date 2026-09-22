package com.example.techfix;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class AdminPaymentsActivity extends AppCompatActivity {

    private LinearLayout paymentListContainer;
    private EditText edtSearchPayment;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_admin_payments
        );

        // =====================================================
        // CONNECT UI
        // =====================================================

        paymentListContainer =
                findViewById(
                        R.id.paymentListContainer
                );

        edtSearchPayment =
                findViewById(
                        R.id.edtSearchPayment
                );

        databaseHelper =
                new DatabaseHelper(this);

        // =====================================================
        // SEARCH
        // =====================================================

        edtSearchPayment.addTextChangedListener(
                new android.text.TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        loadPayments(
                                s.toString().trim()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            android.text.Editable s) {
                    }
                }
        );

        loadPayments("");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {

            loadPayments(
                    edtSearchPayment == null
                            ? ""
                            : edtSearchPayment
                              .getText()
                              .toString()
                              .trim()
            );
        }
    }

    // =====================================================
    // LOAD PAYMENTS
    // =====================================================

    private void loadPayments(
            String searchText) {

        paymentListContainer.removeAllViews();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = null;

        try {

            /*
             * Get payment details together with:
             * - customer name
             * - appointment information
             */

            String query =
                    "SELECT " +
                            "p.paymentId, " +
                            "p.appointmentId, " +
                            "p.customerId, " +
                            "p.amount, " +
                            "p.paymentMethod, " +
                            "p.paymentStatus, " +
                            "p.paymentDate, " +
                            "c.customerName, " +
                            "a.productService " +

                            "FROM payments p " +

                            "LEFT JOIN customers c " +
                            "ON p.customerId = c.customerId " +

                            "LEFT JOIN appointments a " +
                            "ON p.appointmentId = a.appointmentId " +

                            "ORDER BY p.paymentId DESC";

            cursor =
                    db.rawQuery(
                            query,
                            null
                    );

            boolean found = false;

            if (cursor.moveToFirst()) {

                do {

                    int paymentId =
                            cursor.getInt(
                                    cursor.getColumnIndexOrThrow(
                                            "paymentId"
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

                    double amount =
                            cursor.getDouble(
                                    cursor.getColumnIndexOrThrow(
                                            "amount"
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

                    String paymentDate =
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow(
                                            "paymentDate"
                                    )
                            );

                    String customerName =
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow(
                                            "customerName"
                                    )
                            );

                    String productService =
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow(
                                            "productService"
                                    )
                            );

                    if (customerName == null ||
                            customerName.trim().isEmpty()) {

                        customerName =
                                "Customer #" +
                                        customerId;
                    }

                    if (productService == null ||
                            productService.trim().isEmpty()) {

                        productService =
                                "Repair Service";
                    }

                    // =====================================================
                    // SEARCH FILTER
                    // =====================================================

                    if (!matchesSearch(
                            searchText,
                            paymentId,
                            appointmentId,
                            customerId,
                            customerName,
                            productService,
                            paymentMethod,
                            paymentStatus
                    )) {

                        continue;
                    }

                    found = true;

                    createPaymentCard(
                            paymentId,
                            appointmentId,
                            customerId,
                            amount,
                            paymentMethod,
                            paymentStatus,
                            paymentDate,
                            customerName,
                            productService
                    );

                } while (cursor.moveToNext());
            }

            if (!found) {

                if (searchText.isEmpty()) {

                    showEmptyMessage(
                            "No payment records found"
                    );

                } else {

                    showEmptyMessage(
                            "No payments match your search"
                    );
                }
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // =====================================================
    // SEARCH MATCHING
    // =====================================================

    private boolean matchesSearch(
            String searchText,
            int paymentId,
            int appointmentId,
            int customerId,
            String customerName,
            String productService,
            String paymentMethod,
            String paymentStatus) {

        if (searchText == null ||
                searchText.trim().isEmpty()) {

            return true;
        }

        String search =
                searchText.toLowerCase(
                        Locale.getDefault()
                );

        return String.valueOf(paymentId)
                .contains(search)

                || String.valueOf(appointmentId)
                .contains(search)

                || String.valueOf(customerId)
                .contains(search)

                || customerName.toLowerCase(
                Locale.getDefault()
        ).contains(search)

                || productService.toLowerCase(
                Locale.getDefault()
        ).contains(search)

                || paymentMethod.toLowerCase(
                Locale.getDefault()
        ).contains(search)

                || paymentStatus.toLowerCase(
                Locale.getDefault()
        ).contains(search);
    }

    // =====================================================
    // CREATE PAYMENT CARD
    // =====================================================

    private void createPaymentCard(
            int paymentId,
            int appointmentId,
            int customerId,
            double amount,
            String paymentMethod,
            String paymentStatus,
            String paymentDate,
            String customerName,
            String productService) {

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
        // PAYMENT NUMBER
        // =====================================================

        TextView paymentTitle =
                new TextView(this);

        paymentTitle.setText(
                "Payment #" +
                        paymentId
        );

        paymentTitle.setTextSize(
                21
        );

        paymentTitle.setTypeface(
                null,
                Typeface.BOLD
        );

        card.addView(
                paymentTitle
        );

        // =====================================================
        // CUSTOMER
        // =====================================================

        card.addView(
                createInfoText(
                        "Customer: " +
                                customerName
                )
        );

        card.addView(
                createInfoText(
                        "Customer ID: " +
                                customerId
                )
        );

        // =====================================================
        // APPOINTMENT
        // =====================================================

        card.addView(
                createInfoText(
                        "Appointment: #" +
                                appointmentId
                )
        );

        // =====================================================
        // SERVICE
        // =====================================================

        card.addView(
                createInfoText(
                        "Service: " +
                                productService
                )
        );

        // =====================================================
        // AMOUNT
        // =====================================================

        card.addView(
                createInfoText(
                        "Amount: Rs. " +
                                String.format(
                                        Locale.getDefault(),
                                        "%.2f",
                                        amount
                                )
                )
        );

        // =====================================================
        // METHOD
        // =====================================================

        card.addView(
                createInfoText(
                        "Method: " +
                                paymentMethod
                )
        );

        // =====================================================
        // STATUS
        // =====================================================

        TextView statusText =
                createInfoText(
                        "Payment Status: " +
                                paymentStatus
                );

        statusText.setTypeface(
                null,
                Typeface.BOLD
        );

        if ("PAID".equals(
                paymentStatus
        )) {

            statusText.setTextColor(
                    Color.rgb(
                            46,
                            125,
                            50
                    )
            );

        } else {

            statusText.setTextColor(
                    Color.rgb(
                            230,
                            126,
                            34
                    )
            );
        }

        card.addView(
                statusText
        );

        // =====================================================
        // DATE
        // =====================================================

        card.addView(
                createInfoText(
                        "Date: " +
                                paymentDate
                )
        );

        // =====================================================
        // CONFIRM PAYMENT
        // =====================================================

        /*
         * Only pending payments can be confirmed.
         */

        if ("PENDING".equals(
                paymentStatus
        )) {

            Button btnConfirm =
                    new Button(this);

            btnConfirm.setText(
                    "Confirm Payment"
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

            btnConfirm.setLayoutParams(
                    buttonParams
            );

            btnConfirm.setOnClickListener(v -> {

                showConfirmDialog(
                        paymentId,
                        customerName,
                        amount
                );
            });

            card.addView(
                    btnConfirm
            );
        }

        paymentListContainer.addView(
                card
        );
    }

    // =====================================================
    // CONFIRMATION DIALOG
    // =====================================================

    private void showConfirmDialog(
            int paymentId,
            String customerName,
            double amount) {

        new AlertDialog.Builder(this)

                .setTitle(
                        "Confirm Payment"
                )

                .setMessage(
                        "Are you sure you want to mark this payment as PAID?\n\n" +
                                "Customer: " +
                                customerName +
                                "\nAmount: Rs. " +
                                String.format(
                                        Locale.getDefault(),
                                        "%.2f",
                                        amount
                                )
                )

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .setPositiveButton(
                        "Confirm",
                        (dialog, which) -> {

                            confirmPayment(
                                    paymentId
                            );
                        }
                )

                .show();
    }

    // =====================================================
    // CONFIRM PAYMENT
    // =====================================================

    private void confirmPayment(
            int paymentId) {

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                "paymentStatus",
                "PAID"
        );

        int rowsUpdated =
                db.update(
                        "payments",
                        values,
                        "paymentId = ? " +
                                "AND paymentStatus = ?",
                        new String[]{
                                String.valueOf(
                                        paymentId
                                ),
                                "PENDING"
                        }
                );

        if (rowsUpdated > 0) {

            android.widget.Toast.makeText(
                    this,
                    "Payment confirmed successfully",
                    android.widget.Toast.LENGTH_LONG
            ).show();

            loadPayments(
                    edtSearchPayment
                            .getText()
                            .toString()
                            .trim()
            );

        } else {

            android.widget.Toast.makeText(
                    this,
                    "Payment could not be confirmed",
                    android.widget.Toast.LENGTH_LONG
            ).show();
        }
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

        paymentListContainer.addView(
                emptyText
        );
    }
}