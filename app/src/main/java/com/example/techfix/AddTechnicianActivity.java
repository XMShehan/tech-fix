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

public class AddTechnicianActivity extends AppCompatActivity {

    private EditText edtTechnicianId;
    private EditText edtTechnicianName;
    private EditText edtTechnicianPhone;
    private EditText edtTechnicianEmail;
    private EditText edtTechnicianPassword;

    private Spinner spinnerBranch;

    private Button btnSaveTechnician;

    private DatabaseHelper databaseHelper;

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
                R.layout.activity_add_technician
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

        btnSaveTechnician =
                findViewById(R.id.btnSaveTechnician);

        // =====================================================
        // DATABASE
        // =====================================================

        databaseHelper =
                new DatabaseHelper(this);

        // =====================================================
        // LOAD BRANCHES
        // =====================================================

        loadBranches();

        // =====================================================
        // SAVE
        // =====================================================

        btnSaveTechnician.setOnClickListener(
                v -> saveTechnician()
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
    // SAVE TECHNICIAN
    // =========================================================

    private void saveTechnician() {

        String technicianId =
                edtTechnicianId
                        .getText()
                        .toString()
                        .trim();

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

        if (technicianId.isEmpty() ||
                technicianName.isEmpty() ||
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
        // INSERT
        // =====================================================

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                "technicianId",
                technicianId
        );

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

        long result =
                db.insert(
                        "technicians",
                        null,
                        values
                );

        if (result != -1) {

            Toast.makeText(
                    this,
                    "Technician added successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Failed to add technician. Technician ID may already exist.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}