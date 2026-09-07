package com.example.techfix;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CustomerDashboard extends AppCompatActivity {

    private Button btnViewProducts;
    private Button btnMyAppointments;
    private Button btnRepairHistory;
    private Button btnFeedback;

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
    }
}