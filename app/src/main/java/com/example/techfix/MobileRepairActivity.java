package com.example.techfix;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MobileRepairActivity extends AppCompatActivity {

    Button btnDisplayReplacement;
    Button btnBatteryReplacement;
    Button btnChargingPort;
    Button btnCameraRepair;
    Button btnSpeakerRepair;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mobile_repair);

        btnDisplayReplacement = findViewById(R.id.btnDisplayReplacement);
        btnBatteryReplacement = findViewById(R.id.btnBatteryReplacement);
        btnChargingPort = findViewById(R.id.btnChargingPort);
        btnCameraRepair = findViewById(R.id.btnCameraRepair);
        btnSpeakerRepair = findViewById(R.id.btnSpeakerRepair);

        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            finish();
        });
    }
}