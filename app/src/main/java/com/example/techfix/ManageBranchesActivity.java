package com.example.techfix;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Locale;

public class ManageBranchesActivity extends AppCompatActivity {

    private Button btnAddBranch;
    private Button btnDeleteBranch;

    private EditText edtSearchBranches;

    private LinearLayout branchContainer;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_manage_branches);

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

        btnAddBranch =
                findViewById(R.id.btnAddBranch);

        btnDeleteBranch =
                findViewById(R.id.btnDeleteBranch);

        edtSearchBranches =
                findViewById(R.id.edtSearchBranches);

        branchContainer =
                findViewById(R.id.branchContainer);

        // =====================================================
        // DATABASE
        // =====================================================

        databaseHelper =
                new DatabaseHelper(this);

        // =====================================================
        // ADD BRANCH
        // =====================================================

        btnAddBranch.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            ManageBranchesActivity.this,
                            AddBranchActivity.class
                    );

            startActivity(intent);
        });

        // =====================================================
        // DELETE BRANCH
        // =====================================================

        btnDeleteBranch.setOnClickListener(v -> {

            showDeleteBranchDialog();

        });

        // =====================================================
        // SEARCH
        // =====================================================

        edtSearchBranches.addTextChangedListener(
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

                        loadBranches(
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

        loadBranches("");
    }

    // =========================================================
    // REFRESH
    // =========================================================

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null &&
                edtSearchBranches != null) {

            loadBranches(
                    edtSearchBranches
                            .getText()
                            .toString()
                            .trim()
            );
        }
    }

    // =========================================================
    // LOAD BRANCHES
    // =========================================================

    private void loadBranches(
            String searchText) {

        branchContainer.removeAllViews();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = null;

        try {

            String search =
                    "%"
                            + searchText
                            + "%";

            cursor = db.rawQuery(

                    "SELECT branchId, branchCode, branchName, " +
                            "address, phone, email, status, " +
                            "latitude, longitude " +
                            "FROM branches " +

                            "WHERE branchCode LIKE ? " +
                            "OR branchName LIKE ? " +
                            "OR address LIKE ? " +
                            "OR phone LIKE ? " +
                            "OR email LIKE ? " +
                            "OR status LIKE ? " +

                            "ORDER BY branchCode",

                    new String[]{
                            search,
                            search,
                            search,
                            search,
                            search,
                            search
                    }
            );

            boolean hasBranches =
                    false;

            while (cursor.moveToNext()) {

                hasBranches = true;

                int branchId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "branchId"
                                )
                        );

                String branchCode =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "branchCode"
                                )
                        );

                String branchName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "branchName"
                                )
                        );

                String address =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "address"
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

                String status =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "status"
                                )
                        );

                double latitude =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        "latitude"
                                )
                        );

                double longitude =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        "longitude"
                                )
                        );

                createBranchCard(
                        branchId,
                        branchCode,
                        branchName,
                        address,
                        phone,
                        email,
                        status,
                        latitude,
                        longitude
                );
            }

            if (!hasBranches) {

                TextView emptyText =
                        new TextView(this);

                if (searchText.isEmpty()) {

                    emptyText.setText(
                            "No branches available"
                    );

                } else {

                    emptyText.setText(
                            "No branches found"
                    );
                }

                emptyText.setTextSize(16);

                emptyText.setTextColor(
                        Color.GRAY
                );

                emptyText.setGravity(
                        Gravity.CENTER
                );

                emptyText.setPadding(
                        0,
                        40,
                        0,
                        40
                );

                branchContainer.addView(
                        emptyText
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
    }

    // =========================================================
    // CREATE BRANCH CARD
    // =========================================================

    private void createBranchCard(
            int branchId,
            String branchCode,
            String branchName,
            String address,
            String phone,
            String email,
            String status,
            double latitude,
            double longitude) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                20,
                20,
                20,
                20
        );

        card.setBackgroundColor(
                Color.WHITE
        );

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                0,
                0,
                16
        );

        card.setLayoutParams(
                cardParams
        );

        // =====================================================
        // BRANCH CODE
        // =====================================================

        TextView txtCode =
                new TextView(this);

        txtCode.setText(
                "Branch Code: "
                        + safeText(branchCode)
        );

        txtCode.setTextSize(15);

        txtCode.setTextColor(
                Color.DKGRAY
        );

        txtCode.setTypeface(
                null,
                Typeface.BOLD
        );

        card.addView(txtCode);

        // =====================================================
        // BRANCH NAME
        // =====================================================

        TextView txtName =
                new TextView(this);

        txtName.setText(
                safeText(branchName)
        );

        txtName.setTextSize(20);

        txtName.setTextColor(
                Color.BLACK
        );

        txtName.setTypeface(
                null,
                Typeface.BOLD
        );

        LinearLayout.LayoutParams nameParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        nameParams.setMargins(
                0,
                8,
                0,
                12
        );

        txtName.setLayoutParams(
                nameParams
        );

        card.addView(txtName);

        // =====================================================
        // DETAILS
        // =====================================================

        card.addView(
                createInfoText(
                        "Address: "
                                + safeText(address)
                )
        );

        card.addView(
                createInfoText(
                        "Phone: "
                                + safeText(phone)
                )
        );

        card.addView(
                createInfoText(
                        "Email: "
                                + safeText(email)
                )
        );

        TextView txtStatus =
                createInfoText(
                        "Status: "
                                + safeText(status)
                );

        txtStatus.setTypeface(
                null,
                Typeface.BOLD
        );

        card.addView(txtStatus);

        card.addView(
                createInfoText(
                        "Location: "
                                + String.format(
                                Locale.getDefault(),
                                "%.6f, %.6f",
                                latitude,
                                longitude
                        )
                )
        );

        // =====================================================
        // UPDATE BUTTON
        // =====================================================

        Button btnUpdate =
                new Button(this);

        btnUpdate.setText(
                "Update Branch"
        );

        btnUpdate.setTextSize(14);

        LinearLayout.LayoutParams updateParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        updateParams.setMargins(
                0,
                14,
                0,
                0
        );

        btnUpdate.setLayoutParams(
                updateParams
        );

        btnUpdate.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            ManageBranchesActivity.this,
                            UpdateBranchActivity.class
                    );

            intent.putExtra(
                    "branchId",
                    branchId
            );

            startActivity(intent);
        });

        card.addView(
                btnUpdate
        );

        branchContainer.addView(
                card
        );
    }

    // =========================================================
    // DELETE BRANCH DIALOG
    // =========================================================

    private void showDeleteBranchDialog() {

        ArrayList<Integer> branchIds =
                new ArrayList<>();

        ArrayList<String> branchCodes =
                new ArrayList<>();

        ArrayList<String> branchNames =
                new ArrayList<>();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor = db.rawQuery(
                    "SELECT branchId, branchCode, branchName " +
                            "FROM branches " +
                            "ORDER BY branchCode",
                    null
            );

            while (cursor.moveToNext()) {

                branchIds.add(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "branchId"
                                )
                        )
                );

                branchCodes.add(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "branchCode"
                                )
                        )
                );

                branchNames.add(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "branchName"
                                )
                        )
                );
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        if (branchIds.isEmpty()) {

            new AlertDialog.Builder(this)
                    .setTitle(
                            "Delete Branch"
                    )
                    .setMessage(
                            "No branches are available."
                    )
                    .setPositiveButton(
                            "OK",
                            null
                    )
                    .show();

            return;
        }

        ArrayList<String> displayList =
                new ArrayList<>();

        for (int i = 0;
             i < branchIds.size();
             i++) {

            displayList.add(
                    branchCodes.get(i)
                            + " - "
                            + branchNames.get(i)
            );
        }

        Spinner spinner =
                new Spinner(this);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        displayList
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(
                adapter
        );

        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        layout.setPadding(
                24,
                10,
                24,
                10
        );

        TextView instruction =
                new TextView(this);

        instruction.setText(
                "Select the branch you want to delete:"
        );

        instruction.setTextSize(15);

        instruction.setPadding(
                0,
                0,
                0,
                12
        );

        layout.addView(
                instruction
        );

        layout.addView(
                spinner
        );

        new AlertDialog.Builder(this)
                .setTitle(
                        "Delete Branch"
                )
                .setView(layout)
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            int position =
                                    spinner
                                            .getSelectedItemPosition();

                            int selectedBranchId =
                                    branchIds.get(
                                            position
                                    );

                            String selectedName =
                                    branchNames.get(
                                            position
                                    );

                            new AlertDialog.Builder(this)
                                    .setTitle(
                                            "Confirm Delete"
                                    )
                                    .setMessage(
                                            "Delete "
                                                    + selectedName
                                                    + "?"
                                    )
                                    .setNegativeButton(
                                            "Cancel",
                                            null
                                    )
                                    .setPositiveButton(
                                            "Delete",
                                            (d, w) -> {

                                                SQLiteDatabase writeDb =
                                                        databaseHelper
                                                                .getWritableDatabase();

                                                int deleted =
                                                        writeDb.delete(
                                                                "branches",
                                                                "branchId = ?",
                                                                new String[]{
                                                                        String.valueOf(
                                                                                selectedBranchId
                                                                        )
                                                                }
                                                        );

                                                if (deleted > 0) {

                                                    Toast.makeText(
                                                            this,
                                                            "Branch deleted successfully",
                                                            Toast.LENGTH_SHORT
                                                    ).show();

                                                    loadBranches(
                                                            edtSearchBranches
                                                                    .getText()
                                                                    .toString()
                                                                    .trim()
                                                    );

                                                } else {

                                                    Toast.makeText(
                                                            this,
                                                            "Unable to delete branch",
                                                            Toast.LENGTH_SHORT
                                                    ).show();
                                                }
                                            }
                                    )
                                    .show();
                        }
                )
                .show();
    }

    // =========================================================
    // INFO TEXT
    // =========================================================

    private TextView createInfoText(
            String text) {

        TextView textView =
                new TextView(this);

        textView.setText(
                text
        );

        textView.setTextSize(15);

        textView.setTextColor(
                Color.DKGRAY
        );

        textView.setPadding(
                0,
                4,
                0,
                4
        );

        return textView;
    }

    // =========================================================
    // SAFE TEXT
    // =========================================================

    private String safeText(
            String value) {

        if (value == null ||
                value.trim().isEmpty()) {

            return "Not provided";
        }

        return value;
    }
}