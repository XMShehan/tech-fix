package com.example.techfix;

import android.content.ContentValues;
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

public class AddTechnicianActivity extends AppCompatActivity {

    EditText edtTechnicianId;
    EditText edtTechnicianName;
    EditText edtTechnicianPhone;
    EditText edtTechnicianEmail;
    EditText edtTechnicianPassword;

    Button btnSaveTechnician;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_add_technician);

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

        btnSaveTechnician =
                findViewById(R.id.btnSaveTechnician);

        // Database
        databaseHelper = new DatabaseHelper(this);

        // Save Technician
        btnSaveTechnician.setOnClickListener(v -> {

            String technicianId =
                    edtTechnicianId.getText().toString().trim();

            String technicianName =
                    edtTechnicianName.getText().toString().trim();

            String technicianPhone =
                    edtTechnicianPhone.getText().toString().trim();

            String technicianEmail =
                    edtTechnicianEmail.getText().toString().trim();

            String technicianPassword =
                    edtTechnicianPassword.getText().toString().trim();

            // Check required fields
            if (technicianId.isEmpty() ||
                    technicianName.isEmpty() ||
                    technicianPhone.isEmpty() ||
                    technicianEmail.isEmpty() ||
                    technicianPassword.isEmpty()) {

                Toast.makeText(
                        this,
                        "Please fill all fields",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Get writable database
            SQLiteDatabase db =
                    databaseHelper.getWritableDatabase();

            // Store technician data
            ContentValues values = new ContentValues();

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
                    technicianPhone
            );

            values.put(
                    "email",
                    technicianEmail
            );

            values.put(
                    "password",
                    technicianPassword
            );

            // Insert into technicians table
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
                        "Failed to add technician",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}