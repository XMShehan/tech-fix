package com.example.techfix;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

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

        // Update Technician button
        btnUpdateTechnician.setOnClickListener(v -> {

            String technicianId =
                    edtTechnicianId.getText().toString();

            String technicianName =
                    edtTechnicianName.getText().toString();

            String technicianPhone =
                    edtTechnicianPhone.getText().toString();

            String technicianEmail =
                    edtTechnicianEmail.getText().toString();

            String specialization =
                    edtTechnicianSpecialization.getText().toString();

            // Database will be connected later.
            finish();
        });
    }
}