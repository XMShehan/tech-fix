package com.example.techfix;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class CustomerDashboard extends AppCompatActivity {

    private LinearLayout btnViewProducts;
    private LinearLayout btnMyAppointments;
    private LinearLayout btnRepairHistory;
    private LinearLayout btnFeedback;
    private LinearLayout btnLogout;

    private TextView txtWelcome;

    // Logged-in customer information
    private String customerId;
    private String customerName;
    private String customerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_customer_dashboard
        );

        // =====================================================
        // CONNECT UI
        // =====================================================

        btnViewProducts =
                findViewById(R.id.btnViewProducts);

        btnMyAppointments =
                findViewById(R.id.btnMyAppointments);

        btnRepairHistory =
                findViewById(R.id.btnRepairHistory);

        btnFeedback =
                findViewById(R.id.btnFeedback);

        btnLogout =
                findViewById(R.id.btnLogout);

        txtWelcome =
                findViewById(R.id.txtWelcome);

        // =====================================================
        // GET CUSTOMER INFORMATION FROM LOGIN
        // =====================================================

        customerId =
                getIntent().getStringExtra(
                        "customerId"
                );

        customerName =
                getIntent().getStringExtra(
                        "customerName"
                );

        customerEmail =
                getIntent().getStringExtra(
                        "customerEmail"
                );

        // =====================================================
        // DISPLAY CUSTOMER NAME
        // =====================================================

        if (customerName != null &&
                !customerName.trim().isEmpty()) {

            txtWelcome.setText(
                    "Welcome, " +
                            customerName +
                            " 👋"
            );

        } else {

            txtWelcome.setText(
                    "Welcome 👋"
            );
        }

        // =====================================================
        // VIEW PRODUCTS
        // =====================================================

        btnViewProducts.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            CustomerDashboard.this,
                            ProductListActivity.class
                    );

            intent.putExtra(
                    "customerId",
                    customerId
            );

            intent.putExtra(
                    "customerName",
                    customerName
            );

            intent.putExtra(
                    "customerEmail",
                    customerEmail
            );

            startActivity(intent);
        });

        // =====================================================
        // MY APPOINTMENTS
        // =====================================================

        btnMyAppointments.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            CustomerDashboard.this,
                            MyAppointmentsActivity.class
                    );

            intent.putExtra(
                    "customerId",
                    customerId
            );

            intent.putExtra(
                    "customerName",
                    customerName
            );

            intent.putExtra(
                    "customerEmail",
                    customerEmail
            );

            startActivity(intent);
        });

        // =====================================================
        // REPAIR HISTORY
        // =====================================================

        btnRepairHistory.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            CustomerDashboard.this,
                            RepairHistoryActivity.class
                    );

            intent.putExtra(
                    "customerId",
                    customerId
            );

            intent.putExtra(
                    "customerName",
                    customerName
            );

            intent.putExtra(
                    "customerEmail",
                    customerEmail
            );

            startActivity(intent);
        });

        // =====================================================
        // FEEDBACK
        // =====================================================

        btnFeedback.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            CustomerDashboard.this,
                            CustomerFeedbackActivity.class
                    );

            intent.putExtra(
                    "customerId",
                    customerId
            );

            intent.putExtra(
                    "customerName",
                    customerName
            );

            intent.putExtra(
                    "customerEmail",
                    customerEmail
            );

            startActivity(intent);
        });

        // =====================================================
        // LOGOUT
        // =====================================================

        btnLogout.setOnClickListener(v -> {

            showLogoutConfirmation();
        });
    }

    // =====================================================
    // LOGOUT CONFIRMATION
    // =====================================================

    private void showLogoutConfirmation() {

        new AlertDialog.Builder(this)

                .setTitle(
                        "Logout"
                )

                .setMessage(
                        "Are you sure you want to logout?"
                )

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .setPositiveButton(
                        "Logout",
                        (dialog, which) -> logoutUser()
                )

                .show();
    }

    // =====================================================
    // LOGOUT USER
    // =====================================================

    private void logoutUser() {

        Intent intent =
                new Intent(
                        CustomerDashboard.this,
                        LoginActivity.class
                );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }
}