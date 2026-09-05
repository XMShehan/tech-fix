package com.example.techfix;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CustomerDashboard extends AppCompatActivity {

    Button btnViewProducts;
    Button btnMyAppointments;
    Button btnRepairHistory;
    Button btnFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customer_dashboard);

        btnViewProducts = findViewById(R.id.btnViewProducts);
        btnMyAppointments = findViewById(R.id.btnMyAppointments);
        btnRepairHistory = findViewById(R.id.btnRepairHistory);
        btnFeedback = findViewById(R.id.btnFeedback);

        // View Products
        btnViewProducts.setOnClickListener(v -> {

            Intent intent = new Intent(
                    CustomerDashboard.this,
                    ProductListActivity.class
            );

            startActivity(intent);
        });

        // My Appointments
        btnMyAppointments.setOnClickListener(v -> {

            Intent intent = new Intent(
                    CustomerDashboard.this,
                    MyAppointmentsActivity.class
            );

            startActivity(intent);
        });

        // Repair History
        btnRepairHistory.setOnClickListener(v -> {
            // Repair History will be added later
        });

        // Feedback
        btnFeedback.setOnClickListener(v -> {

            Intent intent = new Intent(
                    CustomerDashboard.this,
                    FeedbackActivity.class
            );

            startActivity(intent);
        });
    }
}