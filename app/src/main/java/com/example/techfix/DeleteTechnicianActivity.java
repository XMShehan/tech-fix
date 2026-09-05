package com.example.techfix;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DeleteTechnicianActivity extends AppCompatActivity {

    private EditText edtTechnicianId;
    private Button btnCancel;
    private Button btnDeleteTechnician;

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

        // Cancel button
        btnCancel.setOnClickListener(v -> {
            finish();
        });

        // Delete Technician button
        btnDeleteTechnician.setOnClickListener(v -> {
            finish();
        });
    }
}