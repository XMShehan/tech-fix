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
import android.view.WindowManager;
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

public class ManageTechniciansActivity extends AppCompatActivity {

    private Button btnAddTechnician;
    private Button btnDeleteTechnician;

    private EditText edtSearchTechnicians;

    private LinearLayout technicianContainer;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        // Helps the screen resize correctly when keyboard appears
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );

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

        // =====================================================
        // CONNECT UI
        // =====================================================

        btnAddTechnician =
                findViewById(R.id.btnAddTechnician);

        btnDeleteTechnician =
                findViewById(R.id.btnDeleteTechnician);

        edtSearchTechnicians =
                findViewById(R.id.edtSearchTechnicians);

        technicianContainer =
                findViewById(R.id.technicianContainer);

        // =====================================================
        // DATABASE
        // =====================================================

        databaseHelper =
                new DatabaseHelper(this);

        // =====================================================
        // ADD TECHNICIAN
        // =====================================================

        btnAddTechnician.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            ManageTechniciansActivity.this,
                            AddTechnicianActivity.class
                    );

            startActivity(intent);
        });

        // =====================================================
        // DELETE TECHNICIAN
        // =====================================================

        btnDeleteTechnician.setOnClickListener(v -> {

            showDeleteTechnicianDialog();

        });

        // =====================================================
        // SEARCH
        // =====================================================

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

                        loadTechnicians(
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

        loadTechnicians("");
    }

    // =========================================================
    // REFRESH WHEN RETURNING TO SCREEN
    // =========================================================

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null &&
                edtSearchTechnicians != null) {

            loadTechnicians(
                    edtSearchTechnicians
                            .getText()
                            .toString()
                            .trim()
            );
        }
    }

    // =========================================================
    // LOAD TECHNICIANS
    // =========================================================

    private void loadTechnicians(
            String searchText) {

        technicianContainer.removeAllViews();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = null;

        try {

            String search =
                    "%"
                            + searchText
                            + "%";

            /*
             * IMPORTANT:
             * Current technicians table has:
             *
             * technicianId
             * technicianName
             * phone
             * email
             * password
             * branch
             *
             * There is NO specialization column.
             */

            cursor = db.rawQuery(

                    "SELECT technicianId, technicianName, " +
                            "phone, email, branch " +
                            "FROM technicians " +

                            "WHERE technicianId LIKE ? " +
                            "OR technicianName LIKE ? " +
                            "OR phone LIKE ? " +
                            "OR email LIKE ? " +
                            "OR branch LIKE ? " +

                            "ORDER BY technicianId",

                    new String[]{
                            search,
                            search,
                            search,
                            search,
                            search
                    }
            );

            boolean hasTechnicians =
                    false;

            while (cursor.moveToNext()) {

                hasTechnicians = true;

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

                String branch =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "branch"
                                )
                        );

                createTechnicianCard(
                        technicianId,
                        technicianName,
                        phone,
                        email,
                        branch
                );
            }

            if (!hasTechnicians) {

                TextView emptyText =
                        new TextView(this);

                if (searchText.isEmpty()) {

                    emptyText.setText(
                            "No technicians available"
                    );

                } else {

                    emptyText.setText(
                            "No technicians found"
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

                technicianContainer.addView(
                        emptyText
                );
            }

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to load technicians: "
                            + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // =========================================================
    // CREATE TECHNICIAN CARD
    // =========================================================

    private void createTechnicianCard(
            String technicianId,
            String technicianName,
            String phone,
            String email,
            String branch) {

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
        // TECHNICIAN ID
        // =====================================================

        TextView txtId =
                new TextView(this);

        txtId.setText(
                "Technician ID: "
                        + safeText(technicianId)
        );

        txtId.setTextSize(15);

        txtId.setTextColor(
                Color.DKGRAY
        );

        txtId.setTypeface(
                null,
                Typeface.BOLD
        );

        card.addView(txtId);

        // =====================================================
        // NAME
        // =====================================================

        TextView txtName =
                new TextView(this);

        txtName.setText(
                safeText(technicianName)
        );

        txtName.setTextSize(20);

        txtName.setTextColor(
                Color.BLACK
        );

        txtName.setTypeface(
                null,
                Typeface.BOLD
        );

        txtName.setPadding(
                0,
                8,
                0,
                12
        );

        card.addView(txtName);

        // =====================================================
        // DETAILS
        // =====================================================

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

        card.addView(
                createInfoText(
                        "Branch: "
                                + safeText(branch)
                )
        );

        // =====================================================
        // UPDATE BUTTON
        // =====================================================

        Button btnUpdate =
                new Button(this);

        btnUpdate.setText(
                "Update Technician"
        );

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
                            ManageTechniciansActivity.this,
                            UpdateTechnicianActivity.class
                    );

            intent.putExtra(
                    "technicianId",
                    technicianId
            );

            startActivity(intent);
        });

        card.addView(
                btnUpdate
        );

        technicianContainer.addView(
                card
        );
    }

    // =========================================================
    // DELETE TECHNICIAN DIALOG
    // =========================================================

    private void showDeleteTechnicianDialog() {

        ArrayList<String> technicianIds =
                new ArrayList<>();

        ArrayList<String> technicianNames =
                new ArrayList<>();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor = db.rawQuery(
                    "SELECT technicianId, technicianName " +
                            "FROM technicians " +
                            "ORDER BY technicianId",
                    null
            );

            while (cursor.moveToNext()) {

                technicianIds.add(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "technicianId"
                                )
                        )
                );

                technicianNames.add(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "technicianName"
                                )
                        )
                );
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        if (technicianIds.isEmpty()) {

            new AlertDialog.Builder(this)
                    .setTitle(
                            "Delete Technician"
                    )
                    .setMessage(
                            "No technicians are available."
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
             i < technicianIds.size();
             i++) {

            displayList.add(
                    technicianIds.get(i)
                            + " - "
                            + technicianNames.get(i)
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
                "Select the technician you want to delete:"
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
                        "Delete Technician"
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

                            if (position < 0 ||
                                    position >= technicianIds.size()) {

                                Toast.makeText(
                                        this,
                                        "Please select a technician",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            String selectedId =
                                    technicianIds.get(
                                            position
                                    );

                            String selectedName =
                                    technicianNames.get(
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
                                                                "technicians",
                                                                "technicianId = ?",
                                                                new String[]{
                                                                        selectedId
                                                                }
                                                        );

                                                if (deleted > 0) {

                                                    Toast.makeText(
                                                            this,
                                                            "Technician deleted successfully",
                                                            Toast.LENGTH_SHORT
                                                    ).show();

                                                    loadTechnicians(
                                                            edtSearchTechnicians
                                                                    .getText()
                                                                    .toString()
                                                                    .trim()
                                                    );

                                                } else {

                                                    Toast.makeText(
                                                            this,
                                                            "Unable to delete technician",
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