package com.example.techfix;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ManageServicesActivity extends AppCompatActivity {

    Button btnAddService;
    Button btnDeleteService;

    EditText edtSearch;

    LinearLayout serviceContainer;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_manage_services);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
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
        btnAddService = findViewById(R.id.btnAddService);
        btnDeleteService = findViewById(R.id.btnDeleteService);
        edtSearch = findViewById(R.id.edtSearch);
        serviceContainer = findViewById(R.id.serviceContainer);

        // Database
        databaseHelper = new DatabaseHelper(this);

        // Add Service button
        btnAddService.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ManageServicesActivity.this,
                    AddServiceActivity.class
            );

            startActivity(intent);
        });

        // Delete Service button
        btnDeleteService.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ManageServicesActivity.this,
                    DeleteServiceActivity.class
            );

            startActivity(intent);
        });

        // Search services
        edtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count) {

                loadServices(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        loadServices("");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {

            String searchText =
                    edtSearch.getText().toString().trim();

            loadServices(searchText);
        }
    }

    private void loadServices(String searchText) {

        serviceContainer.removeAllViews();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor;

        if (searchText.isEmpty()) {

            cursor = db.rawQuery(
                    "SELECT serviceId, serviceName, description, price, duration, status " +
                            "FROM services",
                    null
            );

        } else {

            cursor = db.rawQuery(
                    "SELECT serviceId, serviceName, description, price, duration, status " +
                            "FROM services " +
                            "WHERE serviceId LIKE ? " +
                            "OR serviceName LIKE ? " +
                            "OR description LIKE ?",
                    new String[]{
                            "%" + searchText + "%",
                            "%" + searchText + "%",
                            "%" + searchText + "%"
                    }
            );
        }

        if (cursor.getCount() == 0) {

            TextView emptyText = new TextView(this);

            if (searchText.isEmpty()) {
                emptyText.setText("No services available");
            } else {
                emptyText.setText("No matching services found");
            }

            emptyText.setTextSize(16);
            emptyText.setTextColor(Color.GRAY);
            emptyText.setPadding(10, 20, 10, 20);

            serviceContainer.addView(emptyText);

        } else {

            while (cursor.moveToNext()) {

                String serviceId =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("serviceId")
                        );

                String serviceName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("serviceName")
                        );

                String description =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("description")
                        );

                double price =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow("price")
                        );

                String duration =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("duration")
                        );

                String status =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("status")
                        );

                // Container for one service
                LinearLayout serviceLayout =
                        new LinearLayout(this);

                serviceLayout.setOrientation(
                        LinearLayout.VERTICAL
                );

                serviceLayout.setPadding(
                        12,
                        15,
                        12,
                        15
                );

                // Service ID
                TextView txtId = new TextView(this);

                txtId.setText(
                        "Service ID: " + serviceId
                );

                txtId.setTextSize(16);
                txtId.setTextColor(Color.BLACK);

                serviceLayout.addView(txtId);

                // Service Name
                TextView txtName = new TextView(this);

                txtName.setText(
                        "Service Name: " + serviceName
                );

                txtName.setTextSize(16);
                txtName.setTextColor(Color.BLACK);

                serviceLayout.addView(txtName);

                // Description
                TextView txtDescription = new TextView(this);

                txtDescription.setText(
                        "Description: " + description
                );

                txtDescription.setTextSize(16);
                txtDescription.setTextColor(Color.BLACK);

                serviceLayout.addView(txtDescription);

                // Price
                TextView txtPrice = new TextView(this);

                txtPrice.setText(
                        "Price: " + price
                );

                txtPrice.setTextSize(16);
                txtPrice.setTextColor(Color.BLACK);

                serviceLayout.addView(txtPrice);

                // Duration
                TextView txtDuration = new TextView(this);

                txtDuration.setText(
                        "Duration: " + duration
                );

                txtDuration.setTextSize(16);
                txtDuration.setTextColor(Color.BLACK);

                serviceLayout.addView(txtDuration);

                // Status
                TextView txtStatus = new TextView(this);

                txtStatus.setText(
                        "Status: " + status
                );

                txtStatus.setTextSize(16);
                txtStatus.setTextColor(Color.BLACK);

                serviceLayout.addView(txtStatus);

                // Update Service button
                Button btnUpdate = new Button(this);

                btnUpdate.setText("Update Service");
                btnUpdate.setTextSize(14);

                LinearLayout.LayoutParams buttonParams =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                buttonParams.setMargins(
                        0,
                        10,
                        0,
                        10
                );

                btnUpdate.setLayoutParams(buttonParams);

                serviceLayout.addView(btnUpdate);

                // Update button click
                btnUpdate.setOnClickListener(v -> {

                    Intent intent = new Intent(
                            ManageServicesActivity.this,
                            UpdateServiceActivity.class
                    );

                    intent.putExtra(
                            "serviceId",
                            serviceId
                    );

                    startActivity(intent);
                });

                serviceContainer.addView(serviceLayout);
            }
        }

        cursor.close();
    }
}