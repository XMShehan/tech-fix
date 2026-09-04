package com.example.techfix;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ComputerRepairActivity extends AppCompatActivity {

    Button btnSoftwareInstallation;
    Button btnHardwareRepair;
    Button btnWindowsInstallation;
    Button btnComputerUpgrade;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_computer_repair);

        btnSoftwareInstallation = findViewById(R.id.btnSoftwareInstallation);
        btnHardwareRepair = findViewById(R.id.btnHardwareRepair);
        btnWindowsInstallation = findViewById(R.id.btnWindowsInstallation);
        btnComputerUpgrade = findViewById(R.id.btnComputerUpgrade);

        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            finish();
        });
    }
}