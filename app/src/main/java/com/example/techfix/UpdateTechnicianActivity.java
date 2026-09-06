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

public class UpdateTechnicianActivity extends AppCompatActivity {

    EditText edtTechnicianId;
    EditText edtTechnicianName;
    EditText edtTechnicianPhone;
    EditText edtTechnicianEmail;
    EditText edtTechnicianSpecialization;

    Button btnUpdateTechnician;

    DatabaseHelper databaseHelper;

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
        edtTechnicianId = findViewById(R.id.edtTechnicianId);
        edtTechnicianName = findViewById(R.id.edtTechnicianName);
        edtTechnicianPhone = findViewById(R.id.edtTechnicianPhone);
        edtTechnicianEmail = findViewById(R.id.edtTechnicianEmail);
        edtTechnicianSpecialization =
                findViewById(R.id.edtTechnicianSpecialization);

        btnUpdateTechnician =
                findViewById(R.id.btnUpdateTechnician);

        // Database
        databaseHelper = new DatabaseHelper(this);

        // Get Technician ID from previous page
        String technicianId =
                getIntent().getStringExtra("technicianId");

        // Show Technician ID
        if (technicianId != null) {
            edtTechnicianId.setText(technicianId);
        }

        // Update Technician
        btnUpdateTechnician.setOnClickListener(v -> {

            String id =
                    edtTechnicianId.getText().toString().trim();

            String name =
                    edtTechnicianName.getText().toString().trim();

            String phone =
                    edtTechnicianPhone.getText().toString().trim();

            String email =
                    edtTechnicianEmail.getText().toString().trim();

            String specialization =
                    edtTechnicianSpecialization.getText().toString().trim();

            if (id.isEmpty() ||
                    name.isEmpty() ||
                    phone.isEmpty() ||
                    email.isEmpty() ||
                    specialization.isEmpty()) {

                Toast.makeText(
                        UpdateTechnicianActivity.this,
                        "Please fill all fields",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            SQLiteDatabase db =
                    databaseHelper.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put("technicianName", name);
            values.put("phone", phone);
            values.put("email", email);
            values.put("specialization", specialization);

            int rowsUpdated = db.update(
                    "technicians",
                    values,
                    "technicianId = ?",
                    new String[]{id}
            );

            if (rowsUpdated > 0) {

                Toast.makeText(
                        UpdateTechnicianActivity.this,
                        "Technician updated successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        UpdateTechnicianActivity.this,
                        "Technician not found",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}