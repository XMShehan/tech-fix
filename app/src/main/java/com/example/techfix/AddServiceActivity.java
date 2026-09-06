package com.example.techfix;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddServiceActivity extends AppCompatActivity {

    EditText edtServiceId;
    EditText edtServiceName;
    EditText edtDescription;
    EditText edtPrice;
    EditText edtDuration;

    Spinner spinnerStatus;

    Button btnCancel;
    Button btnSaveService;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_service);

        // Find views
        edtServiceId = findViewById(R.id.edtServiceId);
        edtServiceName = findViewById(R.id.edtServiceName);
        edtDescription = findViewById(R.id.edtDescription);
        edtPrice = findViewById(R.id.edtPrice);
        edtDuration = findViewById(R.id.edtDuration);

        spinnerStatus = findViewById(R.id.spinnerStatus);

        btnCancel = findViewById(R.id.btnCancel);
        btnSaveService = findViewById(R.id.btnSaveService);

        // Database
        databaseHelper = new DatabaseHelper(this);

        // Status options
        String[] statusOptions = {
                "Active",
                "Inactive"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                statusOptions
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerStatus.setAdapter(adapter);

        // Cancel button
        btnCancel.setOnClickListener(v -> {
            finish();
        });

        // Save Service button
        btnSaveService.setOnClickListener(v -> {

            String serviceId =
                    edtServiceId.getText().toString().trim();

            String serviceName =
                    edtServiceName.getText().toString().trim();

            String description =
                    edtDescription.getText().toString().trim();

            String priceText =
                    edtPrice.getText().toString().trim();

            String duration =
                    edtDuration.getText().toString().trim();

            String status =
                    spinnerStatus.getSelectedItem().toString();

            // Validation
            if (serviceId.isEmpty() ||
                    serviceName.isEmpty() ||
                    description.isEmpty() ||
                    priceText.isEmpty() ||
                    duration.isEmpty()) {

                Toast.makeText(
                        AddServiceActivity.this,
                        "Please fill all fields",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Convert price to double
            double price;

            try {
                price = Double.parseDouble(priceText);
            } catch (NumberFormatException e) {

                Toast.makeText(
                        AddServiceActivity.this,
                        "Please enter a valid price",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Open database
            SQLiteDatabase db =
                    databaseHelper.getWritableDatabase();

            // Store values
            ContentValues values = new ContentValues();

            values.put("serviceId", serviceId);
            values.put("serviceName", serviceName);
            values.put("description", description);
            values.put("price", price);
            values.put("duration", duration);
            values.put("status", status);

            // Insert service
            long result = db.insert(
                    "services",
                    null,
                    values
            );

            if (result != -1) {

                Toast.makeText(
                        AddServiceActivity.this,
                        "Service added successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        AddServiceActivity.this,
                        "Failed to add service. Service ID may already exist.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}