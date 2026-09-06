package com.example.techfix;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UpdateServiceActivity extends AppCompatActivity {

    EditText edtServiceId;
    EditText edtServiceName;
    EditText edtDescription;
    EditText edtPrice;
    EditText edtDuration;

    Spinner spinnerStatus;

    Button btnCancel;
    Button btnUpdateService;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_update_service);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        // Find views
        edtServiceId = findViewById(R.id.edtServiceId);
        edtServiceName = findViewById(R.id.edtServiceName);
        edtDescription = findViewById(R.id.edtDescription);
        edtPrice = findViewById(R.id.edtPrice);
        edtDuration = findViewById(R.id.edtDuration);

        spinnerStatus = findViewById(R.id.spinnerStatus);

        btnCancel = findViewById(R.id.btnCancel);
        btnUpdateService = findViewById(R.id.btnUpdateService);

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

        // Get Service ID from Manage Services page
        String serviceId = getIntent().getStringExtra("serviceId");

        if (serviceId != null) {
            edtServiceId.setText(serviceId);
        }

        // Cancel button
        btnCancel.setOnClickListener(v -> {
            finish();
        });

        // Update Service button
        btnUpdateService.setOnClickListener(v -> {

            String id = edtServiceId.getText().toString().trim();
            String serviceName = edtServiceName.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();
            String priceText = edtPrice.getText().toString().trim();
            String duration = edtDuration.getText().toString().trim();
            String status = spinnerStatus.getSelectedItem().toString();

            // Validation
            if (id.isEmpty() ||
                    serviceName.isEmpty() ||
                    description.isEmpty() ||
                    priceText.isEmpty() ||
                    duration.isEmpty()) {

                Toast.makeText(
                        UpdateServiceActivity.this,
                        "Please fill all fields",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            double price;

            try {
                price = Double.parseDouble(priceText);
            } catch (NumberFormatException e) {

                Toast.makeText(
                        UpdateServiceActivity.this,
                        "Please enter a valid price",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Update database
            SQLiteDatabase db =
                    databaseHelper.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put("serviceName", serviceName);
            values.put("description", description);
            values.put("price", price);
            values.put("duration", duration);
            values.put("status", status);

            int result = db.update(
                    "services",
                    values,
                    "serviceId = ?",
                    new String[]{id}
            );

            if (result > 0) {

                Toast.makeText(
                        UpdateServiceActivity.this,
                        "Service updated successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        UpdateServiceActivity.this,
                        "Service not found",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}