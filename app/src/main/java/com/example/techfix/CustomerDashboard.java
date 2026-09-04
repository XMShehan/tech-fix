package com.example.techfix;

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
    }
}