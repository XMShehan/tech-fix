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

public class ManageTechniciansActivity extends AppCompatActivity {

    Button btnAddTechnician;
    Button btnDeleteTechnician;

    EditText edtSearchTechnicians;

    LinearLayout technicianContainer;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_manage_technicians);

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
        btnAddTechnician =
                findViewById(R.id.btnAddTechnician);

        btnDeleteTechnician =
                findViewById(R.id.btnDeleteTechnician);

        edtSearchTechnicians =
                findViewById(R.id.edtSearchTechnicians);

        technicianContainer =
                findViewById(R.id.technicianContainer);

        // Database
        databaseHelper = new DatabaseHelper(this);

        // Add Technician button
        btnAddTechnician.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ManageTechniciansActivity.this,
                    AddTechnicianActivity.class
            );

            startActivity(intent);
        });

        // Delete Technician button
        btnDeleteTechnician.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ManageTechniciansActivity.this,
                    DeleteTechnicianActivity.class
            );

            startActivity(intent);
        });

        // Search technicians
        edtSearchTechnicians.addTextChangedListener(
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

                        loadTechnicians(s.toString());
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );

        loadTechnicians("");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            loadTechnicians(
                    edtSearchTechnicians.getText().toString()
            );
        }
    }

    private void loadTechnicians(String searchText) {

        technicianContainer.removeAllViews();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor;

        if (searchText == null || searchText.trim().isEmpty()) {

            cursor = db.rawQuery(
                    "SELECT technicianId, technicianName, phone, email, specialization " +
                            "FROM technicians",
                    null
            );

        } else {

            String search = "%" + searchText.trim() + "%";

            cursor = db.rawQuery(
                    "SELECT technicianId, technicianName, phone, email, specialization " +
                            "FROM technicians " +
                            "WHERE technicianId LIKE ? " +
                            "OR technicianName LIKE ? " +
                            "OR phone LIKE ? " +
                            "OR email LIKE ? " +
                            "OR specialization LIKE ?",
                    new String[]{
                            search,
                            search,
                            search,
                            search,
                            search
                    }
            );
        }

        if (cursor.getCount() == 0) {

            TextView emptyText = new TextView(this);

            if (searchText == null || searchText.trim().isEmpty()) {
                emptyText.setText("No technicians available");
            } else {
                emptyText.setText("No technicians found");
            }

            emptyText.setTextSize(16);
            emptyText.setTextColor(Color.GRAY);
            emptyText.setPadding(10, 20, 10, 20);

            technicianContainer.addView(emptyText);

        } else {

            while (cursor.moveToNext()) {

                String technicianId =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "technicianId"
                                )
                        );

                String technicianName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "technicianName"
                                )
                        );

                String phone =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "phone"
                                )
                        );

                String email =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "email"
                                )
                        );

                String specialization =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "specialization"
                                )
                        );

                // Container for one technician
                LinearLayout technicianLayout =
                        new LinearLayout(this);

                technicianLayout.setOrientation(
                        LinearLayout.VERTICAL
                );

                technicianLayout.setPadding(
                        12,
                        15,
                        12,
                        15
                );

                // Technician ID
                TextView txtId = new TextView(this);

                txtId.setText(
                        "Technician ID: " + technicianId
                );

                txtId.setTextSize(16);
                txtId.setTextColor(Color.BLACK);

                technicianLayout.addView(txtId);

                // Technician Name
                TextView txtName = new TextView(this);

                txtName.setText(
                        "Technician Name: " + technicianName
                );

                txtName.setTextSize(16);
                txtName.setTextColor(Color.BLACK);

                technicianLayout.addView(txtName);

                // Phone
                TextView txtPhone = new TextView(this);

                txtPhone.setText(
                        "Phone: " + phone
                );

                txtPhone.setTextSize(16);
                txtPhone.setTextColor(Color.BLACK);

                technicianLayout.addView(txtPhone);

                // Email
                TextView txtEmail = new TextView(this);

                txtEmail.setText(
                        "Email: " + email
                );

                txtEmail.setTextSize(16);
                txtEmail.setTextColor(Color.BLACK);

                technicianLayout.addView(txtEmail);

                // Specialization
                TextView txtSpecialization =
                        new TextView(this);

                txtSpecialization.setText(
                        "Specialization: " + specialization
                );

                txtSpecialization.setTextSize(16);
                txtSpecialization.setTextColor(Color.BLACK);

                technicianLayout.addView(
                        txtSpecialization
                );

                // Update Technician button
                Button btnUpdate = new Button(this);

                btnUpdate.setText(
                        "Update Technician"
                );

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

                technicianLayout.addView(btnUpdate);

                // Update button click
                btnUpdate.setOnClickListener(v -> {

                    Intent intent = new Intent(
                            ManageTechniciansActivity.this,
                            UpdateTechnicianActivity.class
                    );

                    intent.putExtra(
                            "technicianId",
                            technicianId
                    );

                    startActivity(intent);
                });

                technicianContainer.addView(
                        technicianLayout
                );
            }
        }

        cursor.close();
    }
}