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

public class DeleteTechnicianActivity extends AppCompatActivity {

    private EditText edtTechnicianId;
    private Button btnCancel;
    private Button btnDeleteTechnician;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_delete_technician);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars = insets.getInsets(
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
        btnCancel = findViewById(R.id.btnCancel);
        btnDeleteTechnician = findViewById(R.id.btnDeleteTechnician);

        // Database
        databaseHelper = new DatabaseHelper(this);

        // Cancel button
        btnCancel.setOnClickListener(v -> {
            finish();
        });

        // Delete Technician button
        btnDeleteTechnician.setOnClickListener(v -> {

            String technicianId =
                    edtTechnicianId.getText().toString().trim();

            // Validation
            if (technicianId.isEmpty()) {

                Toast.makeText(
                        DeleteTechnicianActivity.this,
                        "Please enter Technician ID",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Delete from database
            SQLiteDatabase db =
                    databaseHelper.getWritableDatabase();

            int result = db.delete(
                    "technicians",
                    "technicianId = ?",
                    new String[]{technicianId}
            );

            if (result > 0) {

                Toast.makeText(
                        DeleteTechnicianActivity.this,
                        "Technician deleted successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        DeleteTechnicianActivity.this,
                        "Technician ID not found",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}