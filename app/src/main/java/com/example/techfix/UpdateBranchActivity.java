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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UpdateBranchActivity extends AppCompatActivity {

    private EditText edtBranchCode;
    private EditText edtBranchName;
    private EditText edtAddress;
    private EditText edtPhone;
    private EditText edtEmail;
    private EditText edtLatitude;
    private EditText edtLongitude;

    private Spinner spinnerStatus;

    private Button btnCancel;
    private Button btnUpdateBranch;

    private DatabaseHelper databaseHelper;

    private int branchId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // =====================================================
        // KEYBOARD / SCROLL FIX
        // =====================================================

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );

        EdgeToEdge.enable(this);

        setContentView(
                R.layout.activity_update_branch
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

        edtBranchCode =
                findViewById(
                        R.id.edtBranchCode
                );

        edtBranchName =
                findViewById(
                        R.id.edtBranchName
                );

        edtAddress =
                findViewById(
                        R.id.edtAddress
                );

        edtPhone =
                findViewById(
                        R.id.edtPhone
                );

        edtEmail =
                findViewById(
                        R.id.edtEmail
                );

        edtLatitude =
                findViewById(
                        R.id.edtLatitude
                );

        edtLongitude =
                findViewById(
                        R.id.edtLongitude
                );

        spinnerStatus =
                findViewById(
                        R.id.spinnerStatus
                );

        btnCancel =
                findViewById(
                        R.id.btnCancel
                );

        btnUpdateBranch =
                findViewById(
                        R.id.btnUpdateBranch
                );

        // =====================================================
        // DATABASE
        // =====================================================

        databaseHelper =
                new DatabaseHelper(this);

        // =====================================================
        // STATUS SPINNER
        // =====================================================

        String[] statusValues = {
                "Active",
                "Inactive"
        };

        ArrayAdapter<String> statusAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        statusValues
                );

        statusAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerStatus.setAdapter(
                statusAdapter
        );

        // =====================================================
        // GET BRANCH ID
        // =====================================================

        branchId =
                getIntent().getIntExtra(
                        "branchId",
                        -1
                );

        if (branchId == -1) {

            Toast.makeText(
                    this,
                    "Branch information is missing",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        // =====================================================
        // LOAD SELECTED BRANCH
        // =====================================================

        loadBranch();

        // =====================================================
        // CANCEL
        // =====================================================

        btnCancel.setOnClickListener(v -> {

            finish();

        });

        // =====================================================
        // UPDATE
        // =====================================================

        btnUpdateBranch.setOnClickListener(v -> {

            updateBranch();

        });
    }

    // =========================================================
    // LOAD SELECTED BRANCH
    // =========================================================

    private void loadBranch() {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor =
                    db.rawQuery(
                            "SELECT branchCode, branchName, address, " +
                                    "phone, email, status, latitude, longitude " +
                                    "FROM branches " +
                                    "WHERE branchId = ?",
                            new String[]{
                                    String.valueOf(branchId)
                            }
                    );

            if (cursor.moveToFirst()) {

                // =================================================
                // BRANCH CODE
                // =================================================

                edtBranchCode.setText(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "branchCode"
                                )
                        )
                );

                // =================================================
                // BRANCH NAME
                // =================================================

                edtBranchName.setText(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "branchName"
                                )
                        )
                );

                // =================================================
                // ADDRESS
                // =================================================

                edtAddress.setText(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "address"
                                )
                        )
                );

                // =================================================
                // PHONE
                // =================================================

                edtPhone.setText(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "phone"
                                )
                        )
                );

                // =================================================
                // EMAIL
                // =================================================

                edtEmail.setText(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "email"
                                )
                        )
                );

                // =================================================
                // STATUS
                // =================================================

                String status =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "status"
                                )
                        );

                if (status != null) {

                    for (int i = 0;
                         i < spinnerStatus.getCount();
                         i++) {

                        String spinnerValue =
                                spinnerStatus
                                        .getItemAtPosition(i)
                                        .toString();

                        if (status.equalsIgnoreCase(
                                spinnerValue
                        )) {

                            spinnerStatus.setSelection(i);

                            break;
                        }
                    }
                }

                // =================================================
                // LATITUDE
                // =================================================

                edtLatitude.setText(
                        String.valueOf(
                                cursor.getDouble(
                                        cursor.getColumnIndexOrThrow(
                                                "latitude"
                                        )
                                )
                        )
                );

                // =================================================
                // LONGITUDE
                // =================================================

                edtLongitude.setText(
                        String.valueOf(
                                cursor.getDouble(
                                        cursor.getColumnIndexOrThrow(
                                                "longitude"
                                        )
                                )
                        )
                );

            } else {

                Toast.makeText(
                        this,
                        "Branch not found",
                        Toast.LENGTH_LONG
                ).show();

                finish();
            }

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to load branch",
                    Toast.LENGTH_LONG
            ).show();

            finish();

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // =========================================================
    // UPDATE BRANCH
    // =========================================================

    private void updateBranch() {

        String branchCode =
                edtBranchCode
                        .getText()
                        .toString()
                        .trim();

        String branchName =
                edtBranchName
                        .getText()
                        .toString()
                        .trim();

        String address =
                edtAddress
                        .getText()
                        .toString()
                        .trim();

        String phone =
                edtPhone
                        .getText()
                        .toString()
                        .trim();

        String email =
                edtEmail
                        .getText()
                        .toString()
                        .trim();

        String status =
                spinnerStatus
                        .getSelectedItem()
                        .toString();

        String latitudeText =
                edtLatitude
                        .getText()
                        .toString()
                        .trim();

        String longitudeText =
                edtLongitude
                        .getText()
                        .toString()
                        .trim();

        // =====================================================
        // VALIDATION
        // =====================================================

        if (branchCode.isEmpty()) {

            edtBranchCode.setError(
                    "Branch code is required"
            );

            edtBranchCode.requestFocus();

            return;
        }

        if (branchName.isEmpty()) {

            edtBranchName.setError(
                    "Branch name is required"
            );

            edtBranchName.requestFocus();

            return;
        }

        if (address.isEmpty()) {

            edtAddress.setError(
                    "Address is required"
            );

            edtAddress.requestFocus();

            return;
        }

        // =====================================================
        // LATITUDE
        // =====================================================

        double latitude;

        try {

            latitude =
                    Double.parseDouble(
                            latitudeText
                    );

        } catch (NumberFormatException e) {

            edtLatitude.setError(
                    "Enter a valid latitude"
            );

            edtLatitude.requestFocus();

            return;
        }

        // =====================================================
        // LONGITUDE
        // =====================================================

        double longitude;

        try {

            longitude =
                    Double.parseDouble(
                            longitudeText
                    );

        } catch (NumberFormatException e) {

            edtLongitude.setError(
                    "Enter a valid longitude"
            );

            edtLongitude.requestFocus();

            return;
        }

        // =====================================================
        // UPDATE DATABASE
        // =====================================================

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                "branchCode",
                branchCode
        );

        values.put(
                "branchName",
                branchName
        );

        values.put(
                "address",
                address
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
                "status",
                status
        );

        values.put(
                "latitude",
                latitude
        );

        values.put(
                "longitude",
                longitude
        );

        int updated =
                db.update(
                        "branches",
                        values,
                        "branchId = ?",
                        new String[]{
                                String.valueOf(
                                        branchId
                                )
                        }
                );

        // =====================================================
        // RESULT
        // =====================================================

        if (updated > 0) {

            Toast.makeText(
                    this,
                    "Branch updated successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Unable to update branch",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}