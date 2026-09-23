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

import java.util.ArrayList;

public class UpdateTechnicianActivity extends AppCompatActivity {

    private EditText edtTechnicianId;
    private EditText edtTechnicianName;
    private EditText edtTechnicianPhone;
    private EditText edtTechnicianEmail;
    private EditText edtTechnicianPassword;

    private Spinner spinnerBranch;

    private Button btnUpdateTechnician;

    private DatabaseHelper databaseHelper;

    private String technicianId;

    private final ArrayList<String> branchNames =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // =====================================================
        // KEYBOARD / SCROLL HANDLING
        // =====================================================

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );

        setContentView(
                R.layout.activity_update_technician
        );

        // =====================================================
        // FIND VIEWS
        // =====================================================

        edtTechnicianId =
                findViewById(R.id.edtTechnicianId);

        edtTechnicianName =
                findViewById(R.id.edtTechnicianName);

        edtTechnicianPhone =
                findViewById(R.id.edtTechnicianPhone);

        edtTechnicianEmail =
                findViewById(R.id.edtTechnicianEmail);

        edtTechnicianPassword =
                findViewById(R.id.edtTechnicianPassword);

        spinnerBranch =
                findViewById(R.id.spinnerBranch);

        btnUpdateTechnician =
                findViewById(R.id.btnUpdateTechnician);

        // =====================================================
        // DATABASE
        // =====================================================

        databaseHelper =
                new DatabaseHelper(this);

        // =====================================================
        // GET ID
        // =====================================================

        technicianId =
                getIntent()
                        .getStringExtra("technicianId");

        if (technicianId == null ||
                technicianId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Technician information is missing",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }

        edtTechnicianId.setText(
                technicianId
        );

        edtTechnicianId.setEnabled(false);

        // =====================================================
        // LOAD BRANCHES
        // =====================================================

        loadBranches();

        // =====================================================
        // LOAD TECHNICIAN
        // =====================================================

        loadTechnicianDetails();

        // =====================================================
        // UPDATE
        // =====================================================

        btnUpdateTechnician.setOnClickListener(
                v -> updateTechnician()
        );
    }

    // =========================================================
    // LOAD ACTIVE BRANCHES
    // =========================================================

    private void loadBranches() {

        branchNames.clear();

        branchNames.add("Select Branch");

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor =
                    db.rawQuery(
                            "SELECT branchName " +
                                    "FROM branches " +
                                    "WHERE status = ? " +
                                    "ORDER BY branchName",

                            new String[]{
                                    "Active"
                            }
                    );

            while (cursor.moveToNext()) {

                branchNames.add(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "branchName"
                                )
                        )
                );
            }

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to load branches",
                    Toast.LENGTH_SHORT
            ).show();

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        branchNames
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerBranch.setAdapter(adapter);
    }

    // =========================================================
    // LOAD TECHNICIAN DETAILS
    // =========================================================

    private void loadTechnicianDetails() {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        "technicians",

                        new String[]{
                                "technicianName",
                                "phone",
                                "email",
                                "password",
                                "branch"
                        },

                        "technicianId = ?",

                        new String[]{
                                technicianId
                        },

                        null,
                        null,
                        null
                );

        if (cursor.moveToFirst()) {

            edtTechnicianName.setText(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "technicianName"
                            )
                    )
            );

            edtTechnicianPhone.setText(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "phone"
                            )
                    )
            );

            edtTechnicianEmail.setText(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "email"
                            )
                    )
            );

            edtTechnicianPassword.setText(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "password"
                            )
                    )
            );

            String branch =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "branch"
                            )
                    );

            if (branch != null) {

                for (int i = 0;
                     i < spinnerBranch.getCount();
                     i++) {

                    if (spinnerBranch
                            .getItemAtPosition(i)
                            .toString()
                            .equalsIgnoreCase(
                                    branch.trim()
                            )) {

                        spinnerBranch.setSelection(i);

                        break;
                    }
                }
            }

        } else {

            Toast.makeText(
                    this,
                    "Technician not found",
                    Toast.LENGTH_LONG
            ).show();

            finish();
        }

        cursor.close();
    }

    // =========================================================
    // UPDATE TECHNICIAN
    // =========================================================

    private void updateTechnician() {

        String technicianName =
                edtTechnicianName
                        .getText()
                        .toString()
                        .trim();

        String phone =
                edtTechnicianPhone
                        .getText()
                        .toString()
                        .trim();

        String email =
                edtTechnicianEmail
                        .getText()
                        .toString()
                        .trim();

        String password =
                edtTechnicianPassword
                        .getText()
                        .toString()
                        .trim();

        if (technicianName.isEmpty() ||
                phone.isEmpty() ||
                email.isEmpty() ||
                password.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (spinnerBranch.getSelectedItemPosition() == 0) {

            Toast.makeText(
                    this,
                    "Please select an assigned branch",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String branch =
                spinnerBranch
                        .getSelectedItem()
                        .toString();

        // =====================================================
        // UPDATE
        // =====================================================

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                "technicianName",
                technicianName
        );

        values.put(
                "phone",
                phone
        );

        values.put(
                "email",
                email
        );

        values.put(
                "password",
                password
        );

        values.put(
                "branch",
                branch
        );

        int result =
                db.update(
                        "technicians",
                        values,
                        "technicianId = ?",
                        new String[]{
                                technicianId
                        }
                );

        if (result > 0) {

            Toast.makeText(
                    this,
                    "Technician updated successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Failed to update technician",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}