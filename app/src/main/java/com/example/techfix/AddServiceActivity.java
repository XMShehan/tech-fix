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

    private EditText edtServiceId;
    private EditText edtServiceName;
    private EditText edtDescription;
    private EditText edtPrice;
    private EditText edtDuration;

    private Spinner spinnerCategory;
    private Spinner spinnerStatus;

    private Button btnCancel;
    private Button btnSaveService;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_add_service
        );

        // =====================================================
        // FIND VIEWS
        // =====================================================

        edtServiceId =
                findViewById(
                        R.id.edtServiceId
                );

        edtServiceName =
                findViewById(
                        R.id.edtServiceName
                );

        edtDescription =
                findViewById(
                        R.id.edtDescription
                );

        edtPrice =
                findViewById(
                        R.id.edtPrice
                );

        edtDuration =
                findViewById(
                        R.id.edtDuration
                );

        spinnerCategory =
                findViewById(
                        R.id.spinnerCategory
                );

        spinnerStatus =
                findViewById(
                        R.id.spinnerStatus
                );

        btnCancel =
                findViewById(
                        R.id.btnCancel
                );

        btnSaveService =
                findViewById(
                        R.id.btnSaveService
                );

        // =====================================================
        // DATABASE
        // =====================================================

        databaseHelper =
                new DatabaseHelper(this);

        // =====================================================
        // CATEGORY OPTIONS
        // =====================================================

        String[] categoryOptions = {
                "Mobile",
                "Computer",
                "Other"
        };

        ArrayAdapter<String> categoryAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        categoryOptions
                );

        categoryAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerCategory.setAdapter(
                categoryAdapter
        );

        // =====================================================
        // STATUS OPTIONS
        // =====================================================

        String[] statusOptions = {
                "Active",
                "Inactive"
        };

        ArrayAdapter<String> statusAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        statusOptions
                );

        statusAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerStatus.setAdapter(
                statusAdapter
        );

        // =====================================================
        // CANCEL
        // =====================================================

        btnCancel.setOnClickListener(
                v -> finish()
        );

        // =====================================================
        // SAVE SERVICE
        // =====================================================

        btnSaveService.setOnClickListener(
                v -> saveService()
        );
    }

    // =========================================================
    // SAVE SERVICE
    // =========================================================

    private void saveService() {

        String serviceId =
                edtServiceId
                        .getText()
                        .toString()
                        .trim();

        String serviceName =
                edtServiceName
                        .getText()
                        .toString()
                        .trim();

        String description =
                edtDescription
                        .getText()
                        .toString()
                        .trim();

        String priceText =
                edtPrice
                        .getText()
                        .toString()
                        .trim();

        String duration =
                edtDuration
                        .getText()
                        .toString()
                        .trim();

        String category =
                spinnerCategory
                        .getSelectedItem()
                        .toString();

        String status =
                spinnerStatus
                        .getSelectedItem()
                        .toString();

        // =====================================================
        // VALIDATION
        // =====================================================

        if (serviceId.isEmpty() ||
                serviceName.isEmpty() ||
                description.isEmpty() ||
                priceText.isEmpty() ||
                duration.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // =====================================================
        // PRICE VALIDATION
        // =====================================================

        double price;

        try {

            price =
                    Double.parseDouble(
                            priceText
                    );

            if (price <= 0) {

                Toast.makeText(
                        this,
                        "Price must be greater than 0",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Please enter a valid price",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // =====================================================
        // DATABASE
        // =====================================================

        SQLiteDatabase db =
                databaseHelper
                        .getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                "serviceId",
                serviceId
        );

        values.put(
                "serviceName",
                serviceName
        );

        values.put(
                "category",
                category
        );

        values.put(
                "description",
                description
        );

        values.put(
                "price",
                price
        );

        values.put(
                "duration",
                duration
        );

        values.put(
                "status",
                status
        );

        // =====================================================
        // INSERT
        // =====================================================

        long result =
                db.insert(
                        "services",
                        null,
                        values
                );

        if (result != -1) {

            Toast.makeText(
                    this,
                    "Service added successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Failed to add service. Service ID may already exist.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}