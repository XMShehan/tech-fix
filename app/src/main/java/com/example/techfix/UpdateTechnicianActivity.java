package com.example.techfix;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UpdateTechnicianActivity extends AppCompatActivity {

    EditText edtTechnicianId;
    EditText edtTechnicianName;
    EditText edtTechnicianPhone;
    EditText edtTechnicianEmail;
    EditText edtTechnicianPassword;

    Button btnUpdateTechnician;

    DatabaseHelper databaseHelper;

    String technicianId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_update_technician);

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

        btnUpdateTechnician =
                findViewById(R.id.btnUpdateTechnician);

        // Database
        databaseHelper =
                new DatabaseHelper(this);

        // Get technician ID
        technicianId =
                getIntent().getStringExtra("technicianId");

        // Show technician ID and load details
        if (technicianId != null) {

            edtTechnicianId.setText(technicianId);

            // Technician ID should not be changed
            edtTechnicianId.setEnabled(false);

            loadTechnicianDetails();
        }

        // Update Technician
        btnUpdateTechnician.setOnClickListener(v -> {

            updateTechnician();
        });
    }

    private void loadTechnicianDetails() {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = db.query(
                "technicians",
                new String[]{
                        "technicianName",
                        "phone",
                        "email",
                        "password"
                },
                "technicianId = ?",
                new String[]{technicianId},
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {

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

            String password =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "password"
                            )
                    );

            // Display current details
            edtTechnicianName.setText(technicianName);
            edtTechnicianPhone.setText(phone);
            edtTechnicianEmail.setText(email);
            edtTechnicianPassword.setText(password);
        }

        cursor.close();
    }

    private void updateTechnician() {

        String technicianName =
                edtTechnicianName.getText().toString().trim();

        String phone =
                edtTechnicianPhone.getText().toString().trim();

        String email =
                edtTechnicianEmail.getText().toString().trim();

        String password =
                edtTechnicianPassword.getText().toString().trim();

        // Validation
        if (technicianName.isEmpty() ||
                phone.isEmpty() ||
                email.isEmpty() ||
                password.isEmpty()) {

            Toast.makeText(
                    UpdateTechnicianActivity.this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

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

        int result = db.update(
                "technicians",
                values,
                "technicianId = ?",
                new String[]{technicianId}
        );

        if (result > 0) {

            Toast.makeText(
                    UpdateTechnicianActivity.this,
                    "Technician updated successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    UpdateTechnicianActivity.this,
                    "Failed to update technician",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}