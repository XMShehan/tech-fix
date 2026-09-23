package com.example.techfix;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UpdateServiceActivity extends AppCompatActivity {

    private EditText edtServiceId;
    private EditText edtServiceName;
    private EditText edtDescription;
    private EditText edtPrice;
    private EditText edtDuration;

    private Spinner spinnerCategory;
    private Spinner spinnerStatus;

    private Button btnCancel;
    private Button btnUpdateService;

    private DatabaseHelper databaseHelper;

    private String serviceId;

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // =====================================================
        // KEYBOARD HANDLING
        // =====================================================

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );

        setContentView(
                R.layout.activity_update_service
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

        btnUpdateService =
                findViewById(
                        R.id.btnUpdateService
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
        // GET SERVICE ID
        // =====================================================

        serviceId =
                getIntent().getStringExtra(
                        "serviceId"
                );

        if (serviceId != null &&
                !serviceId.trim().isEmpty()) {

            edtServiceId.setText(
                    serviceId
            );

            // Service ID cannot be changed
            edtServiceId.setEnabled(
                    false
            );

            loadServiceDetails();

        } else {

            Toast.makeText(
                    this,
                    "Service information is missing",
                    Toast.LENGTH_LONG
            ).show();

            finish();
        }

        // =====================================================
        // CANCEL
        // =====================================================

        btnCancel.setOnClickListener(
                v -> finish()
        );

        // =====================================================
        // UPDATE SERVICE
        // =====================================================

        btnUpdateService.setOnClickListener(
                v -> updateService()
        );
    }

    // =========================================================
    // LOAD SERVICE DETAILS
    // =========================================================

    private void loadServiceDetails() {

        SQLiteDatabase db =
                databaseHelper
                        .getReadableDatabase();

        Cursor cursor =
                db.query(
                        "services",

                        new String[]{
                                "serviceName",
                                "category",
                                "description",
                                "price",
                                "duration",
                                "status"
                        },

                        "serviceId = ?",

                        new String[]{
                                serviceId
                        },

                        null,
                        null,
                        null
                );

        if (cursor.moveToFirst()) {

            String serviceName =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "serviceName"
                            )
                    );

            String category =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "category"
                            )
                    );

            String description =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "description"
                            )
                    );

            double price =
                    cursor.getDouble(
                            cursor.getColumnIndexOrThrow(
                                    "price"
                            )
                    );

            String duration =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "duration"
                            )
                    );

            String status =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "status"
                            )
                    );

            edtServiceName.setText(
                    serviceName
            );

            edtDescription.setText(
                    description
            );

            edtPrice.setText(
                    String.valueOf(price)
            );

            edtDuration.setText(
                    duration
            );

            setSpinnerSelection(
                    spinnerCategory,
                    category
            );

            setSpinnerSelection(
                    spinnerStatus,
                    status
            );

        } else {

            Toast.makeText(
                    this,
                    "Service not found",
                    Toast.LENGTH_LONG
            ).show();

            finish();
        }

        cursor.close();
    }

    // =========================================================
    // SET SPINNER SELECTION
    // =========================================================

    private void setSpinnerSelection(
            Spinner spinner,
            String value) {

        if (value == null) {
            return;
        }

        for (int i = 0;
             i < spinner.getCount();
             i++) {

            String item =
                    spinner
                            .getItemAtPosition(i)
                            .toString();

            if (item.equalsIgnoreCase(
                    value.trim()
            )) {

                spinner.setSelection(i);

                break;
            }
        }
    }

    // =========================================================
    // UPDATE SERVICE
    // =========================================================

    private void updateService() {

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

        if (serviceName.isEmpty() ||
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
        // DATABASE UPDATE
        // =====================================================

        SQLiteDatabase db =
                databaseHelper
                        .getWritableDatabase();

        ContentValues values =
                new ContentValues();

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

        int result =
                db.update(
                        "services",
                        values,
                        "serviceId = ?",
                        new String[]{
                                serviceId
                        }
                );

        if (result > 0) {

            Toast.makeText(
                    this,
                    "Service updated successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Service not found",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}