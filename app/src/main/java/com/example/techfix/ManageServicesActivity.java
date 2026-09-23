package com.example.techfix;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ManageServicesActivity extends AppCompatActivity {

    private Button btnAddService;
    private Button btnDeleteService;

    private EditText edtSearch;

    private LinearLayout serviceContainer;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(
                R.layout.activity_manage_services
        );

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

        // =====================================================
        // CONNECT UI
        // =====================================================

        btnAddService =
                findViewById(
                        R.id.btnAddService
                );

        btnDeleteService =
                findViewById(
                        R.id.btnDeleteService
                );

        edtSearch =
                findViewById(
                        R.id.edtSearch
                );

        serviceContainer =
                findViewById(
                        R.id.serviceContainer
                );

        // =====================================================
        // DATABASE
        // =====================================================

        databaseHelper =
                new DatabaseHelper(this);

        // =====================================================
        // ADD SERVICE
        // =====================================================

        btnAddService.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    ManageServicesActivity.this,
                                    AddServiceActivity.class
                            );

                    startActivity(intent);
                }
        );

        // =====================================================
        // DELETE SERVICE
        // =====================================================

        btnDeleteService.setOnClickListener(
                v -> showDeleteServiceDialog()
        );

        // =====================================================
        // SEARCH
        // =====================================================

        edtSearch.addTextChangedListener(
                new TextWatcher() {

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

                        loadServices(
                                s.toString().trim()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );

        // =====================================================
        // INITIAL LOAD
        // =====================================================

        loadServices("");
    }

    // =========================================================
    // ON RESUME
    // =========================================================

    @Override
    protected void onResume() {

        super.onResume();

        if (databaseHelper != null &&
                edtSearch != null) {

            loadServices(
                    edtSearch
                            .getText()
                            .toString()
                            .trim()
            );
        }
    }

    // =========================================================
    // LOAD SERVICES
    // =========================================================

    private void loadServices(
            String searchText) {

        serviceContainer.removeAllViews();

        SQLiteDatabase db =
                databaseHelper
                        .getReadableDatabase();

        Cursor cursor;

        if (searchText == null ||
                searchText.trim().isEmpty()) {

            cursor =
                    db.rawQuery(
                            "SELECT serviceId, serviceName, category, " +
                                    "description, price, duration, status " +
                                    "FROM services " +
                                    "ORDER BY serviceId",
                            null
                    );

        } else {

            String search =
                    "%" +
                            searchText.trim() +
                            "%";

            cursor =
                    db.rawQuery(
                            "SELECT serviceId, serviceName, category, " +
                                    "description, price, duration, status " +
                                    "FROM services " +
                                    "WHERE serviceId LIKE ? " +
                                    "OR serviceName LIKE ? " +
                                    "OR category LIKE ? " +
                                    "OR description LIKE ? " +
                                    "OR status LIKE ? " +
                                    "ORDER BY serviceId",

                            new String[]{
                                    search,
                                    search,
                                    search,
                                    search,
                                    search
                            }
                    );
        }

        // =====================================================
        // EMPTY RESULT
        // =====================================================

        if (cursor.getCount() == 0) {

            TextView emptyText =
                    new TextView(this);

            if (searchText == null ||
                    searchText.trim().isEmpty()) {

                emptyText.setText(
                        "No services available"
                );

            } else {

                emptyText.setText(
                        "No matching services found"
                );
            }

            emptyText.setTextSize(16);

            emptyText.setTextColor(
                    Color.rgb(
                            96,
                            96,
                            96
                    )
            );

            emptyText.setPadding(
                    10,
                    30,
                    10,
                    30
            );

            serviceContainer.addView(
                    emptyText
            );

            cursor.close();

            return;
        }

        // =====================================================
        // DISPLAY SERVICES
        // =====================================================

        while (cursor.moveToNext()) {

            String serviceId =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "serviceId"
                            )
                    );

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

            // =================================================
            // SERVICE CONTAINER
            // =================================================

            LinearLayout serviceLayout =
                    new LinearLayout(this);

            serviceLayout.setOrientation(
                    LinearLayout.VERTICAL
            );

            serviceLayout.setPadding(
                    16,
                    18,
                    16,
                    18
            );

            // =================================================
            // SERVICE ID
            // =================================================

            TextView txtId =
                    new TextView(this);

            txtId.setText(
                    "Service ID: " +
                            serviceId
            );

            txtId.setTextSize(15);

            txtId.setTextColor(
                    Color.DKGRAY
            );

            serviceLayout.addView(
                    txtId
            );

            // =================================================
            // SERVICE NAME
            // =================================================

            TextView txtName =
                    new TextView(this);

            txtName.setText(
                    "Service Name: " +
                            serviceName
            );

            txtName.setTextSize(18);

            txtName.setTextColor(
                    Color.BLACK
            );

            txtName.setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
            );

            serviceLayout.addView(
                    txtName
            );

            // =================================================
            // CATEGORY
            // =================================================

            TextView txtCategory =
                    new TextView(this);

            txtCategory.setText(
                    "Category: " +
                            category
            );

            txtCategory.setTextSize(15);

            txtCategory.setTextColor(
                    Color.rgb(
                            25,
                            118,
                            210
                    )
            );

            serviceLayout.addView(
                    txtCategory
            );

            // =================================================
            // DESCRIPTION
            // =================================================

            TextView txtDescription =
                    new TextView(this);

            txtDescription.setText(
                    "Description: " +
                            description
            );

            txtDescription.setTextSize(15);

            txtDescription.setTextColor(
                    Color.DKGRAY
            );

            serviceLayout.addView(
                    txtDescription
            );

            // =================================================
            // PRICE
            // =================================================

            TextView txtPrice =
                    new TextView(this);

            txtPrice.setText(
                    "Estimated Price: Rs. " +
                            String.format(
                                    java.util.Locale.getDefault(),
                                    "%.2f",
                                    price
                            )
            );

            txtPrice.setTextSize(15);

            txtPrice.setTextColor(
                    Color.DKGRAY
            );

            serviceLayout.addView(
                    txtPrice
            );

            // =================================================
            // DURATION
            // =================================================

            TextView txtDuration =
                    new TextView(this);

            txtDuration.setText(
                    "Duration: " +
                            duration
            );

            txtDuration.setTextSize(15);

            txtDuration.setTextColor(
                    Color.DKGRAY
            );

            serviceLayout.addView(
                    txtDuration
            );

            // =================================================
            // STATUS
            // =================================================

            TextView txtStatus =
                    new TextView(this);

            txtStatus.setText(
                    "Status: " +
                            status
            );

            txtStatus.setTextSize(15);

            txtStatus.setTextColor(
                    Color.DKGRAY
            );

            serviceLayout.addView(
                    txtStatus
            );

            // =================================================
            // UPDATE BUTTON
            // =================================================

            Button btnUpdate =
                    new Button(this);

            btnUpdate.setText(
                    "Update Service"
            );

            btnUpdate.setTextSize(14);

            LinearLayout.LayoutParams buttonParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            buttonParams.setMargins(
                    0,
                    14,
                    0,
                    8
            );

            btnUpdate.setLayoutParams(
                    buttonParams
            );

            serviceLayout.addView(
                    btnUpdate
            );

            // =================================================
            // UPDATE CLICK
            // =================================================

            btnUpdate.setOnClickListener(
                    v -> {

                        Intent intent =
                                new Intent(
                                        ManageServicesActivity.this,
                                        UpdateServiceActivity.class
                                );

                        intent.putExtra(
                                "serviceId",
                                serviceId
                        );

                        startActivity(intent);
                    }
            );

            // =================================================
            // ADD SERVICE CARD
            // =================================================

            serviceContainer.addView(
                    serviceLayout
            );
        }

        cursor.close();
    }

    // =========================================================
    // DELETE SERVICE DIALOG
    // =========================================================

    private void showDeleteServiceDialog() {

        SQLiteDatabase db =
                databaseHelper
                        .getReadableDatabase();

        Cursor cursor = null;

        ArrayList<String> serviceIds =
                new ArrayList<>();

        ArrayList<String> serviceDisplayNames =
                new ArrayList<>();

        try {

            cursor =
                    db.rawQuery(
                            "SELECT serviceId, serviceName " +
                                    "FROM services " +
                                    "ORDER BY serviceId",
                            null
                    );

            while (cursor.moveToNext()) {

                String serviceId =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "serviceId"
                                )
                        );

                String serviceName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "serviceName"
                                )
                        );

                serviceIds.add(
                        serviceId
                );

                serviceDisplayNames.add(
                        serviceId +
                                " - " +
                                serviceName
                );
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        // =====================================================
        // NO SERVICES
        // =====================================================

        if (serviceIds.isEmpty()) {

            Toast.makeText(
                    this,
                    "No services available to delete",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // =====================================================
        // SPINNER
        // =====================================================

        LinearLayout dialogLayout =
                new LinearLayout(this);

        dialogLayout.setOrientation(
                LinearLayout.VERTICAL
        );

        int padding =
                (int) (
                        20 *
                                getResources()
                                        .getDisplayMetrics()
                                        .density
                );

        dialogLayout.setPadding(
                padding,
                padding,
                padding,
                padding
        );

        TextView instruction =
                new TextView(this);

        instruction.setText(
                "Select the service you want to delete:"
        );

        instruction.setTextSize(15);

        instruction.setTextColor(
                Color.DKGRAY
        );

        instruction.setPadding(
                0,
                0,
                0,
                padding / 2
        );

        dialogLayout.addView(
                instruction
        );

        Spinner spinnerServices =
                new Spinner(this);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        serviceDisplayNames
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerServices.setAdapter(
                adapter
        );

        dialogLayout.addView(
                spinnerServices
        );

        // =====================================================
        // SHOW DIALOG
        // =====================================================

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle(
                                "Delete Service"
                        )
                        .setView(
                                dialogLayout
                        )
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .setPositiveButton(
                                "Delete",
                                null
                        )
                        .create();

        dialog.setOnShowListener(
                d -> {

                    Button deleteButton =
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE
                            );

                    deleteButton.setTextColor(
                            Color.rgb(
                                    179,
                                    38,
                                    30
                            )
                    );

                    deleteButton.setOnClickListener(
                            v -> {

                                int selectedPosition =
                                        spinnerServices
                                                .getSelectedItemPosition();

                                if (selectedPosition < 0 ||
                                        selectedPosition >=
                                                serviceIds.size()) {

                                    Toast.makeText(
                                            ManageServicesActivity.this,
                                            "Please select a service",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }

                                String selectedServiceId =
                                        serviceIds.get(
                                                selectedPosition
                                        );

                                String selectedDisplayName =
                                        serviceDisplayNames.get(
                                                selectedPosition
                                        );

                                // ---------------------------------
                                // FINAL CONFIRMATION
                                // ---------------------------------

                                new AlertDialog.Builder(
                                        ManageServicesActivity.this
                                )
                                        .setTitle(
                                                "Confirm Delete"
                                        )
                                        .setMessage(
                                                "Are you sure you want to delete:\n\n" +
                                                        selectedDisplayName +
                                                        "?"
                                        )
                                        .setNegativeButton(
                                                "Cancel",
                                                null
                                        )
                                        .setPositiveButton(
                                                "Delete",
                                                (confirmDialog,
                                                 which) -> {

                                                    deleteService(
                                                            selectedServiceId
                                                    );
                                                }
                                        )
                                        .show();

                                dialog.dismiss();
                            }
                    );
                }
        );

        dialog.show();
    }

    // =========================================================
    // DELETE SERVICE
    // =========================================================

    private void deleteService(
            String serviceId) {

        SQLiteDatabase db =
                databaseHelper
                        .getWritableDatabase();

        int deleted =
                db.delete(
                        "services",
                        "serviceId = ?",
                        new String[]{
                                serviceId
                        }
                );

        if (deleted > 0) {

            Toast.makeText(
                    this,
                    "Service deleted successfully",
                    Toast.LENGTH_SHORT
            ).show();

            loadServices(
                    edtSearch
                            .getText()
                            .toString()
                            .trim()
            );

        } else {

            Toast.makeText(
                    this,
                    "Unable to delete service",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}