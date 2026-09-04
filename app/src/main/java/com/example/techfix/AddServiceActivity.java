package com.example.techfix;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class AddServiceActivity extends AppCompatActivity {

    EditText edtServiceName;
    EditText edtDescription;
    EditText edtPrice;
    EditText edtDuration;

    Spinner spinnerStatus;

    Button btnCancel;
    Button btnSaveService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_service);

        edtServiceName = findViewById(R.id.edtServiceName);
        edtDescription = findViewById(R.id.edtDescription);
        edtPrice = findViewById(R.id.edtPrice);
        edtDuration = findViewById(R.id.edtDuration);

        spinnerStatus = findViewById(R.id.spinnerStatus);

        btnCancel = findViewById(R.id.btnCancel);
        btnSaveService = findViewById(R.id.btnSaveService);

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

        // Save button
        btnSaveService.setOnClickListener(v -> {

            String serviceName = edtServiceName.getText().toString();
            String description = edtDescription.getText().toString();
            String price = edtPrice.getText().toString();
            String duration = edtDuration.getText().toString();
            String status = spinnerStatus.getSelectedItem().toString();

            // For now, just close the page
            // We will connect this to the service list next.
            finish();
        });
    }
}