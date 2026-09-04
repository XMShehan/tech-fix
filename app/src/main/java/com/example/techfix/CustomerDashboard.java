package com.example.techfix;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CustomerDashboard extends AppCompatActivity {

    Button btnComputerRepair;
    Button btnMobileRepair;
    Button btnMyAppointments;
    Button btnRepairHistory;
    Button btnFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customer_dashboard);

        btnComputerRepair = findViewById(R.id.btnComputerRepair);
        btnMobileRepair = findViewById(R.id.btnMobileRepair);
        btnMyAppointments = findViewById(R.id.btnMyAppointments);
        btnRepairHistory = findViewById(R.id.btnRepairHistory);
        btnFeedback = findViewById(R.id.btnFeedback);

        btnComputerRepair.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerDashboard.this, ComputerRepairActivity.class);
            startActivity(intent);
        });

        btnMobileRepair.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerDashboard.this, MobileRepairActivity.class);
            startActivity(intent);
        });
    }
}