package com.example.techfix;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class AppointmentActivity extends AppCompatActivity {

    RadioButton radioAutoDetect;
    RadioButton radioManual;

    Spinner spinnerBranch;

    EditText edtProductService;
    EditText edtCategory;
    EditText edtPrice;
    EditText edtDate;
    EditText edtTime;

    Button btnAddPhoto;
    Button btnCancel;
    Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_appointment);

        radioAutoDetect = findViewById(R.id.radioAutoDetect);
        radioManual = findViewById(R.id.radioManual);

        spinnerBranch = findViewById(R.id.spinnerBranch);

        edtProductService = findViewById(R.id.edtProductService);
        edtCategory = findViewById(R.id.edtCategory);
        edtPrice = findViewById(R.id.edtPrice);
        edtDate = findViewById(R.id.edtDate);
        edtTime = findViewById(R.id.edtTime);

        btnAddPhoto = findViewById(R.id.btnAddPhoto);
        btnCancel = findViewById(R.id.btnCancel);
        btnConfirm = findViewById(R.id.btnConfirm);

        // Get product details from ProductListActivity
        String productName = getIntent().getStringExtra("productName");
        String category = getIntent().getStringExtra("category");
        double price = getIntent().getDoubleExtra("price", 0);
        int quantity = getIntent().getIntExtra("quantity", 0);

        // Display product details
        if (productName != null) {
            edtProductService.setText(productName);
        }

        if (category != null) {
            edtCategory.setText(category);
        }

        if (price > 0) {
            edtPrice.setText("Rs. " + String.format("%.2f", price));
        }

        // Auto Detect option
        radioAutoDetect.setOnClickListener(v -> {
            radioAutoDetect.setChecked(true);
            radioManual.setChecked(false);
            spinnerBranch.setEnabled(false);
        });

        // Manual selection option
        radioManual.setOnClickListener(v -> {
            radioManual.setChecked(true);
            radioAutoDetect.setChecked(false);
            spinnerBranch.setEnabled(true);
        });

        // Add photo
        btnAddPhoto.setOnClickListener(v -> {
            // Photo functionality will be added later
        });

        // Cancel
        btnCancel.setOnClickListener(v -> {
            finish();
        });

        // Confirm
        btnConfirm.setOnClickListener(v -> {
            // Appointment confirmation will be added later
        });

        // Initially Auto Detect is selected
        radioAutoDetect.setChecked(true);
        radioManual.setChecked(false);
        spinnerBranch.setEnabled(false);
    }
}